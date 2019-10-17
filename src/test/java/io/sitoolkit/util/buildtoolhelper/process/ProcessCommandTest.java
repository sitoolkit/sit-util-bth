package io.sitoolkit.util.buildtoolhelper.process;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import io.sitoolkit.util.buildtoolhelper.maven.MavenProject;
import io.sitoolkit.util.buildtoolhelper.maven.MavenUtils;
import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.SystemUtils;
import org.junit.Test;

@Slf4j
public class ProcessCommandTest {

  @Test
  public void testExitCode() {

    if (SystemUtils.IS_OS_MAC) {
      int exitCode = new ProcessCommand().command("bash").args("NoSuchFile").execute();
      assertThat(exitCode, is(127));
    }

  }

  @Test
  public void testStdoutLog() {
    MavenProject mavenProject = MavenProject.load(".");
    List<String> classPaths = new ArrayList<>();
    mavenProject.mvnw("compile", "-X", "-Dmaven.test.skip")
            .stdout(line -> {
              if (line.endsWith(".jar")) {
                classPaths.add(line.trim());
              }
            }).execute();

    String classPath = classPaths.stream()
            .reduce((s1, s2) -> String.join(File.pathSeparator, s1, s2))
            .orElse("");

    File lombokJarFile = MavenUtils.getLocalRepository().toPath()
        .resolve("org").resolve("projectlombok")
        .resolve("lombok").resolve("1.18.8").resolve("lombok-1.18.8.jar").toFile();


    String srcPath = Paths.get(".").resolve("src").resolve("main").resolve("java")
        .toAbsolutePath().normalize().toString();

    String targetPath = Paths.get(".")
        .resolve("target").resolve("generated-sources").resolve("sit-util-bth").resolve("delombok")
        .toAbsolutePath().normalize().toString();

    assert lombokJarFile.exists();

    // エラーが発生するコード
    int exitCode1 = new ProcessCommand().command("java")
        .args("-jar", lombokJarFile.getAbsolutePath(), "delombok", "pretty", srcPath, "-d", targetPath)
        .stdout(log::info).stderr(log::warn)
        .execute();

    assertThat(exitCode1, is(0));

    // エラーが発生しないコード
    int exitCode2 = new ProcessCommand().command("java")
            .args("-jar", lombokJarFile.getAbsolutePath(),
                    "delombok", "-e", "UTF-8", "-c", classPath, srcPath, "-d", targetPath)
            .stdout(log::info).stderr(log::warn)
            .execute();

    assertThat(exitCode2, is(0));

    assert new File(targetPath).exists();
  }
}
