package io.sitoolkit.util.buidtoolhelper.gradle;

import io.sitoolkit.util.buidtoolhelper.process.ProcessCommand;

public class GradleCommand extends ProcessCommand {

    @Override
    public String getCommand() {
        return GradleUtils.getCommand();
    }

}
