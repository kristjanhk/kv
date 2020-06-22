package eu.kyngas.kv.kv.rest;

import eu.kyngas.kv.kv.model.KvGraphItem;
import eu.kyngas.kv.kv.model.KvGraphItem.PriceItem;
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

import static eu.kyngas.kv.kv.model.KvItem.*;
import static eu.kyngas.kv.kv.model.KvItem.listSales;
import static eu.kyngas.kv.util.Predicates.withPrevious;
import static java.time.format.DateTimeFormatter.ISO_DATE_TIME;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;

@Path("kv/graph")
@Produces(MediaType.TEXT_HTML)
public class KvGraphResource {
  @ResourcePath("kv/sales")
  Template sales;
  @ResourcePath("kv/rents")
  Template rents;

  @GET
  @Path("sales")
  public TemplateInstance getSales() {
    return sales.data("items", toGraphItems(listSales()));
  }

  @GET
  @Path("rents")
  public TemplateInstance getRents() {
    return rents.data("items", toGraphItems(listRents()));
  }

  @TemplateExtension
  static String timeFormatted(PriceItem item) {
    return item.getTime().format(ISO_DATE_TIME);
  }

  private List<KvGraphItem> toGraphItems(List<KvItem> items) {
    return items.stream()
      .map(item -> KvGraphItem.builder()
        .uniqueId(item.getUniqueId())
        .link(item.getLink())
        .data(item.getChangeItems().stream()
                .sorted(comparing(kvChangeItem -> kvChangeItem.getInsertDate().truncatedTo(ChronoUnit.HOURS)))
                .filter(withPrevious((change, prev) -> prev == null || !prev.getPrice().equals(change.getPrice())))
                .map(change -> new PriceItem(change.getPublishDate().truncatedTo(ChronoUnit.HOURS), change.getPrice()))
                .collect(toList()))
        .build())
      .collect(toList());
  }
}
