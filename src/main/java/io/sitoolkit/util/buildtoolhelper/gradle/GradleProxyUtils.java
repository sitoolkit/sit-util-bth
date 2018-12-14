package io.sitoolkit.util.buildtoolhelper.gradle;

import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.stream.Collectors;

import io.sitoolkit.util.buildtoolhelper.proxysetting.ProxyProtocol;
import io.sitoolkit.util.buildtoolhelper.proxysetting.ProxySetting;
import io.sitoolkit.util.buildtoolhelper.proxysetting.ProxyUtils;
import io.sitoolkit.util.buildtoolhelper.util.PropertiesUtil;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GradleProxyUtils implements ProxyUtils {

    @Getter
    private static GradleProxyUtils instance = new GradleProxyUtils();

    private GradleProxyUtils() {
    }

    @Override
    public List<ProxySetting> readProxySetting() {
        Path settingFile = GradleUtils.getUserSettingFilePath();
        return readProxySetting(settingFile);
    }

    public List<ProxySetting> readProxySetting(Path settingFile) {
        if (!settingFile.toFile().exists()) {
            return Collections.emptyList();
        }

        Properties properties;
        try {
            properties = PropertiesUtil.loadFile(settingFile);
        } catch (Exception e) {
            log.warn("Failed to read {}", settingFile, e);
            return Collections.emptyList();
        }

        List<ProxySetting> proxySettings = ProxyProtocol.allLowerCaseNames().stream()
                .map((protocol) -> {
                    ProxySetting proxySetting = new ProxySetting();
                    proxySetting.setProxySettings(protocol,
                            getProxyProperty(properties, protocol, "proxyHost"),
                            getProxyProperty(properties, protocol, "proxyPort"),
                            getProxyProperty(properties, protocol, "proxyUser"),
                            getProxyProperty(properties, protocol, "proxyPassword"),
                            getProxyProperty(properties, protocol, "nonProxyHosts"));
                    return proxySetting;
                }).filter(Objects::nonNull).collect(Collectors.toList());

        if (!proxySettings.isEmpty()) {
            log.info("Use gradle proxy settings: {}", settingFile);
        }

        return proxySettings;
    }

    private String getProxyProperty(Properties properties, String protocol, String attr) {
        return properties.getProperty("systemProp." + protocol + "." + attr);
    }
}