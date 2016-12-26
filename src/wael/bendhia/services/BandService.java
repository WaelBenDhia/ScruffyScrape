package wael.bendhia.services;

import java.util.Set;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import wael.bendhia.dao.BandDao;
import wael.bendhia.entities.Band;

@Path("/BandService")
public class BandService {

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
}