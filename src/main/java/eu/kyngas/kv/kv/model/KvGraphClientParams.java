package eu.kyngas.kv.kv.model;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.ws.rs.QueryParam;

@RegisterForReflection
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class KvGraphClientParams {
  @QueryParam("active")
  private Boolean active;
  @QueryParam("floor")
  private Integer floor;
  @QueryParam("rooms")
  private Integer rooms;
  @QueryParam("min_size")
  private Double minSize;
  @QueryParam("max_size")
  private Double maxSize;
  @QueryParam("max_items")
  private Integer maxItems;

  @QueryParam("start_day")
  private String startDay;
  @QueryParam("end_day")
  private String endDay;

  @QueryParam("min_price")
  private Double minPrice;
  @QueryParam("max_price")
  private Double maxPrice;
  @QueryParam("min_price_m2")
  private Double minPriceM2;
  @QueryParam("max_price_m2")
  private Double maxPriceM2;
}
