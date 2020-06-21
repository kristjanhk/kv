package eu.kyngas.kv.kv.model;

import eu.kyngas.kv.util.StringUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Slf4j
public class KvSearchPageParser {

  public static List<KvSearchPageItem> parse(Document document, String type) {
    return document.getElementsByClass("object-type-apartment object-item").stream()
      .map(element -> {
        Location location = getLocation(getAddress(element));
        return KvSearchPageItem.builder()
          .id(getId(element))
          .type(type)
          .imgLink(getImgLink(element))
          .county(location.getCounty())
          .area(location.getArea())
          .district(location.getDistrict())
          .address(location.getAddress())
          .details(getDetails(element))
          .rooms(getRooms(element))
          .roomSize(getRoomSize(element))
          .roomFloor(getRoomFloor(element))
          .totalFloor(getTotalFloor(element))
          .publishDate(getPublishDate(element))
          .price(getPrice(element))
          .priceM2(getPriceM2(element))
          .build();
      })
      .collect(Collectors.toList());
  }

  public static int getPageCount(Document document) {
    return document.getElementsByClass("jump-pagination-list").first()
      .children().stream()
      .flatMap(child -> child.children().stream())
      .filter(child -> child.hasClass("count"))
      .findAny()
      .map(child -> Integer.parseInt(child.text()))
      .orElseThrow();
  }

  static int getId(Element element) {
    return Integer.parseInt(element.attr("id"));
  }

  static String getImgLink(Element element) {
    Element el = element.getElementsByClass("object-photo").first();
    return logEx(el, () ->  el.getElementsByTag("img").first().attr("data-original"));
  }

  static String getAddress(Element element) {
    Element el = element.getElementsByClass("object-title-a").first();
    return logEx(el, () -> el.text().strip());
  }

  static String getDetails(Element element) {
    Element el = element.getElementsByClass("object-excerpt").first();
    return logEx(el, () -> el.text().strip());
  }

  static Integer getRooms(Element element) {
    Element el = element.getElementsByClass("object-rooms").first();
    return logEx(el, () -> Integer.parseInt(el.text()));
  }

  static Double getRoomSize(Element element) {
    Element el = element.getElementsByClass("object-m2").first();
    return logEx(el, () -> {
      String text = el.text().strip();
      return text.isEmpty() ? null : Double.parseDouble(text.split(" ")[0]);
    });
  }

  static Integer getRoomFloor(Element element) {
    Element el = element.getElementsByClass("object-excerpt").first();
    return logEx(el, () -> el.text().startsWith("Korrus")
      ? Integer.parseInt(getFloorNumber(el).split("/")[0])
      : null);
  }

  private static String getFloorNumber(Element element) {
    return element.text().strip().split(",")[0].strip().split(" ")[1];
  }

  static Integer getTotalFloor(Element element) {
    Element el = element.getElementsByClass("object-excerpt").first();
    return logEx(el, () -> el.text().startsWith("Korrus")
      ? Integer.parseInt(getFloorNumber(el).split("/")[1])
      : null);
  }

  static LocalDateTime getPublishDate(Element element) {
    Element el = element.getElementsByClass("object-added-date").first();
    return logEx(el, () -> {
      LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.HOURS);
      String timeAfter = el.text().strip();
      if (timeAfter.isEmpty()) {
        return now;
      }

      timeAfter = timeAfter.replace(" tagasi", "");
      int amount = Integer.parseInt(timeAfter.split(" ")[0]);
      if (timeAfter.endsWith("h")) {
        return now.minusHours(amount);
      }
      if (timeAfter.endsWith("p")) {
        return now.minusDays(amount);
      }
      if (timeAfter.endsWith("min")) {
        return LocalDateTime.now().minusMinutes(amount).truncatedTo(ChronoUnit.HOURS);
      }
      return null;
    });
  }

  static Double getPrice(Element element) {
    Element el = element.getElementsByClass("object-price-value").first();
    return logEx(el, () -> {
      String text = el.text().strip();
      return Double.parseDouble(text.substring(0, text.length() - 1).replaceAll(" ", ""));
    });
  }

  static Double getPriceM2(Element element) {
    Element el = element.getElementsByClass("object-m2-price").first();
    return logEx(el, () -> {
      String text = el.text().strip();
      if (text.isEmpty()) {
        return null;
      }
      return Double.parseDouble(text.replace("€/m²", "").replaceAll(" ", ""));
    });
  }

  @Data
  static class Location {
    private String county;
    private String area;
    private String district;
    private String address;
  }

  static Location getLocation(String input) {
    List<String> parts = Arrays.stream(input.strip().split(", "))
      .map(String::strip)
      .filter(s -> !s.equals(s.toUpperCase()))
      .map(StringUtil::capitalize)
      .collect(Collectors.toList());

    Location result = new Location();
    result.setCounty(parts.get(0));
    result.setArea(parts.get(1));

    if (parts.size() == 3) {
      result.setDistrict(parts.get(2));
      return result;
    }

    result.setDistrict(parts.get(parts.size() - 2));
    result.setAddress(parts.get(parts.size() - 1));
    return result;
  }

  private static <T> T logEx(Element el, Supplier<T> supplier) {
    try {
      return supplier.get();
    } catch (Exception e) {
      log.error("Ex {} using element {}", e.getMessage(), el, e);
      return null;
    }
  }
}
