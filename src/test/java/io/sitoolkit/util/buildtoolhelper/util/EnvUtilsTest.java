package io.sitoolkit.util.buildtoolhelper.util;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import org.junit.Test;

public class EnvUtilsTest {

  @Test
  public void test() throws IOException, URISyntaxException {
    assertThat(EnvUtils.loadEnv().isEmpty(), is(true));

    Path originalEnv = Paths.get(getClass().getResource("original.env").toURI());
    Path targetEnv = Paths.get(".env");
    targetEnv.toFile().deleteOnExit();
    Files.copy(originalEnv, targetEnv);

    Map<String, String> loadedEnv = EnvUtils.loadEnv();

    assertThat(loadedEnv.get("key"), is("value"));
  }
}
