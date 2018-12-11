package io.sitoolkit.util.buildtoolhelper.config;

import io.sitoolkit.util.buildtoolhelper.proxysetting.ProxySetting;

public class ProxyUtils {

    private ProxyUtils() {
    }

    public static ProxySetting readProxySetting() {
        SitoolkitConfig config = SitoolkitConfig.getInstance();

        ProxySetting proxySetting = new ProxySetting();
        proxySetting.setProxySettings(config.getProxyHost(), config.getProxyPort(),
                config.getProxyUser(), config.getProxyPassword(), "");

        return proxySetting;
    }

}
