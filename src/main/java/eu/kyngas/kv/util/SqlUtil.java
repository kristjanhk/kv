package eu.kyngas.kv.util;

import io.quarkus.hibernate.orm.panache.runtime.JpaOperations;

import javax.persistence.Query;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.EntityType;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class SqlUtil {

  @SuppressWarnings("unchecked")
  public static <T> List<T> selectRaw(String sql, Map<String, Object> params) {
    Query query = JpaOperations.getEntityManager().createNativeQuery(sql);
    if (params.containsKey("limit")) {
      query.setMaxResults((int) params.remove("limit"));
    }
    params.forEach(query::setParameter);
    return (List<T>) query.getResultList();
  }

  public static List<String> getColumns(Class<?> entityClass, String... columnsExcluded) {
    EntityType<?> type = JpaOperations.getEntityManager().getMetamodel().entity(entityClass);
    Set<String> excluded = Set.of(columnsExcluded);
    return type.getAttributes().stream()
      .map(Attribute::getName)
      .filter(column -> !excluded.contains(column))
      .collect(Collectors.toList());
  }

}
