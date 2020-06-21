package eu.kyngas.kv.auto24.model;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@RegisterForReflection
public class Auto24SearchPageItem {
  private long id;
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

  public String getLink() {
    return "https://www.auto24.ee/used/" + id;
  }

  public Auto24Item toAuto24Item() {
    Auto24ChangeItem changeItem = Auto24ChangeItem.builder()
      .insertDate(LocalDateTime.now())
      .price(price)
      .build();
    Auto24Item item = Auto24Item.builder()
      .externalId(id)
      .insertDate(LocalDateTime.now())
      .link(getLink())
      .imgLink(imgLink)
      .mark(mark)
      .model(model)
      .engine(engine)
      .power(power)
      .year(year)
      .fuelType(fuelType)
      .transmissionType(transmissionType)
      .auction(auction)
      .changeItems(List.of(changeItem))
      .build();
    changeItem.setAuto24Item(item);
    return item;
  }
}
