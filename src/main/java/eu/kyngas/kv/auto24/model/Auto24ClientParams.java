package eu.kyngas.kv.auto24.model;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.*;

import javax.ws.rs.QueryParam;

@RegisterForReflection
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Auto24ClientParams {
  @QueryParam("otsi")
  private final String otsi = "otsi";

  @QueryParam("ae")
  private String order;
  @QueryParam("a")
  private int vehicleType;
  @QueryParam("bw")
  private int model;
  @QueryParam("b")
  private int mark;
  @QueryParam("af")
  private int showTotal;
  @QueryParam("f1")
  private int minYear;
  @QueryParam("f2")
  private int maxYear;
  @QueryParam("by")
  private int auction;
  @QueryParam("ad")
  private int maxAge;
  @QueryParam("ab")
  private int location;
  @QueryParam("p")
  private int driveType;
  @QueryParam("i")
  private int transmissionType;
  @QueryParam("h")
  private int fuelType;
  @QueryParam("g1")
  private int minPrice;
  @QueryParam("g2")
  private int maxPrice;
  @QueryParam("k1")
  private int minPower;
  @QueryParam("k2")
  private int maxPower;
  @QueryParam("l1")
  private int minDistance;
  @QueryParam("l2")
  private int maxDistance;
  @QueryParam("j")
  private int bodyType;
  @QueryParam("c")
  private String other;

  public static Auto24ClientParams createLexusParams() {
    return builder()
      .vehicleType(VehicleType.CAR.getValue())
      .model(LexusModel.ALL.getValue())
      .mark(Mark.LEXUS.getValue())
      .showTotal(ShowTotal._200.getValue())
      .minYear(2006)
      .build();
  }

  public static Auto24ClientParams createAllParams() {
    return builder().vehicleType(VehicleType.CAR.getValue()).build();
  }

  @Getter
  @RequiredArgsConstructor
  private enum ShowTotal {
    _20(20),
    _50(50),
    _100(100),
    _200(200);
    private final int value;
  }

  @Getter
  @RequiredArgsConstructor
  private enum VehicleType {
    CAR(101102);
    private final int value;
  }

  @Getter
  @RequiredArgsConstructor
  private enum Mark {
    ALL(0),
    LEXUS(35);
    private final int value;
  }

  @Getter
  @RequiredArgsConstructor
  private enum LexusModel {
    ALL(0),
    LS_ALL(523);
    private final int value;
  }

  @Getter
  @RequiredArgsConstructor
  private enum Auction {
    ALL(0),
    YES(1),
    NO(2);
    private final int value;
  }

  @Getter
  @RequiredArgsConstructor
  private enum Order {
    BY_ADDED_DATE(1),
    BY_PRICE(2),
    BY_YEAR(3),
    BY_MARK(4);
    private final int value;
  }

  @Getter
  @RequiredArgsConstructor
  private enum Age {
    ALL(0),
    _1_DAY(1),
    _2_DAYS(2),
    _3_DAYS(3),
    _7_DAYS(7);
    private final int value;
  }

  @Getter
  @RequiredArgsConstructor
  private enum Location {
    ALL(0),
    ESTONIA(-1),
    TARTU(1),
    TALLINN(3);
    private final int value;
  }

  @Getter
  @RequiredArgsConstructor
  private enum DriveType {
    ALL(0),
    FWD(1),
    RWD(2),
    AWD(3);
    private final int value;
  }

  @Getter
  @RequiredArgsConstructor
  private enum TransmissionType {
    ALL(0),
    MANUAL(1),
    AUTOMATIC(2),
    HYBRID(3);
    private final int value;
  }

  @Getter
  @RequiredArgsConstructor
  private enum FuelType {
    ALL(0),
    GASOLINE(1),
    DIESEL(2),
    ELECTRIC(6),
    LPG(4),
    CNG(9),
    LNG(11),
    HYBRID(5);
    private final int value;
  }
}
