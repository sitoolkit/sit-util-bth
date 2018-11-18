package io.sitoolkit.util.buildtoolhelper.proxysetting;

import io.sitoolkit.util.buildtoolhelper.maven.MavenProxyUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ProxySettingService {

    private static ProxySettingService instance = new ProxySettingService();

    private boolean loaded;

    private ProxySettingService() {
        loaded = false;
    }

    public static ProxySettingService getInstance() {
        return instance;
    }

    public void loadProxy() {

        if (loaded)
            return;

        try {
            ProxySetting proxySetting = MavenProxyUtils.readProxySetting();

            if (proxySetting == null) {
                log.info("read registry proxy settings");
                ProxySettingProcessClient client = new ProxySettingProcessClient();
                proxySetting = client.getRegistryProxy();

                if (proxySetting.isEnabled()) {
                    if (!MavenProxyUtils.writeProxySetting(proxySetting))
                        return;
                }
            }

            setProperties(proxySetting);
        } catch (Exception exp) {
            log.warn("set proxy failed", exp);
        } finally {
            loaded = true;
        }
    }

    private void setProperties(ProxySetting proxySetting) {
        System.setProperty("proxySet", proxySetting.getProxyActive());

        if (proxySetting.isEnabled()) {
            log.info("set proxy properties");
            System.setProperty("proxyHost", proxySetting.getProxyHost());
            System.setProperty("proxyPort", proxySetting.getProxyPort());

            if (proxySetting.getNonProxyHosts() != null
                    && !proxySetting.getNonProxyHosts().isEmpty()) {
                System.setProperty("nonProxyHosts", proxySetting.getNonProxyHosts());
            }
        } else {
            log.info("proxy settings is disabled");
        }
    }
}
