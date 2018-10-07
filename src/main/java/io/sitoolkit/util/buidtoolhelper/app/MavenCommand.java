package io.sitoolkit.util.buidtoolhelper.app;

import java.nio.file.Path;
import java.util.Arrays;

import io.sitoolkit.util.buidtoolhelper.infra.maven.MavenUtils;
import io.sitoolkit.util.buidtoolhelper.infra.process.ConversationProcess;
import io.sitoolkit.util.buidtoolhelper.infra.process.ConversationProcessContainer;
import io.sitoolkit.util.buidtoolhelper.infra.process.ProcessParams;
import io.sitoolkit.util.buidtoolhelper.infra.process.StdoutListener;

public class MavenCommand {

    private ProcessParams processParams = new ProcessParams();

    private MavenCommand() {
    }

    public static MavenCommand build() {
        MavenCommand mvnw = new MavenCommand();
        return mvnw;
    }

    public MavenCommand stdout(StdoutListener listener) {
        processParams.getStdoutListeners().add(listener);
        return this;
    }

    public MavenCommand sync(boolean isSync) {
        processParams.setProcessWait(isSync);
        return this;
    }

    public MavenCommand setParams(String... params) {
        processParams.getCommand().clear();
        processParams.getCommand().add(MavenUtils.getCommand());
        processParams.getCommand().addAll(Arrays.asList(params));
        return this;
    }

    public MavenCommand baseDir(Path baseDir) {
        processParams.setDirectory(baseDir.toFile());
        return this;
    }

    public void execute() {
        ConversationProcess process = ConversationProcessContainer.create();
        process.start(processParams);
    }

}
