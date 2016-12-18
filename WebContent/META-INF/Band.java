package wael.bendhia;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "band")
public class Band implements Comparable<Band>{
	private int volume;
	private String name;
	private String url;
	private String bio;
	private List<Album> albums;

	public Band(){}
	   
	public Band(int volume, String name, String url){
		this.volume = volume;
		this.name = name;
		this.url = url;
	}

	public int getVolume() {
		return volume;
	}

	@XmlElement
	public void setVolume(int volume) {
		this.volume = volume;
	}
	
	public String getName() {
		return name;
	}
	@XmlElement
	public void setName(String name) {
		   this.name = name;
	}
	
	public String getUrl() {
		return url;
	}
	
	@XmlElement
	public void setUrl(String url) {
		   this.url = url;
	}
	
	@Override
	public String toString(){
		return "Name: " + name + " Url: " + url + " Volume: " + volume;
	}

	@Override
	public int compareTo(Band o) {
		return name.toLowerCase().compareTo(o.getName().toLowerCase());
	}
}
