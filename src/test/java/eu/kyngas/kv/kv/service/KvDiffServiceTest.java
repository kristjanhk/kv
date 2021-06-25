package eu.kyngas.kv.kv.service;

import eu.kyngas.kv.core.BaseDatabaseTest;
import eu.kyngas.kv.core.audit.model.AuditAction;
import eu.kyngas.kv.core.audit.model.KvAuditEntity;
import eu.kyngas.kv.core.audit.service.AuditRepository;
import eu.kyngas.kv.kv.model.*;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class KvDiffServiceTest extends BaseDatabaseTest {
  @Inject
  AuditRepository auditRepository;
  @Inject
  KvRepository kvRepository;
  @Inject
  KvFixture kvFixture;
  @Inject
  KvDiffService kvDiffService;

  @Test
  void check_webExists_dbNotExists_itemIsInserted() {
    Kv kv = kvFixture.createKv();

    Long id = checkAndAssert(kv, null);

    assertAudit(id, insert -> assertEquals(kv.getExtId(), insert.getChanges().getExtId()), null);
  }

  @Test
  void check_webExists_dbExists_itemIsUpdated() {
    int year = 1980;
    LocalDateTime time = LocalDateTime.now();
    KvEntity entity = kvFixture.saveKvEntity(b -> b.year(year).publishDate(time));
    Kv kv = kvFixture.createKv(b -> b.extId(entity.getExtId()).year(2000));

    Long id = checkAndAssert(kv, res -> {
      assertEquals(entity.getId(), res.getId());
      assertEquals(kv.getYear(), res.getYear());
      assertNotEquals(time, res.getPublishDate());
    });
    assertAudit(id,
                insert -> assertEquals(year, insert.getChanges().getYear()),
                update -> assertEquals(kv.getYear(), update.getChanges().getYear()));
  }

  @Test
  void check_webNotExists_dbExists_itemIsRemoved() {
    KvEntity entity = kvFixture.saveKvEntity();

    kvDiffService.check(List.of(), entity.getType(), entity.getCounty());

    Long id = assertCheck(entity.getType(), entity.getExtId(), res -> {
      assertEquals(entity.getId(), res.getId());
      assertTrue(res.getRemoved());
    });
    assertAudit(id,
                insert -> assertFalse(insert.getChanges().getRemoved()),
                update -> assertTrue(update.getChanges().getRemoved()));
  }

  @Test
  void check_webNotExists_dbExistsRemoved_itemIsNotUpdated() {
    KvEntity entity = kvFixture.saveKvEntity(b -> b.removed(true));

    kvDiffService.check(List.of(), entity.getType(), entity.getCounty());

    Long id = assertCheck(entity.getType(), entity.getExtId(), res -> {
      assertEquals(entity.getId(), res.getId());
      assertTrue(res.getRemoved());
    });
    assertAudit(id, insert -> assertTrue(insert.getChanges().getRemoved()), null);
  }

  @Test
  void check_webExists_dbIsRemoved_itemIsRestored() {
    KvEntity entity = kvFixture.saveKvEntity(b -> b.removed(true));
    Kv kv = kvFixture.createKv(b -> b.extId(entity.getExtId()));

    Long id = checkAndAssert(kv, res -> {
      assertEquals(entity.getId(), res.getId());
      assertFalse(res.getRemoved());
    });
    assertAudit(id,
                insert -> assertTrue(insert.getChanges().getRemoved()),
                update -> assertFalse(update.getChanges().getRemoved()));
  }

  @Test
  void check_webExistsDuplicate_latestItemIsPreserved() {

  }

  private Long checkAndAssert(Kv kv, Consumer<KvEntity> resultChecker) {
    kvDiffService.check(List.of(kv), kv.getType(), kv.getCounty());
    return assertCheck(kv.getType(), kv.getExtId(), resultChecker);
  }

  private Long assertCheck(KvType type, Long extId, Consumer<KvEntity> resultChecker) {
    List<KvEntity> result = kvRepository.findByTypeAndExtIds(type, List.of(extId), null);
    assertEquals(1, result.size());
    if (resultChecker != null) {
      resultChecker.accept(result.get(0));
    }
    return result.get(0).getId();
  }

  private void assertAudit(Long id, Consumer<KvAuditEntity> insertChecker, Consumer<KvAuditEntity> updateChecker) {
    Map<Long, List<KvAuditEntity>> res = auditRepository.findChanges(KvAuditEntity.class, KvEntity.class, List.of(id));
    assertEquals(1, res.size());

    List<KvAuditEntity> audit = res.get(id);
    assertEquals(updateChecker == null ? 1 : 2, audit.size());

    assertEquals(AuditAction.INSERT, audit.get(0).getAction());
    insertChecker.accept(audit.get(0));

    if (updateChecker != null) {
      assertEquals(AuditAction.UPDATE, audit.get(1).getAction());
      updateChecker.accept(audit.get(1));
    }
  }
}