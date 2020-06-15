package eu.kyngas.kv.client.kv.rss.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import eu.kyngas.kv.database.model.KvChangeItem;
import eu.kyngas.kv.database.model.KvItem;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Data
public class Item {
  private Title title;
  private Description description;
  private String link;
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "EEE, dd MMM yyyy HH:mm:ss z")
  private LocalDateTime pubDate;
  private int guid;

  public boolean isValid() {
    return title != null;
  }

  public KvItem toKvItem(boolean isSale) {
    return KvItem.builder()
      .externalId(guid)
      .dealType(isSale ? "sale" : "rent")
      .link(link)
      .address(title == null ? null : title.getAddress())
      .rooms(title == null ? -1 : title.getRooms())
      .roomSize(description.getParams().getRoomSize())
      .roomFloor(description.getParams().getRoomFloor())
      .totalFloor(description.getParams().getTotalFloor())
      .changeItems(List.of(KvChangeItem.builder()
                             .publishDate(pubDate)
                             .insertDate(LocalDateTime.now().truncatedTo(ChronoUnit.HOURS))
                             .imgLink(description.getImgLink())
                             .price(title == null ? null : title.getPrice())
                             .pricePerM2(description.getParams().getPricePerM2())
                             .build()))
      .build();
  }
}
