package eu.kyngas.kv.client;

import io.vertx.core.json.JsonObject;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

@Builder
@Data
public class Params {
  private String act;
  private int page;
  private String orderby;
  private int pageSize;
  private int dealType;
  private int county;
  private String searchType;
  private String objectType;
  private int parish;
  private int roomsMin;
  private int roomsMax;
  private int priceType;
  private int priceMin;
  private int priceMax;

  public String toQueryParam() {
    String params = JsonObject.mapFrom(this).stream()
      .filter(Objects::nonNull)
      .map(e -> e.getKey() + "=" + e.getValue().toString())
      .collect(Collectors.joining("&"));
    return URLEncoder.encode(params, StandardCharsets.UTF_8);
  }

  public static Params createSaleParams(UnaryOperator<ParamsBuilder> operator) {
    return operator.<ParamsBuilder>compose(b -> b
      .dealType(Deal.SALE.getType())
      .priceType(Price.TOTAL.getType())
      .priceMin(20_000)
      .priceMax(500_000))
      .apply(defaultBuilder())
      .build();
  }

  public static Params createRentParams(UnaryOperator<ParamsBuilder> operator) {
    return operator.<ParamsBuilder>compose(b -> b
      .dealType(Deal.RENT.getType())
      .priceType(Price.TOTAL.getType())
      .priceMin(0)
      .priceMax(1000))
      .apply(defaultBuilder())
      .build();
  }

  private static ParamsBuilder defaultBuilder() {
    return Params.builder()
      .act("search.simple")
      .page(1)
      .orderby(Order.CHEAPEST_FIRST.getType())
      .pageSize(10_000)
      .county(County.TARTU.getType())
      .searchType("new")
      .objectType("1")
      .parish(Parish.TARTUMAA.getType())
      .roomsMin(1)
      .roomsMax(4);
  }

  @Getter
  @RequiredArgsConstructor
  private enum Order {
    CHEAPEST_FIRST("pawl");
    private final String type;
  }

  @Getter
  @RequiredArgsConstructor
  private enum Deal {
    SALE(1), RENT(2);
    private final int type;
  }

  @Getter
  @RequiredArgsConstructor
  private enum County {
    TARTU(12);
    private final int type;
  }

  @Getter
  @RequiredArgsConstructor
  private enum Parish {
    TARTUMAA(1063);
    private final int type;
  }

  @Getter
  @RequiredArgsConstructor
  private enum Price {
    TOTAL(1), AREA(2);
    private final int type;
  }

}
