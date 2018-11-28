package m2t.jobloader.websitechecker;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.net.ssl.HttpsURLConnection;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import m2t.jobloader.websitechecker.model.WebContainerPage;
import m2t.jobloader.websitechecker.model.WebContainerRecord;

@Component
public class WebsiteChecker {
	private List<String> cookies;

	private final String USER_AGENT = "Mozilla/5.0";

	@Value("${m2t.scheduler.websitechecker.url.loginPage}")
	private String loginPageURL;

	@Value("${m2t.scheduler.websitechecker.url.containersPage}")
	private String containersPageURL;

	@Value("${m2t.scheduler.websitechecker.loginPostParams}")
	private String loginPostsParams;

	@Value("${m2t.scheduler.websitechecker.baseURL}")
	private String baseURL;
	
	private HttpsURLConnection conn;

	public WebsiteChecker() {
		cookies = new ArrayList<>();
		CookieHandler.setDefault(new CookieManager());
	}

	public boolean login() throws Exception {
		String loginPage = getPageContent(loginPageURL);
		String loggedInPage = sendPost(loginPageURL, loginPostsParams);

		return isLoogedIn(loggedInPage);
	}

	public WebContainerPage getContainersPage() throws Exception {
		String page = getPageContent(containersPageURL);
		if (!isLoogedIn(page)) {
			if (!login()) {
				throw new Exception("The WebSiteChecker class could not login. Please check settings");
			}
		}
		return exractWebContainerPage(page);
	}

	private WebContainerPage exractWebContainerPage(String html) throws Exception {
		Document document = Jsoup.parse(html);
		WebContainerPage page = new WebContainerPage();
		Elements table = document.getElementsByAttributeValue("class", "full_sit_table");
		if (table.size() < 1) {
			throw new Exception("The WebSiteChecker class could not enter in the containers page");
		}
		Element lastPageEle = document.getElementsByAttributeValue("src", "app_images/pg_last_2.gif").get(0);
		if (!lastPageEle.parent().tag().getName().toUpperCase().equals("A")) {
			page.setMaxPages(1);
		} else {
			String sNum = StringUtils.substringAfterLast(lastPageEle.parent().attr("href"), "pg=");
			page.setMaxPages(Integer.parseInt(sNum));
		}
		Elements rows = table.get(0).getElementsByAttributeValueStarting("class", "sit_row");
		List<WebContainerRecord> records = new ArrayList<>();
		for (Element tr : rows) {
			WebContainerRecord record = extractRecord(tr);
			records.add(record);
		}
		page.setRecords(records);

		return page;
	}

	private WebContainerRecord extractRecord(Element tr) throws Exception {
		WebContainerRecord record = new WebContainerRecord(baseURL);
		for (int index = 0; index < tr.children().size(); index++) {
			switch (index) {
			case 0:
				record.setID(tr.child(index).html());
				break;
			case 1:
				record.setNumber(tr.child(index).html());
				break;
			case 2:
				record.setSupplier(tr.child(index).html());
				break;
			case 3:
				record.setShippingPort(tr.child(index).html());
				break;
			case 4:
				record.setCountry(tr.child(index).html());
				break;
			case 5:
				record.setSize(tr.child(index).html());
				break;
			case 6:
				record.setCapacitySQM(tr.child(index).html());
				break;
			case 7:
				record.setOrderCutOff(tr.child(index).html());
				break;
			case 8:
				record.setLeaveFactory(tr.child(index).html());
				break;
			case 9:
				record.setETD(tr.child(index).html());
				break;
			case 10:
				record.setETA(tr.child(index).html());
				break;
			case 11:
				record.setEstimatedDeliveryDate(tr.child(index).html());
				break;
			case 12:
				record.setBoxesLoaded(tr.child(index).html());
				break;
			case 13:
				record.setDownloadPdfUrl(extractPdfUrl(tr.child(index)));
				break;
			default:
				throw new Exception(
						"While extracting the WebContainerRecord, the system found a new column. Review the code. " + index);

			}
		}
		return record;
	}

	private String extractPdfUrl(Element doubleTD) {
		Elements links = doubleTD.getElementsByAttributeValueContaining("href", "op=pdf");
		if (links.size() == 0) {
			return "";
		} else {
			return links.get(0).attr("href");
		}
	}

	private boolean isLoogedIn(String html) {
		Document doc = Jsoup.parse(html);

		return doc.getElementsContainingOwnText("Logout").size() > 0;
	}

