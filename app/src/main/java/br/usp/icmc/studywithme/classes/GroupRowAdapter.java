package br.usp.icmc.studywithme.classes;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import br.usp.icmc.studywithme.R;

public class GroupRowAdapter extends BaseAdapter {
    private final Context mContext;
    private ArrayList<Grupo> elements;

    public GroupRowAdapter(Context mContext, ArrayList<Grupo> elements){
        this.mContext = mContext;
        this.elements = elements;
    }

    public View getView(int pos, View convertView, ViewGroup parent){
        RelativeLayout rowLayout;
        final Grupo g = elements.get(pos);

        if(convertView == null){
            rowLayout = (RelativeLayout) LayoutInflater.from(mContext).inflate(R.layout.row_list, parent, false);
        }
        else{
            rowLayout = (RelativeLayout) convertView;
        }

        TextView tv = (TextView) rowLayout.findViewById(R.id.row_text_materia);
        tv.setText(g.getMateria());
        tv = (TextView) rowLayout.findViewById(R.id.row_text_data);
        tv.setText(g.getDia());
        tv = (TextView) rowLayout.findViewById(R.id.row_text_hora);
        tv.setText(g.getHora());

        return rowLayout;
    }

    public int getCount(){
        return elements.size();
    }
    public Object getItem(int pos){
        return elements.get(pos);
    }
    public long getItemId(int pos){
        return pos;
    }
}
