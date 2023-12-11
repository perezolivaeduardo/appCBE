package com.programacionintegral.almacen;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Switch;
import android.widget.TextView;

public class ConfiguracionVer extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracion_ver);

        // obtener id_dalamce
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(ConfiguracionVer.this);

        String servidor =  sharedPrefs.getString("servidor_sql", "NULL");
        String  user =  sharedPrefs.getString("usuario", "NULL");
        String pw =  sharedPrefs.getString("password", "NULL");
        Boolean esAlmacen =sharedPrefs.getBoolean("switch_Almacen",false);

        TextView tvservidor = findViewById(R.id.et_config_servidor);
        TextView tvusuario = findViewById(R.id.et_config_usuario);
        TextView tvpw = findViewById(R.id.et_config_contraseña);
        Switch swalmacen = findViewById(R.id.sw_config_esAlmacen);

        tvservidor.setText(servidor);
        tvusuario.setText(user);
        tvpw.setText(pw);
        swalmacen.setChecked(esAlmacen );



    }
}