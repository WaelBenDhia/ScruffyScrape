package wael.bendhia;

import wael.bendhia.dao.BandDao;
import wael.bendhia.entities.Band;
import wael.bendhia.utils.AlbumSearchRequest;
import wael.bendhia.utils.BandSearchRequest;
import wael.bendhia.utils.ScaruffiDB;

public class ParseTester {
	public static void main(String[] args){
		//ScaruffiDB.getInstance().searchAlbums(new AlbumSearchRequest("", 0, 2020, 0, 10, true, 1, 40)).forEach(a -> System.out.println(a.toString()));
		BandDao bandDao = new BandDao();
		bandDao.searchAlbums(new AlbumSearchRequest("beef", 1950, 2016, 0, 10, true, 0, 50)).forEach(album -> System.out.println(album.toString()));;
	}
}
