package com.programacionintegral.almacen;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class inventarioListAdapter extends BaseAdapter {

    private Context mContext;
    private List<inventario> mInventarioList ;
    private List<inventario> copyInventario = new ArrayList<>();


    public inventarioListAdapter (Context mContext, List<inventario> mInventarioList) {
        this.mContext=mContext;
        this.mInventarioList=mInventarioList;
        this.copyInventario.addAll(mInventarioList);
    }

    public void filtra (String texto){
        if (copyInventario.size() ==0){
            copyInventario.addAll(mInventarioList);
        }

       mInventarioList.clear();

        if (texto.length()==0){
            mInventarioList.addAll(copyInventario);

        } else {
            for (inventario inv : copyInventario) {
                if (inv.getDescripcion().contains(texto)){
                    mInventarioList.add(inv);
                }
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mInventarioList.size();
    }

    @Override
    public Object getItem(int position) {
        return mInventarioList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v=View.inflate(mContext,R.layout.item_inventaio,null);
        TextView tv_inv_id=v.findViewById(R.id.tv_inv_id);
        TextView tv_inv_descripcion=v.findViewById(R.id.tv_inv_descripcion);
        TextView tv_inv_existencia=v.findViewById(R.id.tv_inv_existencia);
        TextView tv_inv_fisico=v.findViewById(R.id.tv_inv_fisico);
        TextView tv_inv_conentario=v.findViewById(R.id.tv_inv_comentario);
        Integer id=mInventarioList.get(position).getId_producto();
        Integer ex=mInventarioList.get(position).getExistencia();
        Integer fi=mInventarioList.get(position).getFisico();
        tv_inv_id.setText(id.toString());
        tv_inv_descripcion.setText(mInventarioList.get(position).getDescripcion());
        tv_inv_existencia.setText(ex.toString());
        tv_inv_fisico.setText(fi.toString());
        tv_inv_conentario.setText(mInventarioList.get(position).getComentario());
        return v;
    }
}
