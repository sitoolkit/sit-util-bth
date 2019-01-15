package io.sitoolkit.util.buildtoolhelper.proxysetting;

import java.util.ArrayList;
import java.util.List;

import io.sitoolkit.util.buildtoolhelper.process.ProcessCommand;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ProxySettingProcessClient {

    public List<ProxySetting> getRegistryProxies() {

        List<String> args = new ArrayList<>();
        args.add("query");
        args.add(
                "\"HKEY_CURRENT_USER\\Software\\Microsoft\\Windows\\CurrentVersion\\Internet Settings\"");
        args.add("/v");
        args.add("Proxy*");

        ProxySettingStdoutListener proxyStdoutListener = new ProxySettingStdoutListener();

        new ProcessCommand().command("reg").args(args).stdout(proxyStdoutListener).execute();

        List<ProxySetting> proxySettings = proxyStdoutListener.getProxySettings();

        if (!proxySettings.isEmpty()) {
            log.info("Use registry proxy settings");
        }

        return proxySettings;
    }
}
