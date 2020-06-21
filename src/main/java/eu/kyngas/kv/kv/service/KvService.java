package eu.kyngas.kv.kv.service;

import eu.kyngas.kv.kv.rest.KvClient;
import eu.kyngas.kv.kv.model.KvClientParams;
import eu.kyngas.kv.kv.model.KvSearchPageItem;
import eu.kyngas.kv.kv.model.KvSearchPageParser;
import eu.kyngas.kv.kv.model.rss.Rss;
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
public class KvService {
  @Inject
  @RestClient
  KvClient kvClient;

  public Rss getRssFeed(KvClientParams kvClientParams) {
    return kvClient.getRssFeed("rss.objectsearch", kvClientParams.toEncodedQueryParam());
  }

  public Page<List<KvSearchPageItem>> getSearchFeed(KvClientParams kvClientParams) {
    String type = kvClientParams.getType();
    return new Page<>(1,
                      null,
                      index -> Jsoup.parse(kvClient.getSearchPage(kvClientParams, index)),
                      KvSearchPageParser::getPageCount,
                      index -> index + 1,
                      document -> KvSearchPageParser.parse(document, type));
  }

  public List<KvSearchPageItem> getAllSearchItems(KvClientParams kvClientParams) {
    return getSearchFeed(kvClientParams).collect().stream()
      .flatMap(Collection::stream)
      .filter(distinctBy(KvSearchPageItem::getId))
      .collect(Collectors.toList());
  }
}
