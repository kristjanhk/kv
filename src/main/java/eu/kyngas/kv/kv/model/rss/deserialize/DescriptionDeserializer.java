package eu.kyngas.kv.kv.model.rss.deserialize;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import eu.kyngas.kv.kv.model.rss.DescParams;
import eu.kyngas.kv.kv.model.rss.Description;
import org.apache.commons.text.StringEscapeUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.TextNode;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class DescriptionDeserializer extends StdDeserializer<Description> {
  protected DescriptionDeserializer() {
    super(Description.class);
  }

  @Override
  public Description deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
    String input = ctxt.readValue(p, String.class);
    Document doc = Jsoup.parse(input);

    Description desc = new Description();
    desc.setImgLink(doc.select("a > img").first().attr("src"));
    desc.setParams(getParams(doc));
    desc.setText(getText(doc));

    return desc;
  }

  private DescParams getParams(Document doc) {
    List<String> input = doc.selectFirst("tr")
                             .select("td")
                             .last()
                             .textNodes()
                             .stream().reduce((a, b) -> b)
                             .map(node -> StringEscapeUtils.unescapeHtml4(node.toString()))
                             .map(s -> s.split(", "))
                             .map(Arrays::asList)
                             .orElse(null);

    DescParams descParams = new DescParams();
    descParams.setPricePerM2(getPricePer(input));
    descParams.setRoomSize(getSize(input));
    setFloors(descParams, input);
    descParams.setOther(input);

    return descParams;
  }

  private Double getPricePer(List<String> input) {
    Pattern pattern = Pattern.compile("(\\d{2,}) EUR (\\d+.\\d{2}) EUR/m2");
    return matchRemove(input, pattern, m -> Double.valueOf(m.group(2)));
  }

  private Double getSize(List<String> input) {
    Pattern pattern = Pattern.compile("(\\d+(,\\d)?) m²");
    return matchRemove(input, pattern, m -> Double.valueOf(m.group(1).replace(',', '.')));
  }

  private void setFloors(DescParams params, List<String> input) {
    Pattern pattern = Pattern.compile("(\\d)/(\\d)");
    matchRemove(input, pattern, m -> {
      params.setRoomFloor(Integer.parseInt(m.group(1)));
      params.setTotalFloor(Integer.parseInt(m.group(2)));
      return null;
    });
  }

  private <T> T matchRemove(List<String> input, Pattern pattern, Function<Matcher, T> mapper) {
    for (String s : input) {
      Matcher matcher = pattern.matcher(s);
      if (matcher.find()) {
        return mapper.apply(matcher);
      }
    }
    return null;
  }

  private String getText(Document doc) {
    return doc.select("td")
        .last()
        .textNodes()
        .stream()
        .filter(t -> !t.isBlank())
        .map(TextNode::toString)
        .collect(Collectors.joining(",\n", "\n", ""));

  }
}
