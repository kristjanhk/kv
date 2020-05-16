package eu.kyngas.kv.graph.model;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@RegisterForReflection
@Data
public class GraphItem {
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
