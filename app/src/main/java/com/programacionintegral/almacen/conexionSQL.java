package com.programacionintegral.almacen;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.StrictMode;
import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static android.app.PendingIntent.getActivity;
import static android.preference.PreferenceManager.getDefaultSharedPreferences;
import static android.preference.PreferenceManager.getDefaultSharedPreferencesName;

public class conexionSQL extends Activity {
    static Context context;

    public static Connection ConnectionHelper(String servidor, String user, String pw) {

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Connection connection = null;
        String ConnectionURL = null;

        try {
            Class.forName("net.sourceforge.jtds.jdbc.Driver").newInstance();
        // ConnectionURL = "jdbc:jtds:sqlserver://programacionintegral.database.windows.net:1433;DatabaseName=reko;user=eduardo@programacionintegral;password=Integral#2017;encrypt=true;trustServerCertificate=false;hostNameInCertificate=*.database.windows.net;loginTimeout=30;";
        ConnectionURL = "jdbc:jtds:sqlserver://"+"serverclinicaesperanza.database.windows.net"+":1433;DatabaseName=clinica;user=eduardo;password=Integral#2020";
       //ConnectionURL = "jdbc:jtds:sqlserver://"+servidor+":1433;DatabaseName=clinica;user="+user+";password="+pw;
        // ConnectionURL = "jdbc:jtds:sqlserver://192.168.1.209:1433;DatabaseName=clinica;user=sa;password=integral";
      // ConnectionURL = "jdbc:jtds:sqlserver://192.168.0.25:1433;DatabaseName=clinica;user=sa;password=integral";
      // ConnectionURL = "jdbc:jtds:sqlserver://192.168.1.4:1433;DatabaseName=clinica;user=sistemas;password=Integra#l2023";
        // ConnectionURL = "jdbc:jtds:sqlserver://192.168.1.1;DatabaseName=clinica;user=sa;password=Integral2016";
        // ConnectionURL = "jdbc:jtds:sqlserver://192.168.1.1;DatabaseName=clinica;user=sa;password=Integral2016";

            connection = DriverManager.getConnection(ConnectionURL);

        } catch (SQLException se) {
            Log.e("probar", se.getMessage());
        } catch (ClassNotFoundException e) {
            Log.e("probar", e.getMessage());
        } catch (Exception e) {
            Log.e("probar", e.getMessage());
        }
        return connection;
    }


    public static Connection ConnectionSQL(String cadena) {

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Connection connection = null;
        String ConnectionURL = null;

        try {
            Class.forName("net.sourceforge.jtds.jdbc.Driver").newInstance();

            connection = DriverManager.getConnection(cadena);

        } catch (SQLException se) {
            Log.e("probar", se.getMessage());
        } catch (ClassNotFoundException e) {
            Log.e("probar", e.getMessage());
        } catch (Exception e) {
            Log.e("probar", e.getMessage());
        }
        return connection;
    }


}
