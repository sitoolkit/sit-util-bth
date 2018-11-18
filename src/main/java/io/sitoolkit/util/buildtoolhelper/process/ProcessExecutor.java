package io.sitoolkit.util.buildtoolhelper.process;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;

import io.sitoolkit.util.buildtoolhelper.UnExpectedException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ProcessExecutor {

    private StringBuilderStdoutListener defaultStderrListener = new StringBuilderStdoutListener();

    public void execute(ProcessCommand command) {

        ProcessBuilder pb = toBuilder(command);
        Process process = null;

        try {
            process = pb.start();
            log.info("process {} starts {}", new Object[] { process, command.getWholeCommand() });

            scanStream(process.getInputStream(), stdoutListeners(command));
            scanStream(process.getErrorStream(), stderrListeners(command));

            waitForExit(process, command.getExitCallbacks());

        } catch (Exception e) {
            throw new UnExpectedException(e);
        } finally {
            ProcessConversation.destroy(process);
        }

    }

    public ProcessConversation executeAsync(ProcessCommand params) {

        ProcessConversation conversation = new ProcessConversation();
        Process process = null;

        ProcessBuilder pb = toBuilder(params);

        try {
            process = pb.start();
            conversation.init(process);
            log.info("process {} starts {}", new Object[] { process, params.getWholeCommand() });

            ExecutorService excutor = conversation.getExecutor();

            excutor.execute(() -> scanStream(conversation.getProcess().getInputStream(),
                    stdoutListeners(params)));
            excutor.execute(() -> scanStream(conversation.getProcess().getErrorStream(),
                    stderrListeners(params)));
            excutor.execute(
                    () -> waitForExit(conversation.getProcess(), params.getExitCallbacks()));

        } catch (Exception e) {
            ProcessConversation.destroy(process);
            throw new UnExpectedException(e);
        }

        return conversation;

    }

    private List<StdoutListener> stdoutListeners(ProcessCommand params) {
        List<StdoutListener> stdoutListeners = new ArrayList<>();
        stdoutListeners.addAll(StdoutListenerContainer.getInstance().getStdoutListeners());
        stdoutListeners.addAll(params.getStdoutListeners());
        return stdoutListeners;
    }

    private List<StdoutListener> stderrListeners(ProcessCommand params) {
        List<StdoutListener> stderrListeners = new ArrayList<>();
        stderrListeners.add(defaultStderrListener);
        stderrListeners.addAll(StdoutListenerContainer.getInstance().getStderrListeners());
        stderrListeners.addAll(params.getStderrListeners());
        return stderrListeners;
    }

    private ProcessBuilder toBuilder(ProcessCommand params) {
        ProcessBuilder pb = new ProcessBuilder(params.getWholeCommand());
        pb.environment().putAll(params.getEnv());
        pb.directory(params.getCurrentDirectory().toFile());

        return pb;
    }

    private void scanStream(InputStream is, List<StdoutListener> listeners) {
        try (Scanner scanner = new Scanner(is)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                for (StdoutListener listener : listeners) {
                    listener.nextLine(line);
                }
            }
        }
    }

    private void waitForExit(Process process, List<ProcessExitCallback> callbacks) {
        int exitCode = 0;

        try {

            exitCode = process.waitFor();
            log.info("process {} exits with code : {}", new Object[] { process, exitCode });

        } catch (InterruptedException e) {

            Thread.interrupted();
            log.warn(e.getLocalizedMessage(), e);

        } finally {

            if (exitCode != 0) {
                log.error("{} {}", System.lineSeparator(), defaultStderrListener);
            }

            if (callbacks != null) {
                for (ProcessExitCallback callback : callbacks) {
                    callback.callback(exitCode);
                }
            }

        }
    }

}
