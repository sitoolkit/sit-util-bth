package io.sitoolkit.util.buildtoolhelper.config;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

import io.sitoolkit.util.buildtoolhelper.util.PropertiesUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SitoolkitConfig {
    private static final String CONFIG_DIR = System.getProperty("user.home") + File.separator
            + ".sitoolkit";
    private static final String CONFIG_NAME = "config.properties";
    private static SitoolkitConfig instance;
    private Properties properties;

    private SitoolkitConfig() {
    }

    public static SitoolkitConfig getInstance() {
        if (instance == null) {
            instance = new SitoolkitConfig();
            instance.loadProperties();
        }

        return instance;
    }

    private void loadProperties() {
        Path configFile = getConfigFilePath();
        if (!configFile.toFile().exists()) {
            log.info("{} was not found", configFile);
            return;
        }

        log.info("Read config: {}", configFile);

        try {
            properties = PropertiesUtil.loadFile(configFile);
        } catch (Exception e) {
            log.warn("Failed to read {}", configFile, e);
            return;
        }
    }

    public Path getConfigFilePath() {
        return Paths.get(CONFIG_DIR, CONFIG_NAME);
    }

    public String getProperty(String key) {
        if (properties == null) {
            return "";
        }
        return properties.getProperty(key);
    }
}
