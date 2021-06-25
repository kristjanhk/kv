package eu.kyngas.kv.graph.service;

import eu.kyngas.kv.core.audit.model.KvAuditEntity;
import eu.kyngas.kv.core.audit.service.AuditRepository;
import eu.kyngas.kv.graph.model.KvGraphDto;
import eu.kyngas.kv.graph.model.KvGraphDto.KvGraphPriceDto;
import eu.kyngas.kv.graph.model.KvGraphEntity;
import eu.kyngas.kv.graph.model.KvGraphInput;
import eu.kyngas.kv.kv.model.KvEntity;
import eu.kyngas.kv.kv.service.KvRepository;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@ApplicationScoped
public class KvGraphService {
  @Inject
  KvRepository kvRepository;
  @Inject
  AuditRepository auditRepository;

  public List<KvGraphDto> findGraphItems(KvGraphInput input) {
    List<KvGraphEntity> items = kvRepository.findGraphItems(input);

    Set<Long> ids = items.stream().map(KvGraphEntity::getId).collect(Collectors.toSet());
    Map<Long, List<KvAuditEntity>> changesById =
      auditRepository.findChanges(KvAuditEntity.class, KvEntity.class, ids, KvEntity.PRICE);

    // TODO: KristjanK see leiab praegu ühe kuulutuse kohta hindade muutused ajas
    // TODO: KristjanK tegelikult peaks leidma ühe kindla korteri kohta hindade muutused ajas (võib mitu kuulutust olla)
    // TODO: KristjanK unique_id-de järgi peaks id-d võtma, nende muudatused pärima ja siis tagasi kokku pakkima

    return items.stream()
      .map(e -> KvGraphDto.builder()
        .fullName(e.getFullName())
        .link(e.getLink())
        .prices(getPrices(e, changesById.get(e.getId()), input))
        .build())
      .filter(dto -> !dto.getPrices().isEmpty())
      .collect(Collectors.toList());
  }

  private List<KvGraphPriceDto> getPrices(KvGraphEntity e, List<KvAuditEntity> changes, KvGraphInput input) {
    List<KvGraphPriceDto> prices = changes.stream()
      .map(a -> new KvGraphPriceDto(a.getTime(), a.getChanges().getPrice()))
      .sorted(Comparator.comparing(KvGraphPriceDto::getPublishDate))
      .collect(Collectors.toList());

    LocalDateTime startDate = prices.get(0).getPublishDate();
    LocalDateTime endDate = prices.get(prices.size() - 1).getPublishDate();
    if (input.getDateMin() == null || startDate.isAfter(input.getDateMin().atStartOfDay())) {
      if (input.getDateMax() == null || endDate.isBefore(input.getDateMax().atStartOfDay().plusDays(1))) {
        return prices;
      }
    }
    return List.of();
  }
}
