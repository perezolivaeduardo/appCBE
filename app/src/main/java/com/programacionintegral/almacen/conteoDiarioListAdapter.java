package com.programacionintegral.almacen;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.text.InputType;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;




public class conteoDiarioListAdapter extends BaseAdapter {


    private Context mContext;
    private List<inventariodiario> mConteoList ;
    private List<inventariodiario> copyConteo = new ArrayList<>();
    private String servidor;
    private String user;
    private String pw;

    public conteoDiarioListAdapter(Context mContext, List<inventariodiario> mConteoList) {
        this.mContext= mContext;
        this.mConteoList=mConteoList;
        this.copyConteo.addAll(mConteoList);

        // obtener id_dalamce
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        servidor =  sharedPrefs.getString("servidor_sql", "NULL");
        user =  sharedPrefs.getString("usuario", "NULL");
        pw =  sharedPrefs.getString("password", "NULL");
    }

    public void inicializa(){
        copyConteo.clear();
    }


    public  void filtrar (Boolean todos){
        if (copyConteo.size() == 0) {
            copyConteo.addAll(mConteoList);
        }
        mConteoList.clear();
        if (todos==false) {
            for (inventariodiario emb :copyConteo) {
                mConteoList.add(emb);
            }
        } else {
            for (inventariodiario emb :copyConteo) {
                if (emb.getConsumidos()>0 ) {
                    mConteoList.add(emb);
                }
            }
        }
        notifyDataSetChanged();
    }


    public  void filtrarFaltantes (Boolean todos){
        if (copyConteo.size() == 0) {
            copyConteo.addAll(mConteoList);
        }
        mConteoList.clear();
        if (todos==false) {
            for (inventariodiario emb :copyConteo) {
                mConteoList.add(emb);
            }
        } else {
            for (inventariodiario emb :copyConteo) {
                if (emb.getCantidad() != emb.getLibros() ) {
                    mConteoList.add(emb);
                }
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {return mConteoList.size();}

    @Override
    public Object getItem(int position) {return mConteoList.get(position);}

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v=View.inflate(mContext,R.layout.item_conteo,null);
        TextView tvConteoID=v.findViewById(R.id.tvConteoId);
        TextView tvdescripcion=v.findViewById(R.id.tvconteoDescripcion);
        //TextView tvcomentario=v.findViewById(R.id.tvconteocomentarios);
        TextView tvLibros =v.findViewById(R.id.tvLibros);
         Button btnConteo=v.findViewById(R.id.button);
         TextView tvconteodif = v.findViewById(R.id.tvconteoDiferencia );


        Integer id=mConteoList.get(position).getId_producto();
        String descripcion=mConteoList.get(position).getDescripcion();
        Integer cantidad=mConteoList.get(position).getCantidad();
        String kIdArea = mConteoList.get(position).getId_area().toString();
        String kFecha = mConteoList.get(position).getFecha().toString();
        String Libros = mConteoList.get(position).getLibros().toString();
        String consumidos = mConteoList.get(position).getConsumidos().toString();
        Integer Dif =mConteoList.get(position).getCantidad()-mConteoList.get(position).getLibros();
        String diferencia = Dif.toString();

        if (Dif == 0) {
            tvconteodif.setVisibility(View.INVISIBLE);
        } else if (Dif < 0) {
            tvconteodif.setTextColor(Color.RED);
            tvconteodif.setVisibility(View.VISIBLE);
        } else {
            tvconteodif.setTextColor(Color.BLACK); // Puedes cambiar esto al color que desees cuando la diferencia sea positiva
            tvconteodif.setVisibility(View.VISIBLE);
        }

        tvConteoID.setText(id.toString());
        tvdescripcion.setText(descripcion);
        btnConteo.setText(cantidad.toString());
        tvLibros.setText(consumidos+"/"+Libros.toString());
        tvconteodif.setText(diferencia);

        tvdescripcion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Obtener una instancia del MediaPlayer para reproducir el sonido
                MediaPlayer mediaPlayer = MediaPlayer.create(mContext, R.raw.click);
                // Verificar si se pudo crear el MediaPlayer correctamente
                if (mediaPlayer != null) {
                    // Reproducir el sonido
                    mediaPlayer.start();

                    // Liberar recursos del MediaPlayer cuando termine la reproducción
                    mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            mediaPlayer.release();
                        }
                    });
                }

