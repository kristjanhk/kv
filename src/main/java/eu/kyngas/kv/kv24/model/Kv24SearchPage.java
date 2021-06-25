package eu.kyngas.kv.kv24.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class Kv24SearchPage {
  @NotNull
  private Integer currentPage;
  @NotNull
  private Integer lastPage;
  @NotNull
  private Integer perPage;
  @NotNull
  private Integer total;
  @NotNull
  private Integer from;
  @NotNull
  private Integer to;
  @NotNull
  private String firstPageUrl;
  @NotNull
  private String lastPageUrl;
  private String prevPageUrl;
  @NotNull
  private String nextPageUrl;
  @NotNull
  private String hash;
  @NotNull
  private List<Data> data;

  @lombok.Data
  public static class Data {
    @NotNull
    private Long id;
    @NotNull
    private Integer objektiTyypId;
    @NotNull
    private List<String> commercialTypes; // ?
    @NotNull
    private Integer tehinguTyypId;
    @NotNull
    private Long kvId;
    @NotNull
    private String staatus;
    @NotNull
    private Boolean broneeritud;
    @NotNull
    private Integer userId;
    @NotNull
    private Double hind;
    @NotNull
    private LocalDateTime createdAt; // 2021-05-25T12:00:30.000000Z
    private Double bargainprice; // ?
    private String videoUrl; // ?
    @NotNull
    private Boolean isSold;
    @NotNull
    @JsonProperty("show_A6_location")
    private Integer showA6Location;
    @NotNull
    private String bureauName;
    @NotNull
    private Long bureauId;
    @NotNull
    private String userFullName;
    private String externalLink;
    @NotNull
    private Integer sortOrder; // ?
    @NotNull
    private Integer exclusive; // ?
    @NotNull
    private String activeClientDayString;
    @NotNull
    private Integer sortDays; // ?
    @NotNull
    private List<String> intendedUses; // ?
    private String distanceClass; // ?
    @NotNull
    private Integer isWithoutCollateral; // ?
    @NotNull
    private Double pricePerM2;
    @NotNull
    private String permalink;
    @NotNull
    private Boolean isFavorite;
    @NotNull
    private String detailSummaryString; // 2/4 korrus, ehitusaasta 1959, kivimaja
    @NotNull
    private List<Hoone> hoone;
    @NotNull
    private List<Image> images;
    private Info infoReklaamlauseCurrent;
    @NotNull
    private Info infoLisainfoCurrent; // content html
    @NotNull
    private Address address;
    @NotNull
    private List<Kataster> katastrinumber;
    @NotNull
    private Ruum ruum;
    @NotNull
    private User user;

    @lombok.Data
    public static class Hoone {
      @NotNull
      private Long id;
      @NotNull
      private String korruseid; // int
      private Integer condition; // 4 = keskmine?
    }

    @lombok.Data
    public static class Image {
      @NotNull
      private Long id;
      @NotNull
      private Long relId;
      @NotNull
      private String urlSmall;
      @NotNull
      private String urlMedium;
      @NotNull
      private String urlLarge;
    }

    @lombok.Data
    public static class Info {
      @NotNull
      private Long id;
      @NotNull
      private String tyyp; // reklaamlause, lisainfo
      @NotNull
      private String lang; // et
      @NotNull
      private String content;
    }

    @lombok.Data
    public static class Address {
      @NotNull
      private Long id;
      @JsonProperty("A0")
      private String a0; // riik
      @NotNull
      @JsonProperty("A1")
      private String a1; // maakond (Tartu maakond)
      @NotNull
      @JsonProperty("A2")
      private String a2; // kihelkond? (Tartu linn)
      @NotNull
      @JsonProperty("A3")
      private String a3; // kihelkond? (Tartu linn)
      @NotNull
      @JsonProperty("A4")
      private String a4; // linnaosa? (Annelinn)
      @NotNull
      @JsonProperty("A5")
      private String a5; // tänav (Anne tn)
      @JsonProperty("A6")
      private String a6; // tavaliselt tühi
      @NotNull
      @JsonProperty("A7")
      private String a7; // maja nr (53)
      @JsonProperty("A8")
      private String a8; // korteri nr? (tavaliselt tühi)
      @NotNull
      private String coordinatesLat; // double
      @NotNull
      private String coordinatesLng; // double
      private String address;
      @NotNull
      private String shortAddress;
    }

    @lombok.Data
    public static class Kataster {
      @NotNull
      private Long id;
      private String katastrinumber; // 79516:022:0011
      private String pindala; // double
    }

    @lombok.Data
    public static class Ruum {
      @NotNull
      private Long id;
      @NotNull
      private String pindala; // double
      @NotNull
      private Integer tubadeArv;
      private Integer korrus;
    }

    @lombok.Data
    public static class User {
      @NotNull
      private String name;
      @NotNull
      private String lastname;
      @NotNull
      private String role; // maakler, buroohaldur
      @NotNull
      private Broker brokerData;

      @lombok.Data
      public static class Broker {
        @NotNull
        private Long id;
        @NotNull
        private Bureau bureau;

        @lombok.Data
        public static class Bureau {
          @NotNull
          private Long id;
          @NotNull
          private String name;
          private Long parentId;
          @NotNull
          private Boolean isPrimary;
          private Object parent; // ?
        }
      }
    }
  }
}
