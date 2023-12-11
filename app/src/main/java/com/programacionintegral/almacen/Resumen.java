package com.programacionintegral.almacen;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Resumen extends AppCompatActivity {
    // LISTVIEW CON VARIOS RENGLONES
    private ListView lvSurtido;
    private SurtidoListAdapter adapter;
    private List<surtido> mSurtidoList;

    private AreaListAdapter area_adapter;
    private List<area> mAreaList;
    private Spinner SP_area;
    private TextView tvfecha;
    private String AreaSeleccionada;
    private String fecha;
    private String servidor;
    private String user;
    private String pw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resumen);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //ASOCIAR LA LISTA al adaptador List View
        lvSurtido = (ListView)findViewById(R.id.lv_resumen);
        //lvSurtido = (ListView)findViewById(R.id.lv_surtido);
        mSurtidoList = new ArrayList<>();
        mAreaList = new ArrayList<>();
        adapter = new SurtidoListAdapter(this, mSurtidoList);
        area_adapter = new AreaListAdapter(this,mAreaList);
        lvSurtido.setAdapter(adapter);
        lvSurtido.setTextFilterEnabled(true);

        tvfecha=findViewById(R.id.tv_fecha2);

        SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
        Date fechaActual = Calendar.getInstance().getTime();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(fechaActual); // Configuramos la fecha que se recibe
        //calendar.add(Calendar.DAY_OF_YEAR, -1);
        fechaActual=calendar.getTime();
        fecha = formato.format(fechaActual);
        tvfecha.setText(fecha.toString() );
        setTitle("RESUMEN");

        // obtener id_dalamce
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(Resumen.this);
        servidor =  sharedPrefs.getString("servidor_sql", "NULL");
        user =  sharedPrefs.getString("usuario", "NULL");
        pw =  sharedPrefs.getString("password", "NULL");

      /*  servidor="192.168.0.6";
        user="integral";
        pw=user;
    */
         Obtener_Resumen(fecha);

        lvSurtido.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub
                final surtido msurtido = (surtido) adapter.getItem(position);
                final String desc =msurtido.getDescripcion();
                String mIdSurtido = String.valueOf(msurtido.getId());
                String mfecha = msurtido.getFecha().toString();
                String mAlamcen = msurtido.getAlmacen();
                final String mIdProducto = String.valueOf(msurtido.getId_producto());
                final String miDescripcion = msurtido.getDescripcion();
                Boolean checado = msurtido.isEntregado();
                String desglose= Obtener_Desglose(fecha,String.valueOf(mIdProducto));
                AlertDialog.Builder builder = new AlertDialog.Builder(Resumen.this);
                builder.setMessage("Surtido de "+miDescripcion+" \n\n" +desglose )
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                            }
                        })
                        .show();
            }
        });

        FloatingActionButton fab = findViewById(R.id.fab_fecha);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public String Obtener_Resumen (String fecha) {
        //obtener productos capturados
        String sqltext ="SELECT id_producto, descripcion, SUM(cantidad) AS cantidad FROM alm_surtidos WHERE fecha = CONVERT(DATETIME, '"+fecha+" 00:00:00', 102) GROUP BY id_producto, descripcion" ;
        //Método para obtener la conexión y hacer una consulta.


        mSurtidoList.clear();
        try {
            Connection connect = conexionSQL.ConnectionHelper(servidor,user,pw);

            // Connection connect = conexionSQL.ConnectionHelper("192.168.0.7","integral","Integral");

            Statement st = connect.createStatement();
            ResultSet rs = st.executeQuery(sqltext);
            while (rs.next()) {
                //Se extraen los datos
                Integer midProducto=rs.getInt("id_producto");
                String mdescripcion = rs.getString("descripcion");
                int mcontado = rs.getInt("cantidad");

                mSurtidoList.add(new surtido( 99,  fecha, "", midProducto, mdescripcion, mcontado, false,"",false));

            }

            //Se cierra la conexión
            connect.close();
            adapter.notifyDataSetChanged();
            return  "";
        } catch (SQLException e) {

            //Mostramos el error en caso de no conectarse
            Toast.makeText(getApplicationContext(),
                    e.getMessage().toString(), Toast.LENGTH_SHORT).show();
            return "";
        }
    }

    public String Obtener_Desglose (String fecha,String id_producto) {
        //obtener productos capturados
        String sqltext ="SELECT almacen, SUM(cantidad) AS cantidad FROM alm_surtidos WHERE fecha = CONVERT(DATETIME, '"+fecha+" 00:00:00', 102) GROUP BY id_producto, descripcion, almacen HAVING (id_producto = "+id_producto+")" ;

        String resumen = "";Integer tot=0;

        try {
            Connection connect = conexionSQL.ConnectionHelper(servidor,user,pw);
            Statement st = connect.createStatement();
            ResultSet rs = st.executeQuery(sqltext);
            while (rs.next()) {
                //Se extraen los datos
                Integer micantidad=rs.getInt("cantidad");
                String marea = rs.getString("almacen");
                tot=tot+micantidad;
                resumen =resumen+ String.valueOf(micantidad)+" "+marea +" \n";
            }

            //Se cierra la conexión
            connect.close();
            adapter.notifyDataSetChanged();
            resumen+="_____________________________\n";
            resumen +=" Total surtido en el dia." +String.valueOf(tot);
            return  resumen;
        } catch (SQLException e) {

            //Mostramos el error en caso de no conectarse
            Toast.makeText(getApplicationContext(),
                    e.getMessage().toString(), Toast.LENGTH_SHORT).show();
            return "";
        }
    }

    public void vibrar () {
        Vibrator vibrator=(Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(100);
    }



}
