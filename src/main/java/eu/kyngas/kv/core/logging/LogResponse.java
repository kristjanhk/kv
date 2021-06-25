package eu.kyngas.kv.core.logging;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Builder
public class LogResponse {
  private String prefix;
  private int statusCode;
  private String statusMessage;
  private Map<String, List<String>> headers;
  private String body;
}
