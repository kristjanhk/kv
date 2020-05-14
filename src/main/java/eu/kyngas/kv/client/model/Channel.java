package eu.kyngas.kv.client.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import java.util.List;
import lombok.Data;

@Data
public class Channel {
  private String title;
  private String description;
  private String link;
  @JacksonXmlElementWrapper(useWrapping = false)
  private List<Item> item;
}
