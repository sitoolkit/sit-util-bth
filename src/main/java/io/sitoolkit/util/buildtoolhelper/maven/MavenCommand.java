package io.sitoolkit.util.buildtoolhelper.maven;

import io.sitoolkit.util.buildtoolhelper.process.ProcessCommand;

public class MavenCommand extends ProcessCommand {

    @Override
    public String getCommand() {
        return MavenUtils.getCommand();
    }

}
