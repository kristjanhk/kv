package eu.kyngas.kv.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;

public class Predicates {

  public static <T> Predicate<T> withPrevious(BiPredicate<T, T> predicate) {
    AtomicReference<T> box = new AtomicReference<>();
    return item -> {
      boolean result = predicate.test(item, box.get());
      box.set(item);
      return result;
    };
  }

  public static <T, S> Predicate<T> distinctBy(Function<T, S> keyExtractor) {
    Map<S, Boolean> map = new ConcurrentHashMap<>();
    return t -> map.putIfAbsent(keyExtractor.apply(t), true) == null;
  }
}
