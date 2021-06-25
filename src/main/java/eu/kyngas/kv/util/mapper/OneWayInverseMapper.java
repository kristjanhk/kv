package eu.kyngas.kv.util.mapper;

import org.mapstruct.InheritInverseConfiguration;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public interface OneWayInverseMapper<S, T> {

  @InheritInverseConfiguration(name = "convert")
  S inverse(T target);

  default List<S> inverse(Collection<T> collection) {
    return collection == null
      ? Collections.emptyList()
      : collection.stream().map(this::inverse).collect(Collectors.toList());
  }

}
