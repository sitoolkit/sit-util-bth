package io.sitoolkit.util.buildtoolhelper.proxysetting;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum ProxyProtocol {
    HTTP, HTTPS;

    private static Map<ProxyProtocol, String> nameMap = new HashMap<>();

    static {
        for(ProxyProtocol protocol : values()) {
            nameMap.put(protocol, protocol.name().toLowerCase());
        }
    }

    public static List<ProxyProtocol> getValueList() {
        return Arrays.asList(values());
    }

    public static ProxyProtocol getValue(String name) {
        for(ProxyProtocol v : values()) {
            if(v.name().equalsIgnoreCase(name)) return v;
        }
        return null;
    }

    public static boolean contains(String name) {
        return getValue(name) != null;
    }

    @Override
    public String toString() {
        return nameMap.get(this);
    }
}
