package nz.co.enhance.HelperClasses;


import org.apache.commons.io.FileUtils;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class FileHandler {
    public static void writeOutFileAsHTML(Document resource, String fileName) throws IOException {
        File file = new File(fileName);
        FileUtils.writeStringToFile(file, resource.outerHtml(), "UTF-8");
    }

    public static void writeOutFile(String resource, String fileName) throws IOException {
        File file = new File(fileName);
        FileUtils.writeStringToFile(file, resource, "UTF-8");
    }

    public static String readFileAsString(String fileName) {
        File file = new File(fileName);
        try {
            return FileUtils.readFileToString(file,"UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static InputStream readFileAsInputStream(String fileName) {
        File file = new File(fileName);
        try {
            return  FileUtils.openInputStream(file);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
