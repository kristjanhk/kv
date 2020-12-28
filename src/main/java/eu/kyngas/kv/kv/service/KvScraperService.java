package eu.kyngas.kv.kv.service;

import eu.kyngas.kv.kv.model.KvItem;
import eu.kyngas.kv.kv.model.KvSearchPageItem;
import io.quarkus.narayana.jta.runtime.TransactionConfiguration;
import io.quarkus.runtime.StartupEvent;
import io.quarkus.scheduler.Scheduled;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.List;

import static eu.kyngas.kv.kv.model.KvClientParams.*;
import static eu.kyngas.kv.kv.model.KvItem.*;
import static java.util.function.UnaryOperator.identity;
import static java.util.stream.Collectors.toList;

@Slf4j
@ApplicationScoped
public class KvScraperService {
  @Inject
  KvService kvService;
  @Inject
  KvChangesService kvChangesService;

  void onStart(@Observes StartupEvent e) {
    scrapeTartuRents();
    scrapeTartuSales();
    scrapeTallinnRents();
    scrapeTallinnSales();
  }

  @Scheduled(cron = "0 0 * ? * *") // every hour
  @TransactionConfiguration(timeout = 300)
  @Transactional
  void scrapeTartuSales() {
    List<KvItem> items = kvService.getAllSearchItems(createTartuSaleParams(identity())).stream()
      .map(KvSearchPageItem::toKvItem)
      .collect(toList());
    int count = kvChangesService.check(items, listTartuSales());

    log.info("Saved {} Tartu sale items", count);
  }

  @Scheduled(cron = "0 15 * ? * *") // every hour
  @TransactionConfiguration(timeout = 300)
  @Transactional
  void scrapeTartuRents() {
    List<KvItem> items = kvService.getAllSearchItems(createTartuRentParams(identity())).stream()
      .map(KvSearchPageItem::toKvItem)
      .collect(toList());
    int count = kvChangesService.check(items, listTartuRents());

    log.info("Saved {} Tartu rent items", count);
  }

  @Scheduled(cron = "0 30 * ? * *") // every hour
  @TransactionConfiguration(timeout = 300)
  @Transactional
  void scrapeTallinnSales() {
    List<KvItem> items = kvService.getAllSearchItems(createTallinnSaleParams(identity())).stream()
      .map(KvSearchPageItem::toKvItem)
      .collect(toList());
    int count = kvChangesService.check(items, listTallinnSales());

    log.info("Saved {} Tallinn sale items", count);
  }

  @Scheduled(cron = "0 45 * ? * *") // every hour
  @TransactionConfiguration(timeout = 300)
  @Transactional
  void scrapeTallinnRents() {
    List<KvItem> items = kvService.getAllSearchItems(createTallinnRentParams(identity())).stream()
      .map(KvSearchPageItem::toKvItem)
      .collect(toList());
    int count = kvChangesService.check(items, listTallinnRents());

    log.info("Saved {} Tallinn rent items", count);
  }
}
