package io.sitoolkit.util.buildtoolhelper.util;

import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Properties;

public class PropertiesUtil {

    public static Properties loadFile(Path filePath) throws Exception {
        Properties properties = new Properties();
        try (InputStream inputStream = new FileInputStream(filePath.toFile())) {
            properties.load(inputStream);
        }
        return properties;
    }
}
