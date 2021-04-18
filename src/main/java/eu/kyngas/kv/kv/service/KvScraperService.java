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
    scrapeTartuApartmentSales();
    scrapeTartuHouseSales();
    scrapeTartuRents();

    scrapeTallinnApartmentSales();
    scrapeTallinnHouseSales();
    scrapeTallinnRents();
  }

  @Scheduled(cron = "0 0 * ? * *") // every hour
  @TransactionConfiguration(timeout = 300)
  @Transactional
  void scrapeTartuApartmentSales() {
    List<KvItem> items = kvService.getAllSearchItems(createTartuSaleParams(identity())).stream()
      .map(KvSearchPageItem::toKvItem)
      .collect(toList());
    int count = kvChangesService.check(items, listDeals(Deal.APARTMENT_SALE, Parish.TARTUMAA));

    log.info("Saved {} Tartu apartment sale items", count);
  }

  @Scheduled(cron = "0 10 * ? * *") // every hour
  @TransactionConfiguration(timeout = 300)
  @Transactional
  void scrapeTartuHouseSales() {
    List<KvItem> items = kvService.getAllSearchItems(createTartuSaleParams(b -> b
      .dealType(Deal.HOUSE_SALE.getType()))).stream()
      .map(KvSearchPageItem::toKvItem)
      .collect(toList());
    int count = kvChangesService.check(items, listDeals(Deal.HOUSE_SALE, Parish.TARTUMAA));

    log.info("Saved {} Tartu house sale items", count);
  }

  @Scheduled(cron = "0 20 * ? * *") // every hour
  @TransactionConfiguration(timeout = 300)
  @Transactional
  void scrapeTartuRents() {
    List<KvItem> items = kvService.getAllSearchItems(createTartuRentParams(identity())).stream()
      .map(KvSearchPageItem::toKvItem)
      .collect(toList());
    int count = kvChangesService.check(items, listDeals(Deal.APARTMENT_RENT, Parish.TARTUMAA));

    log.info("Saved {} Tartu apartment rent items", count);
  }

  @Scheduled(cron = "0 30 * ? * *") // every hour
  @TransactionConfiguration(timeout = 300)
  @Transactional
  void scrapeTallinnApartmentSales() {
    List<KvItem> items = kvService.getAllSearchItems(createTallinnSaleParams(identity())).stream()
      .map(KvSearchPageItem::toKvItem)
      .collect(toList());
    int count = kvChangesService.check(items, listDeals(Deal.APARTMENT_SALE, Parish.HARJUMAA));

    log.info("Saved {} Tallinn apartment sale items", count);
  }

  @Scheduled(cron = "0 40 * ? * *") // every hour
  @TransactionConfiguration(timeout = 300)
  @Transactional
  void scrapeTallinnHouseSales() {
    List<KvItem> items = kvService.getAllSearchItems(createTallinnSaleParams(b -> b
      .dealType(Deal.HOUSE_SALE.getType()))).stream()
      .map(KvSearchPageItem::toKvItem)
      .collect(toList());
    int count = kvChangesService.check(items, listDeals(Deal.HOUSE_SALE, Parish.HARJUMAA));

    log.info("Saved {} Tallinn house sale items", count);
  }

  @Scheduled(cron = "0 50 * ? * *") // every hour
  @TransactionConfiguration(timeout = 300)
  @Transactional
  void scrapeTallinnRents() {
    List<KvItem> items = kvService.getAllSearchItems(createTallinnRentParams(identity())).stream()
      .map(KvSearchPageItem::toKvItem)
      .collect(toList());
    int count = kvChangesService.check(items, listDeals(Deal.APARTMENT_RENT, Parish.HARJUMAA));

    log.info("Saved {} Tallinn apartment rent items", count);
  }
}
