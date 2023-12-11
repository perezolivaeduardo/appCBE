package com.programacionintegral.almacen;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Login extends AppCompatActivity {

    private String servidor;
    private String user;
    private String pw;

    private AreaListAdapter area_adapter;
    private List<area> mAreaList;
    private Spinner SP_area;
    private String AreaSeleccionada;
    private int id_Area ;
    private int kTurno;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Habilitar el botón de retroceso en el ActionBar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }


        Button btnEntrar = findViewById(R.id.btn_login_entrar);
        Button btnRevisar = findViewById(R.id.btn_login_revision);
        EditText etUsuario = findViewById(R.id.et_login_usuario);
        EditText etpw = findViewById(R.id.et_login_pw);
        EditText etTurno = findViewById(R.id.etTurno);

        // obtener id_alamcen
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(Login.this);
        servidor = sharedPrefs.getString("servidor_sql", "");
        user = sharedPrefs.getString("usuario", "");
        pw = sharedPrefs.getString("password", "");

        // rellena Combo area.

        mAreaList = new ArrayList<>();
        area_adapter = new AreaListAdapter(this,mAreaList);

        Obtener_almacenes();
        Obtener_Turno();

        etTurno.setText( String.valueOf( kTurno));
        etTurno.setEnabled(false);

        SP_area=findViewById(R.id.spLoginArea);
        SP_area.setAdapter(area_adapter);
        SP_area.setSelection(0);

        AreaSeleccionada=SP_area.toString();

        // Agregar listener para capturar la selección del Spinner
        SP_area.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Obtener el valor seleccionado del Spinner
                AreaSeleccionada = parent.getItemAtPosition(position).toString();
                Obtener_id_Almacen(AreaSeleccionada);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Si no se selecciona ningún valor, puedes manejarlo aquí
            }
        });

        btnEntrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nick, mpw;
                nick = etUsuario.getText().toString();
                mpw = etpw.getText().toString();
                String turno = etTurno.getText().toString();

                Integer id = utilerias.usuario_id(nick, mpw, servidor, user, pw);

                if (id != 0) {
                    // Si existe el usuario  Valida si ya se genero el conteo del dia


                    SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
                    String fechaActual = formato.format(Calendar.getInstance().getTime());

                    Intent intent = new Intent(Login.this, conteoDiario.class);
                    intent.putExtra("fecha", fechaActual);
                    intent.putExtra("area", AreaSeleccionada);
                    intent.putExtra("idArea", id_Area);
                    intent.putExtra("idUsuario", id);
                    intent.putExtra("usuario", nick);
                    intent.putExtra("turno", turno);
                    Login.this.startActivity(intent);
                    finish();

                }
                else
                {
                    // si no encuntra usuarios y contraseña
                    Toast.makeText(getApplicationContext(),
                            "Verificar Usuario o Contraseña", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnRevisar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nick, mpw;
                nick = etUsuario.getText().toString();
                mpw = etpw.getText().toString();

                // Crear un cuadro de diálogo para ingresar el valor del turno
                AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);
                builder.setTitle("Ingrese el valor del turno");

                // Crear un EditText dentro del cuadro de diálogo
                final EditText input = new EditText(Login.this);
                input.setInputType(InputType.TYPE_CLASS_NUMBER);
                builder.setView(input);

                // Configurar el botón "Aceptar" del cuadro de diálogo
                builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String turno = input.getText().toString();

                        Integer id = utilerias.usuario_id(nick, mpw, servidor, user, pw);

                        SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
                        String fechaActual = formato.format(Calendar.getInstance().getTime());

                        Intent intent = new Intent(Login.this, conteoDiario.class);
                        intent.putExtra("fecha", fechaActual);
                        intent.putExtra("area", AreaSeleccionada);
                        intent.putExtra("idArea", id_Area);
                        intent.putExtra("idUsuario", id);
                        intent.putExtra("usuario", nick);
                        intent.putExtra("turno", turno);
                        Login.this.startActivity(intent);
                        finish();
                    }
                });

                // Configurar el botón "Cancelar" del cuadro de diálogo
                builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                // Mostrar el cuadro de diálogo
                builder.show();
            }
        });



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

    public int Obtener_id_Almacen (String Area) {
        //obtener productos capturados
        String sqltext ="SELECT id_area FROM  areas where area='"+Area+"'" ;

        try {
            Connection connect = conexionSQL.ConnectionHelper(servidor,user,pw);
            Statement st = connect.createStatement();
            ResultSet rs = st.executeQuery(sqltext);
            while (rs.next()) {
                //Se extraen los datos
                id_Area = rs.getInt("id_area");

            }

            //Se cierra la conexión
            connect.close();

            return  id_Area;
        } catch (SQLException e) {

            //Mostramos el error en caso de no conectarse
            Toast.makeText(getApplicationContext(),
                    e.getMessage().toString(), Toast.LENGTH_SHORT).show();
            return 0;
        }
    }

    public int Obtener_Turno() {
        kTurno = 0;
        String sqltext = "SELECT valor as turno FROM sys_parametros where id=2";

        try (Connection connect = conexionSQL.ConnectionHelper(servidor, user, pw);
             PreparedStatement preparedStatement = connect.prepareStatement(sqltext);
             ResultSet rs = preparedStatement.executeQuery()) {

            if (rs.next()) {
                kTurno = rs.getInt("turno");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            // También podrías lanzar una excepción personalizada en lugar de simplemente imprimir el error.
        }

        return kTurno;
    }

}
