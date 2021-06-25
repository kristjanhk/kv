package eu.kyngas.kv.core.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import io.quarkiverse.hibernate.types.jackson.JacksonMapper;
import io.quarkiverse.hibernate.types.json.JsonMapper;
import io.quarkus.arc.Unremovable;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;

@Dependent
public class PostgresJsonMapperProducer {

  @Produces
  @Unremovable
  public JsonMapper jsonb(ObjectMapper objectMapper) {
    ObjectMapper mapper = objectMapper.copy();
    mapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
    return new JacksonMapper(mapper);
  }
}
