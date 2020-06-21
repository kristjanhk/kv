package eu.kyngas.kv.auto24.model;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

import static java.util.Comparator.comparing;

@Entity
@Data
@Builder
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
public class Auto24Item extends PanacheEntityBase {
  @Id
  @GeneratedValue
  public Long id;
  private long externalId;
  private LocalDateTime insertDate;
  private String link;
  private String imgLink;
  private String mark;
  private String model;
  private String engine;
  private Double power;
  private Integer year;
  private String fuelType;
  private String transmissionType;
  private boolean auction;

  @OneToMany(mappedBy = "auto24Item", cascade = CascadeType.PERSIST)
  private List<Auto24ChangeItem> changeItems;

  @Transient
  public Auto24ChangeItem getLatestChangeItem() {
    return changeItems.stream()
      .max(comparing(Auto24ChangeItem::getInsertDate))
      .orElseThrow();
  }
}
