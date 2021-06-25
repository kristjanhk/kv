package eu.kyngas.kv.util.mapper;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static java.util.stream.Collectors.toList;

public interface CloneMapper<T, S> {

  T cloneSource(T source);

  S cloneTarget(S target);

  default List<T> cloneSource(Collection<T> collection) {
    return collection == null
      ? Collections.emptyList()
      : collection.stream().map(this::cloneSource).collect(toList());
  }

  default List<S> cloneTarget(Collection<S> collection) {
    return collection == null
      ? Collections.emptyList()
      : collection.stream().map(this::cloneTarget).collect(toList());
  }
}
