package eu.kyngas.kv.client;

import eu.kyngas.kv.database.model.KvItem;
import eu.kyngas.kv.client.model.DescParams;
import eu.kyngas.kv.client.model.Item;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Comparator.*;
import static java.util.function.UnaryOperator.identity;

@Slf4j
@Path("")
@Produces(MediaType.APPLICATION_JSON)
public class QueryResource {
  private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("HH:mm dd.MM.yyyy");

  @Inject
  QueryService queryService;

  @GET
  @Path("sales")
  public List<KvItem> getSales() {
    return queryService.query(Params.createSaleParams(identity())).getChannel().getItem().stream()
      .map(item -> item.toKvItem(true))
      .sorted(comparing((KvItem item) -> item.getPublishDate().toLocalDate(), reverseOrder())
                .thenComparing(KvItem::getPricePerM2, nullsLast(naturalOrder()))
                .thenComparing(KvItem::getPrice))
      .collect(Collectors.toList());
  }

  @GET
  @Path("rents")
  public List<KvItem> getRents() {
    return queryService.query(Params.createRentParams(identity())).getChannel().getItem().stream()
      .map(item -> item.toKvItem(false))
      .sorted(comparing((KvItem item) -> item.getPublishDate().toLocalDate(), reverseOrder())
                .thenComparing(KvItem::getPricePerM2, nullsLast(naturalOrder()))
                .thenComparing(KvItem::getPrice))
      .collect(Collectors.toList());
  }

  @GET
  @Path("console/sales")
  public int getConsoleSales() {
    log.info(queryService.query(Params.createSaleParams(identity())).getChannel().getItem().stream()
               .sorted(comparing((Item item) -> item.getPubDate().toLocalDate(), reverseOrder())
                         .thenComparing((Item item) -> item.getDescription().getParams().getPricePerM2(),
                                        nullsLast(naturalOrder()))
                         .thenComparing(item -> item.getTitle().getPrice()))
               .map(QueryResource::formatItem)
               .collect(Collectors.joining(",\n", "\n", "")));
    return 200;
  }

  @GET
  @Path("console/rents")
  public int getConsoleRents() {
    log.info(queryService.query(Params.createRentParams(identity())).getChannel().getItem().stream()
               .sorted(comparing((Item item) -> item.getPubDate().toLocalDate(), reverseOrder())
                         .thenComparing((Item item) -> item.getDescription().getParams().getPricePerM2(),
                                        nullsLast(naturalOrder()))
                         .thenComparing(item -> item.getTitle().getPrice()))
               .map(QueryResource::formatItem)
               .collect(Collectors.joining(",\n", "\n", "")));
    return 200;
  }

  private static String formatItem(Item item) {
    DescParams params = item.getDescription().getParams();
    return String.join(", ",
                       item.getLink(),
                       item.getPubDate().format(FORMATTER),
                       item.getTitle().getPrice() + " EUR",
                       params.getPricePerM2() + " EUR/m2",
                       params.getRoomSize() + " m2",
                       params.getRoomFloor() + "/" + params.getTotalFloor(),
                       item.getTitle().getAddress(),
                       params.getOther().toString());
  }
}
