package eu.kyngas.kv.graph.model;

import eu.kyngas.kv.kv.model.KvCounty;
import eu.kyngas.kv.kv.model.KvType;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class KvGraphInput {
  @NotNull
  private Boolean active;
  private KvType type;
  private KvCounty county;
  private BigDecimal priceMin;
  private BigDecimal priceMax;
  private BigDecimal sizeMin;
  private BigDecimal sizeMax;
  private LocalDate dateMin;
  private LocalDate dateMax;
  private Integer rooms;
}
