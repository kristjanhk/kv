package eu.kyngas.kv.kv.client;

import eu.kyngas.kv.kv.model.Kv;
import eu.kyngas.kv.util.Page;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jsoup.Jsoup;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static eu.kyngas.kv.util.Predicates.distinctBy;

@ApplicationScoped
public class KvClientService {
  @Inject
  @RestClient
  KvClient kvClient;

  public Page<List<Kv>> getSearchFeed(KvClientParams params) {
    return new Page<>(1,
                      null,
                      index -> Jsoup.parse(kvClient.getSearchPage(params, index)),
                      KvSearchPageParser::getPageCount,
                      index -> index + 1,
                      document -> KvSearchPageParser.parse(params.getType(), document));
  }

  public List<Kv> getAllSearchItems(KvClientParams kvClientParams) {
    return getSearchFeed(kvClientParams).collect().stream()
      .flatMap(Collection::stream)
      .filter(distinctBy(Kv::getExtId))
      .collect(Collectors.toList());
  }
}
