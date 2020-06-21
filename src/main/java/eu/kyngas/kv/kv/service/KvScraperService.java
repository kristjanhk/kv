package eu.kyngas.kv.kv.service;

import eu.kyngas.kv.kv.model.KvItem;
import eu.kyngas.kv.kv.model.KvSearchPageItem;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.List;

import static eu.kyngas.kv.kv.model.KvClientParams.createRentParams;
import static eu.kyngas.kv.kv.model.KvClientParams.createSaleParams;
import static eu.kyngas.kv.kv.model.KvItem.listRents;
import static eu.kyngas.kv.kv.model.KvItem.listSales;
import static java.util.function.UnaryOperator.identity;
import static java.util.stream.Collectors.toList;

@Slf4j
@ApplicationScoped
public class KvScraperService {
  @Inject
  KvService kvService;
  @Inject
  KvChangesService kvChangesService;

  //@Scheduled(cron = "0 0 * ? * *") // every hour
  // @Scheduled(every = "60s")
  @Transactional
  void scrapeSales() {
    List<KvItem> items = kvService.getAllSearchItems(createSaleParams(identity())).stream()
      .map(KvSearchPageItem::toKvItem)
      .collect(toList());
    int count = kvChangesService.check(items, listSales());

    log.info("Saved {} sale items", count);
  }

  //@Scheduled(cron = "0 0 * ? * *") // every hour
  // @Scheduled(every = "60s")
  @Transactional
  void scrapeRents() {
    List<KvItem> items = kvService.getAllSearchItems(createRentParams(identity())).stream()
      .map(KvSearchPageItem::toKvItem)
      .collect(toList());
    int count = kvChangesService.check(items, listRents());

    log.info("Saved {} rent items", count);
  }
}
