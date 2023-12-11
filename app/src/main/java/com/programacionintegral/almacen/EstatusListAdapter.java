package com.programacionintegral.almacen;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

public class EstatusListAdapter extends BaseAdapter {

    private Context mContext;
    private List<estatus> mEstatusList;

    public EstatusListAdapter (Context mContext,List<estatus> mEstatusList) {
        this.mContext=mContext;
        this.mEstatusList=mEstatusList;
    }

    @Override
    public int getCount() {
        return mEstatusList.size();
    }

    @Override
    public Object getItem(int i) {
        return mEstatusList.get(i).getAlmacen();
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        return null;
    }
}
