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
import java.util.stream.Collectors;

import static java.time.LocalDateTime.now;
import static java.util.Comparator.comparing;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toMap;

@Slf4j
@ApplicationScoped
public class ChangesService {

  public int check(List<KvItem> newItems, List<KvItem> dbItems) {
    Map<Long, KvItem> newItemByKvId = newItems.stream().collect(toMap(KvItem::getExternalId, identity()));
    Map<Long, KvItem> dbItemByKvId = dbItems.stream().collect(toMap(KvItem::getExternalId, identity()));
    Map<String, List<KvItem>> dbItemByUniqueId = dbItems.stream().collect(groupingBy(KvItem::getUniqueId));

    markRemovedItems(newItemByKvId, dbItemByKvId);

    int changeCount = 0;
    for (KvItem newItem : newItemByKvId.values()) {
      KvItem dbLatestItem = dbItemByKvId.getOrDefault(newItem.getExternalId(), null);

      if (dbLatestItem == null) {
        markPrevKvId(newItem, newItemByKvId.keySet(), dbItemByUniqueId);
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
      .collect(Collectors.joining(", "));
    log.info("Changed item with externalId {}: {}", newItem.getExternalId(), changes);
  }

  private static String getChange(Object input) {
    return input == null ? "null" : input.toString();
  }

  private static void markRemovedItems(Map<Long, KvItem> newItemByKvId, Map<Long, KvItem> dbItemByKvId) {
    dbItemByKvId.entrySet().stream()
      .filter(e -> !newItemByKvId.containsKey(e.getKey()))
      .forEach(dbItem -> dbItem.getValue().setRemoved(true).setRemovedDate(now()));
  }

  private static void markPrevKvId(KvItem newItem, Set<Long> newItemKvIds, Map<String, List<KvItem>> dbItemByUniqueId) {
      dbItemByUniqueId.getOrDefault(newItem.getUniqueId(), List.of()).stream()
        .filter(KvItem::isRemoved)
        .filter(dbItem -> !newItemKvIds.contains(dbItem.getExternalId()))
        .filter(dbItem -> newItem.getLatestChangeItem().getPublishDate()
          .isAfter(dbItem.getLatestChangeItem().getPublishDate()))
        .max(comparing(dbItem -> dbItem.getLatestChangeItem().getPublishDate()))
        .ifPresent(dbItem -> newItem.setPrevExternalId(dbItem.getExternalId()));
  }
}
