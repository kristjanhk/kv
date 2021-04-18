package eu.kyngas.kv.auto24.service;

import eu.kyngas.kv.auto24.model.Auto24Item;
import eu.kyngas.kv.auto24.model.Auto24SearchPageItem;
import io.quarkus.narayana.jta.runtime.TransactionConfiguration;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.List;

import static eu.kyngas.kv.auto24.model.Auto24ClientParams.createLexusParams;
import static java.util.stream.Collectors.toList;

@Slf4j
@ApplicationScoped
public class Auto24ScraperService {
  @Inject
  Auto24Service auto24Service;
  @Inject
  Auto24ChangesService auto24ChangesService;

  // @Scheduled(cron = "0 0 * ? * *") // every hour
  //@Scheduled(every = "6000s")
  @Transactional
  @TransactionConfiguration(timeout = 600) // 10min
  void scrapeLexus() {
    List<Auto24Item> items = auto24Service.getAllSearchItems(createLexusParams()).stream()
      .map(Auto24SearchPageItem::toAuto24Item)
      .collect(toList());
    int count = auto24ChangesService.check(items, Auto24Item.listAll());

    log.info("Saved {} items", count);
  }
}
