package eu.kyngas.kv.kv.service;

import eu.kyngas.kv.kv.model.KvItem;
import eu.kyngas.kv.kv.model.KvClientParams;
import eu.kyngas.kv.kv.model.KvSearchPageItem;
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
  KvService kvService;
  @Inject
  ChangesService changesService;

  @Scheduled(cron = "0 0 * ? * *") // every hour
  // @Scheduled(every = "60s")
  @Transactional
  void scrapeSales() {
    List<KvItem> items = kvService.getAllSearchItems(KvClientParams.createSaleParams(identity())).stream()
      .map(KvSearchPageItem::toKvItem)
      .collect(toList());
    int count = changesService.check(items, KvItem.listSales());

    log.info("Saved {} sale items", count);
  }

  @Scheduled(cron = "0 0 * ? * *") // every hour
  // @Scheduled(every = "60s")
  @Transactional
  void scrapeRents() {
    List<KvItem> items = kvService.getAllSearchItems(KvClientParams.createRentParams(identity())).stream()
      .map(KvSearchPageItem::toKvItem)
      .collect(toList());
    int count = changesService.check(items, KvItem.listRents());

    log.info("Saved {} rent items", count);
  }
}
