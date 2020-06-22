package eu.kyngas.kv.auto24.model;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.joining;

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
  public String getUniqueId() {
    return Stream.of(mark, model, year, engine, power, fuelType, transmissionType)
      .map(String::valueOf)
      .collect(joining(" - "));
  }

  @Transient
  public Auto24ChangeItem getLatestChangeItem() {
    return changeItems.stream()
      .max(comparing(Auto24ChangeItem::getInsertDate))
      .orElseThrow();
  }
}
