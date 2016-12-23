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
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;

import wael.bendhia.entities.Album;
import wael.bendhia.entities.Band;

public class BandDao {
	private Set<Band> allBands;
	private Set<Band> jazzBands;
	private Set<Band> rockBands;
	
	public static String htmlToString(String html) {
	    if(html==null)
	        return html;
	    Document document = Jsoup.parse(html);
	    document.outputSettings(new Document.OutputSettings().prettyPrint(false));
	    document.select("br").append("\\n");
	    document.select("p").prepend("\\n\\n");
	    String s = document.html().replaceAll("\\\\n", "\n");
	    return Jsoup.clean(s, "", Whitelist.none(), new Document.OutputSettings().prettyPrint(false));
	}
	
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
							null, null, null));
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
							null, null, null));
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
				//Parse name
				band.setName(doc.getElementsByTag("center").get(0).getElementsByTag("font").get(0).text());
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
				
				String text = htmlToString(albumTd.outerHtml());
				Pattern albumPattern = Pattern.compile(".+, ([0-9]*.[0-9]+|[0-9]+)/10");
				Matcher matcher = albumPattern.matcher(text);
				List<String> albumStrings = new ArrayList<>();
				while(matcher.find()){
					albumStrings.add(matcher.group().trim());
				}
				
				Pattern albumNamePattern = Pattern.compile("(^.+)(?=[(].*[)])|(^.+)(?=,)");
				Pattern yearPattern = Pattern.compile("[0-9]{4}");
				Pattern ratingPattern = Pattern.compile("([0-9]\\.[0-9]|[0-9])(?=/10)");
				band.setAlbums(new ArrayList<>());
				for(String albumString : albumStrings){
					
					Matcher nameMatcher = albumNamePattern.matcher(albumString); 
					Matcher yearMatcher = yearPattern.matcher(albumString);
					Matcher ratingMatcher = ratingPattern.matcher(albumString);
					
					String name = nameMatcher.find() ? nameMatcher.group().trim() : "";
					int year = yearMatcher.find() ? Integer.parseInt(yearMatcher.group()) : 0;
					float rating = ratingMatcher.find() ? Float.parseFloat(ratingMatcher.group()) : 0;
					
					band.getAlbums().add(new Album(name, year, rating));
				}
				//Parse related bands
				Elements relatedBandElements = doc.getElementsByTag("table").get(1).getElementsByAttribute("bgcolor").get(0).getElementsByTag("a");
				relatedBandElements.addAll(doc.getElementsByTag("table").get(1).getElementsByAttribute("bgcolor").get(1).getElementsByTag("a"));
				Set<Band> relatedBands = new TreeSet<>();
				for(Element relatedBandElement : relatedBandElements){
					String name = relatedBandElement.text();
					String partialUrl =  relatedBandElement.attr("href");
					if(!partialUrl.contains("vol")){
						int volume = band.getUrl().charAt(3) - '0';
						partialUrl = "vol" + volume + '/' + partialUrl;
					}
					if(!partialUrl.isEmpty() && !name.equals("contact me") && !name.equals("contattami") && !name.isEmpty()){
						relatedBands.add(new Band(name, partialUrl, null, null, null));
					}
				}
				band.setRelatedBands(relatedBands);
			}catch (IOException e) {
				band = null;
				e.printStackTrace();
			}
		}
		return band;
	}
}
