package m2t.jobloader.service.controllers;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringBufferInputStream;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;

import m2t.jobloader.configuration.Configuration;
import m2t.jobloader.dao.model.Client;
import m2t.jobloader.dao.model.Container;
import m2t.jobloader.dao.model.Job;
import m2t.jobloader.dao.repositories.ClientRepository;
import m2t.jobloader.dao.repositories.ContainerRepository;
import m2t.jobloader.dao.repositories.JobRepository;
import m2t.service.model.jobloader.BoxDTO;
import m2t.service.model.jobloader.BoxTypeDTO;
import m2t.service.model.jobloader.ContainerDTO;
import m2t.service.model.jobloader.CreateOrReplaceContainerResponseDTO;
import m2t.service.model.jobloader.CustomerDTO;
import m2t.service.model.jobloader.DocketDTO;
import m2t.service.model.jobloader.GetContainerResponseDTO;
import m2t.service.model.jobloader.JobDTO;
import m2t.test.sheet.SheetsQuickstart;
import m2t.util.dockeparser.controller.DocketParserController;
import m2t.util.dockeparser.controller.DocketParserException;

@RestController()
public class JobLoaderService {

	@Autowired
	ClientRepository clientRepository;
	@Autowired
	JobRepository jobRepository;
	@Autowired
	ContainerRepository containerRepository;
	@Autowired
	Configuration configuration;

	@RequestMapping(path = "/jobloader/create", method = RequestMethod.POST)
	public CreateOrReplaceContainerResponseDTO createOrReplaceContainer(
			@RequestBody(required = true) ContainerDTO containerDTO) {
		CreateOrReplaceContainerResponseDTO response = new CreateOrReplaceContainerResponseDTO();
		// Validate container

		Map<String, Client> clients = new HashMap<>();
		List<Job> jobs = new ArrayList<>();
		String containerNumber = containerDTO.getContainerNumber();
		response.setCointainerNumber(containerNumber	);
		ContainerDTO containerDeleted = deletContainerIfExists(containerNumber);
		response.setDeletedContainer(containerDeleted);
		containerDTO.getDockets().stream().forEach(docket -> {

			Client client = getOrSaveClient(docket.getCustomer());
			clients.put(client.getClientCode(), client);
			jobs.addAll(saveJobs(docket, client, containerNumber));

		});
		Container container = new Container();
		container.setJobs(jobs);
		container.setContainerNumber(containerNumber);
		containerRepository.save(container);

		return response;
	}

