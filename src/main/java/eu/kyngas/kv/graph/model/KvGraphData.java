package eu.kyngas.kv.graph.model;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class KvGraphData {
  @NotNull
  private final int minPrice;
  @NotNull
  private final int maxPrice;
  @NotNull
  private final List<KvGraphDto> data;
}
