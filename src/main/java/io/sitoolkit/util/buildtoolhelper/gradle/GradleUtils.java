package io.sitoolkit.util.buildtoolhelper.gradle;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;

import io.sitoolkit.util.buildtoolhelper.UnExpectedException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GradleUtils {

    private static String gradleCommand = "";

    public static String getCommand() {
        if (StringUtils.isEmpty(gradleCommand)) {
            throw new UnExpectedException("gradlew is not installed");
        } else {
            return gradleCommand;
        }
    }

    public static synchronized void findAndInstall() {
        findAndInstall(Paths.get("."));
    }

    public static synchronized void findAndInstall(Path baseDir) {
        if (StringUtils.isEmpty(gradleCommand)) {
            gradleCommand = find(baseDir);
            if (StringUtils.isEmpty(gradleCommand)) {
                throw new UnsupportedOperationException("gradlew not installed and gradlew auto install is not supported");
            }
        }
        log.info("gradlew command is '" + gradleCommand + "'");
    }

    public static String find(Path baseDir) {
        Path gradlew = SystemUtils.IS_OS_WINDOWS ? baseDir.resolve("gradlew.bat")
                : baseDir.resolve("gradlew");

        if (gradlew.toFile().exists()) {
            return gradlew.toAbsolutePath().toString();
        }

        return "";
    }

}
