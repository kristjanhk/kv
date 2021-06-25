package eu.kyngas.kv.kv24.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class Kv24ClientRequest {
  private Integer page;
  private List<Address> addresses;
  private String areaMin; // double
  private String areaMax; // double
  private String buildYearMin; // int
  private String buildYearMax; // int
  private List<String> dealTypes; // sale
  private String priceMax; // double
  private String priceMin; // double
  private String sortBy; // created_at
  private String sortOrder; // desc
  private String id; // long

  @Data
  @Builder
  public static class Address {
    @JsonProperty("A1")
    private String a1; //Tartu maakond
    @JsonProperty("A2")
    private String a2; //Tartu linn
  }
}
