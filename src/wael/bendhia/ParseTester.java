package wael.bendhia;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import wael.bendhia.dao.BandDao;
import wael.bendhia.entities.Album;
import wael.bendhia.entities.Band;

public class ParseTester {
	public static void main(String[] args){
		BandDao bandDao = new BandDao();
		Band aretha = new Band(1, "Frank Zappa", "zappa.html", null, null);
		bandDao.getBand(aretha);
		System.out.println(aretha.getName() + ":\n" + aretha.getBio());
		for(Album album : aretha.getAlbums())
			System.out.println(album.toString());
		/*for(Band band : bandDao.getAllBandsVolume(1)){
			System.out.println(band.toString());
		}*/
	}
}
