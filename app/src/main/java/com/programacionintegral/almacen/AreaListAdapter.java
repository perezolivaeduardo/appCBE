package com.programacionintegral.almacen;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class AreaListAdapter extends BaseAdapter {

    private Context mContext;
    private List<area> mAreaList;

    public AreaListAdapter (Context mContext,List<area> mAraList) {
        this.mContext=mContext;
        this.mAreaList=mAraList;
    }

    @Override
    public int getCount() {
        return mAreaList.size();
    }

    @Override
    public Object getItem(int position) {
        return mAreaList.get(position).getArea();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = View.inflate(mContext, R.layout.item_area, null);
        TextView tv_codigo = (TextView)v.findViewById(R.id.tv_area);
        tv_codigo.setText(mAreaList.get(position).getArea());
        return v;
    }
}
