package io.sitoolkit.util.buildtoolhelper.gradle;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Optional;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;

import io.sitoolkit.util.buildtoolhelper.proxysetting.ProxySetting;
import io.sitoolkit.util.buildtoolhelper.proxysetting.ProxyUtils;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GradleProxyUtils implements ProxyUtils {

    @Getter
    private static GradleProxyUtils instance = new GradleProxyUtils();

    private GradleProxyUtils() {
    }

    @Override
    public Optional<ProxySetting> readProxySetting() {
        File settingsFile = GradleUtils.getUserSettingFile();
        return readProxySetting(settingsFile);
    }

    public Optional<ProxySetting> readProxySetting(File settingsFile) {
        if (!settingsFile.exists()) {
            return Optional.empty();
        }

        Properties properties = new Properties();
        try (InputStream inputStream = new FileInputStream(settingsFile)) {
            properties.load(inputStream);
        } catch (FileNotFoundException e) {
            log.info("{} was not found", settingsFile.getName());
            return Optional.empty();
        } catch (Exception e) {
            log.warn("Failed to read {}", settingsFile.getName(), e);
            return Optional.empty();
        }

        String proxyPart = null;
        if (StringUtils.isNotEmpty(properties.getProperty("systemProp.http.proxyHost"))) {
            proxyPart = "http";
        } else if (StringUtils.isNotEmpty(properties.getProperty("systemProp.https.proxyHost"))) {
            proxyPart = "https";
        } else {
            return Optional.empty();
        }

        ProxySetting proxySetting = new ProxySetting();
        proxySetting.setProxySettings(
                properties.getProperty("systemProp." + proxyPart + ".proxyHost"),
                properties.getProperty("systemProp." + proxyPart + ".proxyPort"),
                properties.getProperty("systemProp." + proxyPart + ".proxyUser"),
                properties.getProperty("systemProp." + proxyPart + ".proxyPassword"),
                properties.getProperty("systemProp." + proxyPart + ".nonProxyHosts"));
        return Optional.ofNullable(proxySetting);
    }

}
