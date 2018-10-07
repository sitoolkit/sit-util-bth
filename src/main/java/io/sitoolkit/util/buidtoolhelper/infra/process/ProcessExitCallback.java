package io.sitoolkit.util.buidtoolhelper.infra.process;

@FunctionalInterface
public interface ProcessExitCallback {

    void callback(int exitCode);
}
