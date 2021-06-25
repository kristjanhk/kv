package eu.kyngas.kv.kv24.dao;

import eu.kyngas.kv.kv24.model.Kv24ClientRequest;
import eu.kyngas.kv.kv24.model.Kv24SearchPage;
import org.eclipse.microprofile.rest.client.annotation.ClientHeaderParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@RegisterRestClient(configKey = "kv.kv24")
public interface Kv24Client {

  @POST
  @Path("search")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @ClientHeaderParam(name = "X-Requested-With", value = "XMLHttpRequest")
  Kv24SearchPage getSearchPage(Kv24ClientRequest request);
}
