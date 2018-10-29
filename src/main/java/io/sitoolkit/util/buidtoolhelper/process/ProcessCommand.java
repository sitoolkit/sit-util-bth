package io.sitoolkit.util.buidtoolhelper.process;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Data;

@Data
public class ProcessCommand {

    private static Path defaultCurrentDirectory = Paths.get(".");
    private static ProcessExecutor processExecutor = new ProcessExecutor();
    private Path currentDirectory;
    private Map<String, String> env = new HashMap<>();
    private String command;
    private List<String> args = new ArrayList<>();
    private List<StdoutListener> stdoutListeners = new ArrayList<>();
    private List<StdoutListener> stderrListeners = new ArrayList<>();
    private List<ProcessExitCallback> exitCallbacks = new ArrayList<>();

    public static void setDefaultCurrentDirectory(Path defaultCurrentDirectory) {
        ProcessCommand.defaultCurrentDirectory = defaultCurrentDirectory;
    }

    public ProcessCommand currentDirectory(Path currentDirectory) {
        this.currentDirectory = currentDirectory;
        return this;
    }

    public ProcessCommand command(String command) {
        this.command = command;
        return this;
    }

    public ProcessCommand stdout(StdoutListener stdoutListener) {
        stdoutListeners.add(stdoutListener);
        return this;
    }

    public ProcessCommand args(String... args) {
        this.args = Arrays.asList(args);
        return this;
    }

    public ProcessCommand args(List<String> args) {
        this.args = args;
        return this;
    }

    public Path getCurrentDirectory() {
        return currentDirectory == null ? defaultCurrentDirectory : currentDirectory;
    }

    public void execute() {
        processExecutor.execute(this);
    }

    public ProcessConversation executeAsync() {
        return processExecutor.executeAsync(this);
    }

    public List<String> getWholeCommand() {
        List<String> wholeCommand = new ArrayList<>();
        wholeCommand.add(getCommand());
        wholeCommand.addAll(getArgs());
        return wholeCommand;
    }
}
