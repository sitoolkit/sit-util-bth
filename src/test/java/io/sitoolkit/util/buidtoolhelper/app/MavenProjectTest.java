package io.sitoolkit.util.buidtoolhelper.app;

import java.nio.file.Paths;

import org.junit.Test;

import io.sitoolkit.util.buidtoolhelper.maven.MavenProject;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MavenProjectTest {

    @Test
    public void test() {
        MavenProject.load(Paths.get(".")).mvnw("dependency:build-classpath")
                .stdout(line -> log.info(line)).execute();
    }

}
