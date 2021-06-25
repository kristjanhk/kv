package eu.kyngas.kv.graph.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;

import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Immutable
@Entity(name = "v_kv_graph_unique_latest")
public class KvGraphEntity {
  @Id
  private Long id;
  private String fullName;
  private String link;
}
