package eu.kyngas.kv.auto24.model;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@RegisterForReflection
@Builder
@Data
public class Auto24GraphItem {
  private String uniqueId;
  private String link;
  private List<PriceItem> data;

  @RegisterForReflection
  @Data
  public static class PriceItem {
    private final LocalDateTime time;
    private final Double price;
  }
}
