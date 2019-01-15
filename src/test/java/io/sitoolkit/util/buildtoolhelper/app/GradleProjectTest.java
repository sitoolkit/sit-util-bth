package io.sitoolkit.util.buildtoolhelper.app;

import java.nio.file.Paths;

import org.junit.Test;

import io.sitoolkit.util.buildtoolhelper.gradle.GradleProject;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GradleProjectTest {

    @Test
    public void test() {
        GradleProject.load(Paths.get("gradle-sample")).gradlew("clean", "jar")
                .stdout(line -> log.info(line)).execute();
    }

}
