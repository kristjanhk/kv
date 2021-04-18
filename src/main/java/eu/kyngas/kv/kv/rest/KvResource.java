package eu.kyngas.kv.kv.rest;

import eu.kyngas.kv.kv.model.KvClientParams;
import eu.kyngas.kv.kv.model.KvSearchPageItem;
import eu.kyngas.kv.kv.service.KvService;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.function.UnaryOperator.identity;

@Slf4j
@Path("kv")
@Produces(MediaType.APPLICATION_JSON)
public class KvResource {
  @Inject
  KvService kvService;
  @Inject
  @RestClient
  KvClient kvClient;

  @GET
  @Path("debug")
  public List<KvSearchPageItem> getDebug() {
    KvClientParams params = KvClientParams.createTartuSaleParams(identity());
    log.info("KvParams: {}", params);

    List<KvSearchPageItem> items = kvService.getAllSearchItems(params);

    log.info("Items: {}", items.stream()
      .map(KvSearchPageItem::toString)
      .collect(Collectors.joining("\n")));
    log.info("Fetched {} items", items.size());
    return items;
  }

  @GET
  @Path("debug2")
  @Produces(MediaType.TEXT_HTML)
  public String getDebug2() {
    KvClientParams params = KvClientParams.createTartuSaleParams(b -> b.dealType(KvClientParams.Deal.HOUSE_SALE.getType()));
    log.info("KvParams: {}", params);

    return kvClient.getSearchPage(params, 1);
  }


}
