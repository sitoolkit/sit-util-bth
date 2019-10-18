package io.sitoolkit.util.buildtoolhelper.process;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

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
}
