package eu.kyngas.kv.kv.service;

import eu.kyngas.kv.graph.model.KvGraphEntity;
import eu.kyngas.kv.graph.model.KvGraphInput;
import eu.kyngas.kv.kv.model.KvCounty;
import eu.kyngas.kv.kv.model.KvDiff;
import eu.kyngas.kv.kv.model.KvEntity;
import eu.kyngas.kv.kv.model.KvType;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import org.hibernate.Session;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.TypedQuery;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class KvRepository implements PanacheRepository<KvEntity> {

  public List<KvDiff> findDiffs(KvType type, KvCounty county) {
    return find("type = ?1 AND county = ?2", type, county).project(KvDiff.class).list();
  }

  public List<KvEntity> findByTypeAndExtIds(KvType type, Collection<Long> extIds, Boolean removed) {
    String sql = "type = ?1 AND extId IN ?2";
    if (removed == null) {
      return list(sql, type, extIds);
    }
    return list(sql + " AND removed = ?3", type, extIds, removed);
  }

  public List<KvGraphEntity> findGraphItems(KvGraphInput input) {
    String sql = "SELECT id, full_name, link FROM v_kv_graph_unique_latest WHERE type = :type AND county = :county";
    Map<String, Object> sqlParams = new HashMap<>();
    sqlParams.put("type", input.getType().name());
    sqlParams.put("county", input.getCounty().name());

    if (input.getActive() != null && input.getActive()) {
      sql += " AND booked = false AND removed = false";
    }
    if (input.getRooms() != null) {
      sql += " AND rooms = :rooms";
      sqlParams.put("rooms", input.getRooms());
    }
    if (input.getSizeMin() != null) {
      sql += " AND room_size IS NOT NULL AND room_size >= :minRoomSize";
      sqlParams.put("minRoomSize", input.getSizeMin());
    }
    if (input.getSizeMax() != null) {
      sql += " AND room_size IS NOT NULL AND room_size <= :maxRoomSize";
      sqlParams.put("maxRoomSize", input.getSizeMax());
    }
    if (input.getPriceMin() != null) {
      sql += " AND price >= :minPrice";
      sqlParams.put("minPrice", input.getPriceMin());
    }
    if (input.getPriceMax() != null) {
      sql += " AND price <= :maxPrice";
      sqlParams.put("maxPrice", input.getPriceMax());
    }

    TypedQuery<KvGraphEntity> query =
      getEntityManager().unwrap(Session.class).createNativeQuery(sql, KvGraphEntity.class);
    sqlParams.forEach(query::setParameter);
    return query.getResultList();
  }
}
