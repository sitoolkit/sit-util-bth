package io.sitoolkit.util.buildtoolhelper.proxysetting;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import io.sitoolkit.util.buildtoolhelper.process.StdoutListener;
import lombok.AllArgsConstructor;
import lombok.Value;

public class ProxySettingStdoutListener implements StdoutListener {

    private boolean isProxyEnabled = false;

    private boolean isParsed = false;

    private ProxySetting baseSetting = new ProxySetting();

    private List<ProxySetting> proxySettings = new ArrayList<>();

    @Override
    public void nextLine(String line) {
        if (line.trim().startsWith("Proxy")) {
            parse(line.trim());
        }
    }

    public void parseProxyHost() {
        String proxyURLs = baseSetting.getProxyHost();
        if (!isProxyEnabled || StringUtils.isEmpty(proxyURLs)) {
            return;
        }

        if (proxyURLs.contains(";")) {
            for (String protocolDetail : proxyURLs.split(";")) {
                String[] protocolDetails = protocolDetail.split("=");
                String protocol = protocolDetails[0];

                if (ProxyProtocol.allLowerCaseNames().contains(protocol)) {
                    String proxyURL = protocolDetails[1];
                    ProxyURL url = parseProxyURL(proxyURL);

                    proxySettings.add(createProxySettingFromBase(protocol, url));
                }
            }
        } else {
            ProxyURL url = parseProxyURL(proxyURLs);
            proxySettings = ProxyProtocol.allLowerCaseNames().stream().map((protocol) -> {
                return createProxySettingFromBase(protocol, url);
            }).collect(Collectors.toList());
        }

        isParsed = true;
    }

    public List<ProxySetting> getProxySettings() {
        if (!isParsed) {
            parseProxyHost();
        }

        return proxySettings;
    }

    private void parse(String line) {
        String[] details = line.split(" +");
        switch (details[0]) {
        case "ProxyEnable":
            if ("0x1".equals(details[2])) {
                isProxyEnabled = true;
            }
            break;

        case "ProxyServer":
            baseSetting.setProxyHost(details[2]);
            break;

        case "ProxyOverride":
            baseSetting.setNonProxyHosts(details[2].replaceAll(";", "|"));
            break;
        }
    }

    private ProxySetting createProxySettingFromBase(String protocol, ProxyURL url) {
        ProxySetting proxySetting = new ProxySetting();
        proxySetting.setProxySettings(protocol, url.getHost(), url.getPort(),
                baseSetting.getProxyUser(), baseSetting.getProxyPassword(),
                baseSetting.getNonProxyHosts());
        return proxySetting;
    }

    private ProxyURL parseProxyURL(String proxyURL) {
        String[] settings = proxyURL.split(":");
        String host = settings[0];
        String port;
        if (settings.length == 2) {
            port = settings[1];
        } else {
            port = "80";
        }
        return new ProxyURL(host, port);
    }

    @Value
    @AllArgsConstructor
    private class ProxyURL {
        private String host;
        private String port;
    }
}
