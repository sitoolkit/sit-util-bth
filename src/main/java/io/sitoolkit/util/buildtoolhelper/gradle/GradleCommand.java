package io.sitoolkit.util.buildtoolhelper.gradle;

import io.sitoolkit.util.buildtoolhelper.process.ProcessCommand;

public class GradleCommand extends ProcessCommand {

    @Override
    public String getCommand() {
        return GradleUtils.getCommand();
    }

}
