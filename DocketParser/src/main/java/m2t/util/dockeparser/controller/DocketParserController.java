package m2t.util.dockeparser.controller;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import m2t.service.model.jobloader.BoxDTO;
import m2t.service.model.jobloader.ContainerDTO;
import m2t.service.model.jobloader.CustomerDTO;
import m2t.service.model.jobloader.DocketDTO;
import m2t.service.model.jobloader.JobDTO;
import m2t.util.dockeparser.model.SearchResult;

public class DocketParserController {

	private static final String DELIVERY_NOTES = "Delivery Notes:";
	private static final String COUNT = "Count";
	private static final String SIZE_SUMMARY = "Size Summary:";
	private static final String JOB_HEADER = "No. TWO ID";
	private static final String D_ZONE = "D. Zone:";
	private static final String DEALER_CUSTOMER = "Dealer / Customer:";
	private static final String TELEPHONE = "Telephone:";
	private static final String CUSTOM_CODE_END = "__";
	private static final String CUSTOM_CODE_PAGE_START = "__cc__ps";
	private String fileName;
	private InputStream fileContent;
	private List<String> lines;
	private Map<String, JobDTO> jobMap;
	private List<String> allJobs = new ArrayList<>();

	private enum Direction {
		UP, DOWN
	}

	private enum MatchingMode {
		REG_EXP, STARTS_WITH, ENDS_WITH, CONTAINS
	};

	private int cursor = 0;
	private static final String DELIVERY_ADDRESS = "Delivery Address:";

	public DocketParserController(String fileName, InputStream fileContent) {
		this.fileName = fileName;
		this.fileContent = fileContent;
		lines = new BufferedReader(new InputStreamReader(fileContent)).lines().collect(Collectors.toList());
		jobMap = new HashMap<>();

	}

	public ContainerDTO parseContainer() throws DocketParserException {

		ContainerDTO container = new ContainerDTO();
		container.setContainerNumber(translateContainerNumber(fileName));
		container.setDockets(new ArrayList<>());

		int pageNumber = 1;
		expect(canyoufind(CUSTOM_CODE_PAGE_START + pageNumber + CUSTOM_CODE_END), CUSTOM_CODE_PAGE_START);
		do {

			DocketDTO docket = new DocketDTO();
			container.getDockets().add(docket);

			CustomerDTO customer = extractCustomer();
			docket.setCustomer(customer);

			SearchResult countSR = readTill(COUNT, Direction.DOWN, MatchingMode.STARTS_WITH, true);
			SearchResult sizeSummarySR = readTill(SIZE_SUMMARY, Direction.DOWN, MatchingMode.CONTAINS, true);

			// Scan all the jobs and extract the job ids and clients

			List<JobDTO> jobs = extractJobs(sizeSummarySR);
			SearchResult deliveryNotesSR = readTill(DELIVERY_NOTES, Direction.DOWN, MatchingMode.CONTAINS, true);
			for (int index = 0; index < deliveryNotesSR.getRows().size(); index++) {
				String row = deliveryNotesSR.getRows().get(index);
				if (row.equals("2")) {
					continue;
				}
				float size = 0.0f;
				// contains the jobid:
				if (row.matches("M2T-[A-Z]{2}[0-9|A-Z]{6,7}-[0-9|A-Z]{2}: ([0-9]{1,3}.?[0-9]{1,4} ?){1,4}m?")) {
					String jobId = row.replaceAll("M2T-", "").split(":")[0];
					String rowWithoutJobId = row.replaceAll("M2T-[A-Z]{2}[0-9|A-Z]{6,7}-[0-9|A-Z]{2}: ", "");
					size = addSizes(size, rowWithoutJobId);

					if (!row.endsWith("m")) {
						// this measurement has multiple sizes .. check the next row
						String nextLine = deliveryNotesSR.getRows().get(index + 1);
						if (nextLine.matches("([0-9]{1,3}.?[0-9]{1,4} ?){1,4}m?")) {
							// it is the last size
							index++;
							size = addSizes(size, nextLine);

						} else {
							String secondNextLine = deliveryNotesSR.getRows().get(index + 2);
							if (secondNextLine.matches("([0-9]{1,3}.?[0-9]{1,4} ?){1,4}m?")) {
								// it is the last size
								index++;
								size = addSizes(size, secondNextLine);
							}
						}
					}
					JobDTO currentJob = jobMap.get(jobId);
					currentJob.setSize(size);

				}

			}
			docket.getJobs().addAll(jobs);

		} while (searchNext(DELIVERY_ADDRESS, Direction.DOWN, MatchingMode.CONTAINS, false).isFound());

//		ObjectMapper mapper = new ObjectMapper();
//		try {
//			System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(container));
//		} catch (JsonProcessingException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}

		return container;
	}

