package eu.kyngas.kv.kv.model;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@RegisterForReflection
public class KvDiff {
  @NotNull
  private final Long id;
  @NotNull
  private final Long extId;
  @NotNull
  private final String changeId;
}
