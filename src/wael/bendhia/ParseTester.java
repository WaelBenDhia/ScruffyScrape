package wael.bendhia;


import java.util.Map;
import java.util.Set;

import wael.bendhia.dao.BandDao;
import wael.bendhia.entities.Album;
import wael.bendhia.entities.Band;
import wael.bendhia.utils.ScaruffiDB;

public class ParseTester {
	public static void main(String[] args){
		ScaruffiDB.getInstance().getMostInfluentialBands().forEach(band -> System.out.println(band.getName()));
	}
}
