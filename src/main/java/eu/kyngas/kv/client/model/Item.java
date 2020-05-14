package eu.kyngas.kv.client.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import eu.kyngas.kv.database.model.KvItem;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Data
public class Item {
  private Title title;
  private Description description;
  private String link;
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "EEE, dd MMM yyyy HH:mm:ss z")
  private LocalDateTime pubDate;
  private int guid;

  public KvItem toKvItem(boolean isSale) {
    return KvItem.builder()
      .kvId(guid)
      .kvType(isSale ? "sale" : "rent")
      .link(link)
      .publishDate(pubDate)
      .insertDate(LocalDateTime.now().truncatedTo(ChronoUnit.HOURS))
      .address(title.getAddress())
      .imgLink(description.getImgLink())
      .price(title.getPrice())
      .pricePerM2(description.getParams().getPricePerM2())
      .rooms(title.getRooms())
      .roomSize(description.getParams().getRoomSize())
      .roomFloor(description.getParams().getRoomFloor())
      .totalFloor(description.getParams().getTotalFloor())
      .build();
  }
}
