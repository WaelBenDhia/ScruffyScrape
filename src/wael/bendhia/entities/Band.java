package wael.bendhia.entities;

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
	   
	public Band(int volume, String name, String url, String bio, List<Album> albums){
		this.volume = volume;
		this.name = name;
		this.url = url;
		this.bio = bio;
		this.albums = albums;
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
	
	public String getFullUrl(){
		String start = url.substring(0, Math.min(3, url.length()));
		if(start.equals("../")){
			return "http://scaruffi.com/" + url.substring(3, url.length());
		}else{
			return "http://scaruffi.com/vol" + volume + "/" + url;
		}
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
		return "Name: " + name + " Url: " + url + " Volume: " + volume + " Full url: " + getFullUrl() + " Biography: " + bio;
	}

	@Override
	public int compareTo(Band o) {
		return name.toLowerCase().compareTo(o.getName().toLowerCase());
	}
}
