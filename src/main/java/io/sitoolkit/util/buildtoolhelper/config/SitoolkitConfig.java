package io.sitoolkit.util.buildtoolhelper.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Properties;

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
        Properties properties = new Properties();
        File configFile = new File(CONFIG_DIR, CONFIG_NAME);

        log.info("Read config: {}", configFile);

        try (InputStream inputStream = new FileInputStream(configFile)) {
            properties.load(inputStream);
        } catch (FileNotFoundException e) {
            log.info("{} was not found", CONFIG_NAME);
            return;
        } catch (Exception e) {
            log.warn("Failed to read {}", CONFIG_NAME, e);
            return;
        }

        proxyHost = properties.getProperty("proxyHost");
        proxyPort = properties.getProperty("proxyPort");
        proxyUser = properties.getProperty("proxyUser");
        proxyPassword = properties.getProperty("proxyPassword");
    }
}
