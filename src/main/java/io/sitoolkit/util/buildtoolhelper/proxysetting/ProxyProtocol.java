package io.sitoolkit.util.buildtoolhelper.proxysetting;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public enum ProxyProtocol {
    HTTP, HTTPS;

    private static Map<ProxyProtocol, String> nameMap = new HashMap<>();

    static {
        for(ProxyProtocol protocol : values()) {
            nameMap.put(protocol, protocol.name().toLowerCase());
        }
    }

    public static Collection<String> allLowerCaseNames() {
        return nameMap.values();
    }
}
