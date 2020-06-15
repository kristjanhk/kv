package eu.kyngas.kv.client.kv;

import eu.kyngas.kv.client.kv.model.KvSearchPageItem;
import lombok.extern.slf4j.Slf4j;

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

  @GET
  @Path("debug")
  public List<KvSearchPageItem> getDebug() {
    KvParams params = KvParams.createSaleParams(identity());
    log.debug("KvParams: {}", params);

    List<KvSearchPageItem> items = kvService.getAllSearchItems(params);

    log.info("Items: {}", items.stream()
      .map(KvSearchPageItem::toString)
      .collect(Collectors.joining("\n")));
    log.info("Fetched {} items", items.size());
    return items;
  }


}
