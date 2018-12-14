package io.sitoolkit.util.buildtoolhelper.maven;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.w3c.dom.Document;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MavenUtils {

    private static String mvnCommand = "";

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

    public static Path getUserSettingFilePath() {
        Path mavenUserHomeDir = Paths.get(System.getProperty("user.home"), ".m2");
        return mavenUserHomeDir.resolve("settings.xml");
    }

    public static Document parseSettingFile(File settingFile) throws Exception {
        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            return builder.parse(settingFile);

        } catch (Exception exp) {
            throw exp;
        }
    }

}
