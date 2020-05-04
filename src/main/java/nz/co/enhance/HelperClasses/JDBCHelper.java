package nz.co.enhance.HelperClasses;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class JDBCHelper {

    String URL;
    String query = null;
    ResultSet rs;
    Statement stmt;
    Connection conn = null;
    String userName;
    String password;
    String driverName = "oracle.jdbc.driver.OracleDriver"; //default to oracle

    public JDBCHelper(String URL, String userName, String password, String query) {
        this.URL = URL;
        this.userName = userName;
        this.password = password;
        this.query = query;
        setup();
    }

    public JDBCHelper(String URL, String driverName, String userName, String password, String query) {
        this.URL = URL;
        this.userName = userName;
        this.password = password;
        this.query = query;
        this.driverName = driverName;
        setup();
    }


    private void setup() {
        createConnection();
        sendQuery(query);
    }

    //this is mainly for testing purposes as usually we would get a results set via the conn
    public JDBCHelper(ResultSet rs) {
        this.rs = rs;
    }

    //You must have ojdbc6.jar in your /jre/lib/ext directory to open an oracle connection successfully
    //You must have sqljdbc4.jar in your /jre/lib/ext directory to open a sqlserver connection successfully
    public void createConnection() {
        // create  the connection object
        try {
            Class.forName(driverName);
            conn = DriverManager.getConnection(URL, userName, password);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //closes after each query in case of assertion errors
    public void sendQuery(String query) {
        try {
            stmt = conn.createStatement();
            stmt.setQueryTimeout(30);
            rs = stmt.executeQuery(query);
        } catch (SQLException e) {
            System.out.println("Query did not return a result set.");
            e.printStackTrace();
        }
    }


    public HashMap<String, List<String>> getData() {
        HashMap<String, List<String>> data = new HashMap<>();
        try {
            ResultSetMetaData rsmd = rs.getMetaData();
            for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                data.put(rsmd.getColumnName(i), new ArrayList<String>());
            }

            while (rs.next()) {
                for (int q = 1; q <= rsmd.getColumnCount(); q++) {
                    data.get(rsmd.getColumnName(q)).add(rs.getString(q));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return data;
    }


}
