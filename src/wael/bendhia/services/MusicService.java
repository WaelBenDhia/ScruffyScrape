package wael.bendhia.services;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import wael.bendhia.dao.BandDao;
import wael.bendhia.entities.Album;
import wael.bendhia.entities.Band;
import wael.bendhia.utils.AlbumSearchRequest;
import wael.bendhia.utils.BandSearchRequest;

@Path("/MusicService")
public class MusicService {

   BandDao bandDao = new BandDao();

   @GET
   @Path("/bands")
   @Produces(MediaType.APPLICATION_JSON)
   public Set<Band> getBands(){
      return bandDao.getAllBands();
   }
   
   @GET
   @Path("/bands/rock")
   @Produces(MediaType.APPLICATION_JSON)
   public Set<Band> getRockBands(){
      return bandDao.getRockBands();
   }
   
   @GET
   @Path("/bands/jazz")
   @Produces(MediaType.APPLICATION_JSON)
   public Set<Band> getJazzBands(){
      return bandDao.getJazzBands();
   }
   
   @GET
   @Path("/band/{volume}/{url}")
   @Produces(MediaType.APPLICATION_JSON)
   public Band getBandFull(@PathParam("volume") String volume, @PathParam("url") String url){
	   return bandDao.getBand(new Band("", volume+"/"+url+".html", "", null, null));
   }
   
   @GET
   @Path("/ratings/distribution")
   @Produces(MediaType.APPLICATION_JSON)
   public Map<Float, Integer> getScoreDistribution(){
	   return bandDao.getScoreDistribution();
   }
   
   @GET
   @Path("/bands/total")
   @Produces(MediaType.APPLICATION_JSON)
   public int getBandCount(){
	   return bandDao.getBandCount();
   }
   
   @GET
   @Path("/bands/influential")
   @Produces(MediaType.APPLICATION_JSON)
   public List<Band> getBandsInfluential(){
	   return bandDao.getMostInfluentialBands();
   }
   
   @POST
   @Path("/albums/search")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public List<Album> searchAlbums(AlbumSearchRequest req){
	   return bandDao.searchAlbums(req);
   }
   
   @POST
   @Path("/albums/searchCount")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public int searchAlbumsCount(AlbumSearchRequest req){
	   return bandDao.searchAlbumsCount(req);
   }
   
   @POST
   @Path("/bands/search")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public Set<Band> searchBands(BandSearchRequest req){
	   return bandDao.searchBands(req);
   }
   
   @POST
   @Path("/bands/searchCount")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public int searchBandsCount(BandSearchRequest req){
	   return bandDao.searchBandsCount(req);
   }
}