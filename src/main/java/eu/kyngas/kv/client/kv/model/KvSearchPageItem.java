package eu.kyngas.kv.client.kv.model;

import eu.kyngas.kv.database.model.KvChangeItem;
import eu.kyngas.kv.database.model.KvItem;
import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@RegisterForReflection
public class KvSearchPageItem {
  private int id;
  private String type;
  private String imgLink;
  private String county;
  private String area;
  private String district;
  private String address;
  private String details;
  private int rooms;
  private Double roomSize;
  private Integer roomFloor;
  private Integer totalFloor;
  private LocalDateTime publishDate;
  private double price;
  private Double priceM2;

  public String getLink() {
    return "https://www.kv.ee/" + id;
  }

  public String getFullAddress() {
    return String.join(", ", county, area, district, address);
  }

  public KvItem toKvItem() {
    KvChangeItem changeItem = KvChangeItem.builder()
      .publishDate(publishDate)
      .insertDate(LocalDateTime.now())
      .imgLink(imgLink)
      .price(price)
      .pricePerM2(priceM2)
      .build();
    KvItem item = KvItem.builder()
      .externalId(id)
      .insertDate(LocalDateTime.now())
      .dealType(type)
      .link(getLink())
      .county(county)
      .area(area)
      .district(district)
      .address(address)
      .rooms(rooms)
      .roomSize(roomSize)
      .roomFloor(roomFloor)
      .totalFloor(totalFloor)
      .changeItems(List.of(changeItem))
      .build();

    changeItem.setKvItem(item);
    return item;
  }
}
