package eu.kyngas.kv.kv.model;

import eu.kyngas.kv.util.StringUtil;
import lombok.Builder;
import lombok.Data;
import lombok.SneakyThrows;

import javax.validation.constraints.NotNull;
import javax.xml.bind.DatatypeConverter;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Data
@Builder
public class Kv {
  private Long id;
  @NotNull
  public Long extId;

  @NotNull
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

  @SneakyThrows
  public String getUniqueId() {
    MessageDigest md = MessageDigest.getInstance("MD5");
    md.update(getUniqueIdRaw().getBytes(StandardCharsets.UTF_8));
    return DatatypeConverter.printHexBinary(md.digest()).toLowerCase();
  }

  @SneakyThrows
  public String getChangeId() {
    MessageDigest md = MessageDigest.getInstance("MD5");
    md.update(getChangeIdRaw().getBytes(StandardCharsets.UTF_8));
    return DatatypeConverter.printHexBinary(md.digest()).toLowerCase();
  }

  /**
   * Must be consistent with kv table unique_id column
   */
  String getUniqueIdRaw() {
    return Stream.of(type, county, area, district, address, rooms, roomSize, floor)
      .filter(Objects::nonNull)
      .map(String::valueOf)
      .collect(Collectors.joining());
  }

  /**
   * Must be consistent with kv table change_id column
   */
  String getChangeIdRaw() {
    return Stream.of(StringUtil.booleanShort(booked), StringUtil.booleanShort(removed), imgLink, price, pricePerM2,
                     county, area, district, address, rooms, roomSize, floor, floorTotal, year)
      .filter(Objects::nonNull)
      .map(String::valueOf)
      .collect(Collectors.joining());
  }
}
