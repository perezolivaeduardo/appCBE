package com.programacionintegral.almacen;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Surtir extends AppCompatActivity implements AdapterView.OnItemClickListener {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_surtir);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //ASOCIAR LA LISTA al adaptador List View
        lvSurtido = (ListView)findViewById(R.id.lv_surtidoconsulta);
        mSurtidoList = new ArrayList<>();
        adapter = new SurtidoListAdapter(this, mSurtidoList);
        lvSurtido.setAdapter(adapter);
        lvSurtido.setTextFilterEnabled(true);

        mAreaList = new ArrayList<>();
        area_adapter = new AreaListAdapter(this,mAreaList);
        tvfecha=findViewById(R.id.tv_fecha);
        swModo=findViewById(R.id.sw_modo);
        swModo.setChecked(false);
        lvSurtido.setBackgroundResource(R.color.colorSurtido); ;

        SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
        Date fechaActual = Calendar.getInstance().getTime();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(fechaActual); // Configuramos la fecha que se recibe
        // calendar.add(Calendar.DAY_OF_YEAR, -1);
        fechaActual=calendar.getTime();
        fecha = formato.format(fechaActual);
        tvfecha.setText(fecha.toString() );
        setTitle("Surtido de Areas");
        final Button btnFirma = findViewById(R.id.btn_firma);

        // obtener id_dalamce
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(Surtir.this);
        servidor =  sharedPrefs.getString("servidor_sql", "NULL");
        user =  sharedPrefs.getString("usuario", "NULL");
        pw =  sharedPrefs.getString("password", "NULL");

        /*
         servidor="192.168.0.2";
          user="integral";
          pw="integral";
        */

        Obtener_almacenes(fecha);
        SP_area=findViewById(R.id.spinnerAlmacen);
        SP_area.setAdapter(area_adapter);
        SP_area.setSelection(0);

        AreaSeleccionada=SP_area.toString();


        //cambio de modo
        swModo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (swModo.isChecked()==true){
                    lvSurtido.setBackgroundResource(R.color.colorEntrega);
                    modoSurtido=false;
                    btnFirma.setVisibility(buttonView.VISIBLE);
                } else {
                    lvSurtido.setBackgroundResource(R.color.colorSurtido); ;
                    modoSurtido=true;
                    btnFirma.setVisibility(buttonView.INVISIBLE);
                }
                Obtener_Surtido (AreaSeleccionada,fecha );
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
                Obtener_Surtido (AlmacenSeleccionado,fecha );
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

  //      Obtener_Surtido (1,AreaSeleccionada,fecha );

        lvSurtido.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub
                final surtido msurtido = (surtido) adapter.getItem(position);
                String desc =msurtido.getDescripcion();
                String mIdSurtido = String.valueOf(msurtido.getId());
                String mfecha = msurtido.getFecha().toString();
                String mAlamcen = msurtido.getAlmacen();
                String mIdProducto = String.valueOf(msurtido.getId_producto());
                Boolean checado = msurtido.isEntregado(); // Se usa solo en la vista el campo de entrwgado pero puede ser entregado o recolectado segun switch

                if (checado==true) {
                    msurtido.setEntregado(false);
                    checado=false;
                }
                else {
                    msurtido.setEntregado(true);
                    checado=true;
                }
                adapter.notifyDataSetChanged();
                if (modoSurtido) {
                    Actualizar_recolectado(mIdSurtido,mfecha,mAlamcen,mIdProducto,checado);
                } else {
                    Actualizar_entregado(mIdSurtido,mfecha,mAlamcen,mIdProducto,checado);
                }
             //   Toast.makeText(Surtir.this,  "Producto: \n"+desc , Toast.LENGTH_SHORT).show();

            }
        });

       FloatingActionButton fab = findViewById(R.id.fab_fecha);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        FloatingActionButton fabr = findViewById(R.id.fab_resumen);
        fabr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Surtir.this,
                        Resumen.class));
            }
        });

        // Quitar seleccion del checkBox.
        FloatingActionButton fab2 = findViewById(R.id.fab2);
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Surtir.this);
                builder.setMessage("Quitar Seleccion de todos los productos ?")
                        .setPositiveButton("SI", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Quitar_seleccion(fecha,AreaSeleccionada);
                                Obtener_Surtido (AreaSeleccionada,fecha );
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User cancelled the dialog
                            }
                        }).show();
            }
        });

        btnFirma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pendientes = String.valueOf(estatus_pendientes_entregar());
                if (Integer.parseInt(pendientes) != 0) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(Surtir.this);
                    builder.setMessage("Quedan " + pendientes + " productos pendientes de entregar.");
                    builder.setPositiveButton("SI", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Intent intent = new Intent(Surtir.this, RecibirSurtido.class);
                            intent.putExtra("almacen", AreaSeleccionada);
                            intent.putExtra("fecha", fecha);
                            intent.putExtra("idsurtido",IdSurtido);
                            startActivity(intent);
                         finish();
                        }
                    });
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User cancelled the dialog
                        }
                    });
                    builder.show();
                } else {
                    //Pasa directo a firmar si no tiene pendientes
                    Intent intent = new Intent(Surtir.this, RecibirSurtido.class);
                    intent.putExtra("almacen", AreaSeleccionada);
                    intent.putExtra("fecha", fecha);
                    startActivity(intent);
                   finish();
                }
            }
        });
    }

    public String Obtener_Surtido (String Almacen,String fecha) {
        //obtener productos capturados
        String sqltext ="SELECT * FROM  alm_surtidos where cerrado=0 and almacen='"+Almacen+"' and  fecha ='"+fecha+"' Order by descripcion" ;
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

    public String Obtener_almacenes (String fecha) {
        //obtener productos capturados
        String sqltext ="SELECT almacen FROM  alm_surtidos where cerrado=0 and  fecha ='"+fecha+"' GROUP BY almacen" ;
        //Método para obtener la conexión y hacer una consulta.

        try {
            Connection connect = conexionSQL.ConnectionHelper( servidor,user,pw);
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

    public int estatus() {
        int estatus = adapter.getCount();
        return estatus;
    }

    public  int estatus_pendientes_entregar (){
        int estatus=0;
        int count =adapter.getCount();
        for (int i =0 ;i<count;i++){
            surtido lsurtido = (surtido) adapter.getItem(i);
            if (lsurtido.isEntregado()==false) {
              estatus+=1;
            }
        }
        return estatus;
    }

    public void Actualizar_entregado (String mIdSurtido , String mfecha , String mAlamcen,String mIdProducto,boolean mchecado ){
        try {
            //Se obtiene la conexión
            Connection connect = conexionSQL.ConnectionHelper(servidor,user,pw );
            //Se genera la consulta
            Statement st = connect.createStatement();
            Timestamp ts ;
            ts= new Timestamp( System.currentTimeMillis());
            String sql ="update alm_surtidos set entregado='"+String.valueOf(mchecado)+"' where id_surtido='"+mIdSurtido+"' and fecha='"+mfecha+"' and almacen='"+mAlamcen+"' and id_producto="+mIdProducto;
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

    public void Actualizar_recolectado (String mIdSurtido , String mfecha , String mAlamcen,String mIdProducto,boolean mchecado ){
        try {
            //Se obtiene la conexión
            Connection connect = conexionSQL.ConnectionHelper(servidor,user,pw );
            //Se genera la consulta
            Statement st = connect.createStatement();
            Timestamp ts ;
            ts= new Timestamp( System.currentTimeMillis());
            String sql ="update alm_surtidos set recolectado='"+String.valueOf(mchecado)+"' where id_surtido='"+mIdSurtido+"' and fecha='"+mfecha+"' and almacen='"+mAlamcen+"' and id_producto="+mIdProducto;
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

    public void Quitar_seleccion ( String mfecha , String mAlamcen ){
        try {
            //Se obtiene la conexión
            Connection connect = conexionSQL.ConnectionHelper(servidor,user,pw );
            //Se genera la consulta
            Statement st = connect.createStatement();
            Timestamp ts ;
            ts= new Timestamp( System.currentTimeMillis());
            String sql;
            if (modoSurtido) {
                sql ="update alm_surtidos set recolectado='0' where cerrado=0 and fecha='"+mfecha+"' and almacen='"+mAlamcen+"'";
            } else {
                sql ="update alm_surtidos set entregado='0' where  cerrado=0 fecha='"+mfecha+"' and almacen='"+mAlamcen+"'";
            }
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
        Toast.makeText(this,"Mensaje",Toast.LENGTH_SHORT);
    }
}

