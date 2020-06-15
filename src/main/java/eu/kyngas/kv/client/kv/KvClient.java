package eu.kyngas.kv.client.kv;

import eu.kyngas.kv.client.kv.rss.model.Rss;
import eu.kyngas.kv.util.LoggingClientRequestFilter;
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("")
@RegisterRestClient
@RegisterProvider(LoggingClientRequestFilter.class)
public interface KvClient {

  @GET
  @Produces(MediaType.APPLICATION_XML)
  Rss getRssFeed(@QueryParam("act") String act, @QueryParam("qry") String params);

  @GET
  @Produces(MediaType.TEXT_HTML)
  String getSearchPage(@BeanParam KvParams kvParams, @QueryParam("page") int page);
}
