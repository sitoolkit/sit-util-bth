package io.sitoolkit.util.buidtoolhelper.infra.process;

import java.util.logging.Level;

import org.slf4j.Logger;

public class LogStdoutListener implements StdoutListener {

    private Logger log;

    private Level level;

    private String name;

    public LogStdoutListener(Logger log, Level level, String name) {
        super();
        this.log = log;
        // TODO Level ?
        this.level = level;
        this.name = name;
    }

    @Override
    public void nextLine(String line) {
        log.info("[{}] {}", new Object[] { name, line });
    }

}
