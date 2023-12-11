package com.programacionintegral.almacen;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Formatter;
import java.util.List;


public abstract class utilerias extends Context {

    static Context context;

    private static String servidor;
    private static  String user;
    private static String pw;


    public static String MES (){

        Calendar mCalendar = Calendar.getInstance();
        Integer mes=mCalendar.get(mCalendar.MONTH)+1 ;
        List<String> list = new ArrayList<String>();
        list.add("ENE");
        list.add("FEB");
        list.add("MAR")    ;
        list.add("ABR");
        list.add("MAY");
        list.add("JUN");
        list.add("JUL");
        list.add("AGO");
        list.add("SEP");
        list.add("OCT");
        list.add("NOV");
        list.add("DIC");
        String m=(String)list.get(mes-1).toString();
        Log.d("utilerias", "mes : "+String.valueOf(mes)+" m:"+m);

        return m;
    }

    public static int usuario_id(String kuser,String kpw, String dbServer,String dbUser,String dbPw) {
        //obtener productos capturados
        Integer  dndoc=0;
        String sqltext ="select id_user as id from usuarios where nick='"+kuser+"' and pw='"+kpw+"'" ;

        try {
            Connection connect = conexionSQL.ConnectionHelper(dbServer,dbUser,dbPw);
            Statement st = connect.createStatement();
            ResultSet rs = st.executeQuery(sqltext);
            while (rs.next()) {
                dndoc = rs.getInt("id");
            }
            //Se cierra la conexión
            connect.close();
            return dndoc;
        } catch (SQLException e) {
            //Mostramos el error en caso de no conectarse
       //     Toast.makeText(getApplicationContext(), e.getMessage().toString(), Toast.LENGTH_SHORT).show();
            return 0;
        }
    }


    public static Integer idUsuario (String usuario){
        String sql="select id_user as id from usuarios where nick='"+usuario+"'";
        Integer id=-1;
    try {
        Connection connect = conexionSQL.ConnectionHelper( "","","");
        Statement st = connect.createStatement();
        ResultSet rs = st.executeQuery(sql);
        //Se cierra la conexión
        while (rs.next()) {
            id =rs.getInt("id");;
        }
        connect.close();
        return id;
    } catch (SQLException e) {
        //Mostramos el error en caso de no conectarse
        return -1;
    }

}

 //Funcion para folear numeros (poner Ceros)
    public static String ceros (Integer numero,Integer ceros){
    Formatter obj=new Formatter();
    String con_ceros= String.valueOf ( obj.format("%0"+ceros+"d", numero));
    return con_ceros;
}

    public  static Date fecha_actual() {
        return (Date) Calendar.getInstance().getTime();
}



}



