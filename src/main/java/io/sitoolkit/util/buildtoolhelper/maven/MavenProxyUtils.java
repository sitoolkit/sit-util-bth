package io.sitoolkit.util.buildtoolhelper.maven;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.lang3.StringUtils;
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
    public List<ProxySetting> readProxySetting() {
        Path settingFile = MavenUtils.getUserSettingFilePath();
        return readProxySetting(settingFile);
    }

    public List<ProxySetting> readProxySetting(Path settingFile) {
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

                if ("true".equals(isActive)
                        && ProxyProtocol.allLowerCaseNames().contains(protocol)) {
                    ProxySetting proxySetting = new ProxySetting();
                    proxySetting.setProxySettings(protocol, host, port, user, password,
                            nonProxyHosts);

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

    public boolean writeProxySetting(ProxySetting proxySetting) {
        Path settingFile = MavenUtils.getUserSettingFilePath();

        try {
            if (!settingFile.toFile().exists()) {
                log.info("create user settings.xml");
                Files.copy(ClassLoader.getSystemResourceAsStream("settings.xml"),
                        settingFile);
            }

            log.info("add proxy to settings.xml");
            Document document = MavenUtils.parseSettingFile(settingFile.toFile());

            Element root = document.getDocumentElement();
            Element proxies = XmlUtil.getChildElement(root, "proxies");
            if (proxies == null) {
                proxies = document.createElement("proxies");
                root.appendChild(proxies);
            }

            Element proxy = document.createElement("proxy");
            Element id = document.createElement("id");
            id.appendChild(document.createTextNode("auto-loaded-by-sit-wt"));
            proxy.appendChild(id);

            Element active = document.createElement("active");
            active.appendChild(document.createTextNode("true"));
            proxy.appendChild(active);

            Element protocol = document.createElement("protocol");
            protocol.appendChild(document.createTextNode("http"));
            proxy.appendChild(protocol);

            Element host = document.createElement("host");
            host.appendChild(document.createTextNode(proxySetting.getProxyHost()));
            proxy.appendChild(host);

            Element port = document.createElement("port");
            port.appendChild(document.createTextNode(proxySetting.getProxyPort()));
            proxy.appendChild(port);

            if (!StringUtils.isEmpty(proxySetting.getNonProxyHosts())) {
                Element nonProxyHosts = document.createElement("nonProxyHosts");
                nonProxyHosts.appendChild(document.createTextNode(proxySetting.getNonProxyHosts()));
                proxy.appendChild(nonProxyHosts);
            }

            proxies.appendChild(proxy);
            XmlUtil.writeXml(document, settingFile.toFile());

            return true;

        } catch (Exception exp) {
            log.warn("write settings.xml failed", exp);
            return false;
        }
    }

}
