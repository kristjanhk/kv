package eu.kyngas.kv.kv.model;

import eu.kyngas.kv.kv.model.KvEntity.KvEntityBuilder;
import eu.kyngas.kv.kv.service.KvRepository;
import eu.kyngas.kv.util.NumberUtil;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.function.UnaryOperator;

import static java.util.function.UnaryOperator.identity;

/**
 * {@link Kv}
 * {@link KvEntity}
 */
@ApplicationScoped
public class KvFixture {
  @Inject
  KvRepository kvRepository;
  @Inject
  KvMapper kvMapper;

  public Kv createKv() {
    return createKv(identity());
  }

  public Kv createKv(UnaryOperator<KvEntityBuilder> operator) {
    KvEntity entity = operator.apply(defaultKvEntityBuilder()).build();
    return kvMapper.convert(entity);
  }

  public KvEntity saveKvEntity() {
    return saveKvEntity(identity());
  }

  public KvEntity saveKvEntity(UnaryOperator<KvEntityBuilder> operator) {
    KvEntity entity = operator.apply(defaultKvEntityBuilder()).build();
    kvRepository.persistAndFlush(entity);
    return entity;
  }

  private static KvEntityBuilder defaultKvEntityBuilder() {
    return KvEntity.builder()
      .extId(-new Random().nextLong())
      .type(KvType.APARTMENT_SALE)
      .publishDate(LocalDateTime.now())
      .booked(false)
      .removed(false)
      .link("test_link")
      .imgLink("test_img_link")
      .price(NumberUtil.bigDecimal(10))
      .pricePerM2(NumberUtil.bigDecimal(1))
      .county(KvCounty.TARTUMAA)
      .area("Tartu")
      .district("Kesklinn")
      .address("Pikk 78")
      .rooms(2)
      .roomSize(NumberUtil.bigDecimal(10))
      .floor(4)
      .floorTotal(5)
      .year(1970)
      .details("korrus 4/5, ehitusaasta 1970, test1, test2")
      .description("test_description");
  }
}
