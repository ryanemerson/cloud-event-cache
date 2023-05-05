package io.gingersnap.project;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/")
public class CacheResource {

   private static final NotFoundException NOT_FOUND_EXCEPTION = new NotFoundException();
   @Inject
   Cache cache;

   @GET
   @Path("/{key}")
   @Produces(MediaType.APPLICATION_JSON)
   public String get(String key) {
      String val = cache.get(key);
      if (val == null)
         throw NOT_FOUND_EXCEPTION;
      return val;
   }
}
