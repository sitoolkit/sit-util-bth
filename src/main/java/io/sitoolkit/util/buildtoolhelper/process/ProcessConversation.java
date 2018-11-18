package io.sitoolkit.util.buildtoolhelper.process;

import java.io.Closeable;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ProcessConversation {

    @Getter
    private Process process;

    private PrintWriter processWriter;

    @Getter
    private ExecutorService executor;

    public void init(Process process) {
        this.process = process;
        processWriter = new PrintWriter(process.getOutputStream());
        executor = Executors.newCachedThreadPool();
    }

    public void input(String input) {
        processWriter.println(input);
        processWriter.flush();
    }

    public void destroy() {
        executor.shutdown();
        try {
            executor.awaitTermination(30, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.interrupted();
            log.warn(e.getLocalizedMessage(), e);
        }
        destroy(process);
    }

    public static void destroy(Process process) {
        if (process != null && process.isAlive()) {
            close(process.getInputStream());
            close(process.getOutputStream());
            close(process.getErrorStream());
            process.destroy();
        }
    }

    private static void close(Closeable closeable) {
        try {
            closeable.close();
        } catch (IOException e) {
            log.warn(e.getLocalizedMessage(), e.getMessage());
        }
    }

}
