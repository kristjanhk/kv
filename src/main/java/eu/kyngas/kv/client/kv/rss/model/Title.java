package eu.kyngas.kv.client.kv.rss.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import eu.kyngas.kv.client.kv.rss.model.deserialize.TitleDeserializer;
import lombok.Data;

@Data
@JsonDeserialize(using = TitleDeserializer.class)
public class Title {
  private int rooms;
  private String address;
  private double price;
}
