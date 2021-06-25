package eu.kyngas.kv.kv.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
public enum KvType {
  APARTMENT_SALE(1, true, true,true),
  APARTMENT_RENT(2, true, false, true),
  APARTMENT_SHORT_RENT(30, true, false, false),
  APARTMENT_ALL_RENT(20, true, false, false),
  HOUSE_SALE(3, false, true, true),
  HOUSE_RENT(4, false, false, false),
  HOUSE_SHORT_RENT(31, false, false, false),
  HOUSE_ALL_RENT(21, false, false, false);

  private final int id;
  private final boolean apartment;
  private final boolean sale;
  private final boolean enabled;

  public static List<KvType> getEnabled() {
    return Arrays.stream(values()).filter(e -> e.enabled).collect(Collectors.toList());
  }
}
