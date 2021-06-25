package eu.kyngas.kv.graph.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class KvGraphDto {
  @NotNull
  private String fullName;
  @NotNull
  private String link;
  @NotNull
  private List<KvGraphPriceDto> prices;

  @Data
  public static class KvGraphPriceDto {
    @NotNull
    private final LocalDateTime publishDate;
    @NotNull
    private final BigDecimal price;
  }
}
