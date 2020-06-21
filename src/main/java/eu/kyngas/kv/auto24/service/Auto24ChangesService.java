package eu.kyngas.kv.auto24.service;

import eu.kyngas.kv.auto24.model.Auto24ChangeItem;
import eu.kyngas.kv.auto24.model.Auto24Item;
import io.vertx.core.json.JsonObject;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toMap;

@Slf4j
@ApplicationScoped
public class Auto24ChangesService {

  public int check(List<Auto24Item> newItems, List<Auto24Item> dbItems) {
    Map<Long, Auto24Item> newItemByExtId = newItems.stream().collect(toMap(Auto24Item::getExternalId, identity()));
    Map<Long, Auto24Item> dbItemByExtId = dbItems.stream().collect(toMap(Auto24Item::getExternalId, identity()));

    int changeCount = 0;
    for (Auto24Item newItem : newItemByExtId.values()) {
      Auto24Item dbLatestItem = dbItemByExtId.getOrDefault(newItem.getExternalId(), null);

      if (dbLatestItem == null) {
        log.info("Added new item: {}", newItem);
        newItem.persist();
        changeCount++;
        continue;
      }

      Auto24ChangeItem newLatestChangeItem = newItem.getLatestChangeItem();
      Auto24ChangeItem dbLatestChangeItem = dbLatestItem.getLatestChangeItem();

      if (!newLatestChangeItem.equals(dbLatestChangeItem)) {
        logChangedItem(dbLatestItem, newLatestChangeItem, dbLatestChangeItem);
        dbLatestItem.getChangeItems().add(newLatestChangeItem.setAuto24Item(dbLatestItem));
        changeCount++;
      }
    }
    return changeCount;
  }

  private void logChangedItem(Auto24Item newItem, Auto24ChangeItem newChangeItem, Auto24ChangeItem prevChangeItem) {
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
}
