package eu.kyngas.kv.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.quarkus.jackson.ObjectMapperCustomizer;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import javax.inject.Singleton;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static com.fasterxml.jackson.databind.PropertyNamingStrategy.SNAKE_CASE;
import static java.util.Arrays.asList;

@SuppressWarnings({"deprecation", "unused"})
@Singleton
public class MapperCustomizer implements ObjectMapperCustomizer {
  public static final ObjectMapper jsonMapper = Json.mapper;
  public static final ObjectMapper jsonPrettyMapper = Json.prettyMapper;

  static {
    asList(jsonMapper, jsonPrettyMapper).forEach(MapperCustomizer::configure);
  }

  @Override
  public void customize(ObjectMapper mapper) {
    configure(mapper);
  }

  private static void configure(ObjectMapper mapper) {
    mapper.setPropertyNamingStrategy(SNAKE_CASE);
    mapper.registerModule(new JavaTimeModule());
    mapper.registerModule(new SimpleModule()
                            .addDeserializer(JsonObject.class, deserializer(Map.class, JsonObject::new))
                            .addDeserializer(JsonArray.class, deserializer(List.class, JsonArray::new)));
  }

  private static <T, S> JsonDeserializer<T> deserializer(Class<S> fromClass, Function<S, T> mapper) {
    return new JsonDeserializer<>() {
      @Override
      public T deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        return mapper.apply(p.getCodec().readValue(p, fromClass));
      }
    };
  }
}
