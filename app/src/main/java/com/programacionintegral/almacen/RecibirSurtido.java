package com.programacionintegral.almacen;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

public class RecibirSurtido extends AppCompatActivity {

    String servidor;
    String user;
    String pw;
    String nombreUsuario="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recibir_surtido);
        Bundle extra = this.getIntent().getExtras();
        final String almacen = extra.getString("almacen","");
        final String fecha = extra.getString("fecha","");
        final int IdSurtido =extra.getInt("idsurtido",0);

        TextView tvalmacen=findViewById(R.id.tv_recibe_almacen);
        TextView tvfecha=findViewById(R.id.tv_recibe_fecha);
        final TextView tvusuario = findViewById(R.id.tv_recibie_usuario);
        final TextView tvpw= findViewById(R.id.tv_contraseña);
        tvalmacen.setText(almacen);
        tvfecha.setText(fecha);
        final TextView tvnombre = findViewById(R.id.tv_recibe_nombre);
        final TextView tvidsurtido = findViewById(R.id.tv_recibe_idsurtido);
        tvidsurtido.setText(String.valueOf( IdSurtido));
        final TextView tvcomentario=findViewById(R.id.tv_recibe_comentarios);
        final TextView tvresumen = findViewById(R.id.tv_recibe_resumen);

        // obtener id_dalamce
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(RecibirSurtido.this);
        servidor =  sharedPrefs.getString("servidor_sql", "NULL");
        user =  sharedPrefs.getString("usuario", "NULL");
        pw =  sharedPrefs.getString("password", "NULL");

        final Button btnValida = findViewById(R.id.btn_valida );
        final Button btnrecibe = findViewById(R.id.btn_recibir);
        btnrecibe.setVisibility(View.INVISIBLE);

        String miResume=Obtener_comentarios(IdSurtido);
        tvresumen.setText(miResume);

        btnrecibe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // cierra el surtido y genera registo de recibido
                Cerrar_surtido(fecha,almacen);
                guarda_recibo(IdSurtido,fecha,almacen,tvusuario.getText().toString(),tvcomentario.getText().toString(),"Comentarios del Sistema");
                finish();
            }
        });

        btnValida.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mipw = tvpw.getText().toString();
                String usuario = tvusuario.getText().toString();
                String pw = validarUruario(usuario);
                if (pw == "zzzz") {
                    Toast.makeText(getApplicationContext(),
                            "Validar Usuario", Toast.LENGTH_SHORT).show();
                            vibrar();
                } else {
                    tvnombre.setText(nombreUsuario);
                    if (pw.length() == 0) {
                        Toast.makeText(getApplicationContext(),
                                "Verificar Contraseña", Toast.LENGTH_SHORT).show();
                    }
                    if (pw.equals(mipw)) {
                        tvpw.setTextColor(Color.BLACK);
                        Toast.makeText(getApplicationContext(),
                                "Es igual " + pw + " y " + mipw, Toast.LENGTH_SHORT).show();
                        btnValida.setVisibility(View.INVISIBLE);
                        btnrecibe.setVisibility(View.VISIBLE);
                        tvnombre.setTextColor(Color.BLACK);
                    } else {
                        tvpw.setTextColor(Color.RED);
                        tvpw.setTextColor(Color.BLACK);
                        tvnombre.setTextColor(Color.RED);
                        Toast.makeText(getApplicationContext(),
                                "Es diferente " + pw + " y " + mipw, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    public String Obtener_comentarios (int idSurtido) {
        //obtener productos capturados
        String sqltext ="SELECT  (SELECT  ' '+char(10) + alm_surtidos.descripcion  + ' ('+LTRIM ( str(cantidad))+') '  FROM alm_surtidos where id_surtido="+String.valueOf(idSurtido)+" and entregado=0 FOR XML PATH('') ) as resumen " ;
        String resumen="Surtido Completo";
        try {
            Connection connect = conexionSQL.ConnectionHelper(servidor,user,pw);
            if (connect==null) { return "";}
            Statement st = connect.createStatement();
            ResultSet rs = st.executeQuery(sqltext);
            while (rs.next()) {
                resumen="Productos que quedaron pendientes"+rs.getString("resumen");
            }
            connect.close();
            return  resumen;
        } catch (SQLException e) {
            Toast.makeText(getApplicationContext(),
                    e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                    return resumen;
        }
    }

    public void vibrar () {
        Vibrator vibrator=(Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(100);
    }

    public void Cerrar_surtido (String mfecha , String mAlamcen ){
        try {
            Connection connect = conexionSQL.ConnectionHelper(servidor,user,pw );
            Statement st = connect.createStatement();
            Timestamp ts= new Timestamp( System.currentTimeMillis());
            String sql ="update alm_surtidos set cerrado=1 where fecha='"+mfecha+"' and almacen='"+mAlamcen+"'";
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

    public void guarda_recibo (int idsurtido, String mfecha , String mAlamcen,String usuario,String comentarios,String comentariosSistema ){
        try {
            Connection connect = conexionSQL.ConnectionHelper(servidor,user,pw );

            // elimina registro anterior si es que existe
            String cmd_elimina="delete from alm_surtidos_recibos where id_surtido="+String.valueOf(idsurtido);
            PreparedStatement psp1=connect.prepareStatement(cmd_elimina);
            psp1.execute();
            //INSERTA REGISTRO EN TABLA ALM_SURTIDOS_RECIBOS
            PreparedStatement psp=connect.prepareStatement("INSERT INTO alm_surtidos_recibos values (?,?,?,?,?,?,?)");
            Timestamp ts ;
            ts= new Timestamp( System.currentTimeMillis());
            psp.setInt(1,idsurtido);
            psp.setString(2,mfecha);
            psp.setString(3,mAlamcen);
            psp.setString(4,usuario);
            psp.setTimestamp(5, ts);
            psp.setString(6,comentarios);
            psp.setString(7,comentariosSistema);
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

    public String validarUruario (String usuario) {
        //obtener productos capturados
        String sqltext ="SELECT * FROM  usuarios where nick='"+usuario+"'" ;
        try {
            Connection connect = conexionSQL.ConnectionHelper(servidor,user,pw);
            if (connect==null) { return "";}
            Statement st = connect.createStatement();
            ResultSet rs = st.executeQuery(sqltext);
            String pw="zzzz";
            while (rs.next()) {
                pw=rs.getString("pw");
                nombreUsuario=String.valueOf(rs.getString("nombre"));
            }
            connect.close();
            return  pw;
        } catch (SQLException e) {
            Toast.makeText(getApplicationContext(),
                    e.getMessage().toString(), Toast.LENGTH_SHORT).show();
            return pw;
        }
    }


}
