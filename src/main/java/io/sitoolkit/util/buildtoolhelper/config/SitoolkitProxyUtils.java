package io.sitoolkit.util.buildtoolhelper.config;

import java.util.Optional;

import org.apache.commons.lang3.StringUtils;


import io.sitoolkit.util.buildtoolhelper.proxysetting.ProxySetting;
import io.sitoolkit.util.buildtoolhelper.proxysetting.ProxyUtils;
import lombok.Getter;

public class SitoolkitProxyUtils implements ProxyUtils {

    @Getter
    private static SitoolkitProxyUtils instance = new SitoolkitProxyUtils();

    private SitoolkitProxyUtils() {
    }

    @Override
    public Optional<ProxySetting> readProxySetting() {
        SitoolkitConfig config = SitoolkitConfig.getInstance();

        if (StringUtils.isEmpty(config.getProxyHost())) {
            return Optional.empty();
        } else {
            ProxySetting proxySetting = new ProxySetting();
            proxySetting.setProxySettings(config.getProxyHost(), config.getProxyPort(),
                    config.getProxyUser(), config.getProxyPassword(), "");

            return Optional.of(proxySetting);
        }
    }

}
