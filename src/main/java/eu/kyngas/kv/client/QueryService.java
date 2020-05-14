package eu.kyngas.kv.client;

import eu.kyngas.kv.client.model.Rss;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class QueryService {
  @Inject
  @RestClient
  QueryClient queryClient;

  public Rss query(Params params) {
    return queryClient.query("rss.objectsearch", params.toQueryParam());
  }
}
