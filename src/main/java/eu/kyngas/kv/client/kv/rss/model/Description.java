package eu.kyngas.kv.client.kv.rss.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import eu.kyngas.kv.client.kv.rss.model.deserialize.DescriptionDeserializer;
import lombok.Data;

@Data
@JsonDeserialize(using = DescriptionDeserializer.class)
public class Description {
  private String imgLink;
  private DescParams params;
  private String text;
}
