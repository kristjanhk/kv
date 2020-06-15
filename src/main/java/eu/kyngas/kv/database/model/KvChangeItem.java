package eu.kyngas.kv.database.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@Builder
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
public class KvChangeItem extends PanacheEntityBase {
  @Id
  @GeneratedValue
  @EqualsAndHashCode.Exclude
  private Long id;
  @EqualsAndHashCode.Exclude
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss dd.MM.yyyy")
  private LocalDateTime publishDate;
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss dd.MM.yyyy")
  @EqualsAndHashCode.Exclude
  private LocalDateTime insertDate;
  private String imgLink;
  private Double price;
  private Double pricePerM2;

  @JsonIgnore
  @ToString.Exclude
  @EqualsAndHashCode.Exclude
  @ManyToOne
  private KvItem kvItem;
}
