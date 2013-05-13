/*
 * This class allows the user to interact with the actuators 
 * through messaging enabling and disabling actions. 
 * From available actuator simulator BasicCoapClient.java file
 *
 * Written by:
 * Franceli Linney Cibrian Roble - linney11@gmail.com
 * Netzahualcoyotl Hernandez Cruz - netzahdzc@gmail.com
 *****************************************************************************/

package src.net.ubisoa.coap.sensing;

import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import org.ws4d.coap.interfaces.CoapMessage;


public class User {

    private Connection connection = null;
    static FakeCollecter fakeObj = new FakeCollecter();

    private void dbConnect() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:dat/sensing.sqlite");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean dbStore(String temp) throws UnsupportedEncodingException {
        boolean res = false;
        String timeStamp = new SimpleDateFormat("dd/MM/yyyy_HH:mm:ss").format(Calendar.getInstance().getTime());
        try {
            if (connection == null) {
                dbConnect();
            }

            Statement s = connection.createStatement();
            s.executeUpdate("CREATE TABLE IF NOT EXISTS temp ("
                    + "node, temperature, date, acction, exp2)");

                      String sql = "INSERT INTO temp VALUES (?, ?, ?, ?, ?)";
            PreparedStatement p = connection.prepareStatement(sql);

            p.setString(1, "00");
            p.setString(2, temp);
            //  p.setString(3, ubisoaMessage.getHumidityM(message.getPayload()));
            p.setString(3, timeStamp);
            p.setString(4, "user");
            p.addBatch();

            connection.setAutoCommit(false);
            p.executeBatch();
            connection.setAutoCommit(true);

            s.close();
            p.close();
            connection.close();
            connection = null;

            res = true;
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                connection.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            } finally {
                connection = null;
            }
        }
        return res;
    }
}
