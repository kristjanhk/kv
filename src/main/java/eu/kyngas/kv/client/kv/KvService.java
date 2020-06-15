package eu.kyngas.kv.client.kv;

import eu.kyngas.kv.client.kv.model.KvSearchPageItem;
import eu.kyngas.kv.client.kv.model.KvSearchPageParser;
import eu.kyngas.kv.client.kv.rss.model.Rss;
import eu.kyngas.kv.client.model.Page;
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

  public Rss getRssFeed(KvParams kvParams) {
    return kvClient.getRssFeed("rss.objectsearch", kvParams.toEncodedQueryParam());
  }

  public Page<List<KvSearchPageItem>> getSearchFeed(KvParams kvParams) {
    String type = kvParams.getType();
    return new Page<>(1,
                      null,
                      index -> Jsoup.parse(kvClient.getSearchPage(kvParams, index)),
                      KvSearchPageParser::getPageCount,
                      document -> KvSearchPageParser.parse(document, type));
  }

  public List<KvSearchPageItem> getAllSearchItems(KvParams kvParams) {
    return getSearchFeed(kvParams).collect().stream()
      .flatMap(Collection::stream)
      .filter(distinctBy(KvSearchPageItem::getId))
      .collect(Collectors.toList());
  }
}
