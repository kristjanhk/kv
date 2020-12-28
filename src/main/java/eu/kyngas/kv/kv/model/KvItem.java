package eu.kyngas.kv.kv.model;

import eu.kyngas.kv.util.StringUtil;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

import static eu.kyngas.kv.kv.model.KvClientParams.County.*;
import static eu.kyngas.kv.kv.model.KvClientParams.Deal.*;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.joining;

@Entity
@Data
@Builder
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
public class KvItem extends PanacheEntityBase {
  @Id
  @GeneratedValue
  public Long id;
  private long externalId;
  private Long prevExternalId;
  private LocalDateTime insertDate;
  private String dealType;
  private String link;
  private String county;
  private String area;
  private String district;
  private String address;
  private int rooms;
  private Double roomSize;
  private Integer roomFloor;
  private Integer totalFloor;
  private boolean removed;
  private LocalDateTime removedDate;

  @OneToMany(mappedBy = "kvItem", cascade = CascadeType.PERSIST)
  private List<KvChangeItem> changeItems;

  @Transient
  public String getUniqueId() {
    return Stream.of(getFullAddress(), rooms, roomSize, roomFloor)
      .map(String::valueOf)
      .collect(joining(" - "));
  }

  @Transient
  public String getFullAddress() {
    return String.join(", ", county, area, district, address);
  }

  @Transient
  public KvChangeItem getLatestChangeItem() {
    return changeItems.stream()
      .max(comparing(KvChangeItem::getPublishDate))
      .orElseThrow();
  }

  public static List<KvItem> listTartuSales() {
    return KvItem.list("dealType = ?1 AND area = ?2",
                       APARTMENT_SALE.name().toLowerCase(),
                       StringUtil.capitalize(TARTU.name().toLowerCase()));
  }

  public static List<KvItem> listTartuRents() {
    return KvItem.list("dealType = ?1 AND area = ?2",
                       APARTMENT_RENT.name().toLowerCase(),
                       StringUtil.capitalize(TARTU.name().toLowerCase()));
  }

  public static List<KvItem> listTallinnSales() {
    return KvItem.list("dealType = ?1 AND area = ?2",
                       APARTMENT_SALE.name().toLowerCase(),
                       StringUtil.capitalize(TALLINN.name().toLowerCase()));
  }

  public static List<KvItem> listTallinnRents() {
    return KvItem.list("dealType = ?1 AND area = ?2",
                       APARTMENT_RENT.name().toLowerCase(),
                       StringUtil.capitalize(TALLINN.name().toLowerCase()));
  }
}
