package eu.kyngas.kv.kv.model;

import io.quarkus.runtime.annotations.RegisterForReflection;
import io.vertx.core.json.JsonObject;
import lombok.*;

import javax.ws.rs.QueryParam;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

@RegisterForReflection
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class KvClientParams {
  @QueryParam("act")
  private String act;
  private int page;
  @QueryParam("orderby")
  private String orderby;
  @QueryParam("page_size")
  private int pageSize;
  @QueryParam("deal_type")
  private int dealType;
  @QueryParam("county")
  private int county;
  @QueryParam("search_type")
  private String searchType;
  @QueryParam("object_type")
  private String objectType;
  @QueryParam("parish")
  private int parish;
  @QueryParam("rooms_min")
  private int roomsMin;
  @QueryParam("rooms_max")
  private int roomsMax;
  @QueryParam("price_type")
  private int priceType;
  @QueryParam("price_min")
  private int priceMin;
  @QueryParam("price_max")
  private int priceMax;

  public String getType() {
    return Arrays.stream(Deal.values())
      .filter(deal -> deal.getType() == dealType)
      .findFirst()
      .map(deal -> deal.name().toLowerCase())
      .orElseThrow();
  }

  public String toEncodedQueryParam() {
    String params = JsonObject.mapFrom(this).stream()
      .filter(Objects::nonNull)
      .map(e -> e.getKey() + "=" + e.getValue().toString())
      .collect(Collectors.joining("&"));
    return URLEncoder.encode(params, StandardCharsets.UTF_8);
  }

  public static KvClientParams createTartuSaleParams(Function<KvClientParamsBuilder, KvClientParamsBuilder> operator) {
    return operator.<KvClientParamsBuilder>compose(b -> b
      .dealType(Deal.APARTMENT_SALE.getType())
      .priceType(Price.TOTAL.getType())
      .priceMin(5000)
      .priceMax(500_000))
      .apply(defaultBuilder())
      .build();
  }

  public static KvClientParams createTartuRentParams(Function<KvClientParamsBuilder, KvClientParamsBuilder> operator) {
    return operator.<KvClientParamsBuilder>compose(b -> b
      .dealType(Deal.APARTMENT_RENT.getType())
      .priceType(Price.TOTAL.getType())
      .priceMin(0)
      .priceMax(1000))
      .apply(defaultBuilder())
      .build();
  }

  public static KvClientParams createTallinnSaleParams(UnaryOperator<KvClientParamsBuilder> operator) {
    return createTartuSaleParams(operator.compose(b -> b
      .county(County.TALLINN.getType())
      .parish(Parish.HARJUMAA.getType())));
  }

  public static KvClientParams createTallinnRentParams(UnaryOperator<KvClientParamsBuilder> operator) {
    return createTartuRentParams(operator.compose(b -> b
      .county(County.TALLINN.getType())
      .parish(Parish.HARJUMAA.getType())));
  }

  private static KvClientParamsBuilder defaultBuilder() {
    return KvClientParams.builder()
      .act("search.simple")
      .page(1)
      .orderby(Order.NEWEST_FIRST.getType())
      .pageSize(10_000)
      .county(County.TARTU.getType())
      .searchType("new")
      .objectType("1")
      .parish(Parish.TARTUMAA.getType())
      .roomsMin(1)
      .roomsMax(8);
  }

  @Getter
  @RequiredArgsConstructor
  private enum Act {
    SEARCH_SIMPLE("search.simple"),
    SEARCH_OBJECT_COORDS("search.objectcoords"),
    SEARCH_OBJECT_INFO("search.objectinfo"),
    SEARCH_MAP("search.map"),
    ;
    private final String type;
  }

  @Getter
  @RequiredArgsConstructor
  private enum Order {
    NONE("cd"),
    CHEAPEST_FIRST("pawl"),
    CHEAPEST_LAST("pdwl"),
    CHEAPEST_M2_FIRST("mawl"),
    CHEAPEST_M2_LAST("mdwl"),
    NEWEST_FIRST("cdwl"),
    NEWEST_LAST("cawl"),
    MAX_SIZE("adwl"),
    MIN_SIZE("aawl");
    private final String type;
  }

  @Getter
  @RequiredArgsConstructor
  public enum Deal {
    APARTMENT_SALE(1),
    APARTMENT_RENT(2),
    APARTMENT_SHORT_RENT(30),
    APARTMENT_ALL_RENT(20),
    HOUSE_SALE(3),
    HOUSE_RENT(4),
    HOUSE_SHORT_RENT(31),
    HOUSE_ALL_RENT(21);
    private final int type;
  }

  @Getter
  @RequiredArgsConstructor
  public enum County {
    TARTU(12),
    TALLINN(1);
    private final int type;
  }

  @Getter
  @RequiredArgsConstructor
  private enum Parish {
    TARTUMAA(1063),
    HARJUMAA(1061);
    private final int type;
  }

  @Getter
  @RequiredArgsConstructor
  private enum Price {
    TOTAL(1),
    AREA(2);
    private final int type;
  }

}
