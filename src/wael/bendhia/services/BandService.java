package wael.bendhia.services;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
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
   public List<Band> getBands(){
      return bandDao.getAllBands();
   }
   
   @GET
   @Path("/bands/vol/{volume}")
   @Produces(MediaType.APPLICATION_JSON)
   public List<Band> getBandsVolume(@PathParam("volume") int volume){
      return bandDao.getAllBandsVolume(volume);
   }
   
   @POST
   @Path("/band")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public Band getBandFull(Band band){
	   if(band != null)
		   return bandDao.getBand(band);
	   else
		   return new Band(0, "You gave me nothing", null, null, null);
   }
}