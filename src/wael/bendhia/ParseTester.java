package wael.bendhia;

import wael.bendhia.utils.AlbumSearchRequest;
import wael.bendhia.utils.ScaruffiDB;

public class ParseTester {
	public static void main(String[] args){
		ScaruffiDB.getInstance().searchAlbums(new AlbumSearchRequest("Trout", 0, 2020, 0, 10, true)).forEach(a -> System.out.println(a.toString()));
	}
}
