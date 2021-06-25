package eu.kyngas.kv.kv.service;

import eu.kyngas.kv.core.config.KvConfig;
import eu.kyngas.kv.kv.client.KvClientService;
import eu.kyngas.kv.kv.model.Kv;
import eu.kyngas.kv.kv.model.KvCounty;
import eu.kyngas.kv.kv.model.KvType;
import io.quarkus.runtime.StartupEvent;
import io.quarkus.scheduler.Scheduled;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;

import static eu.kyngas.kv.kv.client.KvClientParams.createParams;

@Slf4j
@ApplicationScoped
public class KvScrapeService {
  @Inject
  KvClientService kvClientService;
  @Inject
  KvDiffService kvDiffService;
  @Inject
  KvConfig kvConfig;

  private KvCounty county = KvCounty.HARJUMAA;

  void onStart(@Observes StartupEvent e) {
    if (kvConfig.isFullScrapeOnStart()) {
      Arrays.stream(KvCounty.values()).forEach(this::scrape);
    }
  }

  @Scheduled(every = "4m", concurrentExecution = Scheduled.ConcurrentExecution.SKIP)
  void scrape() {
    scrape(county);
    county = county.next();
  }

  private void scrape(KvCounty county) {
    KvType.getEnabled().forEach(type -> {
      log.info("Scraping {} {} items.", county, type);
      try {
        List<Kv> items = kvClientService.getAllSearchItems(createParams(type, county));
        kvDiffService.check(items, type, county);
      } catch (Exception e) {
        log.error("Failed to scrape {} {} items.", county, type, e);
      }
    });
  }
}
