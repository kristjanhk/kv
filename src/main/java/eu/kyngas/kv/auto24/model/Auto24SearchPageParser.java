package eu.kyngas.kv.auto24.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.List;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static java.lang.Double.parseDouble;
import static java.lang.Integer.parseInt;

@Slf4j
public class Auto24SearchPageParser {

  public static List<Auto24SearchPageItem> parse(Document document) {
    return document.getElementsByClass("result-row").stream()
      .map(element -> {
        Description desc = getDescription(element);
        return Auto24SearchPageItem.builder()
          .id(getId(element))
          .imgLink(getImgLink(element))
          .mark(desc.getMark())
          .model(desc.getModel())
          .engine(desc.getEngine())
          .power(desc.getPower())
          .year(getYear(element))
          .fuelType(getFuelType(element))
          .transmissionType(getTransmissionType(element))
          .price(getPrice(element))
          .auction(isAuction(element))
          .build();
      })
      .collect(Collectors.toList());
  }

  public static int getItemsTotal(Document document) {
    Element paginator = document.getElementsByClass("paginator").first();
    return parseInt(paginator.getElementsByTag("strong").text());
  }

  private static Description getDescription(Element element) {
    Element el = element.getElementsByClass("make_and_model").first();
    return Description.parse(el.getElementsByTag("a").first().text());
  }

  private static Integer getId(Element element) {
    Element el = element.getElementsByClass("small-image").first();
    return logEx(el, () -> {
      String link = el.attributes().get("href");
      String id = link.split("/")[2].strip();
      return parseInt(id);
    });
  }

  private static String getImgLink(Element element) {
    Element el = element.getElementsByClass("small-image").first();
    return logEx(el, () -> el.getElementsByTag("img").first().attr("src"));
  }

  private static Integer getYear(Element element) {
    Element el = element.getElementsByClass("year").first();
    return logEx(el, () -> parseInt(el.text().strip()));
  }

  private static String getFuelType(Element element) {
    Element el = element.getElementsByClass("fuel").first();
    return logEx(el, () -> el.text().strip());
  }

  private static String getTransmissionType(Element element) {
    Element el = element.getElementsByClass("transmission").first();
    return logEx(el, () -> el.text().strip());
  }

  private static Double getPrice(Element element) {
    Element el = element.getElementsByClass("price").first();
    return logEx(el, () -> {
      String text = el.text().strip();
      if (text == null) {
        return null;
      }
      String priceText = text.replace("sis. KM", "")
        .replace("Hetkehind", "")
        .replaceAll(" ", "");
      return parseDouble(priceText);
    });
  }

  private static boolean isAuction(Element element) {
    return element.hasClass("auction");
  }

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  private static class Description {
    private static final Pattern PATTERN = Pattern.compile("(\\w+) (.*) (\\d\\.\\d[ \\w]*) (\\d+) kW");

    private String mark;
    private String model;
    private String engine;
    private double power;

    private static Description parse(String input) {
      Matcher matcher = PATTERN.matcher(input);
      return !matcher.matches() ? new Description() : new Description(matcher.group(1),
                                                                      matcher.group(2),
                                                                      matcher.group(3),
                                                                      parseDouble(matcher.group(4)));
    }
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
