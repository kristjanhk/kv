package eu.kyngas.kv.util.mapper;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public interface OneWayMapper<S, T> {

  T convert(S source);

  default List<T> convert(Collection<S> collection) {
    return collection == null
      ? Collections.emptyList()
      : collection.stream().map(this::convert).collect(Collectors.toList());
  }

}
