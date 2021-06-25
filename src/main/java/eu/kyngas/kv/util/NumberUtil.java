package eu.kyngas.kv.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class NumberUtil {

  public static BigDecimal bigDecimal(String input) {
    return new BigDecimal(input).setScale(2, RoundingMode.HALF_UP);
  }

  public static BigDecimal bigDecimal(double input) {
    return new BigDecimal(input).setScale(2, RoundingMode.HALF_UP);
  }
}
