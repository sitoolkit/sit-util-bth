package io.sitoolkit.util.buildtoolhelper.config;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

import io.sitoolkit.util.buildtoolhelper.util.PropertiesUtil;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
public class SitoolkitConfig {
    private static final String CONFIG_DIR = System.getProperty("user.home") + File.separator
            + ".sitoolkit";
    private static final String CONFIG_NAME = "config.properties";

    @Setter(AccessLevel.NONE)
    @Getter(AccessLevel.NONE)
    private static SitoolkitConfig instance;

    @Setter(AccessLevel.NONE)
    @Getter(AccessLevel.NONE)
    private Properties properties;

    private String proxyHost;
    private String proxyPort;
    private String proxyUser;
    private String proxyPassword;

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

        proxyHost = properties.getProperty("proxyHost");
        proxyPort = properties.getProperty("proxyPort");
        proxyUser = properties.getProperty("proxyUser");
        proxyPassword = properties.getProperty("proxyPassword");
    }

    public Path getConfigFilePath() {
        return Paths.get(CONFIG_DIR, CONFIG_NAME);
    }

    public String get(String key) {
        return properties.getProperty(key);
    }
}
