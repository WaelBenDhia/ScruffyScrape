package wael.bendhia.entities;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "band")
public class Band implements Comparable<Band>{
	private String name;
	private String url;
	private String bio;
	private List<Album> albums;

	public Band(){}
	   
	public Band(String name, String url, String bio, List<Album> albums){
		this.name = name;
		this.url = url;
		this.bio = bio;
		this.albums = albums;
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
	public String getFullUrl(){
		return "http://scaruffi.com/" + url.substring(3, url.length());
	}
	
	@XmlElement
	public void setUrl(String url) {
		   this.url = url;
	}
	
	public String getBio(){
		return bio;
	}
	
	@XmlElement
	public void setBio(String bio){
		this.bio = bio;
	}
	
	public List<Album> getAlbums(){
		return albums;
	}
	
	@XmlElement
	public void setAlbums(List<Album> albums){
		this.albums = albums;
	}
	
	@Override
	public String toString(){
		return "Name: " + name + " Url: " + url + " Full url: " + getFullUrl() + " Biography: " + bio;
	}

	@Override
	public int compareTo(Band o) {
		return name.toLowerCase().compareTo(o.getName().toLowerCase());
	}
	
	@Override
	public int hashCode(){
		return url.hashCode();
	}
}
