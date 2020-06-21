package eu.kyngas.kv.auto24.model;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@RegisterForReflection
public class Auto24SearchPageItem {
  private int id;
  private String link;
  private String imgLink;
  private String mark;
  private String model;
  private String engine;
  private Double power;
  private Integer year;
  private String fuelType;
  private String transmissionType;
  private Double price;
  private boolean auction;
}
