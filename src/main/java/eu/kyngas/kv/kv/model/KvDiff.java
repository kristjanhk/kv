package eu.kyngas.kv.kv.model;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class KvDiff {
  @NotNull
  private final Long id;
  @NotNull
  private final Long extId;
  @NotNull
  private final String changeId;
}
