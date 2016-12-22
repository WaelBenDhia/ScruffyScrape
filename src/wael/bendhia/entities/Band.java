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
		setUrl(url);
		setName(name);
		setBio(bio);
		setAlbums(albums);
	}
	
	public String getName() {
		return name;
	}
	@XmlElement
	public void setName(String name) {
		if(name.isEmpty())
			this.name = url.substring(url.indexOf('/'), url.indexOf('.'));
		else
		   this.name = name;
	}
	
	public String getUrl() {
		return url;
	}

	@XmlElement
	public String getFullUrl(){
		return "http://scaruffi.com/" + url;
	}
	
	@XmlElement
	public void setUrl(String url) {
		if(url.substring(0,3).equals("../"))
		   this.url = url.substring(3, url.length());
		else
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
