package io.sitoolkit.util.buildtoolhelper.proxysetting;

import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.util.Optional;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;

import io.sitoolkit.util.buildtoolhelper.config.SitoolkitProxyUtils;
import io.sitoolkit.util.buildtoolhelper.gradle.GradleProxyUtils;
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
            Optional<ProxySetting> proxySetting = Stream
                    .of(SitoolkitProxyUtils.getInstance(), MavenProxyUtils.getInstance(),
                            GradleProxyUtils.getInstance())
                    .map(ProxyUtils::readProxySetting).filter(Optional::isPresent)
                    .map(Optional::get).findFirst();

            ProxySetting resultSetting;
            if (proxySetting.isPresent()) {
                resultSetting = proxySetting.get();
            } else {
                log.info("read registry proxy settings");
                ProxySettingProcessClient client = new ProxySettingProcessClient();
                resultSetting = client.getRegistryProxy();

                if (resultSetting.isEnabled()) {
                    if (!MavenProxyUtils.getInstance().writeProxySetting(resultSetting))
                        return;
                }
            }

            setProperties(resultSetting);
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

            if (StringUtils.isNotEmpty(proxySetting.getProxyUser())) {
                setAuthProperties(proxySetting.getProxyUser(), proxySetting.getProxyPassword());
            }

            if (proxySetting.getNonProxyHosts() != null
                    && !proxySetting.getNonProxyHosts().isEmpty()) {
                System.setProperty("nonProxyHosts", proxySetting.getNonProxyHosts());
            }
        } else {
            log.info("proxy settings is disabled");
        }
    }

    private void setAuthProperties(String user, String password) {
        System.setProperty("jdk.http.auth.tunneling.disabledSchemes", "");

        Authenticator.setDefault(new Authenticator() {
            @Override
            public PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(user, password.toCharArray());
            }
        });
    }
}
