package wael.bendhia.entities;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "album")
public class Album {
	String name;
	int year;
	float rating;
	
	public Album(){
		
	}
	
	public Album(String name, int year, float rating){
		this.name = name;
		this.year = year;
		this.rating = rating;
	}

	public String getName() {
		return name;
	}
	
	@XmlElement
	public void setName(String name) {
		this.name = name;
	}

	public int getYear() {
		return year;
	}

	@XmlElement
	public void setYear(int year) {
		this.year = year;
	}

	public float getRating() {
		return rating;
	}

	@XmlElement
	public void setRating(float rating) {
		this.rating = rating;
	}
	
	
}
