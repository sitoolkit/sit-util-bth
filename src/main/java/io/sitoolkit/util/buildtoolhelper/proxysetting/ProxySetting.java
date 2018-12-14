package io.sitoolkit.util.buildtoolhelper.proxysetting;

import org.apache.commons.lang3.StringUtils;

import lombok.Data;

@Data
public class ProxySetting {
    private String protocol = "";

    private String proxyHost = "";

    private String proxyPort = "";

    private String proxyUser = "";

    private String proxyPassword = "";

    private String nonProxyHosts = "";

    public void setProxySettings(String protocol, String host, String port, String user, String password,
            String nonProxyHosts) {
        this.protocol = protocol;
        this.proxyHost = host;
        this.proxyPort = port;
        this.proxyUser = user;
        this.proxyPassword = password;
        this.nonProxyHosts = nonProxyHosts;
    }

    public boolean isEnabled() {
        return !StringUtils.isEmpty(getProxyHost());
    }
}
