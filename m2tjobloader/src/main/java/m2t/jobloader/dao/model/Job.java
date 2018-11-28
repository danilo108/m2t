package m2t.jobloader.dao.model;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Entity
public class Job {

	
	@Id
	@GeneratedValue
	private Long id;
	
	@Column(name="JOB_CODE")
	private String jobCode;
	@ManyToOne(cascade=CascadeType.DETACH, fetch=FetchType.EAGER, optional=false)
	@JoinColumn(name="ORIGINAL_CLIENT", nullable=false)
	private Client originalClient;
	@OneToOne(cascade=CascadeType.DETACH, fetch=FetchType.EAGER, optional=true)
	@ManyToOne(cascade=CascadeType.DETACH, fetch=FetchType.EAGER, optional=true)
	private Client deliverTo;
	@Column(name="JOB_CLIENT")
	private String jobClient;
	@Column(name="DELIVERY_ADDRESS")
	private String deliveryAddress;
	@Column(name="ORIGINAL_DELIVERY_ADDRESS")
	private String originalDeliveryAddress;
	
	@Column(name="DELIVERY_TO_CODE")
	private String deliverToCode;
	
	@Column(name="TOT_BOXES")
	private int totalBoxes;
	@Column(name="TOT_PANELS")
	private int totalPanels;
	@Column(name="TOT_HARDWARE")
	private int totalHardware;
	@Column(name="TOT_FRAMES")
	private int totalFrames;
	@Column(name="NOTES")
	private String notes;
	@Column(name="TOT_BLINDS")
	private Integer totalBlinds;
	
	@Column(name="CONTAINER")
	private String container;
	
	@Column(name="SIZE")
	private String size;
	
	@Column(name="SIZE_SQM")
	private float sizeSQM;
	
	@Column(name="ARRIVED_ON")
	private Date arrived;
	
	public Job() {
		totalBlinds = new Integer(0);
	}
	

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	

	public Integer getTotalBlinds() {
		return totalBlinds;
	}

	public void setTotalBlinds(Integer totalBlinds) {
		this.totalBlinds = totalBlinds;
	}

	public String getDeliverToCode() {
		return deliverToCode;
	}

	public void setDeliverToCode(String deliverToCode) {
		this.deliverToCode = deliverToCode;
	}

	public String getJobClient() {
		return jobClient;
	}

	public void setJobClient(String jobClient) {
		this.jobClient = jobClient;
	}

	public String getDeliveryAddress() {
		return deliveryAddress;
	}

	public void setDeliveryAddress(String deliveryAddress) {
		this.deliveryAddress = deliveryAddress;
	}

	public String getJobCode() {
		return jobCode;
	}

	public void setJobCode(String jobCode) {
		this.jobCode = jobCode;
	}

	public Client getOriginalClient() {
		return originalClient;
	}

	public void setOriginalClient(Client originalClient) {
		this.originalClient = originalClient;
	}

	public Client getDeliverTo() {
		return deliverTo;
	}

	public void setDeliverTo(Client deliverTo) {
		this.deliverTo = deliverTo;
	}

	public int getTotalBoxes() {
		return totalBoxes;
	}

	public void setTotalBoxes(int totalBoxes) {
		this.totalBoxes = totalBoxes;
	}

	public int getTotalPanels() {
		return totalPanels;
	}

	public void setTotalPanels(int totalPanels) {
		this.totalPanels = totalPanels;
	}

	public int getTotalHardware() {
		return totalHardware;
	}

	public void setTotalHardware(int totalHardware) {
		this.totalHardware = totalHardware;
	}

	public int getTotalFrames() {
		return totalFrames;
	}

	public void setTotalFrames(int totalFrames) {
		this.totalFrames = totalFrames;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public String getContainer() {
		return container;
	}

	public void setContainer(String container) {
		this.container = container;
	}

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public Date getArrived() {
		return arrived;
	}

	public void setArrived(Date arrived) {
		this.arrived = arrived;
	}
	
	
	
	public float getSizeSQM() {
		return sizeSQM;
	}


	public void setSizeSQM(float sizeSQM) {
		this.sizeSQM = sizeSQM;
	}


	public String getOriginalDeliveryAddress() {
		return originalDeliveryAddress;
	}

	public void setOriginalDeliveryAddress(String originalDeliveryAddress) {
		this.originalDeliveryAddress = originalDeliveryAddress;
	}

	@Override
	public String toString() {
		ObjectMapper mapper = new ObjectMapper();
		try {
			return mapper.writeValueAsString(this);
		} catch (JsonProcessingException e) {
			return super.toString();
		}
	}
	
	
	
}
