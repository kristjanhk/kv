package eu.kyngas.kv.kv.client;

import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.faulttolerance.Timeout;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.ws.rs.BeanParam;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

@RegisterRestClient(configKey = "kv.kv")
public interface KvClient {

  @GET
  @Produces(MediaType.TEXT_HTML)
  @Retry
  @Timeout(5000)
  String getSearchPage(@BeanParam KvClientParams kvClientParams, @QueryParam("page") int page);
}
