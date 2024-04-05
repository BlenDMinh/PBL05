package common;

public final class Env {
  public static String POSTGRES_USER = System.getenv("POSTGRES_USER");
  public static String POSTGRES_PASSWORD = System.getenv("POSTGRES_PASSWORD");
  public static String POSTGRES_DB = System.getenv("POSTGRES_DB");
  public static String DB_HOST = System.getenv("DB_HOST");
  public static String[] CORS_ALLOWS = System.getenv("CORS_ALLOWS").split(",");

  private Env() {
  }
}
