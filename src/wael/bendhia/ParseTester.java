package wael.bendhia;

import wael.bendhia.dao.BandDao;
import wael.bendhia.entities.Band;
import wael.bendhia.utils.AlbumSearchRequest;
import wael.bendhia.utils.ScaruffiDB;

public class ParseTester {
	public static void main(String[] args){
		//ScaruffiDB.getInstance().searchAlbums(new AlbumSearchRequest("", 0, 2020, 0, 10, true, 1, 40)).forEach(a -> System.out.println(a.toString()));
		BandDao bandDao = new BandDao();
		//bandDao.getBestAlbumsAllTimeDates();
		bandDao.getBestAlbums60sDates();
		/*bandDao.getBestAlbums70sDates();
		bandDao.getBestAlbums80sDates();
		bandDao.getBestAlbums90sDates();
		bandDao.getBestAlbums00sDates();
		bandDao.getBestAlbums10sDates();*/
	}
}
