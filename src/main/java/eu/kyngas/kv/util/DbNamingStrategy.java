package eu.kyngas.kv.util;

import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;

@SuppressWarnings("unused")
public class DbNamingStrategy extends PhysicalNamingStrategyStandardImpl {
  private static final String CAMEL_CASE_REGEX = "([a-z]+)([A-Z]+)";
  private static final String SNAKE_CASE_PATTERN = "$1\\_$2";

  public static final DbNamingStrategy INSTANCE = new DbNamingStrategy();

  @Override
  public Identifier toPhysicalCatalogName(Identifier name, JdbcEnvironment context) {
    return format(super.toPhysicalCatalogName(name, context));
  }

  @Override
  public Identifier toPhysicalSchemaName(Identifier name, JdbcEnvironment context) {
    return format(super.toPhysicalSchemaName(name, context));
  }

  @Override
  public Identifier toPhysicalTableName(Identifier name, JdbcEnvironment context) {
    return format(super.toPhysicalTableName(name, context));
  }

  @Override
  public Identifier toPhysicalSequenceName(Identifier name, JdbcEnvironment context) {
    return format(super.toPhysicalSequenceName(name, context));
  }

  @Override
  public Identifier toPhysicalColumnName(Identifier name, JdbcEnvironment context) {
    return format(super.toPhysicalColumnName(name, context));
  }

  private Identifier format(Identifier identifier) {
    if (identifier == null) {
      return null;
    }
    String name = identifier.getText();
    String formattedName = name.replaceAll(CAMEL_CASE_REGEX, SNAKE_CASE_PATTERN).toLowerCase();
    return !formattedName.equals(name) ? Identifier.toIdentifier(formattedName, identifier.isQuoted()) : identifier;
  }
}
