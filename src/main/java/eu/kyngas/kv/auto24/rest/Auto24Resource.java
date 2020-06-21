package eu.kyngas.kv.auto24.rest;

import eu.kyngas.kv.auto24.model.Auto24ClientParams;
import eu.kyngas.kv.auto24.service.Auto24Service;
import eu.kyngas.kv.auto24.model.Auto24SearchPageItem;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Slf4j
@Path("auto")
@Produces(MediaType.APPLICATION_JSON)
public class Auto24Resource {
  @Inject
  Auto24Service auto24Service;

  @GET
  @Path("debug")
  public List<Auto24SearchPageItem> getItems() {
    Auto24ClientParams params = Auto24ClientParams.createSaleParams();
    log.debug("AutoParams: {}", params);

    List<Auto24SearchPageItem> results = auto24Service.getAllSearchItems(params);
    log.info("Result count: " + results.size());
    return results;
  }


}
