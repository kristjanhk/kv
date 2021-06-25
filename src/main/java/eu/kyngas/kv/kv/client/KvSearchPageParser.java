package eu.kyngas.kv.kv.client;

import eu.kyngas.kv.kv.model.Kv;
import eu.kyngas.kv.kv.model.KvCounty;
import eu.kyngas.kv.kv.model.KvType;
import eu.kyngas.kv.util.NumberUtil;
import eu.kyngas.kv.util.StringUtil;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class KvSearchPageParser {
  private static final String KV_LINK = "https://www.kv.ee/";

  public static List<Kv> parse(KvType type, Document document) {
    String objectType = type.isApartment() ? "object-type-apartment" : "object-type-house";
    return document.getElementsByClass(objectType + " object-item").stream()
      .map(element -> {
        Location location = getLocation(getAddress(element));
        List<String> details = getDetails(element);
        long extId = getExtId(element);
        Integer[] floor = getFloor(type, details);

        return Kv.builder()
          .extId(extId)
          .type(type)
          .publishDate(getPublishDate(element))
          .booked(getBooked(element))
          .removed(false)
          .link(KV_LINK + extId)
          .imgLink(getImgLink(element))
          .price(getPrice(element))
          .pricePerM2(getPricePerM2(element))
          .county(location.getCounty())
          .area(location.getArea())
          .district(location.getDistrict())
          .address(location.getAddress())
          .rooms(getRooms(element))
          .roomSize(getRoomSize(element))
          .floor(floor == null ? null : floor[0])
          .floorTotal(floor == null ? null : floor[1])
          .year(getYear(details))
          .details(String.join(", ", details))
          .description(getDescription(element))
          .build();
      })
      .collect(Collectors.toList());
  }

  public static int getPageCount(Document document) {
    Elements elements = document.getElementsByClass("jump-pagination-list");
    if (elements.isEmpty()) {
      return 1;
    }
    return elements.first()
      .children().stream()
      .flatMap(child -> child.children().stream())
      .filter(child -> child.hasClass("count"))
      .findAny()
      .map(child -> Integer.parseInt(child.text()))
      .orElse(1);
  }

  static long getExtId(Element element) {
    return Long.parseLong(element.attr("id"));
  }

  static LocalDateTime getPublishDate(Element element) {
    Element el = element.getElementsByClass("object-added-date").first();
    return logEx(el, () -> {
      String timeAfter = el.text().strip();
      if (timeAfter.isEmpty()) {
        return LocalDateTime.now();
      }

      timeAfter = timeAfter.replace(" tagasi", "");
      int amount = Integer.parseInt(timeAfter.split(" ")[0]);
      if (timeAfter.endsWith("h")) {
        return LocalDateTime.now().minusHours(amount);
      }
      if (timeAfter.endsWith("p")) {
        return LocalDateTime.now().minusDays(amount);
      }
      if (timeAfter.endsWith("min")) {
        return LocalDateTime.now().minusMinutes(amount);
      }
      throw new IllegalStateException("Unknown date type: " + timeAfter);
    });
  }

  static Boolean getBooked(Element element) {
    Element el = element.getElementsByClass("object-title").first();
    return logEx(el, () -> el.children().stream()
      .filter(child -> !child.hasClass("object-title-a") && child.hasText())
      .map(child -> child.text().strip())
      .anyMatch("(Broneeritud)"::equals));
  }

  static String getImgLink(Element element) {
    Element el = element.getElementsByClass("object-photo").first();
    return logEx(el, () -> el.getElementsByTag("img").first().attr("data-original"));
  }

  static BigDecimal getPrice(Element element) {
    Element el = element.getElementsByClass("object-price-value").first();
    return logEx(el, () -> Optional.of(el.text().strip())
      .filter(s -> !s.isEmpty())
      .map(s -> NumberUtil.bigDecimal(s.replaceAll("\\D", "")))
      .orElseThrow());
  }

  static BigDecimal getPricePerM2(Element element) {
    Element el = element.getElementsByClass("object-m2-price").first();
    return logEx(el, () -> Optional.of(el.text().strip())
      .filter(s -> !s.isEmpty())
      .map(s -> NumberUtil.bigDecimal(s.replaceAll("\\D", "")))
      .orElse(null));
  }

  @Data
  static class Location {
    private KvCounty county;
    private String area;
    private String district;
    private String address;
    private String roomNr;
  }

  static Location getLocation(String input) {
    List<String> parts = Arrays.stream(input.strip().split(","))
      .map(String::strip)
      .filter(s -> !s.equals(s.toUpperCase()))
      .map(StringUtil::capitalize)
      .collect(Collectors.toList());

    Location result = new Location();
    result.setCounty(KvCounty.of(parts.get(0)));
    result.setArea(parts.get(1));

    if (parts.size() == 3) {
      if (parts.get(2).matches(".*\\d.*")) {
        return fillAddress(result, parts.get(2));
      }
      return result.setDistrict(parts.get(2));
    }

    result.setDistrict(parts.get(parts.size() - 2));
    return fillAddress(result, parts.get(parts.size() - 1));
  }

  private static Location fillAddress(Location location, String input) {
    input = input.replaceAll("\\.", "");
    input = input.replaceFirst("\\s(tn|tee)", "");
    input = input.replaceFirst("\\smaantee", " mnt");
    input = input.replaceFirst("\\spuiestee", " pst");

    // TODO: KristjanK parse roomNr

    return location.setAddress(input);
  }

  static Integer getRooms(Element element) {
    Element el = element.getElementsByClass("object-rooms").first();
    return logEx(el, () -> Integer.parseInt(el.text()));
  }

  static BigDecimal getRoomSize(Element element) {
    Element el = element.getElementsByClass("object-m2").first();
    return logEx(el, () -> Optional.of(el.text().strip())
      .filter(s -> !s.isEmpty())
      .map(s -> NumberUtil.bigDecimal(s.split(" ")[0]))
      .orElse(null));
  }

  static Integer[] getFloor(KvType type, List<String> details) {
    return logEx(details, () -> {
      if (type.isApartment()) {
        String korrus = details.stream().filter(d -> d.contains("korrus")).findFirst().orElse(null);
        if (korrus != null && korrus.startsWith("korrus ")) {  // korrus 3/5
          String[] split = korrus.split(" ")[1].split("/");
          return new Integer[]{Integer.parseInt(split[0]), Integer.parseInt(split[1])};
        }
        if (korrus != null && korrus.endsWith(" korrus")) { // 1. korrus
          return new Integer[]{Integer.parseInt(korrus.split("\\.")[0]), null};
        }
        return null;
      }

      // 2 korrust
      return details.stream()
        .filter(detail -> detail.endsWith(" korrust"))
        .findFirst()
        .map(detail -> new Integer[]{null, Integer.parseInt(detail.split(" ")[0])})
        .orElse(null);
    });
  }

  static Integer getYear(List<String> details) {
    return details.stream()
      .filter(detail -> detail.startsWith("ehitusaasta"))
      .findFirst()
      .map(detail -> Integer.parseInt(detail.split(" ")[1]))
      .orElse(null);
  }

  static String getDescription(Element element) {
    Element el = element.getElementsByClass("object-excerpt").last();
    return logEx(el, () -> el.text().strip());
  }

  static String getAddress(Element element) {
    Element el = element.getElementsByClass("object-title-a").first();
    return logEx(el, () -> el.text().strip());
  }

  static List<String> getDetails(Element element) {
    Element el = element.getElementsByClass("object-excerpt").first();
    return logEx(el, () -> Arrays.stream(el.text().split(","))
      .map(s -> s.strip().toLowerCase())
      .filter(s -> !s.startsWith("..."))
      .collect(Collectors.toList()));
  }

  static <T> T logEx(Object data, Supplier<T> supplier) {
    try {
      return supplier.get();
    } catch (Exception e) {
      log.error("Ex {} using {}", e.getMessage(), data, e);
      return null;
    }
  }
}
