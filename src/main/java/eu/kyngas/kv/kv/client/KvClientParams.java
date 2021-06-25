package eu.kyngas.kv.kv.client;

import eu.kyngas.kv.kv.model.KvCounty;
import eu.kyngas.kv.kv.model.KvType;
import io.quarkus.runtime.annotations.RegisterForReflection;
import io.vertx.core.json.JsonObject;
import lombok.*;

import javax.ws.rs.QueryParam;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Objects;
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

  public KvType getType() {
    return Arrays.stream(KvType.values())
      .filter(type -> type.getId() == dealType)
      .findFirst()
      .orElseThrow();
  }

  public String toEncodedQueryParam() {
    String params = JsonObject.mapFrom(this).stream()
      .filter(Objects::nonNull)
      .map(e -> e.getKey() + "=" + e.getValue().toString())
      .collect(Collectors.joining("&"));
    return URLEncoder.encode(params, StandardCharsets.UTF_8);
  }

  public static KvClientParams createParams(KvType type, KvCounty county) {
    return defaultBuilder()
      .dealType(type.getId())
      .priceType(Price.TOTAL.getType())
      .priceMin(type.isSale() ? 1_000 : 0)
      .priceMax(type.isSale() ? 1_000_000 : 5_000)
      .county(county.getId())
      .build();
  }

  private static KvClientParamsBuilder defaultBuilder() {
    return KvClientParams.builder()
      .act(Act.SEARCH_SIMPLE.getType())
      .page(1)
      .orderby(Order.NEWEST_FIRST.getType())
      .pageSize(100)
      .searchType("new")
      .objectType("1")
      .roomsMin(1)
      .roomsMax(15);
  }

  @Getter
  @RequiredArgsConstructor
  private enum Act {
    SEARCH_SIMPLE("search.simple"),
    SEARCH_OBJECT_COORDS("search.objectcoords"),
    SEARCH_OBJECT_INFO("search.objectinfo"),
    SEARCH_MAP("search.map"),
    SHOW("object.show"), //?act=object.show&printable&object_id=
    GET_IMAGES_JSON("object.getImagesJson"), //?act=object.getImagesJson&object_id=
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
  private enum Price {
    TOTAL(1),
    AREA(2);
    private final int type;
  }

}
