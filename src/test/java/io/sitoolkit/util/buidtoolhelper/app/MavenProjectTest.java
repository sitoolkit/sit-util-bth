package io.sitoolkit.util.buidtoolhelper.app;

import java.nio.file.Paths;

import org.junit.Test;

public class MavenProjectTest {

    @Test
    public void test() {
        MavenProject.load(Paths.get(".")).mvnw("dependency:build-classpath").execute();
    }

}
