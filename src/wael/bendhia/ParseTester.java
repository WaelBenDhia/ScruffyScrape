package wael.bendhia;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import wael.bendhia.dao.BandDao;
import wael.bendhia.entities.Album;
import wael.bendhia.entities.Band;

public class ParseTester {
	public static void main(String[] args){
		BandDao bandDao = new BandDao();
		/*Band huskerDu = new Band(4, "Husker Du", "huskerdu.html", null, null, null);
		Band frankZappa = new Band(1, "Frank Zappa", "zappa.html", null, null, null);
		bandDao.getBand(huskerDu);
		System.out.println(huskerDu.getName() + ":\n" + huskerDu.getBio());
		for(Album album : huskerDu.getAlbums())
			System.out.println(album.toString());
		bandDao.getBand(frankZappa);
		System.out.println(frankZappa.getName() + ":\n" + frankZappa.getBio());
		for(Album album : frankZappa.getAlbums())
			System.out.println(album.toString());
		*/
		for(Band band : bandDao.getJazzBands())
			System.out.println(band.toString());
	}
}
