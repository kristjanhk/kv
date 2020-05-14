package eu.kyngas.kv.database.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Transient;
import java.time.LocalDateTime;
import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;

@Builder
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
public class KvItem extends PanacheEntityBase {
  @Id
  @GeneratedValue
  @EqualsAndHashCode.Exclude
  private Long id;
  private int kvId;
  private int prevKvId;
  private String kvType;
  private String link;
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss dd.MM.yyyy")
  private LocalDateTime publishDate;
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss dd.MM.yyyy")
  @EqualsAndHashCode.Exclude
  private LocalDateTime insertDate;
  private String address;
  private String imgLink;
  private Double price;
  private Double pricePerM2;
  private int rooms;
  private Double roomSize;
  private Integer roomFloor;
  private Integer totalFloor;
  private boolean removed;

  @Transient
  public String getUniqueId() {
    return Stream.of(address, rooms, roomSize, roomFloor)
      .map(String::valueOf)
      .collect(joining("-"));
  }
}
