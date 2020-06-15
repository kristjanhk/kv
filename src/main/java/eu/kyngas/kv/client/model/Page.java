package eu.kyngas.kv.client.model;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Data;
import org.jsoup.nodes.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@Data
@RegisterForReflection
public class Page<T> {
  private final T result;
  private final int index;
  private final Integer size;
  private final Function<Integer, Document> pageFetcher;
  private final Function<Document, Integer> pageCountParser;
  private final Function<Document, T> resultParser;

  public Page(int index,
              Integer size,
              Function<Integer, Document> pageFetcher,
              Function<Document, Integer> pageCountParser,
              Function<Document, T> resultParser) {
    this.index = index;
    this.pageFetcher = pageFetcher;
    this.pageCountParser = pageCountParser;
    this.resultParser = resultParser;

    Document document = pageFetcher.apply(index);
    this.size = size != null ? size : pageCountParser.apply(document);
    this.result = resultParser.apply(document);
  }

  public boolean isLast() {
    return index + 1 > size;
  }

  public Page<T> next() {
    return isLast() ? null : new Page<>(index + 1, size, pageFetcher, pageCountParser, resultParser);
  }

  public List<T> collect() {
    if (isLast()) {
      return new ArrayList<>(List.of(result));
    }
    List<T> results = next().collect();
    results.add(0, result);
    return results;
  }
}
