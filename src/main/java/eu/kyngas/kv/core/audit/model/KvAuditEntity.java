package eu.kyngas.kv.core.audit.model;

import eu.kyngas.kv.kv.model.KvEntity;
import lombok.*;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;

import static io.quarkiverse.hibernate.types.json.JsonTypes.JSON_BIN;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity(name = "audit")
public class KvAuditEntity extends AuditEntity {

  @Type(type = JSON_BIN)
  @Column(columnDefinition = JSON_BIN)
  private KvEntity changes;
}
