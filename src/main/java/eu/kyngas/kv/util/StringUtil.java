package eu.kyngas.kv.util;

public class StringUtil {

  public static String capitalize(String input) {
    return input == null || input.isBlank() ? input : input.substring(0, 1).toUpperCase() + input.substring(1);
  }
}
