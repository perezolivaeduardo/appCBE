package com.programacionintegral.almacen;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.Formatter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener  {
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
    private   boolean swAlmacen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        String versionName="";
        // determinar vesrion de la APP
        try {
            versionName = getApplicationContext().getPackageManager().getPackageInfo(getApplicationContext().getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        TextView tvver = findViewById(R.id.tvversion);
        tvver.setText("V:"+versionName);

        // obtener id_dalamce
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);

       servidor =  sharedPrefs.getString("servidor_sql", "NULL");
       user =  sharedPrefs.getString("usuario", "NULL");
       pw =  sharedPrefs.getString("password", "NULL");
       swAlmacen =sharedPrefs.getBoolean("switch_Almacen",false);

        if (swAlmacen == false){
            toolbar.setTitle("Hospital");
            setSupportActionBar(toolbar);
        }

     final String MiIp = getIPAddressIPv4("");

     TextView tvservidor=findViewById(R.id.tvserver);
     TextView tvuser=findViewById(R.id.tvUsuario);
     TextView tvpw=findViewById(R.id.tvpw);
     TextView tvVersion=findViewById(R.id.tvversion);
     TextView tvInfo=findViewById(R.id.tvInfo);
        tvInfo.setText("");


        WifiManager wm = (WifiManager) getApplicationContext().getApplicationContext().getSystemService(WIFI_SERVICE);
        String ipAddress = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());


       

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_fecha);
        fab.setOnClickListener(view -> {

            if (swAlmacen == false){
                startActivity(new Intent(MainActivity.this,Login.class));
            }
                else {
                startActivity(new Intent(MainActivity.this,Surtir.class));
            }

        });


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        tvservidor.setText(servidor);
        tvuser.setText(user);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(MainActivity.this,
                    SettingsActivity.class));
            return true;
        } else if (id == R.id.action_config){
            startActivity(new Intent(MainActivity.this,
                    ConfiguracionVer.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id==R.id.nav_cerrar){ finish();}

        if (swAlmacen == false){
            return false;
        }
    if (id == R.id.nav_resumen) {
            startActivity(new Intent(MainActivity.this,
                    Resumen.class));

        } else if (id==R.id.nav_consulta){
            startActivity(new Intent(MainActivity.this,
                Consultar.class));
        } else if(id==R.id.nav_pendietes){
             startActivity(new Intent(MainActivity.this,
                Pendientes.class));
        }  else if (id == R.id.nav_update) {
            Uri uri = Uri.parse("http://www.clinicaesperanza.com.mx/almacen.apk");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);


        } else if(id==R.id.nav_capturaInventario){
        startActivity(new Intent(MainActivity.this,
          Captura_Inventario.class));

    } else if(id==R.id.nav_Inventario_Almacen){
    //********** PRUEBA  ************************
        if (PingExample("192.162.1.1")){

        }


    } else if(id==R.id.nav_preparar){
        Intent intent = new Intent(MainActivity.this,Login.class);
        MainActivity.this.startActivity(intent);
    }
    else if ( id == R.id.nav_conexion) {

        if (ProbarConexionAzure()){
            Toast.makeText(getApplicationContext(),
                    "Si tiene conexion con Internet", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(),
                    "Sin conexión", Toast.LENGTH_SHORT).show();
        }
        return true;

    }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public static String getIPAddressIPv4(String id) {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                if (intf.getName().contains(id)) {
                    List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                    for (InetAddress addr : addrs) {

                        if (!addr.isLoopbackAddress()) {
                            String sAddr = addr.getHostAddress();
                            if (addr instanceof Inet4Address) {
                                return sAddr;
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public boolean ProbarConexionAzure() {
        //obtener productos capturados
        String sqltext ="select count(id_user) as x from usuarios" ;
        Log.d("probar", " Conslta para probar :" +sqltext);
        try {
            Connection connect = conexionSQL.ConnectionHelper(servidor,user,pw );
            Log.d("probar", " Despues de Conexion helper");
            if (connect==null) {
                Log.d("probar", " regerso coneccion null");
                return false;
            }
            Log.d("probar", " Conexion OK");
            Statement st = connect.createStatement();
            ResultSet rs = st.executeQuery(sqltext);
            while (rs.next()) {
                int nproductos;
                nproductos = rs.getInt("x");
                Log.d("probar", " Resultado de la consula"+String.valueOf(nproductos));
            }

            //Se cierra la conexión
            connect.close();
            return  true;
        } catch (SQLException e) {
            Log.d("probar", " Error"+e.getMessage());

            return false;
        }
    }

    public static boolean PingExample (String url){
            try{
                InetAddress address = InetAddress.getByName(url);
                boolean reachable = address.isReachable(10000);
                System.out.println("Is host reachable? " + reachable);
                return true;
            } catch (Exception e){
                e.printStackTrace();
            }
        return false;
    }

    public void vibrar () {
        Vibrator vibrator=(Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(100);
    }


}
