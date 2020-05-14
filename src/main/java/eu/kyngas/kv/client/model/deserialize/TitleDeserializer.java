package eu.kyngas.kv.model.deserialize;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import eu.kyngas.kv.model.Title;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TitleDeserializer extends StdDeserializer<Title> {
  private static final Pattern YYR_PATTERN = Pattern.compile("Anda üürile korter, (\\d) tuba (.*), ([0-9. ]+) EUR");
  private static final Pattern MYYK_PATTERN = Pattern.compile("Müüa korter, (\\d) tuba (.*), ([0-9. ]+) EUR");

  protected TitleDeserializer() {
    super(Title.class);
  }

  @Override
  public Title deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
    String input = ctxt.readValue(p, String.class).replace('\u00A0',' ');
    Matcher matcher = getValidMatcher(input);
    if (matcher == null) {
      return null;
    }

    Title title = new Title();
    title.setRooms(Integer.parseInt(matcher.group(1)));
    title.setAddress(matcher.group(2));
    title.setPrice(Double.parseDouble(matcher.group(3).replaceAll(" ", "")));

    return title;
  }

  private Matcher getValidMatcher(String input) {
    Matcher matcher = YYR_PATTERN.matcher(input);
    if (matcher.matches()) {
      return matcher;
    }
    matcher = MYYK_PATTERN.matcher(input);
    if (matcher.matches()) {
      return matcher;
    }
    return null;
  }
}
