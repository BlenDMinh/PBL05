package common;

public final class Env {
  public static String POSTGRES_USER = System.getenv("POSTGRES_USER");
  public static String POSTGRES_PASSWORD = System.getenv("POSTGRES_PASSWORD");
  public static String POSTGRES_DB = System.getenv("POSTGRES_DB");
  public static String DB_HOST = System.getenv("DB_HOST");
  public static String[] CORS_ALLOWS = System.getenv("CORS_ALLOWS").split(",");
  public static String FROM_EMAIL = System.getenv("FROM_EMAIL");
  public static String EMAIL_PASSWORD = System.getenv("EMAIL_PASSWORD");

  private Env() {
  }
}
