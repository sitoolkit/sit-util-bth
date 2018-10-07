package io.sitoolkit.util.buidtoolhelper.infra.maven;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import io.sitoolkit.util.buidtoolhelper.domain.proxysetting.ProxySetting;
import io.sitoolkit.util.buidtoolhelper.infra.process.ProcessParams;
import io.sitoolkit.util.buidtoolhelper.infra.util.XmlUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MavenUtils {

    private static String mvnCommand = "";

    public static List<String> getCommand(ProcessParams params) {
        List<String> mvnCommand = new ArrayList<String>();
        mvnCommand.add(getCommand());
        params.setCommand(mvnCommand);
        return mvnCommand;
    }

    public static String getCommand() {
        while (mvnCommand == null || mvnCommand.isEmpty()) {
            log.info("wait for installing Maven...");

            try {
                Thread.sleep(3000L);
            } catch (InterruptedException e) {
                log.warn(e.getLocalizedMessage(), e);
            }
        }

        return mvnCommand;
    }

    public static synchronized void findAndInstall() {
        findAndInstall(Paths.get("."));
    }

    public static synchronized void findAndInstall(Path baseDir) {
        if (StringUtils.isEmpty(mvnCommand)) {
            mvnCommand = find(baseDir);
            if (StringUtils.isEmpty(mvnCommand)) {
                MavenWrapperDownloader.download(baseDir);
                mvnCommand = find(baseDir);
            }
        }
        log.info("mvn command is '" + mvnCommand + "'");
    }

    public static String find(Path baseDir) {
        Path mvnw = SystemUtils.IS_OS_WINDOWS ? baseDir.resolve("mvnw.cmd")
                : baseDir.resolve("mvnw");

        if (mvnw.toFile().exists()) {
            return mvnw.toAbsolutePath().toString();
        }

        return "";
    }

    public static File getLocalRepository() {
        File mavenUserHomeDir = new File(System.getProperty("user.home"), ".m2");
        File settingsXml = new File(mavenUserHomeDir, "settings.xml");
        File defaultLocalRepository = new File(mavenUserHomeDir, "repository");

        if (!settingsXml.exists()) {
            return defaultLocalRepository;
        }

        try {

            Document document = parseSettingFile(settingsXml);

            String localRepository = XPathFactory.newInstance().newXPath()
                    .compile("/settings/localRepository").evaluate(document);

            if (StringUtils.isEmpty(localRepository)) {
                return defaultLocalRepository;
            }

            return new File(localRepository);

        } catch (Exception e) {
            log.warn("fail to get maven local repository path ", e);
            return defaultLocalRepository;
        }
    }

    public static void setSitWtVersion(File pomFile, String newVersion) {
        setSitWtVersion(pomFile, newVersion, pomFile);
    }

    static int setSitWtVersion(File pomFile, String newVersion, File destPomFile) {

        if (StringUtils.isEmpty(newVersion)) {
            log.warn("new sitwt.version must not be empty");
            return 2;
        }

        try {
            Document document = parseSettingFile(pomFile);

            Node versionNode = (Node) XPathFactory.newInstance().newXPath()
                    .compile("/project/properties/sitwt.version")
                    .evaluate(document, XPathConstants.NODE);

            String currentVersion = versionNode.getTextContent();

            if (currentVersion.equals(newVersion)) {
                log.warn("sitwt.version in {} is {}",
                        new Object[] { pomFile.getAbsolutePath(), currentVersion });
                return 1;
            }

            log.warn("set sitwt.version in {} {} -> {}",
                    new Object[] { pomFile.getAbsolutePath(), currentVersion, newVersion });
            versionNode.setTextContent(newVersion);

            Transformer transformer = TransformerFactory.newInstance().newTransformer();

            DOMSource domSource = new DOMSource(document);
            StreamResult result = new StreamResult(destPomFile);
            transformer.transform(domSource, result);

            return 0;
        } catch (Exception e) {
            log.error("fail to update sitwt.version", e);
            return -1;
        }
    }

    public static void main(String[] args) {
        setSitWtVersion(new File("distribution-pom.xml"), "1.1");
    }

    static File getUserSettingFile() {
        File mavenUserHomeDir = new File(System.getProperty("user.home"), ".m2");
        return new File(mavenUserHomeDir, "settings.xml");
    }

    static Document parseSettingFile(File settingFile) throws Exception {
        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            return builder.parse(settingFile);

        } catch (Exception exp) {
            throw exp;
        }
    }

    public static ProxySetting readProxySetting() {
        File settingsXml = getUserSettingFile();
        return readProxySetting(settingsXml);
    }

    public static ProxySetting readProxySetting(File settingsXml) {
        if (!settingsXml.exists()) {
            return null;
        }

        try {
            Document document = parseSettingFile(settingsXml);
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
        File settingsXml = getUserSettingFile();

        try {
            if (!settingsXml.exists()) {
                log.info("create user settings.xml");
                Files.copy(ClassLoader.getSystemResourceAsStream("settings.xml"),
                        settingsXml.toPath());
            }

            log.info("add proxy to settings.xml");
            Document document = parseSettingFile(settingsXml);

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
