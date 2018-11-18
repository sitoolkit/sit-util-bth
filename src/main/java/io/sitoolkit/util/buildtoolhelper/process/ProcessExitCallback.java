package io.sitoolkit.util.buildtoolhelper.process;

@FunctionalInterface
public interface ProcessExitCallback {

    void callback(int exitCode);
}
