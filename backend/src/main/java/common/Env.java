package common;

public final class Env {
  public static String POSTGRES_USER = System.getenv("POSTGRES_USER");
  public static String POSTGRES_PASSWORD = System.getenv("POSTGRES_PASSWORD");
  public static String POSTGRES_DB = System.getenv("POSTGRES_DB");
  public static String DB_HOST = System.getenv("DB_HOST");
  public static String[] CORS_ALLOWS = System.getenv("CORS_ALLOWS").split(",");
  public static String FROM_EMAIL = System.getenv("FROM_EMAIL");
  public static String EMAIL_PASSWORD = System.getenv("EMAIL_PASSWORD");
  public static String MINIO_ENDPOINT = System.getenv("MINIO_ENDPOINT");
  public static String MINIO_ROOT_USER = System.getenv("MINIO_ROOT_USER");
  public static String MINIO_ROOT_PASSWORD = System.getenv("MINIO_ROOT_PASSWORD");
  public static String MINIO_PUBLIC_ENDPOINT = System.getenv("MINIO_PUBLIC_ENDPOINT");
  public static String AWS_ACCESS_KEY = System.getenv("AWS_ACCESS_KEY");
  public static String AWS_SECRET_KEY = System.getenv("AWS_SECRET_KEY");
  public static String S3_BUCKET_NAME = System.getenv("S3_BUCKET_NAME");
  public static String S3_ACCESS_POINT = System.getenv("S3_ACCESS_POINT");

  private Env() {
  }
}
