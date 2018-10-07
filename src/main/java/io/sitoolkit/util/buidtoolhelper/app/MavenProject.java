package io.sitoolkit.util.buidtoolhelper.app;

import java.nio.file.Path;

import io.sitoolkit.util.buidtoolhelper.infra.maven.MavenUtils;

public class MavenProject {

    private Path projectDir;

    private MavenProject(Path projectDir) {
        super();
        this.projectDir = projectDir;
    }

    public MavenCommand mvnw(String... params) {
        return MavenCommand.build().setParams(params).sync(true);
    }

    public static MavenProject load(Path projectDir) {
        MavenProject mvnPrj = new MavenProject(projectDir);
        MavenUtils.findAndInstall(projectDir);
        return mvnPrj;
    }
}
