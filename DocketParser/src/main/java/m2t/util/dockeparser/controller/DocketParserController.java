package m2t.util.dockeparser.controller;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.junit.platform.commons.logging.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import m2t.service.model.jobloader.BoxDTO;
import m2t.service.model.jobloader.BoxTypeDTO;
import m2t.service.model.jobloader.ContainerDTO;
import m2t.service.model.jobloader.CustomerDTO;
import m2t.service.model.jobloader.DocketDTO;
import m2t.service.model.jobloader.JobDTO;
import m2t.util.dockeparser.model.DecomposedJobRow;
import m2t.util.dockeparser.model.SearchResult;

public class DocketParserController {

	private static final String OF_REGEX = " \\d{1,3} of \\d{1,3}";
	private static final String JOB_ID_REGEX = "[A-Z]{2}[0-9|A-Z]{6,7}-[0-9|A-Z]{2} ";
	private static final String ROW_NUM_REGEX = "\\d{1,3} ";
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
	private static final Logger logger = Logger.getLogger(DocketParserController.class.getName());
	   

	private enum Direction {
		UP, DOWN
	}

	private enum MatchingMode {
		REG_EXP, STARTS_WITH, ENDS_WITH, CONTAINS
	};

	private int cursor = 0;
	private static final String DELIVERY_ADDRESS = "Delivery Address:";
	private static final String FULL_NORMAL_ROW_REGEXP = "";
	private Map<String, DecomposedJobRow> jobRows = new HashMap<>();
	
	public DocketParserController(String fileName, InputStream fileContent) {
		this.fileName = fileName;
		this.fileContent = fileContent;
		lines = new BufferedReader(new InputStreamReader(fileContent)).lines().collect(Collectors.toList());
		jobMap = new HashMap<>();

	}

	public DocketParserController(String fileName, String content) {
		this.fileName = fileName;

		this.fileContent = new ByteArrayInputStream(content.getBytes());
		lines = new BufferedReader(new InputStreamReader(fileContent)).lines().collect(Collectors.toList());
		jobMap = new HashMap<>();

	}

