package wael.bendhia.dao;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

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
	
	public Band getBand(){
		return new Band(1, "Frank Zappa", "empty", null, null);
	}
	
	public Band getScruff(){
		Element content = doc.getElementsByTag("center").get(0).getElementsByTag("font").get(0);;
		return new Band(2, content.text(), "", null, null);
	}
}
