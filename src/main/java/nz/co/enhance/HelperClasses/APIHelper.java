package nz.co.enhance.HelperClasses;

import com.fasterxml.jackson.databind.ObjectMapper;

public class APIHelper {

    public static String objectToJson(Object obj) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(obj);
        } catch (Exception e) {
            return null;
        }
    }
}
