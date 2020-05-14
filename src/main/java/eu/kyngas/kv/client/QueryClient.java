package eu.kyngas.kv.client;

import eu.kyngas.kv.model.Rss;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

@Path("")
@RegisterRestClient
public interface QueryClient {

  @GET
  @Produces(MediaType.APPLICATION_XML)
  Rss query(@QueryParam("act") String act, @QueryParam("qry") String params);
}
