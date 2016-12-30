package wael.bendhia.dao;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;

import wael.bendhia.entities.Album;
import wael.bendhia.entities.Band;
import wael.bendhia.utils.AlbumSearchRequest;
import wael.bendhia.utils.BandSearchRequest;
import wael.bendhia.utils.ScaruffiDB;

public class BandDao {
	private ScaruffiDB sdb;
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
		sdb = ScaruffiDB.getInstance();
		allBands = new TreeSet<>();
		jazzBands = new TreeSet<>();
		rockBands = new TreeSet<>();
	}
	
	public void clearAndUpdateTables(){
		sdb.clearTables();
		for(Band band : getAllBands()){
			getBand(band);
		}
		getBestAlbumsAllTimeDates();
		getBestAlbums60sDates();
		getBestAlbums70sDates();
		getBestAlbums80sDates();
		getBestAlbums90sDates();
		getBestAlbums00sDates();
		getBestAlbums10sDates();
	}
	
	public Set<Band> getAllBands(){
		allBands = sdb.getAll();
		if(allBands.isEmpty()){
			allBands.addAll(getRockBands());
			allBands.addAll(getJazzBands());
			for(Band band : allBands){
				sdb.insertOrUpdateFull(band);
				System.out.println("Inserting into db: " + band.getName());
			}
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
		Band retBand = null;
		if(band.getBio() == null || band.getAlbums() == null){
			retBand = sdb.getBandFull(band.getUrl());
			if(
					retBand == null ||
					retBand.getBio() == null ||
					retBand.getBio().isEmpty() ||
					retBand.getAlbums() == null ||
					retBand.getAlbums().isEmpty()){
				String url = band.getFullUrl();
				try{
					Document doc = Jsoup.connect(url).get();
					retBand = new Band();
					retBand.setName(band.getName());
					retBand.setUrl(band.getUrl());
					//Parse name
					try{
						String name = doc.getElementsByTag("center").get(0).getElementsByTag("font").get(0).text();
						if(!name.contains("Scaruffi"))
							retBand.setName(name);
					}catch (Exception e) {}
					//Parse Bio
					try{
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
						retBand.setBio(fullBio);
					}catch (Exception e) {}
					//Parse albums
					try{
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
						Pattern yearPattern = Pattern.compile("(?<=[(])[0-9]{4}(?=[)])");
						Pattern ratingPattern = Pattern.compile("([0-9]\\.[0-9]|[0-9])(?=/10)");
						List<Album> albums = new ArrayList<>();
						for(String albumString : albumStrings){
							
							Matcher nameMatcher = albumNamePattern.matcher(albumString); 
							Matcher yearMatcher = yearPattern.matcher(albumString);
							Matcher ratingMatcher = ratingPattern.matcher(albumString);
							
							String name = nameMatcher.find() ? nameMatcher.group().trim() : "";
							int year = yearMatcher.find() ? Integer.parseInt(yearMatcher.group()) : 0;
							float rating = ratingMatcher.find() ? Float.parseFloat(ratingMatcher.group()) : 0;
							
							albums.add(new Album(name, year, rating, null));
						}
						retBand.setAlbums(albums);
					}catch (Exception e) {}
					//Parse related bands
					try{
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
							if(
									!partialUrl.isEmpty() &&
									!partialUrl.contains("mail") &&
									!partialUrl.contains("http") &&
									!partialUrl.contains("history") &&
									!partialUrl.contains("oldavant") &&
									!name.isEmpty() &&
									!name.contains("contact") &&
									!name.contains("contattami")
									){
								relatedBands.add(new Band(name, partialUrl, null, null, null));
							}
						}
						retBand.setRelatedBands(relatedBands);
					}catch (Exception e) {}
					System.out.println("Full insert of " + retBand.getName());
					sdb.insertOrUpdateFull(retBand);
				}catch (IOException e) {
					retBand = null;
					e.printStackTrace();
				}
			}
		}
		return retBand;
	}
	
	public Set<Band> getAllBandsVolume(int volume){
		Set<Band> bands = new TreeSet<>();
		String url = "http://www.scaruffi.com/vol" + volume + "/";
		try {
			Document doc = Jsoup.connect(url).get();
			Elements selects = doc.getElementsByTag("select");
			for(Element select : selects){
				Elements optionsTemp = select.getElementsByTag("option");
				for(int i = 1; i < optionsTemp.size(); i++)
						bands.add(new Band(optionsTemp.get(i).text(), "vol"+volume+"/"+optionsTemp.get(i).attr("value"), null, null, null));
				}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bands;
	}

	public int getBandCount(){
		return sdb.getBandCount();
	}
	
	public List<Band> getMostInfluentialBands(){
		return sdb.getMostInfluentialBands();
	}
	
	public Map<Float, Integer> getScoreDistribution(){
		return sdb.getScoreDistribution();
	}
	
	public void getBestAlbumsAllTimeDates(){
		String url = "http://scaruffi.com/music/picbest.html";
		try {
			Document doc = Jsoup.connect(url).get();
			
			Pattern yearPattern = Pattern.compile("(?<=(, ))[0-9]{4}");
			Pattern bandNamePattern = Pattern.compile("(?<=([0-9]*[)] )).*(?=:)");
			Pattern albumNamePattern = Pattern.compile("(?<=(: )).*");
			
			Elements linerElements = doc.getElementsByTag("center").get(0).getElementsByTag("table").get(0).getElementsByTag("tr");
			
			List<Album> albums = new ArrayList<>();
			
			for(Element linerElement : linerElements){
				
				String bandAlbumName = linerElement.getElementsByTag("td").get(0).getElementsByTag("font").get(0).text();
				String linerNotes = linerElement.getElementsByTag("td").get(1).text();
				
				int year = 0;
				String albumName = "";
				String bandName = "";
				
				Matcher yearMatcher = yearPattern.matcher(linerNotes);
				Matcher albumMatcher = albumNamePattern.matcher(bandAlbumName);
				Matcher bandMatcher = bandNamePattern.matcher(bandAlbumName);
				
				
				if(yearMatcher.find())
					year = Integer.parseInt(yearMatcher.group(0));
				
				
				if(albumMatcher.find())
					albumName = albumMatcher.group(0);
				
				
				if(bandMatcher.find())
					bandName = bandMatcher.group(0);
				
				albums.add(new Album(albumName, year, 0, new Band(bandName, null, null, null, null)));
				
			}
			
			for(Album album : albums){
				System.out.println("Rows changed for " + album.getBand().getName() + "-" + album.getName() + " :" + sdb.updateAlbumDate(album));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void getBestAlbums60sDates(){
		String url = "http://scaruffi.com/ratings/60.html";
		try {
			Document doc = Jsoup.connect(url).get();
			
			Elements albumElements = doc
					.getElementsByTag("center").get(0)
					.getElementsByTag("table").get(2)
					.getElementsByTag("tbody").get(0)
					.getElementsByTag("tr").get(0)
					.getElementsByTag("td").get(0)
					.getElementsByTag("ul").get(0)
					.getElementsByTag("li");
			
			for(Album album : getAbumsFromTopListElements(albumElements)){
				System.out.println("Rows changed for " + album.getBand().getName() + "-" + album.getName() + " :" + sdb.updateAlbumDate(album));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void getBestAlbums70sDates(){
		String url = "http://scaruffi.com/ratings/70.html";
		try {
			Document doc = Jsoup.connect(url).get();
			
			Elements elements = doc
					.getElementsByTag("center").get(0)
					.getElementsByTag("table").get(2)
					.getElementsByTag("tbody").get(0)
					.getElementsByTag("tr").get(0)
					.getElementsByTag("td").get(0)
					.getElementsByTag("ul");
			
			Elements albumElements = new Elements();
			for(Element element : elements){
				albumElements.addAll(element.getElementsByTag("li"));
			}

			for(Album album : getAbumsFromTopListElements(albumElements)){
				System.out.println("Rows changed for " + album.getBand().getName() + "-" + album.getName() + " :" + sdb.updateAlbumDate(album));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void getBestAlbums80sDates(){
		String url = "http://scaruffi.com/ratings/80.html";
		try {
			Document doc = Jsoup.connect(url).get();
			
			Elements elements = doc
					.getElementsByTag("center").get(0)
					.getElementsByTag("table").get(2)
					.getElementsByTag("tbody").get(0)
					.getElementsByTag("tr").get(0)
					.getElementsByTag("td").get(0)
					.getElementsByTag("ul");
			
			Elements albumElements = new Elements();
			for(Element element : elements){
				albumElements.addAll(element.getElementsByTag("li"));
			}
			
			for(Album album : getAbumsFromTopListElements(albumElements)){
				System.out.println("Rows changed for " + album.getBand().getName() + "-" + album.getName() + " :" + sdb.updateAlbumDate(album));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void getBestAlbums90sDates(){
		String url = "http://scaruffi.com/ratings/90.html";
		try {
			Document doc = Jsoup.connect(url).get();
			
			Elements elements = doc
					.getElementsByTag("center").get(0)
					.getElementsByTag("table").get(2)
					.getElementsByTag("tbody").get(0)
					.getElementsByTag("tr").get(0)
					.getElementsByTag("td").get(0)
					.getElementsByTag("ul");
			
			Elements albumElements = new Elements();
			for(Element element : elements){
				albumElements.addAll(element.getElementsByTag("li"));
			}
			
			for(Album album : getAbumsFromTopListElements(albumElements)){
				System.out.println("Rows changed for " + album.getBand().getName() + "-" + album.getName() + " :" + sdb.updateAlbumDate(album));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void getBestAlbums00sDates(){
		String url = "http://scaruffi.com/ratings/00.html";
		try {
			Document doc = Jsoup.connect(url).get();
			
			Elements elements = doc
					.getElementsByTag("center").get(0)
					.getElementsByTag("table").get(3)
					.getElementsByTag("tbody").get(0)
					.getElementsByTag("tr").get(0)
					.getElementsByTag("td").get(0)
					.getElementsByTag("ul");
			
			Elements albumElements = new Elements();
			for(Element element : elements){
				albumElements.addAll(element.getElementsByTag("li"));
			}
			
			for(Album album : getAbumsFromTopListElements(albumElements)){
				System.out.println("Rows changed for " + album.getBand().getName() + "-" + album.getName() + " :" + sdb.updateAlbumDate(album));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void getBestAlbums10sDates(){
		String url = "http://scaruffi.com/ratings/10.html";
		try {
			Document doc = Jsoup.connect(url).get();
			
			Elements elements = doc
					.getElementsByTag("center").get(0)
					.getElementsByTag("table").get(3)
					.getElementsByTag("tbody").get(0)
					.getElementsByTag("tr").get(0)
					.getElementsByTag("td").get(0)
					.getElementsByTag("ul");
			
			Elements albumElements = new Elements();
			for(Element element : elements){
				albumElements.addAll(element.getElementsByTag("li"));
			}
			
			for(Album album : getAbumsFromTopListElements(albumElements)){
				System.out.println("Rows changed for " + album.getBand().getName() + "-" + album.getName() + " :" + sdb.updateAlbumDate(album));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private List<Album> getAbumsFromTopListElements(Elements albumElements){
		List<Album> albums = new ArrayList<>();
		
		Pattern yearPattern = Pattern.compile("(?<=[(])[0-9]{4}(?=[)])");
		Pattern bandNamePattern = Pattern.compile(".*(?=:)");
		Pattern albumNamePattern = Pattern.compile("(?<=(: )).*(?=[(])");
		
		for(Element albumElement : albumElements){
			
			String bandAlbumName = albumElement.text();
			
			int year = 0;
			String albumName = "";
			String bandName = "";
			
			Matcher yearMatcher = yearPattern.matcher(bandAlbumName);
			Matcher albumMatcher = albumNamePattern.matcher(bandAlbumName);
			Matcher bandMatcher = bandNamePattern.matcher(bandAlbumName);
			
			
			if(yearMatcher.find())
				year = Integer.parseInt(yearMatcher.group(0));
			
			
			if(albumMatcher.find())
				albumName = albumMatcher.group(0);
			
			
			if(bandMatcher.find())
				bandName = bandMatcher.group(0);
			
			albums.add(new Album(albumName, year, 0, new Band(bandName, null, null, null, null)));
			
		}
		
		return albums;
	}
	
	public List<Album> searchAlbums(AlbumSearchRequest req){
		return sdb.searchAlbums(req);
	}
	
	public int searchAlbumsCount(AlbumSearchRequest req){
		return sdb.searchAlbumsCount(req);
	}
	
	public Set<Band> searchBands(BandSearchRequest req){
		return sdb.SearchBands(req);
	}
	
	public int searchBandsCount(BandSearchRequest req){
		return sdb.searchBandsCount(req);
	}
}
