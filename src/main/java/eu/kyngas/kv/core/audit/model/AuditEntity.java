package eu.kyngas.kv.core.audit.model;

import io.quarkiverse.hibernate.types.json.JsonBinaryType;
import io.quarkiverse.hibernate.types.json.JsonTypes;
import lombok.Data;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Immutable
@MappedSuperclass
@TypeDef(name = JsonTypes.JSON_BIN, typeClass = JsonBinaryType.class)
public abstract class AuditEntity {
  @Id
  @GeneratedValue
  private Long eventId;

  @NotNull
  private Long id;
  @NotNull
  private String tableName;

  @NotNull
  private LocalDateTime time;
  @NotNull
  @Enumerated(EnumType.STRING)
  private AuditAction action;
}
