package eu.kyngas.kv.core.audit.service;

import eu.kyngas.kv.core.audit.model.AuditEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import org.hibernate.Session;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.TypedQuery;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ApplicationScoped
public class AuditRepository implements PanacheRepository<AuditEntity> {

  public <T extends AuditEntity> Map<Long, List<T>> findChanges(Class<T> auditClass,
                                                                Class<?> tableClass,
                                                                Collection<Long> ids) {
    return findChanges(auditClass, tableClass, ids, null);
  }

  public <T extends AuditEntity> Map<Long, List<T>> findChanges(Class<T> auditClass,
                                                                Class<?> tableClass,
                                                                Collection<Long> ids,
                                                                String changeColumn) {
    String sql = "SELECT * FROM audit WHERE table_name = :tableName AND id IN :ids";
    Map<String, Object> params = new HashMap<>();
    params.put("tableName", getEntityManager().getMetamodel().entity(tableClass).getName());
    params.put("ids", ids);

    if (changeColumn != null) {
      sql += " AND changes -> :changeColumn IS NOT NULL";
      params.put("changeColumn", changeColumn);
    }

    TypedQuery<T> query = getEntityManager().unwrap(Session.class).createNativeQuery(sql, auditClass);
    params.forEach(query::setParameter);
    return query.getResultList().stream().collect(Collectors.groupingBy(AuditEntity::getId));
  }
}
