package io.sitoolkit.util.buidtoolhelper.maven;

import java.nio.file.Path;
import java.nio.file.Paths;

import io.sitoolkit.util.buidtoolhelper.process.ProcessCommand;

public class MavenProject {

    private Path projectDir;

    private MavenProject(Path projectDir) {
        super();
        this.projectDir = projectDir.toAbsolutePath().normalize();
    }

    public ProcessCommand mvnw(String... args) {
        return new MavenCommand().currentDirectory(projectDir).args(args);
    }

    public boolean available() {
        return projectDir.resolve("pom.xml").toFile().exists();
    }

    public static MavenProject load(Path projectDir) {
        MavenProject mvnPrj = new MavenProject(projectDir);
        if (mvnPrj.available()) {
            MavenUtils.findAndInstall(projectDir);
        }
        return mvnPrj;
    }

    public static MavenProject load(String projectDir) {
        return load(Paths.get(projectDir));
    }
}
