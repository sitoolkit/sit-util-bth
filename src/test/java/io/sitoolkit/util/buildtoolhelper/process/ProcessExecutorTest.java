package io.sitoolkit.util.buildtoolhelper.process;

import java.io.File;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

public class ProcessExecutorTest {

  @Test
  public void testLargeStderr() throws URISyntaxException {

    Path mainClassPathAbs = Paths.get(getClass().getResource("StderrPrinter.class").toURI());
    Path baseDir = Paths.get("target", "test-classes").toAbsolutePath();
    Path mainClassPath = baseDir.relativize(mainClassPathAbs);
    String mainClass = mainClassPath.toString();
    mainClass = StringUtils.substringBeforeLast(mainClass, ".").replace(File.separator, ".");

    ProcessCommand command =
        new ProcessCommand()
            .command("java")
            .currentDirectory(baseDir)
            .args(mainClass);

    ProcessExecutor executor = new ProcessExecutor();
    executor.execute(command);

  }
}