	@RequestMapping(path = "/test")
	public @ResponseBody Map<String, String> test() throws GeneralSecurityException, IOException {
		Map results = new HashMap<>();
		Path directory = Paths.get(configuration.getTestDocketsFolder());
		Files.list(directory).forEach(file -> {
			if (!file.toFile().isDirectory() && file.toFile().getName().endsWith("pdf")) {
				String txt = readTxtFromPDF(file);
				try {
					Files.write((new File(file.toFile().getAbsolutePath().replaceAll("pdf", "txt")).toPath()), txt.getBytes());
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				String containerName = file.toFile().getName();
				DocketParserController parser = new DocketParserController(containerName,
						new ByteArrayInputStream(txt.getBytes()));
				ContainerDTO containerDTO = null;
				try {
					containerDTO = parser.parseContainer();
					CreateOrReplaceContainerResponseDTO result = createOrReplaceContainer(containerDTO);
					int count = jobRepository.findByContainerOrderByTotalBoxesDesc(result.getCointainerNumber()).stream()
							.mapToInt(job -> job.getTotalBoxes()).sum();
					results.put(result.getCointainerNumber(), "" + count);
				} catch (DocketParserException e) {

					e.printStackTrace();
					results.put(containerName, e.getMessage());
				}
			}

		});

		return results;
	}

	private String readTxtFromPDF(Path file) {
		PdfReader reader;
		try {
			reader = new PdfReader(new FileInputStream(file.toFile()));
		} catch (IOException e1) {
			e1.printStackTrace();
			return null;

		}
		StringWriter sw = new StringWriter();
		BufferedWriter bw = new BufferedWriter(sw);
		try {
			bw.write("__cc__ps1__\n");

			for (int page = 1; page <= reader.getNumberOfPages(); page++) {
				bw.write(PdfTextExtractor.getTextFromPage(reader, page));
			}
			bw.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				bw.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return sw.getBuffer().toString();
	}

	private Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
		String CREDENTIALS_FILE_PATH = "/test-sheet-1538740486050-4571e6210c32.json";
		GoogleCredential credential = GoogleCredential
				.fromStream(SheetsQuickstart.class.getResourceAsStream(CREDENTIALS_FILE_PATH))
				.createScoped(SheetsScopes.all());
		return credential;
	}

	@RequestMapping(path = "/containers/{containerNumber}", method = RequestMethod.GET)
	public GetContainerResponseDTO getContainer(@PathVariable(name = "containerNumber") String containerNumber) {
		GetContainerResponseDTO response = new GetContainerResponseDTO();
		Container container = containerRepository.findByContainerNumber(containerNumber);

		if (container == null) {
			response.setFound(false);
			return response;
		}
		response.setContainerNumber(containerNumber);
		response.setFound(true);
		response.setArrivalTime(container.getArrival());
		for (Job job : container.getJobs()) {
			JobDTO j = convertToDTO(job);
			response.getJobs().add(j);
		}

		return response;
	}

	private JobDTO convertToDTO(Job job) {
		JobDTO j = new JobDTO();
		if (job.getOriginalClient() != null) {
			j.setOriginalClientCode(job.getOriginalClient().getClientCode());
			j.setJobClient(job.getJobClient());
			j.setJobOriginalDeliveryAddress(job.getOriginalClient().getAddress());
		}
		if (job.getDeliverTo() != null) {
			j.setJobDeliverTo(job.getDeliverTo().getClientCode());
			j.setJobDeliveryAddress(job.getDeliverTo().getAddress());
		}
		j.setJobNumber(job.getJobCode());
		j.setTotalBoxes(job.getTotalBoxes());
		j.setTotalFrames(job.getTotalFrames());
		j.setTotalHardware(job.getTotalHardware());
		j.setTotalPanels(job.getTotalPanels());
		return j;
	}

	private ContainerDTO deletContainerIfExists(String containerNumber) {
		Container container = containerRepository.findByContainerNumber(containerNumber);
		if (container != null) {
			for (Job job : container.getJobs()) {
				jobRepository.delete(job);
			}
			containerRepository.delete(container);
		}

		// TODO: get a parser from Container to ContainerDTO and return it
		return null;
	}

	private List<Job> saveJobs(DocketDTO docket, Client client, String containerNumber) {

		List<Job> jobs = new ArrayList<>();
		docket.getJobs().stream().forEach(jobDTO -> {
			Job job = new Job();
			job.setOriginalClient(client);
			job.setOriginalDeliveryAddress(docket.getCustomer().getAddress());
			parseBoxes(jobDTO, job);

			parseSize(jobDTO, job);

			job.setJobCode(jobDTO.getJobNumber());
			job.setJobClient(jobDTO.getJobClient());

			job.setContainer(containerNumber);
			job = jobRepository.save(job);
			jobs.add(job);
		});
		return jobs;
	}

	private void parseSize(JobDTO jobDTO, Job job) {
		if (jobDTO.getSize() == 0) {
			job.setSize("??");
		} else if (jobDTO.getSize() <= 0.8) {
			job.setSize("-s");
		} else if (jobDTO.getSize() <= 1.1) {
			job.setSize("S");
		} else if (jobDTO.getSize() <= 1.6) {
			job.setSize("-m");
		} else if (jobDTO.getSize() <= 1.8) {
			job.setSize("M");
		} else if (jobDTO.getSize() <= 2.4) {
			job.setSize("L");
		} else if (jobDTO.getSize() <= 2.6) {
			job.setSize("VL");
		} else {
			job.setSize("XL");
		}
	}

	private void parseBoxes(JobDTO jobDTO, Job job) {
		int total = 0;
		for (BoxDTO box : jobDTO.getBoxes()) {
			if (BoxTypeDTO.PANEL.equals(box.getBoxType())) {
				job.setTotalPanels(job.getTotalPanels() + 1);
			} else if (BoxTypeDTO.HARDWARE.equals(box.getBoxType())) {
				job.setTotalHardware(job.getTotalHardware() + 1);
			} else {
				job.setTotalFrames(job.getTotalFrames() + 1);
			}
			total++;
		}
		job.setTotalBoxes(total);

	}

	private Client getOrSaveClient(CustomerDTO boxClient) {
		Client client = clientRepository.findByClientCode(boxClient.getCode());
		if (client == null) {
			client = new Client();
			client.setAddress(boxClient.getAddress());
			client.setClientCode(boxClient.getCode());
			client.setPhone(boxClient.getPhone());
			client = clientRepository.save(client);

		}
		return client;
	}

}