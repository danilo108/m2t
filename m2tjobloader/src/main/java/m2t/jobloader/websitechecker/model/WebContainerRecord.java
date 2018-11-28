package m2t.jobloader.websitechecker.model;

import m2t.jobloader.dao.model.Container;

public class WebContainerRecord {

	private String ID;
	private String Number;
	private String Supplier;
	private String Country;
	private String Size;
	private String ShippingPort;
	private String CapacitySQM;
	private String OrderCutOff;
	private String LeaveFactory;
	private String ETD;
	private String ETA;
	private String EstimatedDeliveryDate;
	private String BoxesLoaded;
	private String downloadPdfUrl;
	private String DetailsAction;
	private String baseURL;
	private Container container;
	
	public WebContainerRecord(String baseURL) {
		this.baseURL = baseURL;
	}
	public String getID() {
		return ID;
	}
	public void setID(String iD) {
		ID = iD;
	}
	public String getNumber() {
		return Number;
	}
	public void setNumber(String number) {
		Number = number;
	}
	public String getSupplier() {
		return Supplier;
	}
	public void setSupplier(String supplier) {
		Supplier = supplier;
	}


	public String getShippingPort() {
		return ShippingPort;
	}
	public void setShippingPort(String shippingPort) {
		ShippingPort = shippingPort;
	}
	public String getCountry() {
		return Country;
	}
	public void setCountry(String country) {
		Country = country;
	}
	public String getSize() {
		return Size;
	}
	public void setSize(String size) {
		Size = size;
	}
	public String getCapacitySQM() {
		return CapacitySQM;
	}
	public void setCapacitySQM(String capacitySQM) {
		CapacitySQM = capacitySQM;
	}
	public String getOrderCutOff() {
		return OrderCutOff;
	}
	public void setOrderCutOff(String orderCutOff) {
		OrderCutOff = orderCutOff;
	}
	public String getLeaveFactory() {
		return LeaveFactory;
	}
	public void setLeaveFactory(String leaveFactory) {
		LeaveFactory = leaveFactory;
	}
	public String getETD() {
		return ETD;
	}
	public void setETD(String eTD) {
		ETD = eTD;
	}
	public String getETA() {
		return ETA;
	}
	public void setETA(String eTA) {
		ETA = eTA;
	}
	public String getEstimatedDeliveryDate() {
		return EstimatedDeliveryDate;
	}
	public void setEstimatedDeliveryDate(String estimatedDeliveryDate) {
		EstimatedDeliveryDate = estimatedDeliveryDate;
	}
	public String getBoxesLoaded() {
		return BoxesLoaded;
	}
	public void setBoxesLoaded(String boxesLoaded) {
		BoxesLoaded = boxesLoaded;
	}
	public String getDownloadPdfUrl() {
		return downloadPdfUrl;
	}
	public void setDownloadPdfUrl(String downloadPdfUrl) {
		this.downloadPdfUrl = downloadPdfUrl;
	}
	public String getDetailsAction() {
		return DetailsAction;
	}
	public void setDetailsAction(String detailsAction) {
		DetailsAction = detailsAction;
	}	
	
	public String getFullDownloadPDFURL() {
		return baseURL + downloadPdfUrl;
	}
	public Container getContainer() {
		return container;
	}
	public void setContainer(Container container) {
		this.container = container;
	}
	
	
}
