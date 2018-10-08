package io.sitoolkit.util.buidtoolhelper.process;

@FunctionalInterface
public interface ProcessExitCallback {

    void callback(int exitCode);
}
