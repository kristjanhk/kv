package eu.kyngas.kv.auto24.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.time.LocalDateTime;

@Entity
@Data
@Builder
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
public class Auto24ChangeItem extends PanacheEntityBase {
  @Id
  @GeneratedValue
  @EqualsAndHashCode.Exclude
  private Long id;
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss dd.MM.yyyy")
  @EqualsAndHashCode.Exclude
  private LocalDateTime insertDate;
  private Double price;

  @JsonIgnore
  @ToString.Exclude
  @EqualsAndHashCode.Exclude
  @ManyToOne
  private Auto24Item auto24Item;
}
