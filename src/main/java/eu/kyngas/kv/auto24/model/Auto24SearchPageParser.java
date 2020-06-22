package eu.kyngas.kv.auto24.model;

import lombok.Data;
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
    return logEx(el, () -> {
      String text = el.text().strip();
      return text == null || text.isEmpty() ? null : parseInt(text);
    });
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
  private static class Description {
    private static final Pattern POWER_PATTERN = Pattern.compile(" (\\d+) kW");
    private static final Pattern ENGINE_PATTERN = Pattern.compile(" (\\d\\.\\d[- \\w]*)");
    private static final Pattern PATTERN = Pattern.compile("(\\w+) (.*)");

    private String mark;
    private String model;
    private String engine;
    private Double power;

    private static Description parse(String input) {
      Description description = new Description();

      Matcher powerMatcher = POWER_PATTERN.matcher(input);
      if (powerMatcher.find()) {
        description.setPower(parseDouble(powerMatcher.group(1)));
        input = input.replaceAll(POWER_PATTERN.pattern(), "");
      }

      Matcher engineMatcher = ENGINE_PATTERN.matcher(input);
      if (engineMatcher.find()) {
        description.setEngine(engineMatcher.group(1));
        input = input.replaceAll(ENGINE_PATTERN.pattern(), "");
      }

      Matcher matcher = PATTERN.matcher(input);
      if (matcher.matches()) {
        description.setMark(matcher.group(1)).setModel(matcher.group(2));
      }

      return description;
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
