package nz.co.enhance.HelperClasses;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

public class PropertiesHandler {

    public static Properties loadProperties(String path) {
        Properties properties = new Properties();
        try {
            FileInputStream input = new FileInputStream(path);
            properties.load(input);
            return properties;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void writeOutProperties(Properties properties, String fileName) {
        try {
            FileOutputStream out = new FileOutputStream(fileName);
            properties.store(out, null);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
