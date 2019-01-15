package io.sitoolkit.util.buildtoolhelper.config;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import io.sitoolkit.util.buildtoolhelper.proxysetting.ProxyProtocol;
import io.sitoolkit.util.buildtoolhelper.proxysetting.ProxySetting;
import io.sitoolkit.util.buildtoolhelper.proxysetting.ProxyUtils;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SitoolkitProxyUtils implements ProxyUtils {

    @Getter
    private static SitoolkitProxyUtils instance = new SitoolkitProxyUtils();

    private SitoolkitProxyUtils() {
    }

    @Override
    public List<ProxySetting> readProxySettings() {
        SitoolkitConfig config = SitoolkitConfig.getInstance();

        List<ProxySetting> proxySettings = ProxyProtocol.getValueList().stream().map((protocol) -> {
            String host = config.getProperty("proxy." + protocol + ".host");
            if (StringUtils.isEmpty(host)) {
                return null;
            } else {
                return new ProxySetting(protocol, host,
                        config.getProperty("proxy." + protocol + ".port"),
                        config.getProperty("proxy." + protocol + ".user"),
                        config.getProperty("proxy." + protocol + ".password"),
                        config.getProperty("proxy." + protocol + ".nonProxyHosts"));
            }
        }).filter(Objects::nonNull).collect(Collectors.toList());

        if (!proxySettings.isEmpty()) {
            log.info("Use SI-Toolkit proxy settings: {}",
                    SitoolkitConfig.getInstance().getConfigFilePath());
        }

        return proxySettings;
    }

}
