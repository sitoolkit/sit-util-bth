package io.sitoolkit.util.buidtoolhelper.infra.process;

class NopProcessExitCallback implements ProcessExitCallback {

    @Override
    public void callback(int exitCode) {
        // NOP
    }

}
