package eu.kyngas.kv.kv24.service;

import eu.kyngas.kv.kv24.dao.Kv24Client;
import eu.kyngas.kv.kv24.model.Kv24ClientRequest;
import eu.kyngas.kv.kv24.model.Kv24SearchPage;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Slf4j
@Path("kv24")
@Produces(MediaType.APPLICATION_JSON)
public class Kv24Resource {
  @Inject
  @RestClient
  Kv24Client kv24Client;

  @GET
  @Path("debug3")
  public Kv24SearchPage getDebug3() {
    return kv24Client.getSearchPage(Kv24ClientRequest.builder()
                                      .page(1)
                                      .addresses(List.of(Kv24ClientRequest.Address.builder()
                                                           .a1("Tartu maakond")
                                                           .a2("Tartu linn")
                                                           .build()))
                                      .dealTypes(List.of("sale"))
                                      .sortBy("created_at")
                                      .sortOrder("desc")
                                      .build());
  }
}
