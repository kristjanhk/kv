package eu.kyngas.kv.auto24.service;

import eu.kyngas.kv.auto24.model.Auto24ClientParams;
import eu.kyngas.kv.auto24.rest.Auto24Client;
import eu.kyngas.kv.auto24.model.Auto24SearchPageItem;
import eu.kyngas.kv.auto24.model.Auto24SearchPageParser;
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
public class Auto24Service {
  @Inject
  @RestClient
  Auto24Client auto24Client;

  public Page<List<Auto24SearchPageItem>> getSearchFeed(Auto24ClientParams auto24ClientParams) {
    return new Page<>(0,
                      null,
                      index -> Jsoup.parse(auto24Client.getSearchPage(auto24ClientParams, index)),
                      Auto24SearchPageParser::getItemsTotal,
                      index -> index + auto24ClientParams.getShowTotal(),
                      Auto24SearchPageParser::parse);
  }

  public List<Auto24SearchPageItem> getAllSearchItems(Auto24ClientParams auto24ClientParams) {
    return getSearchFeed(auto24ClientParams).collect().stream()
      .flatMap(Collection::stream)
      .filter(distinctBy(Auto24SearchPageItem::getId))
      .collect(Collectors.toList());
  }

}
