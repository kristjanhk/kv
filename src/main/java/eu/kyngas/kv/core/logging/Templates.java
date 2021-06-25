package eu.kyngas.kv.core.logging;

import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;

@CheckedTemplate
public class Templates {

  public static native TemplateInstance request(LogRequest request);

  public static native TemplateInstance response(LogResponse response);
}
