package com.programacionintegral.almacen;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CalendarView;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import harmony.java.awt.Color;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

public class Consultar extends AppCompatActivity implements AdapterView.OnItemClickListener {
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
    private Switch swModo;
    private boolean modoSurtido=true;
    private int IdSurtido;
    private FloatingActionButton fb2;
    CalendarView calendario;
    private SimpleDateFormat formato;
    private String AlmacenSeleccionado;
    private FloatingActionButton fabFecha;
    String fechaSQL;

    private static final String NOMBRE_DIRECTORIO = "surtidos";
    String NOMBRE_DOCUMENTO = "prueba.pdf";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consultar);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        tvfecha=findViewById(R.id.tv_fecha_consulta );
        swModo=findViewById(R.id.sw_modo_consulta);
        calendario=findViewById(R.id.calendarViewconsultar);

        mAreaList = new ArrayList<>();
        area_adapter = new AreaListAdapter(this,mAreaList);
        final SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");

       swModo.setVisibility(VISIBLE);

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(Consultar.this);
        servidor =  sharedPrefs.getString("servidor_sql", "NULL");
        user =  sharedPrefs.getString("usuario", "NULL");
        pw =  sharedPrefs.getString("password", "NULL");

       SP_area=findViewById(R.id.spinnerAlmacenconsulta);
       SP_area.setAdapter(area_adapter);

        //ASOCIAR LA LISTA al adaptador List View
        lvSurtido = (ListView)findViewById(R.id.lv_surtidoconsulta);
        mSurtidoList = new ArrayList<>();
        adapter = new SurtidoListAdapter(this, mSurtidoList);
        lvSurtido.setAdapter(adapter);
        lvSurtido.setTextFilterEnabled(true);

        mAreaList = new ArrayList<>();
        area_adapter = new AreaListAdapter(this,mAreaList);

        FloatingActionButton fab = findViewById(R.id.fab_fecha);
        FloatingActionButton fabImprimir=findViewById(R.id.fab_imprimir);

        // Manda a imprimir con boton fabImprimir
        fabImprimir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick( View v ) {
                crearPDF();
            }
        });

        // Permisos
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,},
                    1000);
        }

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
           calendario.setVisibility(VISIBLE);
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        calendario.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange( @NonNull CalendarView view, int year, int month, int dayOfMonth ) {
                month+=1;
                fecha= dayOfMonth+"/"+month+"/"+year;
                fechaSQL =year+"/"+month+"/"+dayOfMonth;
                tvfecha.setText(fecha);
                // Obtiene los Almacenes que tubieron surtido en esa fecha
                Obtener_almacenes(fechaSQL);
                SP_area.setAdapter(area_adapter);
                SP_area.setSelection(0);
                calendario.setVisibility(INVISIBLE);
            }
        });

        // selecciona AREA
        SP_area.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SP_area.setSelection(position);
                String selState = SP_area.getSelectedItem().toString();
                String AlmacenSeleccionado =selState;
                AreaSeleccionada=selState;
                Obtener_Surtido (AlmacenSeleccionado,fechaSQL );
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        //cambio de modo
        swModo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (swModo.isChecked()==true){
                    lvSurtido.setBackgroundResource(R.color.colorEntrega);
                    modoSurtido=false;
                } else {
                    lvSurtido.setBackgroundResource(R.color.colorSurtido); ;
                    modoSurtido=true;
                }
                Obtener_Surtido (AreaSeleccionada,fechaSQL );
            }
        });
    }

    public String Obtener_almacenes (String fecha) {
        //obtener productos capturados
        String sqltext ="SELECT almacen FROM  alm_surtidos where fecha ='"+fecha+"' GROUP BY almacen" ;
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
        String sqltext ="SELECT * FROM  alm_surtidos where   almacen='"+Almacen+"' and  fecha ='"+fecha+"' Order by descripcion" ;
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

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Toast.makeText(getApplicationContext(),
                "Mensjae", Toast.LENGTH_SHORT).show();
    }


    public void crearPDF() {
        Document documento = new Document();
        try {
            File file = crearFichero(NOMBRE_DOCUMENTO);
            FileOutputStream ficheroPDF = new FileOutputStream(file.getAbsolutePath());

            PdfWriter writer = PdfWriter.getInstance(documento, ficheroPDF);

            documento.open();

            documento.add(new Paragraph("TABLA \n\n"));
            documento.add(new Paragraph( "Esto es mi texto"+"\n\n"));

            // Insertamos una tabla
            PdfPTable tabla = new PdfPTable(3);
            tabla.setWidthPercentage(100);
            //Datos del ancho de cada columna.
            tabla.setWidths(new float[] {10, 80, 10});

            //Añadimos los títulos a la tabla.
            Paragraph columna1 = new Paragraph("ID");
            columna1.getFont().setStyle(Font.BOLD);
            columna1.getFont().setSize(10);
            columna1.getFont().setColor(Color.BLUE);
            tabla.addCell(columna1);
            Paragraph columna2 = new Paragraph("Descripcion");
            columna2.getFont().setStyle(Font.BOLD);
            columna2.getFont().setSize(10);
            tabla.addCell(columna2);
            Paragraph columna3 = new Paragraph("Cant.");
            columna3.getFont().setStyle(Font.BOLD);
            columna3.getFont().setSize(10);
            tabla.addCell(columna3);



            for (surtido sug :mSurtidoList) {
                    Integer id=sug.getId_producto();
                    String desc=sug.getDescripcion();
                    int cant=sug.getCantidad();

                tabla.addCell(id.toString());
                tabla.addCell(desc);
                tabla.addCell(String.valueOf(cant));
            }

            documento.add(tabla);

        } catch(DocumentException e) {
        } catch(IOException e) {
        } finally {
            documento.close();
        }
    }

    public File crearFichero( String nombreFichero) {
        File ruta = getRuta();

        File fichero = null;
        if(ruta != null) {
            fichero = new File(ruta, nombreFichero);
        }

        return fichero;
    }

    public File getRuta() {
        File ruta = null;
        if(Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            ruta = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), NOMBRE_DIRECTORIO);
            if(ruta != null) {
                if(!ruta.mkdirs()) {
                    if(!ruta.exists()) {
                        return null;
                    }
                }
            }

        }
        return ruta;
    }

    public String Obtener_Pendientes (String Almacen,String fecha) {
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

}
