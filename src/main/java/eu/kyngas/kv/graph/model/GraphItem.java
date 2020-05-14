package eu.kyngas.kv.graph.model;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class GraphItem {
  private String uniqueId;
  private String link;
  private List<PriceItem> data;

  @Data
  public static class PriceItem {
    private final LocalDateTime time;
    private final Double price;
  }
}
