package eu.kyngas.kv.kv.model.rss;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import lombok.Data;

import java.util.List;

@Data
public class Channel {
  private String title;
  private String description;
  private String link;
  @JacksonXmlElementWrapper(useWrapping = false)
  private List<Item> item;
}
