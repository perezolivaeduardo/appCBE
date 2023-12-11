package com.programacionintegral.almacen;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class Captura_Inventario extends AppCompatActivity {

    final Context c = this;
    int cantidad;
    String comentario;

    private ListView lvInventario;
    private inventarioListAdapter adapter;
    private List<inventario> mInventario;

    private String servidor;
    private String user;
    private String pw;

    private EditText filterText = null;
    private ImageButton btn_quitar_filtro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_captura__inventario);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab_fecha);
        btn_quitar_filtro=findViewById(R.id.btn_todo );

        // obtener id_dalamce
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(Captura_Inventario.this);
        servidor =  sharedPrefs.getString("servidor_sql", "NULL");
        user =  sharedPrefs.getString("usuario", "NULL");
        pw =  sharedPrefs.getString("password", "NULL");

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Recargando Información", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                Obtener_Inventario();
                vibrar();
            }
        });

        btn_quitar_filtro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.filtra(""); //quita filtro
                filterText.setText("");
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TextWatcher filterTextWatcher = new TextWatcher() {

            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                adapter.filtra(filterText.getText().toString().toUpperCase());
            }
        };

        filterText = findViewById(R.id.et_filtro);
        filterText.addTextChangedListener(filterTextWatcher);

        //Asociar la Lista con l adaptador ListView
        lvInventario=findViewById(R.id.lv_inventarios);
        mInventario= new ArrayList<>();
        adapter= new inventarioListAdapter( this,mInventario);
        lvInventario.setAdapter(adapter);
        lvInventario.setTextFilterEnabled(true);

        lvInventario.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub
                final inventario minvetario = (inventario) adapter.getItem(position);

                String mdesc =minvetario.getDescripcion();
               final String mId = String.valueOf(minvetario.getId_producto() );
               String mfisico = String.valueOf(minvetario.getFisico()) ;
               String mcomentario=minvetario.getComentario();

               //Toast.makeText(getApplicationContext(), mdesc+"/"+mId, Toast.LENGTH_SHORT).show();

                //inicio lanza dialogo para pedir cantidad

                LayoutInflater layoutInflaterAndroid = LayoutInflater.from(c);
                View mView = layoutInflaterAndroid.inflate(R.layout.user_input_dialog_box,  null);
                AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(c);
                alertDialogBuilderUserInput.setView(mView);
                final EditText userInputDialogEditText = (EditText) mView.findViewById(R.id.user_input_box);
                final EditText userInputComentario = (EditText) mView.findViewById(R.id.et_comentario);
                if (minvetario.getFisico() == 0) {
                    userInputDialogEditText.setText("");
                } else {
                    userInputDialogEditText.setText(mfisico);
                }

                userInputComentario.setText(mcomentario);

                alertDialogBuilderUserInput
                        .setTitle(mdesc.toString())
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogBox, int id) {
                                if (userInputDialogEditText.length()==0){
                                    vibrar();
                                    return;
                                }
                                cantidad =Integer.parseInt(userInputDialogEditText.getText().toString());
                                comentario=userInputComentario.getText().toString();

                                // actualiza Base de Datos
                                Actualizar_contado(mId,cantidad,comentario);

                                minvetario.setFisico(cantidad);
                                minvetario.setComentario(comentario);
                                adapter.notifyDataSetChanged();
                            }
                            });
                AlertDialog alertDialogAndroid = alertDialogBuilderUserInput.create();
                userInputDialogEditText.requestFocus();
                alertDialogAndroid.show ();
                userInputDialogEditText.requestFocus();
                //fin
            }
        });



        Obtener_Inventario();

    }

    public void Obtener_Inventario () {
        //obtener productos capturados
        String sqltext ="SELECT * FROM  inventario_fisico order by descripcion" ;
        mInventario.clear();
        try {
            Connection connect = conexionSQL.ConnectionHelper(servidor,user,pw);
            Statement st = connect.createStatement();
            ResultSet rs = st.executeQuery(sqltext);
            while (rs.next()) {
                //Se extraen los datos
                int id = rs.getInt("id");
                String descripcion =rs.getString("descripcion");
                int existencia =rs.getInt("existencia");
                int fisico=rs.getInt("fisico");
                String comentario=rs.getString("comentario");

                mInventario.add(new inventario("",id,"",descripcion,existencia,fisico,comentario));

            }
            //Se cierra la conexión
            connect.close();
            adapter.notifyDataSetChanged();
            return  ;
        } catch (SQLException e) {

            //Mostramos el error en caso de no conectarse
            Toast.makeText(c.getApplicationContext(),
                    e.getMessage().toString(), Toast.LENGTH_SHORT).show();
            return ;
        }
    }

    public void vibrar () {
        Vibrator vibrator=(Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(100);
    }

    public void Actualizar_contado (String codigo,Integer cantidad,String Comentario){
        try {
            //Se obtiene la conexión
            Connection connect = conexionSQL.ConnectionHelper(servidor,user,pw);
            //Se genera la consulta
            Statement st = connect.createStatement();
            Timestamp ts ;
            ts= new Timestamp( System.currentTimeMillis());
            String sql ="update inventario_fisico set fisico="+cantidad.toString()+",horacaptura= '"+ts.toString()+"', comentario='"+Comentario+"' where id='"+codigo+"' ";
            PreparedStatement psp=connect.prepareStatement(sql);
            psp.execute();
            vibrar();
            connect.close();//Se cierra la conexión
        } catch (SQLException e) {

            //Mostramos el error en caso de no conectarse
            Toast.makeText(getApplicationContext(),
                    e.getMessage().toString(), Toast.LENGTH_LONG ).show();
            return ;
        }
    };
}
