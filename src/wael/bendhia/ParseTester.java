package wael.bendhia;

import java.io.IOException;
import java.util.List;

import wael.bendhia.dao.BandDao;
import wael.bendhia.entities.Album;
import wael.bendhia.utils.AlbumSearchRequest;

public class ParseTester {
	public static void main(String[] args){
		//ScaruffiDB.getInstance().searchAlbums(new AlbumSearchRequest("", 0, 2020, 0, 10, true, 1, 40)).forEach(a -> System.out.println(a.toString()));
		BandDao bandDao = new BandDao();
		bandDao.getMissingAlbumDateFromMusicBrainz();
	}
}
