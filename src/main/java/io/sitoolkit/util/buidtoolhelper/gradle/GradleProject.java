package io.sitoolkit.util.buidtoolhelper.gradle;

import java.nio.file.Path;
import java.nio.file.Paths;

import io.sitoolkit.util.buidtoolhelper.process.ProcessCommand;

public class GradleProject {

    private Path projectDir;

    private GradleProject(Path projectDir) {
        super();
        this.projectDir = projectDir;
    }

    public ProcessCommand gradlew(String... args) {
        return new GradleCommand().currentDirectory(projectDir).args(args);
    }

    public static GradleProject load(Path projectDir) {
        GradleProject grdlPrj = new GradleProject(projectDir);
        GradleUtils.findAndInstall(projectDir);
        return grdlPrj;
    }

    public static GradleProject load(String projectDir) {
        return load(Paths.get(projectDir));
    }
}
