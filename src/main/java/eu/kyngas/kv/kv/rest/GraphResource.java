package eu.kyngas.kv.kv.rest;

import eu.kyngas.kv.kv.model.GraphItem;
import eu.kyngas.kv.kv.model.KvItem;
import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateExtension;
import io.quarkus.qute.TemplateInstance;
import io.quarkus.qute.api.ResourcePath;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static eu.kyngas.kv.util.Predicates.withPrevious;
import static java.time.format.DateTimeFormatter.ISO_DATE_TIME;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;

@Path("graph")
@Produces(MediaType.TEXT_HTML)
public class GraphResource {
  @ResourcePath("kv/sales")
  Template sales;
  @ResourcePath("kv/rents")
  Template rents;

  @GET
  @Path("sales")
  public TemplateInstance getSales() {
    return sales.data("items", toGraphItems(KvItem.listSales()));
  }

  @GET
  @Path("rents")
  public TemplateInstance getRents() {
    return rents.data("items", toGraphItems(KvItem.listRents()));
  }

  @TemplateExtension
  static String timeFormatted(GraphItem.PriceItem item) {
    return item.getTime().format(ISO_DATE_TIME);
  }

  private List<GraphItem> toGraphItems(List<KvItem> items) {
    return items.stream()
      .map(item -> GraphItem.builder()
        .uniqueId(item.getUniqueId())
        .link("https://www.kv.ee/" + item.getExternalId())
        .data(item.getChangeItems().stream()
                .sorted(comparing(kvChangeItem -> kvChangeItem.getInsertDate().truncatedTo(ChronoUnit.HOURS)))
                .filter(withPrevious((change, prev) -> prev == null || !prev.getPrice().equals(change.getPrice())))
                .map(change -> new GraphItem.PriceItem(change.getPublishDate().truncatedTo(ChronoUnit.HOURS),
                                                       change.getPrice()))
                .collect(toList()))
        .build())
      .collect(toList());
  }
}
