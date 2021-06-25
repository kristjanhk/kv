package eu.kyngas.kv.core.logging;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Builder
public class LogRequest {
  private String prefix;
  private String method;
  private String url;
  private String resource;
  private Map<String, List<String>> headers;
  private Map<String, List<String>> queryParams;
  private String body;
}
