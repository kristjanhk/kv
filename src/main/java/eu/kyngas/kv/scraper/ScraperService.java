package eu.kyngas.kv.scraper;

import eu.kyngas.kv.client.Params;
import eu.kyngas.kv.client.QueryService;
import eu.kyngas.kv.database.DatabaseService;
import eu.kyngas.kv.database.model.KvItem;
import eu.kyngas.kv.filter.FilterService;
import eu.kyngas.kv.client.model.Rss;
import io.quarkus.scheduler.Scheduled;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;

import static java.util.function.UnaryOperator.identity;
import static java.util.stream.Collectors.toList;

@Slf4j
@ApplicationScoped
public class ScraperService {
  @Inject
  ScraperConfiguration scraperConfiguration;
  @Inject
  QueryService queryService;
  @Inject
  DatabaseService databaseService;
  @Inject
  FilterService filterService;

  @Scheduled(cron = "0 0 * ? * *") // every hour
  // @Scheduled(cron = "0 * * ? * *") // every minute
  void scrapeSales() {
    if (!scraperConfiguration.getEnabled()) {
      log.info("Sales scraper is disabled");
      return;
    }

    Rss rss = queryService.query(Params.createSaleParams(identity()));
    List<KvItem> items = rss.getChannel().getItem().stream().map(item -> item.toKvItem(true)).collect(toList());
    List<KvItem> changedItems = filterService.filterChanges(items, databaseService.findSales());

    databaseService.save(changedItems);
    log.info("Saved {} sale items", changedItems.size());
  }

  @Scheduled(cron = "0 0 * ? * *") // every hour
  // @Scheduled(cron = "0 * * ? * *") // every minute
  void scrapeRents() {
    if (!scraperConfiguration.getEnabled()) {
      log.info("Rents scraper is disabled");
      return;
    }

    Rss rss = queryService.query(Params.createRentParams(identity()));
    List<KvItem> items = rss.getChannel().getItem().stream().map(item -> item.toKvItem(false)).collect(toList());
    List<KvItem> changedItems = filterService.filterChanges(items, databaseService.findRents());

    databaseService.save(changedItems);
    log.info("Saved {} rent items", changedItems.size());
  }
}
