package wael.bendhia;


import java.util.Set;

import wael.bendhia.dao.BandDao;
import wael.bendhia.entities.Album;
import wael.bendhia.entities.Band;
import wael.bendhia.utils.ScaruffiDB;

public class ParseTester {
	public static void main(String[] args){
		BandDao bandDao = new BandDao();
		long startTime = System.currentTimeMillis();
		Set<Band> allBands = bandDao.getAllBands();
		long endTime = System.currentTimeMillis();
		System.out.println((endTime-startTime) + "ms to parse and lazy insert " + allBands.size() + " bands into db");
		startTime = System.currentTimeMillis();
		for(Band band : allBands)
			bandDao.getBand(band);
		endTime = System.currentTimeMillis();
		System.out.println((endTime-startTime) + "ms to parse and full insert " + allBands.size() + " bands into db");
	}
}
