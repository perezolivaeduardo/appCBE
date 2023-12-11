package com.programacionintegral.almacen;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CalendarView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.view.View.INVISIBLE;

public class Pendientes extends AppCompatActivity implements AdapterView.OnItemClickListener {

    // LISTVIEW CON VARIOS RENGLONES
    private ListView lvSurtido;
    private PendienteListAdapter adapter;
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
    private Switch swModo;
    private boolean modoSurtido=true;
    private int IdSurtido;
    private FloatingActionButton fb2;
    CalendarView calendario;
    private SimpleDateFormat formato;
    private String AlmacenSeleccionado;


    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consultar);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Productos pendientes de surtir");

        tvfecha=findViewById(R.id.tv_fecha_consulta);
        swModo=findViewById(R.id.sw_modo_consulta);

        mAreaList = new ArrayList<>();
        area_adapter = new AreaListAdapter(this,mAreaList);
        final SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(Pendientes.this);
        servidor =  sharedPrefs.getString("servidor_sql", "NULL");
        user =  sharedPrefs.getString("usuario", "NULL");
        pw =  sharedPrefs.getString("password", "NULL");

       SP_area=findViewById(R.id.spinnerAlmacenconsulta);
       SP_area.setAdapter(area_adapter);

        //ASOCIAR LA LISTA al adaptador List View
        lvSurtido = (ListView)findViewById(R.id.lv_surtidoconsulta);
        mSurtidoList = new ArrayList<>();
        adapter = new PendienteListAdapter(this, mSurtidoList);
        lvSurtido.setAdapter(adapter);
        lvSurtido.setTextFilterEnabled(true);
        calendario=findViewById(R.id.calendarViewconsultar);
        calendario.setVisibility(INVISIBLE);

        mAreaList = new ArrayList<>();
        area_adapter = new AreaListAdapter(this,mAreaList);

        FloatingActionButton fab = findViewById(R.id.fab_fecha);
        fab.setVisibility(INVISIBLE);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        Date fechaActual = java.util.Calendar.getInstance().getTime();
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        calendar.setTime(fechaActual); // Configuramos la fecha que se recibe
        calendar.add(java.util.Calendar.DAY_OF_YEAR, -1);
        fechaActual=calendar.getTime();
        fecha = formato.format(fechaActual);

        Obtener_almacenes(fecha);
        SP_area=findViewById(R.id.spinnerAlmacenconsulta);
        SP_area.setAdapter(area_adapter);
        SP_area.setSelection(0);

        // selecciona AREA
        SP_area.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SP_area.setSelection(position);
                String selState = SP_area.getSelectedItem().toString();
                String AlmacenSeleccionado =selState;
                AreaSeleccionada=selState;
                Obtener_Surtido (AlmacenSeleccionado,fecha );
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


        lvSurtido.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub
                final surtido msurtido = (surtido) adapter.getItem(position);
                String desc =msurtido.getDescripcion();
                String mIdSurtido = String.valueOf(msurtido.getId());
                String mAlamcen = msurtido.getAlmacen();
                String mIdProducto = String.valueOf(msurtido.getId_producto());
                adapter.notifyDataSetChanged();
                Actualizar_fecha(mIdSurtido,fecha,mAlamcen,mIdProducto);
                Toast.makeText(Pendientes.this,  "Producto: "+desc+"\n se añadio a Surtido de HOY" , Toast.LENGTH_SHORT).show();
                return false;
            }
        });
    }

    public String Obtener_almacenes (String fecha) {
        //obtener productos capturados
        String sqltext ="SELECT almacen FROM  alm_surtidos where entregado=0 and fecha <'"+fecha+"' GROUP BY almacen" ;
        mAreaList.clear();
        try {
            Connection connect = conexionSQL.ConnectionHelper(servidor,user,pw);

            Statement st = connect.createStatement();
            ResultSet rs = st.executeQuery(sqltext);
            while (rs.next()) {
                //Se extraen los datos
                String Alma = rs.getString("almacen");
                mAreaList.add(new area(Alma));
            }

            //Se cierra la conexión
            connect.close();
            // area_adapter.notifyDataSetChanged();
            return  "";
        } catch (SQLException e) {

            //Mostramos el error en caso de no conectarse
            Toast.makeText(getApplicationContext(),
                    e.getMessage().toString(), Toast.LENGTH_SHORT).show();
            return "";
        }
    }

    public String Obtener_Surtido (String Almacen,String fecha) {
        //obtener productos capturados
        String sqltext ="SELECT * FROM  alm_surtidos where entregado=0 and  almacen='"+Almacen+"' and  fecha <'"+fecha+"' Order by descripcion" ;
        //Método para obtener la conexión y hacer una consulta.
        mSurtidoList.clear();
        try {
            Connection connect = conexionSQL.ConnectionHelper(servidor,user,pw);
            Statement st = connect.createStatement();
            ResultSet rs = st.executeQuery(sqltext);
            while (rs.next()) {
                //Se extraen los datos
                Integer midSurtido = rs.getInt("id_surtido");
                IdSurtido=midSurtido;
                String mfecha =String.valueOf(rs.getDate("fecha"));
                String mAlmacen=rs.getString("almacen");
                Integer midProducto=rs.getInt("id_producto");
                String mdescripcion = rs.getString("descripcion");
                int mcontado = rs.getInt("cantidad");
                String mComentario =rs.getString("comentarios");
                Boolean checado;
                if (modoSurtido){
                    checado = rs.getBoolean("recolectado");
                } else {
                    checado = rs.getBoolean("entregado");
                }

                // Boolean recolectado=rs.getBoolean("recolectado");

                mSurtidoList.add(new surtido( midSurtido,  mfecha, mAlmacen, midProducto, mdescripcion, mcontado,checado,"",false));
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


    public void Actualizar_fecha (String mIdSurtido , String mfecha , String mAlamcen,String mIdProducto ){

        try {
            //Se obtiene la conexión
            Connection connect = conexionSQL.ConnectionHelper(servidor,user,pw );
            //Se genera la consulta
            Statement st = connect.createStatement();
            Timestamp ts ;
            ts= new Timestamp( System.currentTimeMillis());
            String sql ="update alm_surtidos set fecha='"+mfecha+"' where id_surtido='"+mIdSurtido+"'  and almacen='"+mAlamcen+"' and id_producto="+mIdProducto;
            PreparedStatement psp=connect.prepareStatement(sql);
            psp.execute();

            connect.close();//Se cierra la conexión
        } catch (SQLException e) {
            vibrar();
            //Mostramos el error en caso de no conectarse
            Toast.makeText(getApplicationContext(),
                    e.getMessage().toString(), Toast.LENGTH_LONG ).show();
            return ;
        }


    };


    public void vibrar () {
        Vibrator vibrator=(Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(100);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Toast.makeText(getApplicationContext(),
                "Mensjae", Toast.LENGTH_SHORT).show();
    }
}
