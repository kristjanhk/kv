package eu.kyngas.kv.util.mapper;

public interface BaseMapper<S, T> extends OneWayMapper<S, T>, OneWayInverseMapper<S, T>, CloneMapper<S, T> {
}
