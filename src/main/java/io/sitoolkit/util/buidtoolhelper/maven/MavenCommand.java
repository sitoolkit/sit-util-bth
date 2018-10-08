package io.sitoolkit.util.buidtoolhelper.maven;

import io.sitoolkit.util.buidtoolhelper.process.ProcessCommand;

public class MavenCommand extends ProcessCommand {

    @Override
    public String getCommand() {
        return MavenUtils.getCommand();
    }

}
