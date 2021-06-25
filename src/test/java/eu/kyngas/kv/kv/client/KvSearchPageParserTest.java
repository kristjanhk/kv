package eu.kyngas.kv.kv.client;

import eu.kyngas.kv.core.BaseTest;
import eu.kyngas.kv.kv.client.KvSearchPageParser.Location;
import eu.kyngas.kv.kv.model.Kv;
import eu.kyngas.kv.kv.model.KvCounty;
import eu.kyngas.kv.kv.model.KvType;
import eu.kyngas.kv.util.NumberUtil;
import io.quarkus.test.junit.QuarkusTest;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class KvSearchPageParserTest extends BaseTest {
  private static final Path PATH = Paths.get("src", "test", "resources");

  private static Element element;

  @BeforeAll
  static void beforeAll() throws IOException {
    element = Jsoup.parse(Files.readString(PATH.resolve("kv_search_item.html")))
      .getElementsByClass("object-type-apartment object-item")
      .first();
  }

  @Test
  void parse() throws IOException {
    List<Kv> items = KvSearchPageParser.parse(KvType.APARTMENT_SALE,
                                              Jsoup.parse(Files.readString(PATH.resolve("kv_search_item.html"))));
    assertEquals(1, items.size());
  }

  @Test
  void getPageCount() throws IOException {
    Document pageDocument = Jsoup.parse(Files.readString(PATH.resolve("kv_search_page.html")));
    assertEquals(6, KvSearchPageParser.getPageCount(pageDocument));
  }

  @Test
  void logEx_returnsNullIfException() {
    Object result = KvSearchPageParser.logEx("test", () -> {
      throw new RuntimeException("test");
    });
    assertNull(result);
  }

  @Test
  void getExtId() {
    assertEquals(1234567, KvSearchPageParser.getExtId(element));
  }

  @Test
  void getImgLink() {
    assertEquals("https://example.com/test/123.jpg", KvSearchPageParser.getImgLink(element));
  }

  @Test
  void getAddress() {
    assertEquals("Tartumaa, Tartu, Tartu linn, Ränilinn, Kristalli tn 2a-10", KvSearchPageParser.getAddress(element));
  }

  @Test
  void getRooms() {
    assertEquals(2, KvSearchPageParser.getRooms(element));
  }

  @Test
  void getRoomSize() {
    assertEquals(NumberUtil.bigDecimal(45.6), KvSearchPageParser.getRoomSize(element));
  }

  @Test
  void getFloor_apartment_floorAndTotal() {
    Integer[] floor = KvSearchPageParser.getFloor(KvType.APARTMENT_SALE, List.of("korrus 3/5"));
    assertEquals(3, floor[0]);
    assertEquals(5, floor[1]);
  }

  @Test
  void getFloor_apartment_onlyFloor() {
    Integer[] floor = KvSearchPageParser.getFloor(KvType.APARTMENT_SALE, List.of("1. korrus"));
    assertEquals(1, floor[0]);
    assertNull(floor[1]);
  }

  @Test
  void getFloor_house_onlyTotal() {
    Integer[] floor = KvSearchPageParser.getFloor(KvType.HOUSE_SALE, List.of("2 korrust"));
    assertNull(floor[0]);
    assertEquals(2, floor[1]);
  }

  @Test
  void getFloor_null() {
    Integer[] floor = KvSearchPageParser.getFloor(KvType.APARTMENT_SALE, List.of());
    assertNull(floor);
  }

  @Test
  void getPublishDate_hour() {
    assertTime(LocalDateTime.now().minusHours(6), KvSearchPageParser.getPublishDate(element), 100);
  }

  @Test
  void getPublishDate_day() {
    Element el = Jsoup.parse("<p class=\"object-added-date\"><span>1 p tagasi</span></p>");
    assertTime(LocalDateTime.now().minusDays(1), KvSearchPageParser.getPublishDate(el), 100);
  }

  @Test
  void getPublishDate_minute() {
    Element el = Jsoup.parse("<p class=\"object-added-date\"><span>20 min tagasi</span></p>");
    assertTime(LocalDateTime.now().minusMinutes(20), KvSearchPageParser.getPublishDate(el), 100);
  }

  @Test
  void getPublishDate_empty_returnsNow() {
    Element el = Jsoup.parse("<p class=\"object-added-date\"><span></span></p>");
    assertTime(LocalDateTime.now(), KvSearchPageParser.getPublishDate(el), 100);
  }

  @Test
  void getPublishDate_unknown_throwsEx_isCatchedAndReturnsNull() {
    Element el = Jsoup.parse("<p class=\"object-added-date\"><span>12 aastat tagasi</span></p>");
    assertNull(KvSearchPageParser.getPublishDate(el));
  }

  @Test
  void getPrice() {
    assertEquals(NumberUtil.bigDecimal(45600), KvSearchPageParser.getPrice(element));
  }

  @Test
  void getPricePerM2() {
    assertEquals(NumberUtil.bigDecimal(1000), KvSearchPageParser.getPricePerM2(element));
  }

  @Test
  void getLocation_ad1() {
    Location location = KvSearchPageParser.getLocation("Tartumaa, Tartu, Tartu linn, Tammelinn, Soinaste 31A, INTERNETI PÜSIÜHENDUS HINNA SEES!");
    assertEquals(KvCounty.TARTUMAA, location.getCounty());
    assertEquals("Tartu", location.getArea());
    assertEquals("Tammelinn", location.getDistrict());
    assertEquals("Soinaste 31A", location.getAddress());
  }

  @Test
  void getLocation_ad2() {
    Location location = KvSearchPageParser.getLocation("Tartumaa, Tartu, Tartu linn, Raadi-Kruusamäe, RIDAELAMUBOKS, Pärna allee 17");
    assertEquals(KvCounty.TARTUMAA, location.getCounty());
    assertEquals("Tartu", location.getArea());
    assertEquals("Raadi-Kruusamäe", location.getDistrict());
    assertEquals("Pärna allee 17", location.getAddress());
  }

  @Test
  void getLocation_multipleDistrict() {
    Location location = KvSearchPageParser.getLocation("Tartumaa, Tartu, Tartu linn, Karlova, Kesklinna piiril, Kesk 1a-5");
    assertEquals(KvCounty.TARTUMAA, location.getCounty());
    assertEquals("Tartu", location.getArea());
    assertEquals("Kesklinna piiril", location.getDistrict());
    assertEquals("Kesk 1a-5", location.getAddress());
  }

  @Test
  void getLocation_districtLowercase() {
    Location location = KvSearchPageParser.getLocation("Tartumaa, Tartu, Tartu linn, Vaksali, vaksali, Kastani tn 38");
    assertEquals(KvCounty.TARTUMAA, location.getCounty());
    assertEquals("Tartu", location.getArea());
    assertEquals("Vaksali", location.getDistrict());
    assertEquals("Kastani 38", location.getAddress());
  }

  @Test
  void getLocation_addressLowercase() {
    Location location = KvSearchPageParser.getLocation("Tartumaa, Tartu, Tartu linn, pargi 10");
    assertEquals(KvCounty.TARTUMAA, location.getCounty());
    assertEquals("Tartu", location.getArea());
    assertEquals("Tartu linn", location.getDistrict());
    assertEquals("Pargi 10", location.getAddress());
  }

  @Test
  void getLocation_addressMissing() {
    Location location = KvSearchPageParser.getLocation("Tartumaa, Tartu, Tammelinn");
    assertEquals(KvCounty.TARTUMAA, location.getCounty());
    assertEquals("Tartu", location.getArea());
    assertEquals("Tammelinn", location.getDistrict());
    assertNull(location.getAddress());
  }

  @Test
  void getLocation_test1() {
    Location location = KvSearchPageParser.getLocation(" Tartumaa, Tartu, Veeriku, Saarekese 8 ");
    assertEquals(KvCounty.TARTUMAA, location.getCounty());
    assertEquals("Tartu", location.getArea());
    assertEquals("Veeriku", location.getDistrict());
    assertEquals("Saarekese 8", location.getAddress());
  }

  @Test
  void getBooked() {
    assertTrue(KvSearchPageParser.getBooked(element));
  }

  @Test
  void getYear() {
    assertEquals(2010, KvSearchPageParser.getYear(List.of("ehitusaasta 2010")));
  }

  @Test
  void getDetails() {
    List<String> details = KvSearchPageParser.getDetails(element);
    assertEquals("korrus 3/5", details.get(0));
    assertEquals("test1", details.get(1));
  }

  @Test
  void getDescription() {
    assertEquals("Test2", KvSearchPageParser.getDescription(element));
  }
}