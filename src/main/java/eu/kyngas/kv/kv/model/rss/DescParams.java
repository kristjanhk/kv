package eu.kyngas.kv.kv.model.rss;

import lombok.Data;

import java.util.List;

@Data
public class DescParams {
  private Double pricePerM2;
  private Double roomSize;
  private Integer roomFloor;
  private Integer totalFloor;
  private List<String> other;
}
