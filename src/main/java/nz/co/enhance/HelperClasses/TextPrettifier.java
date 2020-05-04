package nz.co.enhance.HelperClasses;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.StringReader;
import java.io.StringWriter;

public class TextPrettifier {


    public static String convertXMLtoHTMLFriendly(String xmlToConvert) {

        xmlToConvert = prettyFormatXML(xmlToConvert);
        xmlToConvert = xmlToConvert.replace("<", "&lt;");
        xmlToConvert = xmlToConvert.replace(">", "&gt;");
        return xmlToConvert;
    }

    public static String prettyFormatXML(String input) {
        try {
            Source xmlInput = new StreamSource(new StringReader(input));
            StringWriter stringWriter = new StringWriter();
            StreamResult xmlOutput = new StreamResult(stringWriter);
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(xmlInput, xmlOutput);
            return xmlOutput.getWriter().toString();
        } catch (Exception e) {
            return "Not XML or could not be parsed";
        }
    }

    public static String indentJson(String input) {
        try {
            if (input != null) {
                String json;
                JsonParser parser = new JsonParser();
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                JsonElement el = parser.parse(input);
                json = gson.toJson(el);
                return json;
            }
        } catch (Exception e) {
            return input;
        }
        return null;

    }
}