	private String translateContainerNumber(String fileName2) {

		return StringUtils.substringBefore(StringUtils.substringAfterLast(fileName2, "AU"), ".");
	}

	private float addSizes(float size, String rowWithoutJobId) {
		for (String s : rowWithoutJobId.replaceAll("m", "").split(" ")) {
			try {
				Float f = new Float(s);
				size += f.floatValue();
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return size;
	}

	private List<JobDTO> extractJobs(SearchResult searchResult) throws DocketParserException {
		normaliseRowswithDoubleJobs(searchResult);
		allJobs.addAll(searchResult.getRows());
		Map<String, String> jobIdMapper = scanJobIds(searchResult);
		List<JobDTO> jobs = new ArrayList<>();
		JobDTO job = null;
		for (int index = 0; index < searchResult.getRows().size(); index++) {
			String row = searchResult.getRows().get(index);
			if (isAJobRow(row)) {
				String jobId = extractJobId(row);
				if (job == null || !job.getJobNumber().equals(jobId)) {
					job = new JobDTO();
					job.setJobNumber(jobId);
					job.setJobClient(jobIdMapper.get(jobId));
					jobMap.put(jobId, job);
					jobs.add(job);
				}
				String boxType = row.replaceAll("\\d{1,3} [A-Z]{2}[0-9|A-Z]{6,7}-[0-9|A-Z]{2} ", "");
				boxType = boxType.replaceAll(jobIdMapper.get(jobId), "");
				boxType = boxType.replaceAll("[0-9]{1,3} of [0-9]{1,3}", "").trim();
				BoxDTO box = new BoxDTO();
				box.setBoxType(boxType);
				job.getBoxes().add(box);
			} else if (row.contains(SIZE_SUMMARY) || row.contains(COUNT)) {
				continue;
			} else {
				if (!job.getJobClient().contains(row.trim())) {
					job.setJobClient(job.getJobClient() + " " + row.trim());
					jobIdMapper.replace(job.getJobNumber(), job.getJobClient());
				}
			}

		}
		return jobs;
	}

	private void normaliseRowswithDoubleJobs(SearchResult searchResult) {
		boolean normalised = true;

		for (int i = 0; i < searchResult.getRows().size(); i++) {
			String row = searchResult.getRows().get(i);
			int numberOfRows = row.split("[0-9]{1,3} of [0-9]{1,3}").length;
			if (numberOfRows > 1) {
				// there is a double line ... call support !! write it in the response but do
				// not continue!!!
				int rowNumberDigits = row.length() - row.replaceFirst("[0-9]{1,3} ", " ").length();
				int rowNumber = Integer.parseInt(row.substring(0, rowNumberDigits));

				String separator = "xxXxx";
				String[] splitted = row.replaceFirst("[0-9]{1,3} of ", separator).split(separator);
				
		
				int indexOf = splitted[1].indexOf("" + (rowNumber + 1), 1);
				boolean secondRowIsAJob = true;
				if (indexOf < 0) {
					secondRowIsAJob = false;
					//try another method before to give up... check on previous row
					
				}
				if (secondRowIsAJob) {
					String numOfBoxes = splitted[1].substring(0, indexOf);
					String secondRow = row.replaceFirst("[0-9]{1,3} of " + numOfBoxes, separator).split(separator)[1];
					String firstRow = StringUtils.substringBefore(row, secondRow);
					searchResult.getRows().remove(i);
					searchResult.getRows().add(i, firstRow);
					searchResult.getRows().add(i + 1, secondRow);
				} else {
					String secondRow = row.replaceAll(
							"[0-9]{1,3} [A-Z]{2}[0-9|A-Z]{6,7}-[0-9|A-Z]{2} .* [0-9]{1,3} of [0-9]{1,3}", "");
					String firstRow = StringUtils.substringBefore(row, secondRow);
					searchResult.getRows().remove(i);
					searchResult.getRows().add(i, firstRow);
				}

				normalised = false;
				break;

			}else if(!isAJobRow(row) && row.split("[A-Z]{2}[0-9|A-Z]{6,7}-[0-9|A-Z]{2}").length > 1 && !(row.split("M2T-[A-Z]{2}[0-9|A-Z]{6,7}-[0-9|A-Z]{2}").length >1)) {
				Pattern pattern = Pattern.compile("[A-Z]{2}[0-9|A-Z]{6,7}-[0-9|A-Z]{2}");
				Matcher matcher = pattern.matcher(row);
				if(matcher.find()) {
					StringBuffer sb = new StringBuffer();
					sb.append("00 ");
					String jobId = matcher.group();
					sb.append(jobId);
					sb.append(StringUtils.substringAfter(row, jobId));
					searchResult.getRows().remove(i);
					searchResult.getRows().add(i,sb.toString());
					normalised = false;
				}
			}
		}
		if (!normalised) {
			normaliseRowswithDoubleJobs(searchResult);
		}

	}

	private Map<String, String> scanJobIds(SearchResult searchResult) throws DocketParserException {
		Map<String, String> jobIdMapper = new Hashtable<>();
		for (int index = 0; index < searchResult.getRows().size(); index++) {
			String row = searchResult.getRows().get(index);
			if (isAJobRow(row)) {
				// it's a job row
				String jobId = extractJobId(row);

				// search panel frame hardware or timber

				// it's a good candidate to extract the name but if we find another one with a
				// secure key word we use that one
				String clientName = row.replaceAll("\\d{1,3} [A-Z]{2}[0-9|A-Z]{6,7}-[0-9|A-Z]{2} ", "")
						.replaceAll(" (Panel|Frame|Pelmet|louver|hardware|timber) [0-9]{1,3} of [0-9]{1,3}", "");
				if (!jobIdMapper.containsKey(jobId)) {
					jobIdMapper.put(jobId, clientName);
				} else if (jobIdMapper.get(jobId).length() > clientName.length()) {
					jobIdMapper.put(jobId, clientName);
				}
			} else {
				continue;
			}

		}
		return jobIdMapper;
	}

	private boolean isAJobRow(String row) {
		return row.matches("\\d{1,3} [A-Z]{2}[0-9|A-Z]{6,7}-[0-9|A-Z]{2}.*");
	}

	private String extractJobId(String row) throws DocketParserException {
		Pattern jobIdPattern = Pattern.compile("[A-Z]{2}[0-9|A-Z]{6,7}-[0-9|A-Z]{2}");
		Matcher jobIdMatcher = jobIdPattern.matcher(row);
		if (!jobIdMatcher.find()) {
			throw new DocketParserException("after matching the row " + row
					+ " to find a job id ... then the jobid pattern doesn't match naymore. Something wasn't planned well!!!");
		}

		String jobId = jobIdMatcher.group();
		return jobId;
	}

	private CustomerDTO extractCustomer() throws DocketParserException {
		CustomerDTO customer = new CustomerDTO();

		// find the telephone number
		SearchResult searchResult = readTill(DELIVERY_ADDRESS, Direction.DOWN, MatchingMode.STARTS_WITH, true);
		String customerName = extractCustomerName(searchResult);
		customer.setName(customerName);
		String telephone = extractTelephone(searchResult);

		customer.setPhone(telephone);
		// D. Zone:
		searchResult = readTill(D_ZONE, Direction.DOWN, MatchingMode.CONTAINS, true);

		// Clear result from Delivery Address:

		String address = extractAddress(searchResult);
		searchResult = readTill(JOB_HEADER, Direction.DOWN, MatchingMode.CONTAINS, true);
		customer.setAddress(address);
		String clientCode = extracteClientCode(searchResult);
		customer.setCode(clientCode);
		return customer;
	}

	private String extractTelephone(SearchResult searchResult) {
		for (String row : searchResult.getRows()) {
			if (row.contains(TELEPHONE)) {
				return StringUtils.substringAfter(row, TELEPHONE).replaceAll("[^0-9| ]", "").trim();
			}
		}

		return StringUtils.substringBetween(searchResult.getRowText(), TELEPHONE, DELIVERY_ADDRESS);
	}

	private String extractCustomerName(SearchResult searchResult) {
		return StringUtils.substringBetween(searchResult.getRowText(), DEALER_CUSTOMER, TELEPHONE).trim();
	}

	private String extracteClientCode(SearchResult searchResult) {
		String clientCode = "";

		for (int index = 0; index < searchResult.getRows().size(); index++) {
			String row = searchResult.getRows().get(index);
			if (row.matches("[A-Z]\\d \\- [A-Z].*")) {
				// thIS CONTAINS THE ACTUAL ZONE CODE S1 - SYD SO NEXT ROW IS THE CODE
				if((index +1) < searchResult.getRows().size()) {
					clientCode = normaliseClientCode(searchResult.getRows().get(index+1));
				}
				continue;
			} else if (row.equals(D_ZONE)) {
				continue;
			}

			if (StringUtils.isAlpha(row) && StringUtils.isAllUpperCase(row)) {
				clientCode = row;
				break;
			} else if (row.matches("([A-Z]+ )+[A-Z].*")) {
				for (String splitted : row.split("([A-Z]+ )+")) {
					if (!splitted.equals("")) {
						clientCode = StringUtils.substringBefore(row, splitted);
						break;
					}
				}
			}
			if (!clientCode.equals("")) {
				break;
			}
		}
		return clientCode.trim();
	}

	private String normaliseClientCode(String row) {
		String[] spaceSplitted = row.split(" ");
		if(spaceSplitted.length >1) {
			String lastWord = spaceSplitted[spaceSplitted.length-1];
			if(StringUtils.isAllLowerCase(lastWord) || StringUtils.capitalize(lastWord).equals(lastWord)) {
				//THere is the Metro from the DZONE section 
				return StringUtils.substringBefore(row, lastWord).trim();
			}else {
				return row;
			}
			
		}else {
			return row;
		}
	}

	private String extractAddress(SearchResult searchResult) {
		String address = "";
		for (String row : searchResult.getRows()) {
			if (row.contains(DELIVERY_ADDRESS)) {
				address = StringUtils.substringAfterLast(row, DELIVERY_ADDRESS) + " ";
			} else if (StringUtils.isNumeric(row)) {
				continue;
			} else if (StringUtils.contains(row, D_ZONE)) {
				address += StringUtils.substringBefore(row, D_ZONE);
			} else {
				address += row;
			}
		}
		return address.trim();
	}

	private SearchResult readTill(String textToSearch, Direction direction, MatchingMode matchingMode, boolean expect)
			throws DocketParserException {
		SearchResult result = new SearchResult();
		int index = getCursor();
		for (; direction == Direction.DOWN ? index < lines.size()
				: index >= 0; index += (direction == Direction.DOWN ? 1 : -1)) {
			String row = lines.get(index);
			result.addRow(row);
			if (matches(row, textToSearch, matchingMode)) {
				result.setFound(true);
				result.setLineNumber(index);
				break;
			}
		}

		if (expect) {
			expect(result.isFound(), textToSearch);
		}
		setCursor(index);
		return result;
	}

	private boolean matches(String row, String textToSearch, MatchingMode matchingMode) {
		if (matchingMode == MatchingMode.STARTS_WITH && row.startsWith(textToSearch)) {
			return true;
		} else if (matchingMode == MatchingMode.CONTAINS && row.contains(textToSearch)) {
			return true;
		}
		return false;
	}

	private boolean canyoufind(String textToFind) {
		String row = lines.get(cursor);

		return row.contains(textToFind);
	}

	private void expect(boolean expectation, String description) throws DocketParserException {
		if (!expectation) {
			throw new DocketParserException("Expecting at line " + cursor + " " + description);
		}
	}

	private SearchResult searchNext(String textToSearch, Direction direction, MatchingMode matchingMode, boolean expect)
			throws DocketParserException {
		SearchResult result = new SearchResult();
		for (int index = cursor; direction == Direction.DOWN ? index < lines.size()
				: index >= 0; index += (direction == Direction.DOWN ? 1 : -1)) {
			String row = lines.get(index);

			if (row == null) {
				throw new DocketParserException("line number " + index + " is null. Something wrong with the file");
			}
			if (matches(row, textToSearch, matchingMode)) {

				result.setRowText(row);
				result.setLineNumber(index);
				result.setFound(true);
				break;
			}

		}
		if (expect) {
			expect(result.isFound(), textToSearch);
		}

		return result;
	}

	public int getCursor() {
		return cursor;
	}

	public void setCursor(int cursor) {
		this.cursor = cursor;
	}

	public static void main(String[] args) throws DocketParserException, JsonProcessingException, IOException {
		String fileName = "delivery_docket_2018AU5112.pdf.txt";
		String folder = "C:\\Danilo\\\\Andrew\\5112";
		FileInputStream fis = new FileInputStream(Paths.get(folder, fileName).toFile());
		DocketParserController parser = new DocketParserController(fileName, fis);
		ContainerDTO container = parser.parseContainer();
		ObjectMapper mapper = new ObjectMapper();
//		String json = mapper.writeValueAsString(container);
//		System.out.println(json);
		Path jsonFilePath = Paths.get(folder, container.getContainerNumber() + ".json");
		if (jsonFilePath.toFile().exists()) {
			Files.delete(jsonFilePath);
		}

		Files.write(jsonFilePath, mapper.writerWithDefaultPrettyPrinter().writeValueAsBytes(container),
				StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE_NEW);

	}
}
