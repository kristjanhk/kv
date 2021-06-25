package eu.kyngas.kv.core.logging;

import eu.kyngas.kv.core.config.KvConfig;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.client.ClientResponseContext;
import javax.ws.rs.client.ClientResponseFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.Providers;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Provider
@ApplicationScoped
public class ClientLoggingFilter implements ClientRequestFilter, ClientResponseFilter {
  private static final String METHOD = "org.eclipse.microprofile.rest.client.invokedMethod";

  @Context
  Providers providers;
  @Inject
  KvConfig kvConfig;

  @Override
  public void filter(ClientRequestContext ctx) throws IOException {
    if (!kvConfig.isLogClient()) {
      return;
    }
    LogRequest logRequest = LogRequest.builder()
      .prefix("|>>")
      .method(ctx.getMethod())
      .url(ctx.getUri().toString())
      .resource(getResource(ctx))
      .headers(ctx.getStringHeaders())
      .queryParams(Map.of())
      .body(new String(getRequestBody(ctx, providers)))
      .build();
    log.info("Client outgoing request: \n{}", Templates.request(logRequest).render());
  }

  protected static byte[] getRequestBody(ClientRequestContext ctx, Providers providers) throws IOException {
    if (!ctx.hasEntity()) {
      return new byte[0];
    }
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    MessageBodyWriter<?> writer = providers.getMessageBodyWriter(ctx.getEntityClass(),
                                                                 ctx.getEntityType(),
                                                                 ctx.getEntityAnnotations(),
                                                                 ctx.getMediaType());
    if (ctx.getEntity() instanceof InputStream) {
      byte[] body = buffer((InputStream) ctx.getEntity());
      ctx.setEntity(new ByteArrayInputStream(body));
      return body;
    }

    writer.writeTo(cast(ctx.getEntity()),
                   ctx.getEntityClass(),
                   ctx.getEntityType(),
                   ctx.getEntityAnnotations(),
                   withFormatted(ctx.getMediaType()),
                   ctx.getHeaders(),
                   baos);
    return baos.toByteArray();
  }

  private static MediaType withFormatted(MediaType in) {
    HashMap<String, String> params = new HashMap<>(in.getParameters());
    params.put("formatted", "true");
    return new MediaType(in.getType(), in.getType(), params);
  }

  @Override
  public void filter(ClientRequestContext requestContext, ClientResponseContext resCtx) throws IOException {
    if (!kvConfig.isLogClient()) {
      return;
    }
    LogResponse logResponse = LogResponse.builder()
      .prefix("|<<")
      .statusCode(resCtx.getStatus())
      .statusMessage(resCtx.getStatusInfo().getReasonPhrase())
      .headers(resCtx.getHeaders())
      .body(new String(getResponseBody(resCtx)))
      .build();
    log.info("Client incoming response: \n{}", Templates.response(logResponse).render());
  }

  protected static byte[] getResponseBody(ClientResponseContext ctx) throws IOException {
    if (!ctx.hasEntity()) {
      return new byte[0];
    }
    byte[] body = buffer(ctx.getEntityStream());
    ctx.setEntityStream(new ByteArrayInputStream(body));
    return body;
  }

  private static byte[] buffer(InputStream is) throws IOException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    is.transferTo(baos);
    return baos.toByteArray();
  }

  private String getResource(ClientRequestContext ctx) {
    Method method = (Method) ctx.getProperty(METHOD);
    return method.toGenericString();
  }

  @SuppressWarnings("unchecked")
  private static <T> T cast(Object object) {
    return (T) object;
  }
}
