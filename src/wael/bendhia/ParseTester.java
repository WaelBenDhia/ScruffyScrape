package wael.bendhia;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import wael.bendhia.dao.BandDao;
import wael.bendhia.entities.Album;
import wael.bendhia.entities.Band;

public class ParseTester {
	public static void main(String[] args){
		BandDao bandDao = new BandDao();
		/*
		bandDao.getBand(huskerDu);
		System.out.println(huskerDu.getName() + ":\n" + huskerDu.getBio());
		for(Album album : huskerDu.getAlbums())
			System.out.println(album.toString());
		bandDao.getBand(frankZappa);
		System.out.println(frankZappa.getName() + ":\n" + frankZappa.getBio());
		for(Album album : frankZappa.getAlbums())
			System.out.println(album.toString());
		*/
		/*for(Band band : bandDao.getJazzBands())
			System.out.println(band.toString());*/
		//Band anaal = new Band("Anaal Nathrakh", "vol7/anaal.html", null, null, null);
		//Band huskerDu = new Band("Husker Du", "vol4/huskerdu.html", null, null, null);
		Band zappa = new Band("Frank Zappa", "vol1/zappa.html", null, null, null);
		//bandDao.getBand(anaal);
		//bandDao.getBand(huskerDu);
		bandDao.getBand(zappa);
		for(Band band : zappa.getRelatedBands()){
			System.out.println(band.toString());
		}
	}
}
