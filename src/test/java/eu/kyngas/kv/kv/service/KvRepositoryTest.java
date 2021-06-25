package eu.kyngas.kv.kv.service;

import eu.kyngas.kv.core.BaseDatabaseTest;
import eu.kyngas.kv.core.audit.model.AuditAction;
import eu.kyngas.kv.core.audit.model.KvAuditEntity;
import eu.kyngas.kv.core.audit.service.AuditRepository;
import eu.kyngas.kv.graph.model.KvGraphEntity;
import eu.kyngas.kv.graph.model.KvGraphInput;
import eu.kyngas.kv.kv.model.*;
import eu.kyngas.kv.util.NumberUtil;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static eu.kyngas.kv.kv.model.KvCounty.TARTUMAA;
import static eu.kyngas.kv.kv.model.KvType.APARTMENT_RENT;
import static eu.kyngas.kv.kv.model.KvType.APARTMENT_SALE;
import static eu.kyngas.kv.util.StringUtil.capitalizeOnly;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
class KvRepositoryTest extends BaseDatabaseTest {
  @Inject
  KvRepository kvRepository;
  @Inject
  AuditRepository auditRepository;
  @Inject
  KvFixture kvFixture;
  @Inject
  KvMapper kvMapper;

  @Test
  void test_hashesMatch() {
    KvEntity entity = kvFixture.saveKvEntity();
    Kv kv = kvMapper.convert(entity);

    assertEquals(entity.getUniqueId(), kv.getUniqueId());
    assertEquals(entity.getChangeId(), kv.getChangeId());
  }

  @Test
  void test_nulls_hashesMatch() {
    KvEntity entity = kvFixture.saveKvEntity(b -> b
      .pricePerM2(null)
      .address(null)
      .roomSize(null)
      .floor(null)
      .floorTotal(null)
      .year(null));
    Kv kv = kvMapper.convert(entity);

    assertEquals(entity.getUniqueId(), kv.getUniqueId());
    assertEquals(entity.getChangeId(), kv.getChangeId());
  }

  @Test
  void findDiffs() {
    KvEntity entity = kvFixture.saveKvEntity();
    kvFixture.saveKvEntity(b -> b.type(APARTMENT_RENT));
    kvFixture.saveKvEntity(b -> b.county(KvCounty.HARJUMAA));

    List<KvDiff> result = kvRepository.findDiffs(APARTMENT_SALE, TARTUMAA);

    assertEquals(1, result.size());
    assertEquals(entity.getId(), result.get(0).getId());
    assertEquals(entity.getExtId(), result.get(0).getExtId());
    assertEquals(entity.getChangeId(), result.get(0).getChangeId());
  }

  @Test
  void findByTypeAndExtIds() {
    KvEntity entity1 = kvFixture.saveKvEntity();
    KvEntity entity2 = kvFixture.saveKvEntity(b -> b.type(APARTMENT_RENT));
    kvFixture.saveKvEntity();

    List<KvEntity> result = kvRepository.findByTypeAndExtIds(APARTMENT_SALE,
                                                             List.of(entity1.getExtId(), entity2.getExtId()),
                                                             null);

    assertEquals(1, result.size());
    assertEquals(entity1, result.get(0));
  }

  @Test
  void findGraphItems() {
    KvEntity entity = kvFixture.saveKvEntity(b -> b.publishDate(LocalDateTime.now()));
    kvFixture.saveKvEntity(b -> b.publishDate(LocalDateTime.now().minusDays(1)));

    List<KvGraphEntity> result = kvRepository.findGraphItems(KvGraphInput.builder()
                                                               .type(APARTMENT_SALE)
                                                               .county(TARTUMAA)
                                                               .build());

    assertEquals(1, result.size());
    assertEquals(entity.getId(), result.get(0).getId());
    assertEquals(entity.getLink(), result.get(0).getLink());
    assertEquals(createName(entity), result.get(0).getFullName());
  }

  @Test
  void findChanges_onlyPrice() {
    KvEntity entity = kvFixture.saveKvEntity();
    BigDecimal price1 = entity.getPrice();
    BigDecimal price2 = NumberUtil.bigDecimal(20);
    entity.setPrice(price2);
    entity.setBooked(true);

    kvRepository.flush();
    entity.setYear(2000);

    Map<Long, List<KvAuditEntity>> result =
      auditRepository.findChanges(KvAuditEntity.class, KvEntity.class, List.of(entity.getId()), KvEntity.PRICE);

    assertEquals(1, result.size());
    assertEquals(2, result.get(entity.getId()).size());

    List<KvAuditEntity> audit = result.get(entity.getId());

    assertEquals(AuditAction.INSERT, audit.get(0).getAction());
    assertEquals(price1, audit.get(0).getChanges().getPrice());

    assertEquals(AuditAction.UPDATE, audit.get(1).getAction());
    assertEquals(price2, audit.get(1).getChanges().getPrice());
    assertTrue(audit.get(1).getChanges().getBooked());
  }

  private String createName(KvEntity e) {
    String location = Stream.of(capitalizeOnly(e.getCounty().name()), e.getArea(), e.getDistrict(), e.getAddress())
      .filter(Objects::nonNull)
      .collect(Collectors.joining(", "));
    return Stream.of(location, e.getRooms(), e.getRoomSize(), e.getFloor())
      .filter(Objects::nonNull)
      .map(String::valueOf)
      .collect(Collectors.joining(" - "));
  }
}