package eu.kyngas.kv.kv.service;

import eu.kyngas.kv.kv.model.KvChangeItem;
import eu.kyngas.kv.kv.model.KvItem;
import io.vertx.core.json.JsonObject;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static java.time.LocalDateTime.now;
import static java.util.Comparator.comparing;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.*;

@Slf4j
@ApplicationScoped
public class KvChangesService {

  public int check(List<KvItem> newItems, List<KvItem> dbItems) {
    Map<Long, KvItem> newItemByExtId = newItems.stream().collect(toMap(KvItem::getExternalId, identity()));
    Map<Long, KvItem> dbItemByExtId = dbItems.stream().collect(toMap(KvItem::getExternalId, identity()));
    Map<String, List<KvItem>> dbItemByUniqueId = dbItems.stream().collect(groupingBy(KvItem::getUniqueId));

    markRemovedItems(newItemByExtId, dbItemByExtId);

    int changeCount = 0;
    for (KvItem newItem : newItemByExtId.values()) {
      KvItem dbLatestItem = dbItemByExtId.getOrDefault(newItem.getExternalId(), null);

      if (dbLatestItem == null) {
        markPrevExtId(newItem, newItemByExtId.keySet(), dbItemByUniqueId);
        log.info("Added new item: {}", newItem);
        newItem.persist();
        changeCount++;
        continue;
      }

      KvChangeItem newLatestChangeItem = newItem.getLatestChangeItem();
      KvChangeItem dbLatestChangeItem = dbLatestItem.getLatestChangeItem();

      if (!newLatestChangeItem.equals(dbLatestChangeItem)) {
        logChangedItem(dbLatestItem, newLatestChangeItem, dbLatestChangeItem);
        dbLatestItem.getChangeItems().add(newLatestChangeItem.setKvItem(dbLatestItem));
        changeCount++;
      }
    }
    return changeCount;
  }

  private void logChangedItem(KvItem newItem, KvChangeItem newChangeItem, KvChangeItem prevChangeItem) {
    JsonObject newJson = JsonObject.mapFrom(newChangeItem);
    JsonObject prevJson = JsonObject.mapFrom(prevChangeItem);
    String changes = newJson.fieldNames().stream()
      .filter(anObject -> !"id".equals(anObject))
      .filter(name -> !Objects.equals(newJson.getValue(name), prevJson.getValue(name)))
      .map(name -> name + ": " + getChange(prevJson.getValue(name)) + " -> " + getChange(newJson.getValue(name)))
      .collect(joining(", "));
    log.info("Changed item with externalId {}: {}", newItem.getExternalId(), changes);
  }

  private static String getChange(Object input) {
    return input == null ? "null" : input.toString();
  }

  private static void markRemovedItems(Map<Long, KvItem> newItemByExtId, Map<Long, KvItem> dbItemByExtId) {
    dbItemByExtId.entrySet().stream()
      .filter(e -> !newItemByExtId.containsKey(e.getKey()))
      .filter(e -> !e.getValue().isRemoved())
      .forEach(dbItem -> dbItem.getValue().setRemoved(true).setRemovedDate(now()));
  }

  private static void markPrevExtId(KvItem newItem,
                                    Set<Long> newItemExtIds,
                                    Map<String, List<KvItem>> dbItemByUniqueId) {
      dbItemByUniqueId.getOrDefault(newItem.getUniqueId(), List.of()).stream()
        .filter(KvItem::isRemoved)
        .filter(dbItem -> !newItemExtIds.contains(dbItem.getExternalId()))
        .filter(dbItem -> newItem.getLatestChangeItem().getPublishDate()
          .isAfter(dbItem.getLatestChangeItem().getPublishDate()))
        .max(comparing(dbItem -> dbItem.getLatestChangeItem().getPublishDate()))
        .ifPresent(dbItem -> newItem.setPrevExternalId(dbItem.getExternalId()));
  }
}
