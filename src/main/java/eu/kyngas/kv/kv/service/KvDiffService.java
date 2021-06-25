package eu.kyngas.kv.kv.service;

import eu.kyngas.kv.kv.model.*;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.*;

import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;

@Slf4j
@ApplicationScoped
public class KvDiffService {
  @Inject
  KvRepository kvRepository;
  @Inject
  KvMapper kvMapper;

  @Transactional
  void check(List<Kv> items, KvType type, KvCounty county) {
    Map<Long, KvDiff> dbDiffByExtId = kvRepository.findDiffs(type, county).stream()
      .collect(toMap(KvDiff::getExtId, e -> e));
    Map<Long, Kv> webByExtId = items.stream()
      .collect(toMap(Kv::getExtId, e -> e));

    Set<Long> addedExtIds = addItems(webByExtId, dbDiffByExtId.keySet());
    Set<Long> changedExtIds = updateItems(webByExtId, dbDiffByExtId);
    Set<Long> removedExtIds = markRemovedItems(type, webByExtId.keySet(), dbDiffByExtId.keySet());

    log.info("Checked {} {} {} items: \n" +
               "Added {} items: {}, \n" +
               "Changed {} items: {}, \n" +
               "Removed {} items: {}.",
             items.size(), county, type,
             addedExtIds.size(), addedExtIds,
             changedExtIds.size(), changedExtIds,
             removedExtIds.size(), removedExtIds);
  }

  private Set<Long> addItems(Map<Long, Kv> webByExtId, Set<Long> dbExtIds) {
    Set<Long> newExtIds = new HashSet<>(webByExtId.keySet());
    newExtIds.removeAll(dbExtIds);
    if (newExtIds.isEmpty()) {
      return Set.of();
    }

    Set<Kv> items = webByExtId.values().stream()
      .filter(item -> newExtIds.contains(item.getExtId()))
      .collect(toSet());
    kvRepository.persist(kvMapper.inverse(items));
    return newExtIds;
  }

  private Set<Long> updateItems(Map<Long, Kv> webByExtId, Map<Long, KvDiff> dbDiffByExtId) {
    Set<Long> changedExtIds = new HashSet<>(webByExtId.keySet());
    changedExtIds.retainAll(dbDiffByExtId.keySet());
    if (changedExtIds.isEmpty()) {
      return Set.of();
    }

    Set<Kv> items = webByExtId.values().stream()
      .filter(item -> changedExtIds.contains(item.getExtId()))
      .filter(item -> !Objects.equals(item.getChangeId(), dbDiffByExtId.get(item.getExtId()).getChangeId()))
      .collect(toSet());
    items.forEach(item -> {
      item.setId(dbDiffByExtId.get(item.getExtId()).getId());
      kvRepository.getEntityManager().merge(kvMapper.inverse(item));
    });
    return items.stream().map(Kv::getExtId).collect(toSet());
  }

  private Set<Long> markRemovedItems(KvType type, Set<Long> webExtIds, Set<Long> dbExtIds) {
    Set<Long> removedExtIds = new HashSet<>(dbExtIds);
    removedExtIds.removeAll(webExtIds);
    if (removedExtIds.isEmpty()) {
      return Set.of();
    }

    List<KvEntity> entities = kvRepository.findByTypeAndExtIds(type, removedExtIds, false);
    entities.forEach(entity -> {
      entity.setBooked(false);
      entity.setRemoved(true);
      entity.setPublishDate(LocalDateTime.now());
    });
    return entities.stream().map(KvEntity::getExtId).collect(toSet());
  }
}
