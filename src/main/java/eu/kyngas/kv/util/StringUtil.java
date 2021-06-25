package eu.kyngas.kv.util;

public class StringUtil {

  public static String capitalizeOnly(String input) {
    return input == null || input.isBlank()
      ? input
      : input.substring(0, 1).toUpperCase() + input.substring(1).toLowerCase();
  }

  public static String capitalize(String input) {
    return input == null || input.isBlank() ? input : input.substring(0, 1).toUpperCase() + input.substring(1);
  }

  public static String booleanShort(Boolean bool) {
    return bool == null ? "" : bool ? "t" : "f";
  }
}
