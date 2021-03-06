package io.sitoolkit.util.buildtoolhelper.process;

public class StringBuilderStdoutListener implements StdoutListener {

    private StringBuilder builder = new StringBuilder();

    public StringBuilderStdoutListener() {
    }

    @Override
    public void nextLine(String line) {
        builder.append(line);
        builder.append(System.lineSeparator());
    }

    @Override
    public String toString() {
        return builder.toString();
    }
}
