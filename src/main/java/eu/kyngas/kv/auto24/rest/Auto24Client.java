package eu.kyngas.kv.auto24.rest;

import eu.kyngas.kv.auto24.model.Auto24ClientParams;
import eu.kyngas.kv.util.LoggingClientRequestFilter;
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("kasutatud/nimekiri.php")
@RegisterRestClient
@RegisterProvider(LoggingClientRequestFilter.class)
public interface Auto24Client {

  @GET
  @Produces(MediaType.TEXT_HTML)
  String getSearchPage(@BeanParam Auto24ClientParams auto24ClientParams, @QueryParam("ak") int fromIndex);
}
