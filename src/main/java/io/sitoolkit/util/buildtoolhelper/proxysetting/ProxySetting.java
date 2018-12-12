package io.sitoolkit.util.buildtoolhelper.proxysetting;

import java.util.HashMap;

import org.apache.commons.lang3.StringUtils;

import lombok.Data;

@Data
public class ProxySetting {
    private String proxyActive = "false";

    private String proxyHost = "";

    private String proxyPort = "";

    private String proxyUser = "";

    private String proxyPassword = "";

    private String nonProxyHosts = "";

    public void setRegistryResult(HashMap<String, String> proxy) {
        setProxySettings(proxy.get("host"), proxy.get("port"), "", "", proxy.get("nonProxyHosts"));
    }

    public void setProxySettings(String host, String port, String user, String password,
            String nonProxyHosts) {
        this.proxyActive = "true";
        this.proxyHost = host;
        this.proxyPort = port;
        this.proxyUser = user;
        this.proxyPassword = password;
        this.nonProxyHosts = nonProxyHosts;
    }

    public boolean isEnabled() {
        return ("true".equals(getProxyActive()) && !StringUtils.isEmpty(getProxyHost()));
    }
}
