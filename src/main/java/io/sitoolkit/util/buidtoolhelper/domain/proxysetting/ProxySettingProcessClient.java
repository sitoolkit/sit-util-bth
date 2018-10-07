package io.sitoolkit.util.buidtoolhelper.domain.proxysetting;

import java.util.ArrayList;
import java.util.List;

import io.sitoolkit.util.buidtoolhelper.infra.process.ConversationProcess;
import io.sitoolkit.util.buidtoolhelper.infra.process.ConversationProcessContainer;
import io.sitoolkit.util.buidtoolhelper.infra.process.ProcessParams;

public class ProxySettingProcessClient {

    public ProxySetting getRegistryProxy() {
        ProcessParams params = new ProcessParams();

        List<String> command = new ArrayList<>();
        command.add("reg");
        command.add("query");
        command.add(
                "\"HKEY_CURRENT_USER\\Software\\Microsoft\\Windows\\CurrentVersion\\Internet Settings\"");
        command.add("/v");
        command.add("Proxy*");
        params.setCommand(command);

        ProxySettingStdoutListener proxyStdoutListener = new ProxySettingStdoutListener();
        params.getStdoutListeners().add(proxyStdoutListener);

        ConversationProcess process = ConversationProcessContainer.create();
        process.startWithProcessWait(params);

        return proxyStdoutListener.getProxySetting();
    }
}
