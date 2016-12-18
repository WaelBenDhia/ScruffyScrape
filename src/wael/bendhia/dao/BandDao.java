package wael.bendhia.dao;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import wael.bendhia.entities.Album;
import wael.bendhia.entities.Band;

public class BandDao {
	private static final String url = "http://scaruffi.com/";
	private Document doc;
	private List<Band> volume1Bands;
	private List<Band> volume2Bands;
	private List<Band> volume3Bands;
	private List<Band> volume4Bands;
	private List<Band> volume5Bands;
	private List<Band> volume6Bands;
	private List<Band> volume7Bands;
	private List<Band> volume8Bands;
	
	public BandDao(){
		volume1Bands = new ArrayList<>();
		volume2Bands = new ArrayList<>();
		volume3Bands = new ArrayList<>();
		volume4Bands = new ArrayList<>();
		volume5Bands = new ArrayList<>();
		volume6Bands = new ArrayList<>();
		volume7Bands = new ArrayList<>();
		volume8Bands = new ArrayList<>();
		try {
			doc = Jsoup.connect(url).get();
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
	
	public List<Band> getAllBands(){
		List<Band> bandList = new ArrayList<Band>();
		for(int i = 1; i < 9; i++){
			bandList.addAll(getAllBandsVolume(i));
		}
		Collections.sort(bandList);
		return bandList;
	}
	
	public List<Band> getAllBandsVolume(int volume){
		List<Band> bands;
		switch(volume){
		case 1:
			bands = volume1Bands;
			break;
		case 2:
			bands = volume2Bands;
			break;
		case 3:
			bands = volume3Bands;
			break;
		case 4:
			bands = volume4Bands;
			break;
		case 5:
			bands = volume5Bands;
			break;
		case 6:
			bands = volume6Bands;
			break;
		case 7:
			bands = volume7Bands;
			break;
		case 8:
			bands = volume8Bands;
			break;
		default:
			bands = volume1Bands;
			break;
		}
		if(bands.isEmpty()){
			String url = "http://www.scaruffi.com/vol" + volume + "/";
			try {
				Document doc = Jsoup.connect(url).get();
				Elements selects = doc.getElementsByTag("select");
				for(Element select : selects){
					Elements optionsTemp = select.getElementsByTag("option");
					for(int i = 1; i < optionsTemp.size(); i++)
						bands.add(new Band(
								volume,
								optionsTemp.get(i).text(), 
								optionsTemp.get(i).attr("value"),
								null,
								null));
				}
				Collections.sort(bands);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return bands;
	}
	
	public Band getBand(Band band){
		if(band.getBio() == null || band.getAlbums() == null){
			String url = band.getFullUrl();
			try{
				Document doc = Jsoup.connect(url).get();
				//Parse Bio
				Element bioTd = doc.getElementsByTag("table").get(1)
						.getElementsByTag("td").get(1)
						.getElementsByAttribute("width").get(0);
				band.setBio(bioTd.text());
				//Parse albums
				Element albumTd = doc.getElementsByTag("table").get(0)
						.getElementsByTag("td").get(0);
				Pattern ratingPattern = Pattern.compile("([0-9]*\\.[0-9]+|[0-9]+)(?=/10)");
				Matcher matcher = ratingPattern.matcher(albumTd.text());
				List<Float> ratings = new ArrayList<>();
				while(matcher.find()){
					ratings.add(Float.parseFloat(matcher.group()));
				}
				Elements albumNames = albumTd.getElementsByTag("a");
				band.setAlbums(new ArrayList<>());
				for(int i = 0; i < albumNames.size(); i++){
					Element albumName = albumNames.get(i);
					String name = albumName.getElementsByTag("b").get(0).text();
					int year = 0;
					float rating = ratings.get(i);
					try{
						year = Integer.parseInt(albumName.text().substring(Math.min(name.length() + 2, albumName.text().length()), albumName.text().length()-1));
					}catch (Exception e) {
					}
					band.getAlbums().add(new Album(name, year, rating));
				}
			}catch (IOException e) {
				e.printStackTrace();
			}
		}
		return band;
	}
	
	public Band getScruff(){
		Element content = doc.getElementsByTag("center").get(0).getElementsByTag("font").get(0);;
		return new Band(2, content.text(), "", null, null);
	}
}
