package eu.kyngas.kv.graph;

import eu.kyngas.kv.database.DatabaseService;
import eu.kyngas.kv.database.model.KvItem;
import eu.kyngas.kv.graph.model.GraphItem;
import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateExtension;
import io.quarkus.qute.TemplateInstance;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.time.format.DateTimeFormatter.ISO_DATE_TIME;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

@Path("graph")
@Produces(MediaType.TEXT_HTML)
public class GraphResource {
  @Inject
  DatabaseService databaseService;
  @Inject
  Template sales;
  @Inject
  Template rents;

  @GET
  @Path("sales")
  public TemplateInstance getSales() {
    return sales.data("items", toGraphItems(databaseService.findSales()));
  }

  @GET
  @Path("rents")
  public TemplateInstance getRents() {
    return rents.data("items", toGraphItems(databaseService.findRents()));
  }

  @TemplateExtension
  static String timeFormatted(GraphItem.PriceItem item) {
    return item.getTime().format(ISO_DATE_TIME);
  }

  private List<GraphItem> toGraphItems(List<KvItem> items) {
    if (items.isEmpty()) {
      return List.of();
    }

    Map<Integer, List<KvItem>> itemById = items.stream().collect(groupingBy(KvItem::getKvId));
    itemById.values().forEach(list -> list.sort(comparing(KvItem::getInsertDate)));
    return itemById.entrySet().stream()
      .map(e -> {
        GraphItem gItem = new GraphItem();
        gItem.setUniqueId(e.getValue().get(0).getUniqueId());
        gItem.setLink("https://www.kv.ee/" + e.getKey());

        List<GraphItem.PriceItem> priceList = new ArrayList<>();
        Double lastPrice = null;
        for (KvItem item : e.getValue()) {
          if (lastPrice == null || !lastPrice.equals(item.getPrice())) {
            priceList.add(new GraphItem.PriceItem(item.getInsertDate(), item.getPrice()));
            lastPrice = item.getPrice();
          }
        }

        gItem.setData(priceList);
        return gItem;
      }).collect(toList());
  }
}
