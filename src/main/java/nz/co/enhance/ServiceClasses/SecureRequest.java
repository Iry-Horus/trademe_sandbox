package nz.co.enhance.ServiceClasses;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

public class SecureRequest {

    private HttpsURLConnection conn;
    private String URL;
    private List<List<String>> properties;
    private String requestType;
    private String body;
    private int responseCode;
    private String authentication = "";
    private String username;
    private String password;
    private String cookie = "";
    private Map<String, List<String>> headers;


    public SecureRequest(String URL, String body, List<List<String>> properties, String requestType) {
        this.URL = URL;
        this.body = body;
        this.requestType = requestType;
        this.properties = properties;
    }

    public String sendRequest() {
        String outputJSON = "";

        //Note that the following code ignored bad certificates. Use at your own risk on systems you trust.
        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            public void checkClientTrusted(X509Certificate[] certs, String authType) {
            }

            public void checkServerTrusted(X509Certificate[] certs, String authType) {
            }
        }};

        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {
            ;
        }


        try {

            java.net.URL url = new URL(URL);
            conn = (HttpsURLConnection) url.openConnection();


            if (properties != null) {
                for (List<String> property : properties) {
                    conn.addRequestProperty(property.get(0), property.get(1));
                }
            }

            //checks for authentication settings
            if (authentication.equals("Basic")) {

                String userpass = username + ":" + password;
                String basicAuth = "Basic " + javax.xml.bind.DatatypeConverter.printBase64Binary(userpass.getBytes());

                conn.setRequestProperty("Authorization", basicAuth);
            } else if (authentication.equals("Bearer")) {
                conn.setRequestProperty("Authorization", "Bearer " + username);
            }

            //we only do an outputstream if we are posting otherwise we get 404
            if ((requestType.toUpperCase().equals("POST")) || (requestType.toUpperCase().equals("PUT"))) {
                conn.setRequestMethod(requestType);
                conn.setDoOutput(true);
                OutputStream os = conn.getOutputStream();

                if (body != null) {
                    os.write(body.getBytes());
                } else {
                    os.write(0);
                }
                os.flush();
            } else if (requestType.toUpperCase().equals("PATCH")) {
                CloseableHttpClient httpClient = HttpClients.createDefault();
                HttpPatch httpPatch = new HttpPatch(new java.net.URI(URL));
                StringEntity params = new StringEntity(body, ContentType.APPLICATION_JSON);
                httpPatch.setEntity(params);
                CloseableHttpResponse response = httpClient.execute(httpPatch);
                HttpEntity entity = response.getEntity();
                responseCode = response.getStatusLine().getStatusCode();
                String responseString = EntityUtils.toString(entity, "UTF-8");
                System.out.println("PATCH response:  " + responseString);
                return responseString;

            } else {
                conn.setRequestMethod(requestType);
                conn.setDoOutput(false);
            }


            //this does the actual body
            this.responseCode = conn.getResponseCode();

            //get cookies in response
            this.cookie = conn.getHeaderField("Set-Cookie");

            this.headers = conn.getHeaderFields();

            BufferedReader in = null;

            if ((conn.getResponseCode() != HttpURLConnection.HTTP_CREATED) && (conn.getResponseCode() != HttpURLConnection.HTTP_OK) && (conn.getResponseCode() != 302)) {
                in = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            } else if (conn.getHeaderField("Content-Encoding") != null && conn.getHeaderField("Content-Encoding").contains("gzip")) {
                in = new BufferedReader(new InputStreamReader(new GZIPInputStream(conn.getInputStream())));
            } else {
                in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            }

            String output;
            System.out.println("Output from Server ....");
            while ((output = in.readLine()) != null) {
                System.out.println(output);
                outputJSON += output;
            }
            System.out.println("\r\n\r\n");
            conn.disconnect();

        } catch (Exception e) {
            System.out.println("\r\nResponse code: " + responseCode);
            System.out.println(e.getMessage());
        }

        return outputJSON;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public String getCookie() {
        return this.cookie;
    }


    public Map<String, List<String>> getHeaders() {
        return headers;
    }

    public String getHeader(String header) {
        return conn.getHeaderField(header);
    }

    public String getRequestContent() {
        return this.body;
    }

    public String getEndpoint() {
        return this.URL;
    }

}
