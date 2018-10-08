package io.sitoolkit.util.buidtoolhelper.maven;

import java.nio.file.Path;
import java.nio.file.Paths;

import io.sitoolkit.util.buidtoolhelper.process.ProcessCommand;

public class MavenProject {

    private Path projectDir;

    private MavenProject(Path projectDir) {
        super();
        this.projectDir = projectDir;
    }

    public ProcessCommand mvnw(String... args) {
        return new MavenCommand().currentDirectory(projectDir).args(args);
    }

    public static MavenProject load(Path projectDir) {
        MavenProject mvnPrj = new MavenProject(projectDir);
        MavenUtils.findAndInstall(projectDir);
        return mvnPrj;
    }

    public static MavenProject load(String projectDir) {
        return load(Paths.get(projectDir));
    }
}
