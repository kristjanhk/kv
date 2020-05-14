package eu.kyngas.kv.filter;

import eu.kyngas.kv.db.KvItem;
import io.vertx.core.json.JsonObject;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.context.ApplicationScoped;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.*;

@Slf4j
@ApplicationScoped
public class FilterService {

  public List<KvItem> filterChanges(List<KvItem> newItems, List<KvItem> dbItems) {
    Map<Integer, KvItem> newItemByKvId = newItems.stream().collect(toMap(KvItem::getKvId, identity()));
    Map<Integer, List<KvItem>> dbItemByKvId = dbItems.stream().collect(groupingBy(KvItem::getKvId));
    Map<String, List<KvItem>> dbItemByUniqueId = dbItems.stream().collect(groupingBy(KvItem::getUniqueId));

    markRemovedItems(newItemByKvId, dbItemByKvId);

    return newItemByKvId.values().stream().filter(newItem -> {
      Optional<KvItem> dbLatestItem = dbItemByKvId.getOrDefault(newItem.getKvId(), List.of()).stream()
        .max(Comparator.comparing(KvItem::getInsertDate));

      if (dbLatestItem.isEmpty()) {
        markPrevKvId(newItem, newItemByKvId, dbItemByUniqueId);
        log.info("Added new item: {}", newItem);
        return true;
      }

      if (!newItem.equals(dbLatestItem.get())) {
        logChangedItem(newItem, dbLatestItem.get());
        return true;
      }

      return false;
    }).collect(toList());
  }

  private void logChangedItem(KvItem newItem, KvItem prevItem) {
    JsonObject newJson = JsonObject.mapFrom(newItem);
    JsonObject prevJson = JsonObject.mapFrom(prevItem);
    String changes = newJson.fieldNames().stream()
      .filter(name -> !Objects.equals(newJson.getValue(name), prevJson.getValue(name)))
      .map(name -> name + ": " + getChange(prevJson.getValue(name)) + " -> " + getChange(newJson.getValue(name)))
      .collect(Collectors.joining(", "));
    log.info("Changed item with id {}: {}", newItem.getKvId(), changes);
  }

  private String getChange(Object input) {
    return input == null ? "null" : input.toString();
  }

  private void markRemovedItems(Map<Integer, KvItem> newItemByKvId, Map<Integer, List<KvItem>> dbItemByKvId) {
    dbItemByKvId.entrySet().stream()
      .filter(e -> !newItemByKvId.containsKey(e.getKey()))
      .flatMap(e -> e.getValue().stream())
      .forEach(dbItem -> dbItem.setRemoved(true));
  }

  private void markPrevKvId(KvItem newItem,
                            Map<Integer, KvItem> newItemByKvId,
                            Map<String, List<KvItem>> dbItemByUniqueId) {
    List<KvItem> dbUniqueItems = dbItemByUniqueId.getOrDefault(newItem.getUniqueId(), List.of());
    if (dbUniqueItems.stream().filter(KvItem::isRemoved).map(KvItem::getKvId).collect(toSet()).size() == 1) {
      dbUniqueItems.stream()
        .filter(dbItem -> !newItemByKvId.containsKey(dbItem.getKvId()))
        .filter(dbItem -> newItem.getPublishDate().isAfter(dbItem.getPublishDate()))
        .findAny()
        .ifPresent(dbItem -> newItem.setPrevKvId(dbItem.getKvId()));
    }
  }
}
