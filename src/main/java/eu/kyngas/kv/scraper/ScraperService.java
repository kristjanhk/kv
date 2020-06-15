package eu.kyngas.kv.scraper;

import eu.kyngas.kv.client.kv.KvParams;
import eu.kyngas.kv.client.kv.KvService;
import eu.kyngas.kv.client.kv.model.KvSearchPageItem;
import eu.kyngas.kv.database.ChangesService;
import eu.kyngas.kv.database.model.KvItem;
import io.quarkus.scheduler.Scheduled;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.List;

import static java.util.function.UnaryOperator.identity;
import static java.util.stream.Collectors.toList;

@Slf4j
@ApplicationScoped
public class ScraperService {
  @Inject
  ScraperConfiguration scraperConfiguration;
  @Inject
  KvService kvService;
  @Inject
  ChangesService changesService;

  @Scheduled(cron = "0 0 * ? * *") // every hour
  // @Scheduled(every = "60s")
  @Transactional
  void scrapeSales() {
    if (!scraperConfiguration.getEnabled()) {
      log.info("Sales scraper is disabled");
      return;
    }

    List<KvItem> items = kvService.getAllSearchItems(KvParams.createSaleParams(identity())).stream()
      .map(KvSearchPageItem::toKvItem)
      .collect(toList());
    int count = changesService.check(items, KvItem.listSales());

    log.info("Saved {} sale items", count);
  }

  @Scheduled(cron = "0 0 * ? * *") // every hour
  // @Scheduled(every = "60s")
  @Transactional
  void scrapeRents() {
    if (!scraperConfiguration.getEnabled()) {
      log.info("Rents scraper is disabled");
      return;
    }

    List<KvItem> items = kvService.getAllSearchItems(KvParams.createRentParams(identity())).stream()
      .map(KvSearchPageItem::toKvItem)
      .collect(toList());
    int count = changesService.check(items, KvItem.listRents());

    log.info("Saved {} rent items", count);
  }
}
