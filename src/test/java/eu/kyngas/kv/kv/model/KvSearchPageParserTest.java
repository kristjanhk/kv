package eu.kyngas.kv.kv.model;

import eu.kyngas.kv.kv.model.KvSearchPageParser.Location;
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
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class KvSearchPageParserTest {
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
    List<KvSearchPageItem> items =
      KvSearchPageParser.parse(Jsoup.parse(Files.readString(PATH.resolve("kv_search_item.html"))), null);

    assertEquals(1, items.size());
  }

  @Test
  void getPageCount() throws IOException {
    Document pageDocument = Jsoup.parse(Files.readString(PATH.resolve("kv_search_page.html")));
    assertEquals(6, KvSearchPageParser.getPageCount(pageDocument));
  }

  @Test
  void getId() {
    assertEquals(1234567, KvSearchPageParser.getId(element));
  }

  @Test
  void getImgLink() {
    assertEquals("https://example.com/test/123.jpg", KvSearchPageParser.getImgLink(element));
  }

  @Test
  void getAddress() {
    assertEquals("Tartumaa, Tartu, Tartu linn, Ränilinn, Kristalli", KvSearchPageParser.getAddress(element));
  }

  @Test
  void getDetails() {
    assertEquals("Korrus 3/5, test1", KvSearchPageParser.getDetails(element));
  }

  @Test
  void getRooms() {
    assertEquals(2, KvSearchPageParser.getRooms(element));
  }

  @Test
  void getRoomSize() {
    assertEquals(45.6, KvSearchPageParser.getRoomSize(element));
  }

  @Test
  void getRoomFloor() {
    assertEquals(3, KvSearchPageParser.getRoomFloor(element));
  }

  @Test
  void getTotalFloor() {
    assertEquals(5, KvSearchPageParser.getTotalFloor(element));
  }

  @Test
  void getPublishDate() {
    assertEquals(LocalDateTime.now().truncatedTo(ChronoUnit.HOURS).minusHours(6),
                 KvSearchPageParser.getPublishDate(element));
  }

  @Test
  void getPrice() {
    assertEquals(45600, KvSearchPageParser.getPrice(element));
  }

  @Test
  void getPriceM2() {
    assertEquals(1000, KvSearchPageParser.getPriceM2(element));
  }

  @Test
  void getLocation_ad1() {
    Location location = KvSearchPageParser.getLocation("Tartumaa, Tartu, Tartu linn, Tammelinn, Soinaste 31A, INTERNETI PÜSIÜHENDUS HINNA SEES!");
    assertEquals("Tartumaa", location.getCounty());
    assertEquals("Tartu", location.getArea());
    assertEquals("Tammelinn", location.getDistrict());
    assertEquals("Soinaste 31A", location.getAddress());
  }

  @Test
  void getLocation_ad2() {
    Location location = KvSearchPageParser.getLocation("Tartumaa, Tartu, Tartu linn, Raadi-Kruusamäe, RIDAELAMUBOKS, Pärna allee 17");
    assertEquals("Tartumaa", location.getCounty());
    assertEquals("Tartu", location.getArea());
    assertEquals("Raadi-Kruusamäe", location.getDistrict());
    assertEquals("Pärna allee 17", location.getAddress());
  }

  @Test
  void getLocation_multipleDistrict() {
    Location location = KvSearchPageParser.getLocation("Tartumaa, Tartu, Tartu linn, Karlova, Kesklinna piiril, Kesk 1a-5");
    assertEquals("Tartumaa", location.getCounty());
    assertEquals("Tartu", location.getArea());
    assertEquals("Kesklinna piiril", location.getDistrict());
    assertEquals("Kesk 1a-5", location.getAddress());
  }

  @Test
  void getLocation_districtLowercase() {
    Location location = KvSearchPageParser.getLocation("Tartumaa, Tartu, Tartu linn, Vaksali, vaksali, Kastani tn 38");
    assertEquals("Tartumaa", location.getCounty());
    assertEquals("Tartu", location.getArea());
    assertEquals("Vaksali", location.getDistrict());
    assertEquals("Kastani tn 38", location.getAddress());
  }

  @Test
  void getLocation_addressLowercase() {
    Location location = KvSearchPageParser.getLocation("Tartumaa, Tartu, Tartu linn, pargi 10");
    assertEquals("Tartumaa", location.getCounty());
    assertEquals("Tartu", location.getArea());
    assertEquals("Tartu linn", location.getDistrict());
    assertEquals("Pargi 10", location.getAddress());
  }

  @Test
  void getLocation_addressMissing() {
    Location location = KvSearchPageParser.getLocation("Tartumaa, Tartu, Tammelinn");
    assertEquals("Tartumaa", location.getCounty());
    assertEquals("Tartu", location.getArea());
    assertEquals("Tammelinn", location.getDistrict());
    assertNull(location.getAddress());
  }
}