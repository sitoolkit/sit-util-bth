package io.sitoolkit.util.buildtoolhelper.proxysetting;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProxySetting {
    private ProxyProtocol protocol;
    private String proxyHost;
    private String proxyPort;
    private String proxyUser;
    private String proxyPassword;
    private String nonProxyHosts;
}
