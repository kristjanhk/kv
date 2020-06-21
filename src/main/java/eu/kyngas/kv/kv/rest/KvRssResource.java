package eu.kyngas.kv.kv.rest;

import eu.kyngas.kv.kv.model.KvClientParams;
import eu.kyngas.kv.kv.service.KvService;
import eu.kyngas.kv.kv.model.rss.DescParams;
import eu.kyngas.kv.kv.model.rss.Item;
import eu.kyngas.kv.kv.model.KvItem;
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
@Path("kv/rss")
@Produces(MediaType.APPLICATION_JSON)
public class KvRssResource {
  private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("HH:mm dd.MM.yyyy");

  @Inject
  KvService kvService;

  @GET
  @Path("sales")
  public List<KvItem> getSales() {
    return kvService.getRssFeed(KvClientParams.createSaleParams(identity())).getChannel().getItem().stream()
      .filter(Item::isValid)
      .map(item -> item.toKvItem(true))
      .sorted(comparing((KvItem item) -> item.getLatestChangeItem().getPublishDate().toLocalDate(), reverseOrder())
                .thenComparing(item -> item.getLatestChangeItem().getPricePerM2(), nullsLast(naturalOrder()))
                .thenComparing(item -> item.getLatestChangeItem().getPrice()))
      .collect(Collectors.toList());
  }

  @GET
  @Path("rents")
  public List<KvItem> getRents() {
    return kvService.getRssFeed(KvClientParams.createRentParams(identity())).getChannel().getItem().stream()
      .filter(Item::isValid)
      .map(item -> item.toKvItem(false))
      .sorted(comparing((KvItem item) -> item.getLatestChangeItem().getPublishDate().toLocalDate(), reverseOrder())
                .thenComparing(item -> item.getLatestChangeItem().getPricePerM2(), nullsLast(naturalOrder()))
                .thenComparing(item -> item.getLatestChangeItem().getPrice()))
      .collect(Collectors.toList());
  }

  @GET
  @Path("console/sales")
  public int getConsoleSales() {
    log.info(kvService.getRssFeed(KvClientParams.createSaleParams(identity())).getChannel().getItem().stream()
               .sorted(comparing((Item item) -> item.getPubDate().toLocalDate(), reverseOrder())
                         .thenComparing((Item item) -> item.getDescription().getParams().getPricePerM2(),
                                        nullsLast(naturalOrder()))
                         .thenComparing(item -> item.getTitle().getPrice()))
               .map(KvRssResource::formatItem)
               .collect(Collectors.joining(",\n", "\n", "")));
    return 200;
  }

  @GET
  @Path("console/rents")
  public int getConsoleRents() {
    log.info(kvService.getRssFeed(KvClientParams.createRentParams(identity())).getChannel().getItem().stream()
               .sorted(comparing((Item item) -> item.getPubDate().toLocalDate(), reverseOrder())
                         .thenComparing((Item item) -> item.getDescription().getParams().getPricePerM2(),
                                        nullsLast(naturalOrder()))
                         .thenComparing(item -> item.getTitle().getPrice()))
               .map(KvRssResource::formatItem)
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
