package io.sitoolkit.util.buildtoolhelper.maven;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import io.sitoolkit.util.buildtoolhelper.proxysetting.ProxyProtocol;
import io.sitoolkit.util.buildtoolhelper.proxysetting.ProxySetting;
import io.sitoolkit.util.buildtoolhelper.proxysetting.ProxyUtils;
import io.sitoolkit.util.buildtoolhelper.util.XmlUtil;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MavenProxyUtils implements ProxyUtils {

    @Getter
    private static MavenProxyUtils instance = new MavenProxyUtils();

    private MavenProxyUtils() {
    }

    @Override
    public List<ProxySetting> readProxySettings() {
        Path settingFile = MavenUtils.getUserSettingFilePath();
        return readProxySettings(settingFile);
    }

    public List<ProxySetting> readProxySettings(Path settingFile) {
        if (!settingFile.toFile().exists()) {
            return Collections.emptyList();
        }

        try {
            Document document = MavenUtils.parseSettingFile(settingFile.toFile());
            XPath xpath = XPathFactory.newInstance().newXPath();

            List<ProxySetting> proxySettings = new ArrayList<>();
            NodeList proxyList = (NodeList) xpath.evaluate("/settings/proxies/proxy", document,
                    XPathConstants.NODESET);
            for (int num = 0; num < proxyList.getLength(); num++) {
                Element element = (Element) proxyList.item(num);

                String isActive = XmlUtil.getTextContentByTagName(element, "active");
                String protocol = XmlUtil.getTextContentByTagName(element, "protocol");
                String host = XmlUtil.getTextContentByTagName(element, "host");
                String port = XmlUtil.getTextContentByTagName(element, "port");
                String user = XmlUtil.getTextContentByTagName(element, "username");
                String password = XmlUtil.getTextContentByTagName(element, "password");
                String nonProxyHosts = XmlUtil.getTextContentByTagName(element, "nonProxyHosts");

                if ("true".equals(isActive) && ProxyProtocol.contains(protocol)) {
                    ProxySetting proxySetting = new ProxySetting(ProxyProtocol.getValue(protocol),
                            host, port, user, password, nonProxyHosts);

                    proxySettings.add(proxySetting);
                }
            }

            if (!proxySettings.isEmpty()) {
                log.info("Use maven proxy settings: {}", settingFile);
            }

            return proxySettings;

        } catch (Exception exp) {
            log.warn("read settings.xml failed", exp);
            return Collections.emptyList();
        }
    }

}
