package io.sitoolkit.util.buildtoolhelper.proxysetting;

import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
            Optional<List<ProxySetting>> settings = Stream
                    .of(SitoolkitProxyUtils.getInstance(), MavenProxyUtils.getInstance(),
                            GradleProxyUtils.getInstance())
                    .map(ProxyUtils::readProxySettings).filter((l) -> !l.isEmpty()).findFirst();

            List<ProxySetting> proxySettings = null;
            if (settings.isPresent()) {
                proxySettings = settings.get();
            } else {
                // TODO read from registry
                // log.info("read registry proxy settings");
                // ProxySettingProcessClient client = new ProxySettingProcessClient();
                // resultSetting = client.getRegistryProxy();
                //
                // if (resultSetting.isEnabled()) {
                // if (!MavenProxyUtils.getInstance().writeProxySetting(resultSetting))
                // return;
                // }
                log.info("read registry proxy settings");
                ProxySettingProcessClient client = new ProxySettingProcessClient();
                proxySettings = client.getRegistryProxies();
                if (!proxySettings.isEmpty()) {
                    log.info("Use registry proxy settings");
                }
            }

            setProperties(proxySettings);
        } catch (Exception exp) {
            log.warn("set proxy failed", exp);
        } finally {
            loaded = true;
        }
    }

    private void setProperties(List<ProxySetting> proxySettings) {
        if (proxySettings.isEmpty()) {
            log.info("proxy settings is disabled");
            return;
        }

        log.info("set proxy properties");

        proxySettings.stream().forEach((proxySetting) -> {
            String protocol = proxySetting.getProtocol();
            System.setProperty(protocol + ".proxyHost", proxySetting.getProxyHost());
            System.setProperty(protocol + ".proxyPort", proxySetting.getProxyPort());

            if (proxySetting.getNonProxyHosts() != null
                    && !proxySetting.getNonProxyHosts().isEmpty()) {
                System.setProperty(protocol + "nonProxyHosts", proxySetting.getNonProxyHosts());
            }
        });

        setAuthProperties(proxySettings);
    }

    private void setAuthProperties(List<ProxySetting> proxySettings) {
        System.setProperty("jdk.http.auth.tunneling.disabledSchemes", "");
        Map<String, ProxySetting> proxySettingMap = proxySettings.stream().collect(Collectors
                .toMap(ProxySetting::getProtocol, Function.identity(), (doc1, doc2) -> doc1));

        Authenticator.setDefault(new SitoolkitProxyAuthenticator(proxySettingMap));
    }

    class SitoolkitProxyAuthenticator extends Authenticator {
        private Map<String, ProxySetting> proxySettingMap;

        SitoolkitProxyAuthenticator(Map<String, ProxySetting> proxySettingMap) {
            this.proxySettingMap = proxySettingMap;
        }

        @Override
        public PasswordAuthentication getPasswordAuthentication() {
            String protocol = getRequestingURL().getProtocol();
            ProxySetting proxySetting = proxySettingMap.get(protocol);
            String user, password;
            if (proxySetting == null) {
                log.warn("Proxy authentication setting not found: protocol '{}'", protocol);
                user = "";
                password = "";
            } else {
                user = proxySetting.getProxyUser();
                password = proxySetting.getProxyPassword();
            }
            return new PasswordAuthentication(user, password.toCharArray());
        }
    }
}