                // Obtener el color actual del texto
                int currentColor = tvdescripcion.getCurrentTextColor();

                // Verificar el color actual y cambiarlo
                if (currentColor != Color.WHITE) {
                    tvdescripcion.setTextColor(Color.WHITE); // Cambiar a blanco
                } else {
                    tvdescripcion.setTextColor(Color.BLACK); // Cambiar a negro
                }
            }
        });

        tvdescripcion.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                // Obtener una instancia del MediaPlayer para reproducir el sonido
                MediaPlayer mediaPlayer = MediaPlayer.create(mContext, R.raw.click);
                // Verificar si se pudo crear el MediaPlayer correctamente
                if (mediaPlayer != null) {
                    // Reproducir el sonido
                    mediaPlayer.start();

                    // Liberar recursos del MediaPlayer cuando termine la reproducción
                    mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            mediaPlayer.release();
                        }
                    });
                }

                Actualizar_cantidad(Libros,kIdArea,id.toString(),kFecha);
                btnConteo.setText(Libros.toString());
                tvconteodif.setVisibility(View.INVISIBLE);
                mConteoList.get(position).setCantidad(Integer.valueOf(Libros));

                return false;
            }
        });


        //Cuando da clic al boton de conteo

        btnConteo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // TODO Auto-generated method stub
                AlertDialog.Builder ab=new AlertDialog.Builder(mContext);
                ab.setTitle("Enter any string");

                EditText et=new EditText(mContext);
                et.setInputType(InputType.TYPE_CLASS_NUMBER);
                ab.setView(et);
                ab.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                        btnConteo.setText(et.getText().toString());
                        mConteoList.get(position).setCantidad(Integer.valueOf(et.getText().toString()));
                        Integer Dif =mConteoList.get(position).getCantidad()-mConteoList.get(position).getLibros();
                        if (Dif == 0) {
                            tvconteodif.setVisibility(View.INVISIBLE);
                        } else if (Dif < 0) {
                            tvconteodif.setTextColor(Color.RED);
                            tvconteodif.setVisibility(View.VISIBLE);
                        } else {
                            tvconteodif.setTextColor(Color.BLACK); // Puedes cambiar esto al color que desees cuando la diferencia sea positiva
                            tvconteodif.setVisibility(View.VISIBLE);
                        }


                        tvconteodif.setText(Dif.toString());

                        Actualizar_cantidad(et.getText().toString(),kIdArea,id.toString(),kFecha);

                    }
                });

                ab.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        // TODO Auto-generated method stub
                        btnConteo.setText("0");
                        mConteoList.get(position).setCantidad(0);
                    }
                });

                AlertDialog a=ab.create();
                a.show();
            }
         }
        );
        return v;
    }

    public void Actualizar_cantidad (String cantidad,String idarea,String idproducto, String fecha){
        try {
            //Se obtiene la conexión
            Connection connect = conexionSQL.ConnectionHelper(servidor,user,pw);
            //Se genera la consulta
            Statement st = connect.createStatement();
            Timestamp ts ;
            ts= new Timestamp( System.currentTimeMillis());
            String sql ="update inv_conteo_diario set cantidad = {cantidad},  hora = GETDATE(), contado = 1 where id_area={idarea} and id_producto = {idProducto} and fecha='{fecha}'" ;
            sql=sql.replace("{cantidad}",cantidad);
            sql=sql.replace("{idarea}",idarea);
            sql=sql.replace("{idProducto}",idproducto);
            sql=sql.replace("{fecha}",fecha);
            PreparedStatement psp=connect.prepareStatement(sql);
            psp.execute();
            connect.close();//Se cierra la conexión

        } catch (SQLException e) {
            //Mostramos el error en caso de no conectarse
            Toast.makeText(mContext,
                    e.getMessage().toString(), Toast.LENGTH_LONG ).show();
            return ;
        }

    };
}
