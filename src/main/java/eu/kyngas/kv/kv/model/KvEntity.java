package eu.kyngas.kv.kv.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "kv")
public class KvEntity {
  public static final String PRICE = "price";

  @Id
  @GeneratedValue
  private Long id;
  @NotNull
  private Long extId;
  @Generated(GenerationTime.ALWAYS)
  private String uniqueId;
  @Generated(GenerationTime.ALWAYS)
  private String changeId;

  @NotNull
  @Enumerated(EnumType.STRING)
  private KvType type;
  @NotNull
  private LocalDateTime publishDate;
  @NotNull
  private Boolean booked;
  @NotNull
  private Boolean removed;

  @NotNull
  private String link;
  @NotNull
  private String imgLink;

  @NotNull
  private BigDecimal price;
  private BigDecimal pricePerM2;

  @NotNull
  @Enumerated(EnumType.STRING)
  private KvCounty county;
  @NotNull
  private String area;
  private String district;
  private String address;

  @NotNull
  private Integer rooms;
  private BigDecimal roomSize;
  private Integer floor;
  private Integer floorTotal;

  private Integer year;
  private String details;
  private String description;
}
