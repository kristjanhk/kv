package eu.kyngas.kv.kv.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum KvCounty {
  HARJUMAA(1, "Harjumaa"),
  HIIUMAA(2, "Hiiumaa"),
  IDA_VIRUMAA(3, "Ida-Virumaa"),
  JOGEVAMAA(4, "Jõgevamaa"),
  JARVAMAA(5, "Järvamaa"),
  LAANEMAA(6, "Läänemaa"),
  LAANE_VIRUMAA(7, "Lääne-Virumaa"),
  POLVAMAA(8, "Põlvamaa"),
  PARNUMAA(9, "Pärnumaa"),
  RAPLAMAA(10, "Raplamaa"),
  SAAREMAA(11, "Saaremaa"),
  TARTUMAA(12, "Tartumaa"),
  VALGAMAA(13, "Valgamaa"),
  VILJANDIMAA(14, "Viljandimaa"),
  VORUMAA(15, "Võrumaa"),
  //
  ;

  private final int id;
  private final String name;

  public KvCounty next() {
    int nextId = id == 15 ? 1 : id + 1;
    return Arrays.stream(KvCounty.values())
      .filter(county -> county.getId() == nextId)
      .findFirst()
      .orElseThrow();
  }

  public static KvCounty of(String name) {
    return Arrays.stream(KvCounty.values())
      .filter(county -> county.getName().equals(name))
      .findFirst()
      .orElseThrow();
  }
}
