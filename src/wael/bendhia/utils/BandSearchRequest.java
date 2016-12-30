package wael.bendhia.utils;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "bandSearchRequest")
public class BandSearchRequest {
	private String name;
	private int page;
	private int numberOfResults;
	
	public BandSearchRequest(){}
	
	public BandSearchRequest(String name, int page, int numberOfResults) {
		this.name = name;
		this.page = page;
		this.numberOfResults = numberOfResults;
	}
	
	@XmlElement
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@XmlElement
	public int getPage() {
		return page;
	}

	public void setLimit(int page) {
		this.page = page;
	}

	@XmlElement
	public int getNumberOfResults() {
		return numberOfResults;
	}

	public void setNumberOfResults(int numberOfResults) {
		this.numberOfResults = numberOfResults;
	}

}
