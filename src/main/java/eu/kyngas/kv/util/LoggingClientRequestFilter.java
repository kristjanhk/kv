package eu.kyngas.kv.util;

import lombok.extern.slf4j.Slf4j;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;

@Slf4j
public class LoggingClientRequestFilter implements ClientRequestFilter {

  @Override
  public void filter(ClientRequestContext ctx) {
    log.debug("Request uri: {}", ctx.getUri());
  }
}
