package wael.bendhia.utils;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import wael.bendhia.entities.Album;
import wael.bendhia.entities.Band;

import java.sql.Connection;

public class ScaruffiDB {
	private static ScaruffiDB db;
	private Connection connection;
	
	private ScaruffiDB(){
		try{
			Class.forName("com.mysql.jdbc.Driver");
			connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/scaruffi", "wael", "");
		}catch(SQLException e){
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public static ScaruffiDB getInstance(){
		if(db == null)
			db = new ScaruffiDB();
		return db;
	}
	
	public boolean insertLazy(Band band){
		String query = "insert into bands (partialUrl, name, bio) values (?, ?, ?)";
		try {
			PreparedStatement stmt = connection.prepareStatement(query);
			stmt.setString(1, band.getUrl());
			stmt.setString(2, band.getName());
			stmt.setString(3, band.getBio());
			stmt.execute();
			stmt.close();
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public boolean insertOrUpdateFull(Band band){
		insertLazy(band);
		String query = "update bands set name = ?, bio = ? where partialURl = ?";
		try {
			PreparedStatement stmt = connection.prepareStatement(query);
			stmt.setString(1, band.getName());
			stmt.setString(2, band.getBio());
			stmt.setString(3, band.getUrl());
			stmt.execute();
			stmt.close();
			if(band.getRelatedBands() != null)
				for(Band related : band.getRelatedBands()){
					insertLazy(related);
					insertBand2Band(band, related);
				}
			if(band.getAlbums() != null)
				for(Album album : band.getAlbums())
					insert(band, album);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	public boolean insert(Band band, Album album){
		String query = "insert into albums (name, year, rating, band) values (?, ?, ?, ?)";
		try {
			PreparedStatement stmt = connection.prepareStatement(query);
			stmt.setString(1, album.getName());
			stmt.setInt(2, album.getYear());
			stmt.setFloat(3, album.getRating());
			stmt.setString(4, band.getUrl());
			stmt.execute();
			stmt.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean insertBand2Band(Band band, Band related){
		String query = "insert into bands2bands (urlOfBand, urlOfRelated) values (?, ?)";
		try {
			PreparedStatement stmt = connection.prepareStatement(query);
			stmt.setString(1, band.getUrl());
			stmt.setString(2, related.getUrl());
			stmt.execute();
			stmt.close();
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	public boolean clearTables(){
		String queryAlbums = "delete from albums";
		String queryBands2Bands = "delete from bands2bands";
		String queryBands = "delete from bands";
		try{
			PreparedStatement stmt = connection.prepareStatement(queryAlbums);
			stmt.execute();
			stmt.close();
			stmt = connection.prepareStatement(queryBands2Bands);
			stmt.execute();
			stmt.close();
			stmt = connection.prepareStatement(queryBands);
			stmt.execute();
			stmt.close();
			return true;
		}catch (Exception e) {
			return false;
		}
	}
	
	public Set<Band> getAll(){
		String query = "select * from bands";
		Set<Band> bands = new TreeSet<>();
		try{
			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			while(rs.next()){
				bands.add(new Band(
						rs.getString(2),
						rs.getString(1),
						rs.getString(3),
						null,
						null));
			}
			stmt.close();
		}catch (Exception e) {
		}
		return bands;
	}
	
	public Band getLazy(String partialUrl){
		String query = "select * from bands where partialUrl ='" + partialUrl + "'";
		Band band = null;
		try{
			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			while(rs.next()){
				band = new Band(
						rs.getString(2),
						rs.getString(1),
						rs.getString(3),
						null,
						null);
			}
			stmt.close();
		}catch (Exception e) {
		}
		return band;
	}
	
	public List<Album> getAlbums(Band band){
		List<Album> albums = new ArrayList<>();
		String query = "select * from albums where band ='" + band.getUrl() + "'";
		try{
			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			while(rs.next()){
				albums.add(new Album(
						rs.getString(1),
						rs.getInt(2),
						rs.getFloat(3),
						null
						));
			}
			stmt.close();
		}catch (Exception e) {
		}
		return albums;
	}
	
	public Set<Band> getRelatedBands(Band band){
		Set<Band> relatedBands = new TreeSet<>();
		String query = "select * from bands INNER JOIN bands2bands ON bands.partialUrl = bands2bands.urlOfRelated where bands2bands.urlOfBand ='" + band.getUrl() + "'";
		try{
			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			while(rs.next()){
				relatedBands.add(new Band(
						rs.getString(2),
						rs.getString(1),
						rs.getString(3),
						null,
						null));
			}
			stmt.close();
		}catch (Exception e) {
		}
		return relatedBands;
	}
	
	public Band getBandFull(String partialUrl){
		Band band = getLazy(partialUrl);
		if(band != null){
			band.setAlbums(getAlbums(band));
			band.setRelatedBands(getRelatedBands(band));
		}
		return band;
	}
	
	public Map<Float, Integer> getScoreDistribution(){
		String query = "SELECT floor(scaruffi.albums.rating*2)/2, count(*) FROM scaruffi.albums group by floor(scaruffi.albums.rating*2)/2;";
		Map<Float, Integer> distribution = new HashMap<>();
		try{
			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			while(rs.next()){
				distribution.put(rs.getFloat(1), rs.getInt(2));
			}
			stmt.close();
		}catch (Exception e) {
		}
		return distribution;
	}

	private List<Album> parseAlbumFromDB(ResultSet rs){
		List<Album> albums = new ArrayList<>();
		try{
			while(rs.next()){
				albums.add(new Album(
						rs.getString(1),
						rs.getInt(2),
						rs.getFloat(3),
						new Band(
								rs.getString(4),
								rs.getString(5),
								null,
								null,
								null
								)
						));
			}
		}catch (Exception e) {
		}
		return albums;
	}
	
	public List<Album> getAlbumsByRating(float rating){
		List<Album> albums = new ArrayList<>();
		String query = "SELECT albums.name, albums.year, albums.rating, bands.name, bands.partialUrl FROM albums INNER JOIN bands ON albums.band = bands.partialUrl WHERE albums.rating = " + rating;
		try{
			Statement stmt = connection.createStatement();
			albums = parseAlbumFromDB(stmt.executeQuery(query));
			stmt.close();
		}catch (Exception e) {
		}
		return albums;
	}
	
	public int getBandCount(){
		String query = "select count(*) FROM bands;";
		int total = 0;
		try{
			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			while(rs.next())
				total = rs.getInt(1);
			stmt.close();
		}catch (Exception e) {
		}
		return total;
	}
	
	public List<Band> getMostInfluentialBands(){
		String query = "select count(b2b.urlOfBand) as inf, b.name, b.partialUrl FROM bands b inner join bands2bands b2b on b.partialUrl = b2b.urlOfRelated group by b2b.urlOfRelated order by inf desc limit 20";
		List<Band> bands = new ArrayList<>();
		try{
			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			while(rs.next())
				bands.add(new Band(
						rs.getString(2),
						rs.getString(3),
						"",
						null,
						null));
			stmt.close();
		}catch (Exception e) {
		}
		return bands;
	}
	
	public List<Album> searchAlbums(AlbumSearchRequest req){
		String query = "select a.name, a.year, a.rating, b.name, b.partialUrl from albums a inner join bands b on b.partialUrl = a.band where a.rating between "
					+ req.getRatingLower() + " and " + req.getRatingHigher() + " and "
					+ "(a.year between " + req.getYearLower() + " and " + req.getYearHigher()
					+ (req.isIncludeUnknown() ? " or a.year = 0" : "") + ") " 
					+ (req.getName().isEmpty() ? "" : "and instr(lower(a.name), lower('" + req.getName() + "')) ") 
					+ "order by a.rating desc " 
					+ "limit " + (req.getPage() * req.getNumberOfResults()) + "," + req.getNumberOfResults() + ";";
		List<Album> albums = new ArrayList<>();
		try{
			Statement stmt = connection.createStatement();
			albums = parseAlbumFromDB(stmt.executeQuery(query));
			stmt.close();
		}catch (Exception e) {
			e.printStackTrace();
		}
		return albums;
	}
	
	public int searchAlbumsCount(AlbumSearchRequest req){
		int count = 0;
		String query = "select count(*) from albums a inner join bands b on b.partialUrl = a.band where a.rating between "
					+ req.getRatingLower() + " and " + req.getRatingHigher() + " and "
					+ "(a.year between " + req.getYearLower() + " and " + req.getYearHigher()
					+ (req.isIncludeUnknown() ? " or a.year = 0" : "") + ") " 
					+ (req.getName().isEmpty() ? "" : "and instr(lower(a.name), lower('" + req.getName() + "')) ") 
					+ "order by a.rating desc;";
		try{
			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			while(rs.next())
				count = rs.getInt(1);
			stmt.close();
		}catch (Exception e) {
			e.printStackTrace();
		}
		return count;
	}
	
	public int updateAlbumDate(Album album){
		String query = "update albums a inner join bands b on a.band = b.partialUrl set a.year = ? where a.name = ? and b.name = ? and a.year = 0;";
		int result = -1;
		try {
			PreparedStatement stmt = connection.prepareStatement(query);
			stmt.setInt(1, album.getYear());
			stmt.setString(2, album.getName());
			stmt.setString(3, album.getBand().getName());
			result = stmt.executeUpdate();
			stmt.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
}
