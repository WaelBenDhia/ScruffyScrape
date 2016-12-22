package wael.bendhia.dao;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import wael.bendhia.entities.Album;
import wael.bendhia.entities.Band;

public class BandDao {
	private Set<Band> allBands;
	private Set<Band> jazzBands;
	private Set<Band> rockBands;
	
	public BandDao(){
		allBands = new TreeSet<>();
		jazzBands = new TreeSet<>();
		rockBands = new TreeSet<>();
	}
	
	public Set<Band> getAllBands(){
		if(allBands.isEmpty()){
			allBands.addAll(getRockBands());
			allBands.addAll(getJazzBands());
		}
		return allBands;
	}
	
	public Set<Band> getRockBands(){
		if(rockBands.isEmpty()){
			String url = "http://scaruffi.com/music/groups.html";
			try {
				Document doc = Jsoup.connect(url).get();
				Elements bandElements = doc.getElementsByTag("table").get(2).getElementsByTag("a");
				for(Element bandElement : bandElements){
					rockBands.add(new Band(
							bandElement.text(),
							bandElement.attr("href"),
							null,
							null));
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return rockBands;
	}
	
	public Set<Band> getJazzBands(){
		if(jazzBands.isEmpty()){
			String url = "http://scaruffi.com/jazz/musician.html";
			try {
				Document doc = Jsoup.connect(url).get();
				Elements bandElements = doc.getElementsByAttributeValue("width", "400").get(0).getElementsByTag("a");
				for(Element bandElement : bandElements){
					jazzBands.add(new Band(
							bandElement.text(),
							bandElement.attr("href"),
							null,
							null));
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return jazzBands;
	}
	
	public Band getBand(Band band){
		if(band.getBio() == null || band.getAlbums() == null){
			String url = band.getFullUrl();
			try{
				Document doc = Jsoup.connect(url).get();
				//Parse Bio
				List<Node> bioTd = doc.getElementsByTag("table").get(1)
						.getElementsByAttribute("bgcolor").get(0).childNodes();
				String fullBio = "";
				for(Node node : bioTd){
					if(node instanceof TextNode){
						fullBio += ((TextNode) node).getWholeText();
					}else{
						fullBio += ((Element)node).text();
					}
				}
				band.setBio(fullBio);
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
				if(albumNames.isEmpty()){
					albumNames = albumTd.getElementsByTag("b");
				}
				band.setAlbums(new ArrayList<>());
				for(int i = 0; i < albumNames.size(); i++){
					Element albumName = albumNames.get(i);
					String name = "dicks";
					try{
						name = albumName.getElementsByTag("b").get(0).text();
					}catch(IndexOutOfBoundsException e){
						name = albumName.text();
					}
					int year = 0;
					float rating = 0;
					try{
						rating = ratings.get(i);
						year = Integer.parseInt(albumName.text().substring(Math.min(name.length() + 2, albumName.text().length()), albumName.text().length()-1));
					}catch (Exception e) {
					}
					band.getAlbums().add(new Album(name, year, rating));
				}
			}catch (IOException e) {
				band = null;
				e.printStackTrace();
			}
		}
		return band;
	}
}
