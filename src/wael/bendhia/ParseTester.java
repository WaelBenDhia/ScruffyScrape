package wael.bendhia;

import wael.bendhia.dao.BandDao;
import wael.bendhia.entities.Band;

public class ParseTester {
	public static void main(String[] args){
		BandDao bandDao = new BandDao();
		for(Band band : bandDao.getAllBandsVolume(1)){
			System.out.println(band.toString());
		}
	}
}
