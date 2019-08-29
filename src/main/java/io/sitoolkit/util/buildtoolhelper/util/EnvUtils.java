package io.sitoolkit.util.buildtoolhelper.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EnvUtils {

  private EnvUtils() {
  }

  public static Map<String, String> loadEnv() {
    Map<String, String> map = new HashMap<>();

    Path envPath = resolveEnvPath();

    if (!envPath.toFile().exists()) {
      return map;
    }

    Properties env = new Properties();

    log.info("Load env file:{}", envPath.toAbsolutePath());

    try (InputStream is = Files.newInputStream(envPath)) {

      env.load(is);
      env.stringPropertyNames().stream()
          .forEach(propertyName -> map.put(propertyName, env.getProperty(propertyName)));

      return map;

    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }

  }

  static Path resolveEnvPath() {
    return Paths.get(".env");
  }
}
