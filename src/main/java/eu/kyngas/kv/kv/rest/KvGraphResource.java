package eu.kyngas.kv.kv.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import eu.kyngas.kv.kv.model.KvChangeItem;
import eu.kyngas.kv.kv.model.KvGraphClientParams;
import eu.kyngas.kv.kv.model.KvGraphItem;
import eu.kyngas.kv.kv.model.KvGraphItem.PriceItem;
import eu.kyngas.kv.kv.model.KvItem;
import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateExtension;
import io.quarkus.qute.TemplateInstance;
import io.quarkus.qute.api.ResourcePath;

import javax.ws.rs.BeanParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Stream;

import static eu.kyngas.kv.kv.model.KvItem.*;
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
  @Path("tartu/sales")
  public TemplateInstance getTartuSales(@BeanParam KvGraphClientParams params) throws JsonProcessingException {
    List<KvGraphItem> items = toGraphItems(filterByParams(listTartuSales(), params));
    return metadata(sales, params, items).data("items", items);
  }

  @GET
  @Path("tartu/rents")
  public TemplateInstance getTartuRents(@BeanParam KvGraphClientParams params) throws JsonProcessingException {
    List<KvGraphItem> items = toGraphItems(filterByParams(listTartuRents(), params));
    return metadata(rents, params, items).data("items", items);
  }

  @GET
  @Path("tallinn/sales")
  public TemplateInstance getTallinnSales(@BeanParam KvGraphClientParams params) throws JsonProcessingException {
    List<KvGraphItem> items = toGraphItems(filterByParams(listTallinnSales(), params));
    return metadata(sales, params, items).data("items", items);
  }

  @GET
  @Path("tallinn/rents")
  public TemplateInstance getTallinnRents(@BeanParam KvGraphClientParams params) throws JsonProcessingException {
    List<KvGraphItem> items = toGraphItems(filterByParams(listTallinnRents(), params));
    return metadata(rents, params, items).data("items", items);
  }

  @TemplateExtension
  static String timeFormatted(PriceItem item) {
    return item.getTime().format(ISO_DATE_TIME);
  }

  private TemplateInstance metadata(Template template,
                                    KvGraphClientParams params,
                                    List<KvGraphItem> items) {
    int minPrice = params.getMinPrice() != null ? params.getMinPrice().intValue() : items.stream()
      .flatMap(item -> item.getData().stream())
      .mapToInt(item -> item.getPrice().intValue())
      .min()
      .orElseThrow();
    int maxPrice = params.getMaxPrice() != null ? params.getMaxPrice().intValue() : items.stream()
      .flatMap(item -> item.getData().stream())
      .mapToInt(item -> item.getPrice().intValue())
      .max()
      .orElseThrow();
    return template.data("title", "Prices between " + minPrice + " - " + maxPrice)
      .data("minPrice", minPrice)
      .data("maxPrice", maxPrice)
      .data("stepSize", getStepSize(minPrice, maxPrice));
  }

  private int getStepSize(int minPrice, int maxPrice) {
    int diff = maxPrice - minPrice;
    if (diff < 1000) {
      return 10;
    }
    if (diff < 5000) {
      return 50;
    }
    if (diff < 10000) {
      return 100;
    }
    if (diff < 50000) {
      return 500;
    }
    if (diff < 100000) {
      return 1000;
    }
    return 2000;
  }

  private List<KvItem> filterByParams(List<KvItem> items, KvGraphClientParams params) throws JsonProcessingException {
    Stream<KvItem> stream = items.stream();
    if (params.getActive() != null) {
      stream = stream.filter(kvItem -> !kvItem.isRemoved() == params.getActive());
    }
    if (params.getFloor() != null) {
      stream = stream.filter(kvItem -> kvItem.getRoomFloor() != null
        && params.getFloor().equals(kvItem.getRoomFloor()));
    }
    if (params.getRooms() != null) {
      stream = stream.filter(kvItem -> params.getRooms().equals(kvItem.getRooms()));
    }
    if (params.getMinSize() != null) {
      stream = stream.filter(kvItem -> kvItem.getRoomSize() != null && kvItem.getRoomSize() >= params.getMinSize());
    }
    if (params.getMaxSize() != null) {
      stream = stream.filter(kvItem -> kvItem.getRoomSize() != null && kvItem.getRoomSize() <= params.getMaxSize());
    }
    if (params.getStartDay() != null) {
      LocalDate startDay = LocalDate.parse(params.getStartDay());
      stream = stream.filter(kvItem -> kvItem.getChangeItems().stream().anyMatch(item -> {
        LocalDate publishDate = item.getPublishDate().toLocalDate();
        return publishDate.isEqual(startDay) || publishDate.isAfter(startDay);
      }));
    }
    if (params.getEndDay() != null) {
      LocalDate endDay = LocalDate.parse(params.getEndDay());
      stream = stream.filter(kvItem -> kvItem.getChangeItems().stream().anyMatch(item -> {
        LocalDate publishDate = item.getPublishDate().toLocalDate();
        return publishDate.isEqual(endDay) || publishDate.isBefore(endDay);
      }));
    }
    if (params.getMinPrice() != null) {
      stream = stream.filter(kvItem -> {
        KvChangeItem latest = kvItem.getChangeItems().get(kvItem.getChangeItems().size() - 1);
        return latest.getPrice() >= params.getMinPrice();
      });
    }
    if (params.getMaxPrice() != null) {
      stream = stream.filter(kvItem -> {
        KvChangeItem latest = kvItem.getChangeItems().get(kvItem.getChangeItems().size() - 1);
        return latest.getPrice() <= params.getMaxPrice();
      });
    }
    if (params.getMinPriceM2() != null) {
      stream = stream.filter(kvItem -> {
        KvChangeItem latest = kvItem.getChangeItems().get(kvItem.getChangeItems().size() - 1);
        return latest.getPricePerM2() != null && latest.getPricePerM2() >= params.getMinPriceM2();
      });
    }
    if (params.getMaxPriceM2() != null) {
      stream = stream.filter(kvItem -> {
        KvChangeItem latest = kvItem.getChangeItems().get(kvItem.getChangeItems().size() - 1);
        return latest.getPricePerM2() != null && latest.getPricePerM2() <= params.getMaxPriceM2();
      });
    }
    if (params.getMaxItems() != null) {
      stream = stream.limit(params.getMaxItems());
    }
    return stream.collect(toList());
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
