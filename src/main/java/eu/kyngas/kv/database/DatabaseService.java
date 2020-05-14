package eu.kyngas.kv.database;

import eu.kyngas.kv.database.model.KvItem;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import java.util.List;

@Slf4j
@ApplicationScoped
public class DatabaseService {

  @Transactional
  public void save(List<KvItem> kvItems) {
    KvItem.persist(kvItems.stream().filter(item -> item.getId() == null));
  }

  @Transactional
  public List<KvItem> findSales() {
    return KvItem.list("kvType", "sale");
  }

  @Transactional
  public List<KvItem> findRents() {
    return KvItem.list("kvType", "rent");
  }
}
