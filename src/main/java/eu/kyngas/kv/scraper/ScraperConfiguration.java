package eu.kyngas.kv.scraper;

import io.quarkus.arc.config.ConfigProperties;
import lombok.Data;

@Data
@ConfigProperties
public class ScraperConfiguration {
  private Boolean enabled = true;
}
