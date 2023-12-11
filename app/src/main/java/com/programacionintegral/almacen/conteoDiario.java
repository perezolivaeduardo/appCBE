package com.programacionintegral.almacen;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class conteoDiario extends AppCompatActivity {
    String fecha;
    String area;
    Integer id_area;
    Integer idUsuario;
    String usuario;
    TextView tvfecha;
    String turno;

    private String AreaSeleccionada;
    private Switch swFiltro;
    private Boolean flagTodos;
    private Button BtnFaltantes;
    private Button BtnPrecaptura;
    private String servidor;
    private String user;
    private String pw;
    private ListView lvconteo;
    private conteoDiarioListAdapter mConteoDiarioListAdapter;
    private List<inventariodiario> mConteoDiario;
    private AreaListAdapter area_adapter;
    private List<area> mAreaList;
    private Spinner SP_area;
    private String Tecleado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conteo_diario);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        //lee variables que manda activity lanzador
        Bundle extra = this.getIntent().getExtras();
        fecha=extra.getString("fecha");
        area=extra.getString("area");
        id_area=extra.getInt("idArea");
        idUsuario=extra.getInt("idUsuario");
        usuario=extra.getString("usuario");
        turno=extra.getString("turno");

        TextView tvusuario = findViewById(R.id.tvUsuarioCuenta);
        tvusuario.setText(usuario);
       TextView tvArea = findViewById(R.id.tvConteoArea);
       tvArea.setText(area);
       TextView tvConteoTurno = findViewById(R.id.tvConteoTurno);
       tvConteoTurno.setText(turno);

        // obtener datos del servidor
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(conteoDiario.this);
        servidor =  sharedPrefs.getString("servidor_sql", "NULL");
        user =  sharedPrefs.getString("usuario", "NULL");
        pw =  sharedPrefs.getString("password", "NULL");

        //Enlaza lista
        lvconteo= findViewById(R.id.lvConteoDiario);
        mConteoDiario = new ArrayList<>();
        mConteoDiarioListAdapter = new conteoDiarioListAdapter(this, mConteoDiario);
        lvconteo.setAdapter(mConteoDiarioListAdapter);
        lvconteo.setTextFilterEnabled(true);

        mAreaList = new ArrayList<>();
        area_adapter = new AreaListAdapter(this,mAreaList);

        swFiltro=findViewById(R.id.swfiltro);
        BtnFaltantes=findViewById(R.id.ButtonConteoFaltantes);
        BtnPrecaptura = findViewById(R.id.btn_precaptura);


        tvfecha=findViewById(R.id.tv_conteo_fecha);
        tvfecha.setText(fecha);
        toolbar.setTitle(area);

        Obtener_Hoja_inventario(area,fecha,String.valueOf(turno));

        BtnFaltantes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mConteoDiarioListAdapter.filtrarFaltantes(Boolean.TRUE);
            }
        });

        BtnPrecaptura.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                preconteo(String.valueOf(id_area),fecha,turno);
                Obtener_Hoja_inventario(area,fecha,String.valueOf(turno));
            }
        });

        swFiltro.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                flagTodos=isChecked;
                mConteoDiarioListAdapter.filtrar(flagTodos);
            }
        });
    }

    public String Obtener_Hoja_inventario(String area, String fecha, String Turno) {
        //obtener productos capturados
            String sqltext ="SELECT id_area, id_producto, fecha, turno, descripcion + ' (' + CAST(stock AS NVARCHAR(50))+')' AS descripcion, cantidad, hora, id_usuario, id_recibe, contado, comentario, area, stock, consumidos, libros\n"
                    + "FROM            dbo.inv_conteo_diario where turno="+Turno+" and area ='"+area+"' and convert(date,fecha) ='"+fecha+"' Order by descripcion" ;
        //Método para obtener la conexión y hacer una consulta.
        mConteoDiario.clear();
        try {
            Connection connect = conexionSQL.ConnectionHelper(servidor,user,pw);
            Statement st = connect.createStatement();
            ResultSet rs = st.executeQuery(sqltext);
            while (rs.next()) {
                //Se extraen los datos
                Integer midArea = rs.getInt("id_area");
                String mfecha =String.valueOf(rs.getDate("fecha"));
                Integer marea=rs.getInt("id_area");
                Integer midProducto=rs.getInt("id_producto");
                String mdescripcion = rs.getString("descripcion");
                int mcontado = rs.getInt("cantidad");
                int mstock = rs.getInt("stock");
                int mconsumidos = rs.getInt("consumidos");
                int mlibros=rs.getInt("libros");
                String mturno =rs.getString("libros");
                mConteoDiario.add(new inventariodiario( midArea, midProducto,  mfecha, mturno, mdescripcion, mcontado,0,"",false,mstock,mconsumidos,mlibros));
            }
            //Se cierra la conexión
            connect.close();
            mConteoDiarioListAdapter.notifyDataSetChanged();
            return  "";
        } catch (SQLException e) {

            //Mostramos el error en caso de no conectarse
            Toast.makeText(getApplicationContext(),
                    e.getMessage().toString(), Toast.LENGTH_SHORT).show();
            return "";
        }
    }

    public String Obtener_almacenes () {
        //obtener productos capturados
        String sqltext ="SELECT area FROM  areas where stock=1" ;

        try {
            Connection connect = conexionSQL.ConnectionHelper(servidor,user,pw);
            Statement st = connect.createStatement();
            ResultSet rs = st.executeQuery(sqltext);
            while (rs.next()) {
                //Se extraen los datos
                String area = rs.getString("area");
                mAreaList.add(new area(area));
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

    public void preconteo (String area, String fecha, String Turno){
        try {
            //Se obtiene la conexión
            Connection connect = conexionSQL.ConnectionHelper(servidor,user,pw);
            //Se genera la consulta
            Statement st = connect.createStatement();
            Timestamp ts ;
            ts= new Timestamp( System.currentTimeMillis());
            String sql ="update inv_conteo_diario set cantidad = libros,  hora = GETDATE(), contado = 1 where id_area={idarea} and turno = {turno} and fecha='{fecha}'" ;
            sql=sql.replace("{idarea}",area);
            sql=sql.replace("{turno}",Turno);
            sql=sql.replace("{fecha}",fecha);
            PreparedStatement psp=connect.prepareStatement(sql);
            psp.execute();
            connect.close();//Se cierra la conexión

        } catch (SQLException e) {
            //Mostramos el error en caso de no conectarse
            Toast.makeText(getApplicationContext(),
                    e.getMessage().toString(), Toast.LENGTH_LONG ).show();
            return ;
        }

    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Manejar los clics en la toolbar
        switch (item.getItemId()) {
            case android.R.id.home:
                // Si se hizo clic en el botón de "atrás" (flecha de la toolbar), se llama a onBackPressed()
                onBackPressed();
                return true;
            // Agregar otros casos según tus necesidades si tienes más opciones en la toolbar
        }
        return super.onOptionsItemSelected(item);
    }
}