package io.sitoolkit.util.buidtoolhelper.process;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

public class StdoutListenerContainer {

    private static StdoutListenerContainer instance = new StdoutListenerContainer();

    @Getter
    private List<StdoutListener> stdoutListeners = new ArrayList<>();
    @Getter
    private List<StdoutListener> stderrListeners = new ArrayList<>();

    private StdoutListenerContainer() {
    }

    public static StdoutListenerContainer getInstance() {
        return instance;
    }

}
