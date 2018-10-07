package io.sitoolkit.util.buidtoolhelper.infra.process;

import java.io.File;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;

import io.sitoolkit.util.buidtoolhelper.UnExpectedException;
import io.sitoolkit.util.buidtoolhelper.infra.concurrent.ExecutorContainer;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ConversationProcess {

    private static final LogStdoutListener LOG_STDOUT_LISTENER = new LogStdoutListener(log,
            Level.INFO, "stdout");
    private static final LogStdoutListener LOG_STDERR_LISTENER = new LogStdoutListener(log,
            Level.SEVERE, "stderr");

    private Process process;

    private PrintWriter processWriter;

    private StringBuilderStdoutListener defaultStderrListener = new StringBuilderStdoutListener();

    ConversationProcess() {
    }

    public void startWithProcessWait(ProcessParams params) {
        if (params.getExitClallbacks().isEmpty()) {
            params.getExitClallbacks().add(new NopProcessExitCallback());
        }
        params.setProcessWait(true);
        start(params);
    }

    public void start(ProcessParams params) {

        File directory = params.getDirectory();
        if (directory == null) {
            directory = ProcessParams.getDefaultCurrentDir();
        }
        List<String> command = params.getCommand();

        if (process != null && process.isAlive()) {
            log.warn("process {} is alive.", process);
        }

        ProcessBuilder pb = new ProcessBuilder(command);
        pb.environment().putAll(params.getEnviroment());

        try {
            pb.directory(directory);
            process = pb.start();
            log.info("process {} starts {}", new Object[] { process, command });

            List<StdoutListener> stdoutListeners = new ArrayList<>();
            stdoutListeners.add(LOG_STDOUT_LISTENER);
            stdoutListeners.addAll(params.getStdoutListeners());
            stdoutListeners.addAll(StdoutListenerContainer.get().getListeners());

            List<StdoutListener> stderrListeners = new ArrayList<>();
            stderrListeners.add(LOG_STDERR_LISTENER);

            if (params.isProcessWait()) {
                scan(process.getInputStream(), stdoutListeners);
                scan(process.getErrorStream(), stderrListeners);
            } else {
                ExecutorContainer.get()
                        .execute(() -> scan(process.getInputStream(), stdoutListeners));
                ExecutorContainer.get()
                        .execute(() -> scan(process.getErrorStream(), stderrListeners));
            }

            processWriter = new PrintWriter(process.getOutputStream());

            if (params.isProcessWait()) {
                wait(params.getExitClallbacks());
            } else {
                ExecutorContainer.get().execute(() -> wait(params.getExitClallbacks()));
            }

        } catch (Exception e) {
            throw new UnExpectedException(e);
        }
    }

    private void scan(InputStream is, List<StdoutListener> listeners) {
        Scanner scanner = new Scanner(is);

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            for (StdoutListener listener : listeners) {
                listener.nextLine(line);
            }
        }

        scanner.close();

    }

    private void wait(List<ProcessExitCallback> callbacks) {
        int exitCode = 0;

        try {

            exitCode = process.waitFor();
            log.info("process {} exits with code : {}", new Object[] { process, exitCode });

        } catch (InterruptedException e) {

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

    public void input(String input) {
        processWriter.println(input);
        processWriter.flush();
    }

    public void destroy() {
        if (process != null && process.isAlive()) {
            process.destroy();
        }
    }

}
