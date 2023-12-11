package com.programacionintegral.almacen;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

public class SurtidoListAdapter extends BaseAdapter
{
    private Context mContext;
    private List<surtido> mSurtdioList;

    public SurtidoListAdapter (Context mContext,List<surtido> mSurtdioList) {
        this.mContext=mContext;
        this.mSurtdioList=mSurtdioList;
    }

    @Override
    public int getCount() {
        return mSurtdioList.size();
    }

    @Override
    public Object getItem(int position) {
        return mSurtdioList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = View.inflate(mContext, R.layout.item_surtido, null);
        LinearLayout mlinear = v.findViewById(R.id.linear);

        Integer idSurtido =mSurtdioList.get(position).getId();
        TextView tvDescripcion = v.findViewById(R.id.tv_descripcion );
        TextView tvcantidad =v.findViewById(R.id.tv_cantidad );
        ImageView mImg = v.findViewById(R.id.img);
      //  CheckBox checkbox =v.findViewById(R.id.checkrecibido);
        Integer cant =mSurtdioList.get(position).getCantidad();
        tvDescripcion.setText(mSurtdioList.get(position).getDescripcion());
         tvcantidad.setText(cant.toString());
      // checkbox.setChecked(mSurtdioList.get(position).isEntregado());
        mlinear.setMinimumHeight(54);
        if (idSurtido==99){
            mImg.setVisibility( View.INVISIBLE);
            mlinear.setMinimumHeight(34);
        }

        boolean chck =mSurtdioList.get(position).isEntregado();

        if (chck==true) {
            String tempString = mSurtdioList.get(position).getDescripcion();
            SpannableString spanString = new SpannableString(tempString);
            spanString.setSpan(new UnderlineSpan(), 0, spanString.length(), 0);
            spanString.setSpan(new StyleSpan(Typeface.BOLD), 0, spanString.length(), 0);
            spanString.setSpan(new StyleSpan(Typeface.ITALIC), 0, spanString.length(), 0);
            tvDescripcion.setText(spanString);


            tvDescripcion.setBackgroundColor(ContextCompat.getColor(mContext, R.color.lineaSeleccionada));
            tvcantidad.setBackgroundColor(ContextCompat.getColor(mContext, R.color.lineaSeleccionada));
            mImg.setImageResource(android.R.drawable.checkbox_on_background);
           // mImg.setBackgroundColor(ContextCompat.getColor(mContext, R.color.lineaSeleccionada));
        //    checkbox.setBackgroundColor(ContextCompat.getColor(mContext, R.color.lineaSeleccionada));
        } else {
            mImg.setImageResource(android.R.drawable.checkbox_off_background);
        }
        return v;
    }



}
