package eu.kyngas.kv.auto24.rest;

import eu.kyngas.kv.auto24.model.Auto24ClientParams;
import eu.kyngas.kv.auto24.model.Auto24GraphItem;
import eu.kyngas.kv.auto24.model.Auto24GraphItem.PriceItem;
import eu.kyngas.kv.auto24.model.Auto24Item;
import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateExtension;
import io.quarkus.qute.TemplateInstance;
import io.quarkus.qute.api.ResourcePath;

import javax.ws.rs.BeanParam;
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

@Path("auto24/graph")
@Produces(MediaType.TEXT_HTML)
public class Auto24GraphResource {
  @ResourcePath("auto24/chart")
  Template template;

  @GET
  @Path("")
  public TemplateInstance getChart(@BeanParam Auto24ClientParams params) {
    List<Auto24GraphItem> graphItems = toGraphItems(Auto24Item.listAll());
    int minPrice = params.getMinPrice();
    int maxPrice = params.getMaxPrice() != 0 ? params.getMaxPrice() : graphItems.stream()
      .flatMap(item -> item.getData().stream())
      .mapToInt(item -> item.getPrice().intValue())
      .max()
      .orElse(0);
    return template
      .data("items", graphItems)
      .data("title", "Prices between " + minPrice + " - " + maxPrice)
      .data("minPrice", minPrice)
      .data("maxPrice", maxPrice);
  }

  @TemplateExtension
  static String timeFormatted(PriceItem item) {
    return item.getTime().format(ISO_DATE_TIME);
  }

  private List<Auto24GraphItem> toGraphItems(List<Auto24Item> items) {
    return items.stream()
      .map(item -> Auto24GraphItem.builder()
        .uniqueId(item.getUniqueId())
        .link(item.getLink())
        .data(item.getChangeItems().stream()
                .sorted(comparing(kvChangeItem -> kvChangeItem.getInsertDate().truncatedTo(ChronoUnit.HOURS)))
                .filter(withPrevious((change, prev) -> prev == null || !prev.getPrice().equals(change.getPrice())))
                .map(change -> new PriceItem(change.getInsertDate().truncatedTo(ChronoUnit.HOURS), change.getPrice()))
                .collect(toList()))
        .build())
      .collect(toList());
  }

}
