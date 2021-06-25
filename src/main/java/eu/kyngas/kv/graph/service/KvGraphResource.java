package eu.kyngas.kv.graph.service;

import eu.kyngas.kv.graph.model.KvGraphData;
import eu.kyngas.kv.graph.model.KvGraphDto;
import eu.kyngas.kv.graph.model.KvGraphInput;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Tag(name = "graph")
@Path("kv/graph")
@Produces(MediaType.APPLICATION_JSON)
public class KvGraphResource {
  @Inject
  KvGraphService kvGraphService;

  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  public KvGraphData getData(KvGraphInput input) {
    List<KvGraphDto> items = kvGraphService.findGraphItems(input);
    return new KvGraphData(getMinPrice(input, items), getMaxPrice(input, items), items);
  }

  private int getMinPrice(KvGraphInput input, List<KvGraphDto> items) {
    return input.getPriceMin() != null ? input.getPriceMin().intValue() : items.stream()
      .flatMap(item -> item.getPrices().stream())
      .mapToInt(item -> item.getPrice().intValue())
      .min()
      .orElseThrow();
  }

  private int getMaxPrice(KvGraphInput input, List<KvGraphDto> items) {
    return input.getPriceMax() != null ? input.getPriceMax().intValue() : items.stream()
      .flatMap(item -> item.getPrices().stream())
      .mapToInt(item -> item.getPrice().intValue())
      .max()
      .orElseThrow();
  }
}