	public ContainerDTO parseContainer() throws DocketParserException {

		ContainerDTO container = new ContainerDTO();
		container.setContainerNumber(translateContainerNumber(fileName));
		container.setOriginalFileName(fileName);
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

		return StringUtils.substringBefore(
				StringUtils.substringAfterLast(fileName2.replaceAll("[0-9]{4}[a-z|A-Z]{2}", "_PREFIX_"), "_PREFIX_"),
				".");
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
//		normaliseRowswithDoubleJobs(searchResult);
		normaliseRows(searchResult);
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
				boxType = boxType.replaceAll(job.isDoubleRow() ? job.getOriginalClientRow() : job.getJobClient(), "");
				boxType = boxType.replaceAll("[0-9]{1,3} of [0-9]{1,3}", "").trim();
				BoxDTO box = new BoxDTO();
				box.setBoxType(boxType);
				job.getBoxes().add(box);
			} else if (row.contains(SIZE_SUMMARY) || row.contains(COUNT)) {
				continue;
			} else {
				if (!job.getJobClient().contains(row.trim())) {
					if (!job.isDoubleRow()) {
						job.setDoubleRow(true);
						job.setOriginalClientRow(job.getJobClient());
					}
					job.setJobClient(job.getJobClient() + " " + row.trim());
					jobIdMapper.replace(job.getJobNumber(), job.getJobClient());
				}
			}

		}
		return jobs;
	}
	
	
	private void normaliseRows(SearchResult searchResult) {
		try {
			ObjectMapper om = new ObjectMapper();
			logger.info(om.writerWithDefaultPrettyPrinter().writeValueAsString(searchResult));
		} catch (JsonProcessingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		boolean normalised = true;
		
		int expectedRowNumber = 1;
		for(int index = 0; index < searchResult.getRows().size(); index++) {
			String row = searchResult.getRows().get(index);
			
			if(isANormalRow(row)) {
				/*
				 * that could two cases:
				 * 1) the row is normal 
				 * 63 AU90380A-S1 complete client name Panel 1 of 2
				 *  	assert:
				 *  	if there is another row the line number is 64 if there is not, the box number is 2 of 2
				 *  	then
				 *  	the decomposed job can be used as a template
				 *  2) the next row has the continuation of the client name, and eventually even the second next, and what about the third next
				 *  45 AU89891A-S1 R SO080990/SO087242-NSW-Burris (Eric Panel 1 of 2
					Turner)-PO026349/PO028688
					that means that the second line should be added at the first one and the decomposed job will be used as a template
				 */
				DecomposedJobRow jobRow = extractDecomposedJobRow(row);
				if(jobRow == null) {
					continue;
				}
				
				//case 2 begin
				List<String> nonJobRows = rowsBeforNextNormalRow(searchResult.getRows().subList(index + 1, searchResult.getRows().size()));
				//check next rows if they have still the client name
				for(int subListIndex = 0; subListIndex < nonJobRows.size(); subListIndex++) {
					//Remove all the next rows and add that to the DecomposedRow;
					String nonJobRow = nonJobRows.get(subListIndex);
					normalised = false;
					jobRow.setFullClientName(jobRow.getFullClientName() + nonJobRow);
					//remove row from list and remplace the current one with the full row
					searchResult.getRows().remove(index + 1 + subListIndex);
					searchResult.getRows().set(index, jobRow.toString());
				}
				//case 2 end
				
				jobRows.put(jobRow.getJobId(), jobRow);
				
			}else {
				normalised = false;
				/*case 1:
				 * this is the case of two boxes on the same row. which it means it contains two normal Rows is enough to split and chekcs
				 * The row needs to be splitted and then added. 
				 * Then you have to make sure that the third row is normal, 
				 * otherwise it means that the second row just added is broken like this one:
				 * 24 AU89320A-S1 SO087017-NSW-Mayes/Miller-PO028599 Frame 13 of 1325 AU89891A-S1 R SO080990/SO087242-NSW-Burris (Eric Panel 1 of 2
				   Turner)-PO026349/PO028688
				 * case 2:
				 * the it wasn't possible to create a decomponsed job because the box wasn't unrecognised so it needs to go through another iteration
				 */
				//case 1
				String[] splittedRows = splitMultipleJobRow(row);
				if(splittedRows.length > 1) {
						searchResult.getRows().set(index, splittedRows[0]);
					for(int j = 1; j < splittedRows.length; j++) {
						searchResult.getRows().add(index + 1 + j, splittedRows[j]);
					}
				}else {
					//maybe the box wasn't recognised so check in the map if there is another job if there is not put set the box as a frame
					//and move on
					DecomposedJobRow decomponsed = extractDecomposedJobRow(row);
					boolean shouldContinue = false;
					if(decomponsed != null) {
						if(decomponsed.getBoxType() == null) {
							if(jobRows.containsKey(decomponsed.getJobId())) {
								String originalClient = jobRows.get(decomponsed.getJobId()).getBrokenClient();
								String boxType = decomponsed.getBrokenClient().substring(originalClient.length()).trim();
								if(StringUtils.isNotBlank(boxType)) {
									try {
										BoxTypeDTO.valueOf(boxType);
									} catch (Exception e) {
										boxType = BoxTypeDTO.FRAME.getCodeOnDocket();
									}
								}else {
									boxType = BoxTypeDTO.FRAME.getCodeOnDocket();
								}
								decomponsed.setBoxType(boxType);
								decomponsed.setBrokenClient(jobRows.get(decomponsed.getJobId()).getBrokenClient());
								decomponsed.setFullClientName(jobRows.get(decomponsed.getJobId()).getFullClientName());
								searchResult.getRows().set(index, decomponsed.toString());
							}
						}else {
							shouldContinue = true;
						}
					}else {
						//check if there is another box for the same job after .. otherwise just skip it could be an extra name
						//skip the next nonJobRows .. usually 0 rows
						List<String> nonJobRows = rowsBeforNextNormalRow(searchResult.getRows().subList(index + 1, searchResult.getRows().size()));
						int nextJobRowIndex = nonJobRows.size();
						if(index + nextJobRowIndex < searchResult.getRows().size()) {
							String nextJobRow = searchResult.getRows().get(nextJobRowIndex );
							DecomposedJobRow nextDecomposed = extractDecomposedJobRow(nextJobRow);
							if(nextDecomposed != null && nextDecomposed.getBoxType() != null && nextDecomposed.getJobId().equals(decomponsed.getJobId())) {
								String originalClient = nextDecomposed.getBrokenClient();
								String boxType = decomponsed.getBrokenClient().substring(originalClient.length()).trim();
								decomponsed.setBoxType(boxType);
								decomponsed.setBrokenClient(originalClient);
								decomponsed.setFullClientName(nextDecomposed.getFullClientName());
								searchResult.getRows().set(index, decomponsed.toString());
								
							}else {
								shouldContinue = true;
							}
						}
					}
					if(shouldContinue) {
						continue;
					}
				}
				
			}
			
		}
		if(!normalised) {
			logger.info(" ----------------- I'm going to re normalise ------------ "+ searchResult.getRows().get(0) );
			normaliseRows(searchResult);
		}
	}
	
	private String[] splitMultipleJobRow(String row) {
		logger.info("try to split : " + row);
		List<String> rowNumJobId = regExp(ROW_NUM_REGEX+JOB_ID_REGEX, row);
		
		if(rowNumJobId.isEmpty()) {
			logger.info("return empty array");
			return new String[0];
		}
		int found = rowNumJobId.size();
		logger.info("splitting in " + found);
		if(found > 1 ) {
			
			
			String firstGroup = rowNumJobId.get(0);
			int rowNumber = extractRowNumber(row);
			logger.info("first row num " + rowNumber);
			if(rowNumber < 1) {
				return  new String[0];
			}
			String[] result = new String[found];
			for(int i =0; i < found; i++) {
				String currentRowPattern = "" + (rowNumber+i)+" "+JOB_ID_REGEX;
				Matcher jobRowNumIdMatcher = Pattern.compile(currentRowPattern).matcher(row);
				if(!jobRowNumIdMatcher.find()) {
					logger.warning("Could not find a the row numb " + (rowNumber + i) + " with a job id");
					result[i]= "";
					continue;
				}
				String group = jobRowNumIdMatcher.group();
				String splittedRow = "";
				if(i == found-1) {
					splittedRow = row.substring(row.indexOf(group)).trim();
				}else {
					Pattern nextJobIdPattern = Pattern.compile(JOB_ID_REGEX);
					Matcher nextJobIdMatcher = nextJobIdPattern.matcher(row.substring(row.indexOf(group) + group.length()));
					if(!nextJobIdMatcher.find()) {
						logger.warning("I was looking for the next job id after " + group + " ----- from the row " + row + " --- but i didn't find");
						result[i]= "";
						continue;
					}
					String nextJobId = nextJobIdMatcher.group();
					logger.info(" next job id " + nextJobId);
					splittedRow =  row.substring(row.indexOf(group), row.indexOf((rowNumber + i + 1) +" "+ nextJobId)).trim();
				}
				logger.info("splitted row: " + splittedRow);
				result[i] = splittedRow;
				
			}
			return result;
		}else {
			return new String[]{row};
		}
		
		
	}

	private List<String> regExp(String regex, String row) {
		List<String> results = new ArrayList<>();
		try {
			Matcher matcher = Pattern.compile(regex).matcher(row);
			while(matcher.find()) {
				results.add(matcher.group());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return results;
	}

	private DecomposedJobRow extractDecomposedJobRow(String row) {
		int index = 0;
		logger.info("original row: " + row);
		if(row.length() < 4) {
			logger.info("returning null for row " + row);
			return null;
		}
		int rowNumber = extractRowNumber(row);
		if(rowNumber < 1) {
			logger.info("returning null for row " + row);
			return null;
		}
		
		
		
		index = 2;
		if(rowNumber > 9) {
			index = 3; 
		}
		if(rowNumber > 99) {
			index = 4;
		}
		String testStr = row.substring(index);
		testStr = testStr.substring(0, testStr.indexOf(" "));
		logger.info("extracting jobId = " + testStr);
		String jobId = testStr;
		if(jobId == null) {
			logger.info("returning null for row " + row);
			return null;
		}
		index = row.indexOf(jobId) + jobId.length();
		testStr = row.substring(index).trim();
		if(regExp(JOB_ID_REGEX, row).size() > 1) {
			logger.info("returning null because double row for row " + row);
			return null;
			//there are two jobs in the same row
		}
		String ofStr = findLastRegExp(OF_REGEX, testStr);
	
		if (ofStr == null) {
			logger.info("returning null because not of reg ex for row " + row);
			return null;
		}
		//take the last of
		logger.info("extracting of  = " + ofStr);
		String[] ofNums = ofStr.trim().split(" of ");
		if(ofNums.length < 2) {
			logger.info("returning null for row " + row);
			return null;
		}
		int boxNum;
		int maxBoxes;
		try {
			boxNum = Integer.parseInt(ofNums[0]);
			maxBoxes = Integer.parseInt(ofNums[1]);
		} catch (NumberFormatException e) {
			logger.info("returning null for row " );
			return null;
		}
		logger.info("elaborated " + boxNum  + " of " + maxBoxes);
		testStr = testStr.substring(0, testStr.length() - ofStr.length());
		String boxType = null;
		String brokenClient = testStr.trim();
		for(BoxTypeDTO type: BoxTypeDTO.values()) {
			int boxTypeIndex = testStr.toUpperCase().lastIndexOf(type.getCodeOnDocket().toUpperCase());
			if(boxTypeIndex >= 0) {
				boxType = testStr.substring(boxTypeIndex).trim();
				brokenClient = testStr.substring(0, boxTypeIndex).trim();
				break;
			}
		}
		logger.info("elaborate boxType = " + boxType);
		logger.info("extracting client  = " + brokenClient);
		DecomposedJobRow decomposed = new DecomposedJobRow();
		decomposed.setBoxNumber(boxNum);
		decomposed.setBoxType(boxType);
		decomposed.setBrokenClient(brokenClient);
		decomposed.setFullClientName(brokenClient);
		decomposed.setJobId(jobId);
		decomposed.setRowNumber(rowNumber);
		decomposed.setTotalBoxes(maxBoxes);
		logger.info("OK 		-----------------------		returning " + decomposed.toString());
		return decomposed;
	}

	private String findLastRegExp(String regex, String testStr) {
		Matcher matcher = Pattern.compile(regex).matcher(testStr);
		String result = null;
		while(matcher.find()) {
			result = matcher.group();
		}
		return result;
	}

	private String findFirstRegExp(String regex, String testStr) {
		Matcher matcher = Pattern.compile(regex).matcher(testStr);
		if(!matcher.find()) {
			return null;
		}
		return matcher.group();
	}

	private int extractRowNumber(String row) {
		logger.info("extracting rowNumber " + row);
		Pattern pattern = Pattern.compile("^"+ROW_NUM_REGEX);
		Matcher matcher = pattern.matcher(row);
		if(!matcher.find()) {
			return -1;
		}
		try {
			String found = matcher.group().trim();
			logger.info("found row number "  + found);
			return Integer.parseInt(found);
		} catch (NumberFormatException e) {
			return -1;
		}
		
		
	}

	/**
	 * Extract all the rows from index 0 where the method isAnormalRow return false till the first return true or the end of the rows;
	 * @param subList
	 * @return
	 */
	private List<String> rowsBeforNextNormalRow(List<String> rows) {
		List<String> notJobRows = new ArrayList<>();
		for(String row: rows) {
			if(!isANormalRow(row)) {
				if(splitMultipleJobRow(row).length < 1) {
					notJobRows.add(row);
				}
			}else {
				break;
			}
		}
		return notJobRows;
	}

	/**
	 * Check that the row has all the elements of the decomposed row and only 1 [rowid jobid] and the DecomposedJobRow can be extracted 
	 * 
	 * @param row
	 * @return
	 */
	private boolean isANormalRow(String row) {
		if(row.contains(COUNT) || row.contains(SIZE_SUMMARY)) {
			return true;
		}
		//check if there is only one job
		boolean onlyOneJob = regExp(ROW_NUM_REGEX + JOB_ID_REGEX, row).size() == 1;
		//check if the DecomponsedRow can be extracted
		DecomposedJobRow decomposed = extractDecomposedJobRow(row);
		return onlyOneJob && decomposed != null && decomposed.getBoxType() != null;
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
					// try another method before to give up... check on previous row

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

			} else if (!isAJobRow(row) && row.split("[A-Z]{2}[0-9|A-Z]{6,7}-[0-9|A-Z]{2}").length > 1
					&& !(row.split("M2T-[A-Z]{2}[0-9|A-Z]{6,7}-[0-9|A-Z]{2}").length > 1)) {
				Pattern pattern = Pattern.compile("[A-Z]{2}[0-9|A-Z]{6,7}-[0-9|A-Z]{2}");
				Matcher matcher = pattern.matcher(row);
				if (matcher.find()) {
					StringBuffer sb = new StringBuffer();
					sb.append("00 ");
					String jobId = matcher.group();
					sb.append(jobId);
					sb.append(StringUtils.substringAfter(row, jobId));
					searchResult.getRows().remove(i);
					searchResult.getRows().add(i, sb.toString());
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
						.replaceAll(" (Panel|Frame|Pelmet|louver|hardware|timber|Blind) [0-9]{1,3} of [0-9]{1,3}", "");
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
				if ((index + 1) < searchResult.getRows().size()) {
					clientCode = normaliseClientCode(searchResult.getRows().get(index + 1));
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
		if (spaceSplitted.length > 1) {
			String lastWord = spaceSplitted[spaceSplitted.length - 1];
			if (StringUtils.isAllLowerCase(lastWord) || StringUtils.capitalize(lastWord).equals(lastWord)) {
				// THere is the Metro from the DZONE section
				return StringUtils.substringBefore(row, lastWord).trim();
			} else {
				return row;
			}

		} else {
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

//	public static void main(String[] args) throws DocketParserException, JsonProcessingException, IOException {
//		String fileName = "delivery_docket_2018AU5112.pdf.txt";
//		String folder = "C:\\Danilo\\\\Andrew\\5112";
//		FileInputStream fis = new FileInputStream(Paths.get(folder, fileName).toFile());
//		DocketParserController parser = new DocketParserController(fileName, fis);
//		ContainerDTO container = parser.parseContainer();
//		ObjectMapper mapper = new ObjectMapper();
////		String json = mapper.writeValueAsString(container);
////		System.out.println(json);
//		Path jsonFilePath = Paths.get(folder, container.getContainerNumber() + ".json");
//		if (jsonFilePath.toFile().exists()) {
//			Files.delete(jsonFilePath);
//		}
//
//		Files.write(jsonFilePath, mapper.writerWithDefaultPrettyPrinter().writeValueAsBytes(container),
//				StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE_NEW);
//
//	}
}