	private String getPageContent(String url) throws Exception {

		URL obj = new URL(url);
		conn = (HttpsURLConnection) obj.openConnection();

		// default is GET
		conn.setRequestMethod("GET");

		conn.setUseCaches(false);

		// act like a browser
		conn.setRequestProperty("Host", "cloud.thewindowoutfitters.com");
		conn.setRequestProperty("Connection", "keep-alive");
		conn.setRequestProperty("Pragma", "no-cache");
		conn.setRequestProperty("Cache-Control", "no-cache");
		conn.setRequestProperty("Upgrade-Insecure-Requests", "1");
//			conn.setRequestProperty("User-Agent","Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.77 Safari/537.36");
		conn.setRequestProperty("User-Agent", USER_AGENT);
		conn.setRequestProperty("Accept",
				"text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
		conn.setRequestProperty("Accept-Language", "en-US,en;q=0.9,it;q=0.8");
		if (cookies != null) {
			for (String cookie : this.cookies) {
				conn.addRequestProperty("Cookie", cookie.split(";", 1)[0]);
			}
		}
		int responseCode = conn.getResponseCode();
		System.out.println("\nSending 'GET' request to URL : " + url);
		System.out.println("Response Code : " + responseCode);

		BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();

		// Get the response cookies
		setCookies(conn.getHeaderFields().get("Set-Cookie"));

		return response.toString();

	}
	
	public  InputStream getResponseInputStrem(String url) throws Exception {

		URL obj = new URL(url);
		conn = (HttpsURLConnection) obj.openConnection();

		// default is GET
		conn.setRequestMethod("GET");

		conn.setUseCaches(false);

		// act like a browser
		conn.setRequestProperty("Host", "cloud.thewindowoutfitters.com");
		conn.setRequestProperty("Connection", "keep-alive");
		conn.setRequestProperty("Pragma", "no-cache");
		conn.setRequestProperty("Cache-Control", "no-cache");
		conn.setRequestProperty("Upgrade-Insecure-Requests", "1");
//			conn.setRequestProperty("User-Agent","Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.77 Safari/537.36");
		conn.setRequestProperty("User-Agent", USER_AGENT);
		conn.setRequestProperty("Accept",
				"text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
		conn.setRequestProperty("Accept-Language", "en-US,en;q=0.9,it;q=0.8");
		if (cookies != null) {
			for (String cookie : this.cookies) {
				conn.addRequestProperty("Cookie", cookie.split(";", 1)[0]);
			}
		}
		int responseCode = conn.getResponseCode();
		System.out.println("\nSending 'GET' request to URL : " + url);
		System.out.println("Response Code : " + responseCode);
		setCookies(conn.getHeaderFields().get("Set-Cookie"));
		return conn.getInputStream();


	}

	private String sendPost(String url, String postParams) throws Exception {

		URL obj = new URL(url);
		conn = (HttpsURLConnection) obj.openConnection();

		// Acts like a browser
		conn.setUseCaches(false);
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Host", "cloud.thewindowoutfitters.com");
		conn.setRequestProperty("Connection", "keep-alive");
		conn.setRequestProperty("Content-Length", "69");
		conn.setRequestProperty("Pragma", "no-cache");
		conn.setRequestProperty("Cache-Control", "no-cache");
		conn.setRequestProperty("Origin", "https://cloud.thewindowoutfitters.com");
		conn.setRequestProperty("Upgrade-Insecure-Requests", "1");
		conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		conn.setRequestProperty("User-Agent",
				"Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.77 Safari/537.36");
		conn.setRequestProperty("Accept",
				"text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
		conn.setRequestProperty("Referer", "https://cloud.thewindowoutfitters.com/");
		conn.setRequestProperty("Accept-Encoding", "gzip, deflate, br");
		conn.setRequestProperty("Accept-Language", "en-US,en;q=0.9,it;q=0.8");
		if (cookies != null) {
			for (String cookie : this.cookies) {
				conn.addRequestProperty("Cookie", cookie.split(";", 1)[0]);
			}
		}

		conn.setDoOutput(true);
		conn.setDoInput(true);

		// Send post request
		DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
		wr.writeBytes(postParams);
		wr.flush();
		wr.close();

		int responseCode = conn.getResponseCode();
		System.out.println("\nSending 'POST' request to URL : " + url);
		System.out.println("Post parameters : " + postParams);
		System.out.println("Response Code : " + responseCode);

		BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
		System.out.println(response.toString());
		setCookies(conn.getHeaderFields().get("Set-Cookie"));
		return response.toString();
	}

	public List<String> getCookies() {
		return cookies;
	}

	public void setCookies(List<String> cookies) {
		this.cookies = cookies;
	}

}
