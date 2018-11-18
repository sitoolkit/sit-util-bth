package io.sitoolkit.util.buildtoolhelper.maven;

import java.io.File;
import java.nio.file.Files;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import io.sitoolkit.util.buildtoolhelper.proxysetting.ProxySetting;
import io.sitoolkit.util.buildtoolhelper.util.XmlUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MavenProxyUtils {

    private MavenProxyUtils() {
        // TODO Auto-generated constructor stub
    }

    public static ProxySetting readProxySetting() {
        File settingsXml = MavenUtils.getUserSettingFile();
        return readProxySetting(settingsXml);
    }

    public static ProxySetting readProxySetting(File settingsXml) {
        if (!settingsXml.exists()) {
            return null;
        }

        try {
            Document document = MavenUtils.parseSettingFile(settingsXml);
            XPath xpath = XPathFactory.newInstance().newXPath();

            ProxySetting proxySetting = new ProxySetting();
            NodeList proxyList = (NodeList) xpath.evaluate("/settings/proxies/proxy", document,
                    XPathConstants.NODESET);
            for (int num = 0; num < proxyList.getLength(); num++) {
                NodeList proxy = proxyList.item(num).getChildNodes();

                String isActive = "";
                String host = "";
                String port = "";
                String nonProxyHosts = "";
                for (int idx = 0; idx < proxy.getLength(); idx++) {
                    Node proxyItem = proxy.item(idx);

                    switch (proxyItem.getNodeName()) {
                        case "active":
                            isActive = proxyItem.getTextContent();
                            break;
                        case "host":
                            host = proxyItem.getTextContent();
                            break;
                        case "port":
                            port = proxyItem.getTextContent();
                            break;
                        case "nonProxyHosts":
                            nonProxyHosts = proxyItem.getTextContent();
                            break;
                        default:
                            break;
                    }
                }

                if ("true".equals(isActive)) {
                    log.info("read maven proxy settings");
                    proxySetting.setProxySettings(host, port, nonProxyHosts);
                    break;
                }
            }

            return proxySetting;

        } catch (Exception exp) {
            log.warn("read settings.xml failed", exp);
            return null;
        }
    }

    public static boolean writeProxySetting(ProxySetting proxySetting) {
        File settingsXml = MavenUtils.getUserSettingFile();

        try {
            if (!settingsXml.exists()) {
                log.info("create user settings.xml");
                Files.copy(ClassLoader.getSystemResourceAsStream("settings.xml"),
                        settingsXml.toPath());
            }

            log.info("add proxy to settings.xml");
            Document document = MavenUtils.parseSettingFile(settingsXml);

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
            XmlUtil.writeXml(document, settingsXml);

            return true;

        } catch (Exception exp) {
            log.warn("write settings.xml failed", exp);
            return false;
        }
    }

}