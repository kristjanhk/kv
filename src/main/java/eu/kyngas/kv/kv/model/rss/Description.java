package eu.kyngas.kv.kv.model.rss;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import eu.kyngas.kv.kv.model.rss.deserialize.DescriptionDeserializer;
import lombok.Data;

@Data
@JsonDeserialize(using = DescriptionDeserializer.class)
public class Description {
  private String imgLink;
  private DescParams params;
  private String text;
}
