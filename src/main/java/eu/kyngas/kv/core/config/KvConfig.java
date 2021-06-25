package eu.kyngas.kv.core.config;

import io.quarkus.arc.config.ConfigProperties;
import lombok.Data;

@Data
@ConfigProperties
public class KvConfig {
  private boolean fullScrapeOnStart = false;
  private boolean logClient = false;
}
