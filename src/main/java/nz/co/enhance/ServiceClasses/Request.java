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
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

public class Request {
    private String endpoint;
    private List<List<String>> headers;
    private String requestType;
    private String body;
    private int responseCode;
    private String authentication = "";
    private String username;
    private String password;
    private List<String> cookies;
    private Map<String, List<String>> responseHeaders;
    private HttpURLConnection conn;


    public Request(String endpoint, String body, List<List<String>> headers, String requestType) {
        this.endpoint = endpoint;
        this.body = body;
        this.requestType = requestType;
        this.headers = headers;
    }

    public String sendRequest() {
        String output = "";

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

            URL url = new URL(endpoint);
            conn = (HttpURLConnection) url.openConnection();
            conn.setAllowUserInteraction(true);
            conn.setInstanceFollowRedirects(true);
            if (headers != null) {
                for (List<String> property : headers) {
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

            if (requestType.toUpperCase().equals("PATCH")) {
                //getting around the PATCH issue
                CloseableHttpClient httpClient = HttpClients.createDefault();
                HttpPatch httpPatch = new HttpPatch(new URI(endpoint));
                StringEntity params = new StringEntity(body, ContentType.APPLICATION_JSON);
                httpPatch.setEntity(params);
                CloseableHttpResponse response = httpClient.execute(httpPatch);
                HttpEntity entity = response.getEntity();
                responseCode = response.getStatusLine().getStatusCode();
                String responseString = EntityUtils.toString(entity, "UTF-8");
                System.out.println("PATCH response:  " + responseString);
                return responseString;

            }

            if (body != null) {  //we only do an outputstream if we have a body otherwise we get 404
                conn.setRequestMethod(requestType);
                conn.setDoOutput(true);
                OutputStream os = conn.getOutputStream();
                os.write(body.getBytes());
                os.flush();
            } else {
                conn.setRequestMethod(requestType);
                conn.setDoOutput(false);
            }


            //this does the actual body
            this.responseCode = conn.getResponseCode();
            //get cookies in response
            this.cookies = conn.getHeaderFields().get("set-cookie");

            //get headers in respones
            this.responseHeaders = conn.getHeaderFields();

            BufferedReader in = null;

            if ((conn.getResponseCode() != HttpURLConnection.HTTP_CREATED) && (conn.getResponseCode() != HttpURLConnection.HTTP_OK) && (conn.getResponseCode() != 302)) {
                output = readErrorStreamToString(conn);
            } else if (conn.getHeaderField("Content-Encoding") != null && conn.getHeaderField("Content-Encoding").contains("gzip")) {
                String outputLine;
                in = new BufferedReader(new InputStreamReader(new GZIPInputStream(conn.getInputStream())));
                while ((outputLine = in.readLine()) != null) {
                    output += outputLine + "\n";
                }
            } else {
                output = readInputStreamToString(conn);
            }

            if (output == null) {
                output = "";
            }

            conn.disconnect();

        } catch (
                Exception e) {
            System.out.println("\r\nResponse code: " + responseCode);
            System.out.println(e.getMessage());


        }

        return output;
    }

    private String readInputStreamToString(HttpURLConnection connection) {
        String result = null;
        StringBuffer sb = new StringBuffer();
        InputStream is = null;

        try {
            is = new BufferedInputStream(connection.getInputStream());
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String inputLine = "";
            while ((inputLine = br.readLine()) != null) {
                sb.append(inputLine);
            }
            result = sb.toString();
        } catch (Exception e) {
            result = null;
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                }
            }
        }

        return result;
    }

    private String readErrorStreamToString(HttpURLConnection connection) {
        String result = null;
        StringBuffer sb = new StringBuffer();
        InputStream is = null;

        try {
            is = new BufferedInputStream(connection.getErrorStream());
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String inputLine = "";
            while ((inputLine = br.readLine()) != null) {
                sb.append(inputLine);
            }
            result = sb.toString();
        } catch (Exception e) {
            result = null;
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                }
            }
        }

        return result;
    }

    public void setAuthentication(String type, String username, String password) {
        this.authentication = type;
        this.username = username;
        this.password = password;
    }


    public int getResponseCode() {
        return responseCode;
    }

    public List<String> getCookies() {
        return this.cookies;
    }

    public Map<String, List<String>> getResponseHeaders() {
        return this.responseHeaders;
    }

    public List<List<String>> getHeaders() {
        return headers;
    }

    public String getHeader(String header) {
        return conn.getHeaderField(header);
    }

    public String getBody() {
        return this.body;
    }

    public String getEndpoint() {
        return this.endpoint;
    }

    public String getRequestType() {
        return this.requestType;
    }


}
