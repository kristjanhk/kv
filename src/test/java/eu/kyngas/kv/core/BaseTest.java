package eu.kyngas.kv.core;


import org.junit.jupiter.api.Assertions;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.assertTrue;

public abstract class BaseTest {

  protected void assertTime(LocalDateTime expected, LocalDateTime actual, int delta) {
    if (expected == null && actual == null) {
      return;
    }
    if (expected == null || actual == null) {
      Assertions.fail();
    }
    long millis1 = expected.toInstant(ZoneOffset.UTC).toEpochMilli();
    long millis2 = actual.toInstant(ZoneOffset.UTC).toEpochMilli();
    assertTrue(Math.abs(millis1 - millis2) <= delta);
  }
}
