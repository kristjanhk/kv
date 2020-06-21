package eu.kyngas.kv.kv.model.rss;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import eu.kyngas.kv.kv.model.rss.deserialize.TitleDeserializer;
import lombok.Data;

@Data
@JsonDeserialize(using = TitleDeserializer.class)
public class Title {
  private int rooms;
  private String address;
  private double price;
}
