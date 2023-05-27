package com.example.clienet.projeto_mercado3;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jefer on 12/03/2018.
 */

public class MyAdapter5 extends SimpleAdapter {

    private LayoutInflater mInflater;

    private Context mcon;

    SQLiteDatabase db;

    Cliente cliente;

    SharedPreferences pref;
    SharedPreferences.Editor editor;

    ArrayList<HashMap<String,Object>> listData = new ArrayList<HashMap<String,Object>>();

    Intent intentData;

    private static final String TAG = MainActivity.class.getSimpleName();

    public MyAdapter5(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to, Intent intent) {
        super(context, data, resource, from, to);
        mInflater = LayoutInflater.from(context);
        mcon=context;
        listData = (ArrayList<HashMap<String, Object>>) data;
        intentData=intent;
    }

    public View getView(final int position, View convertView, ViewGroup parent){
        // here you let SimpleAdapter built the view normally.
        View v = super.getView(position, convertView, parent);

        ImageButton img = (ImageButton) v.getTag();
        if(img == null){
            img = (ImageButton) v.findViewById(R.id.icon);
            v.setTag(img); // <<< THIS LINE !!!!
        }
        // get the url from the data you passed to the `Map`
        String url = (String) ((Map)getItem(position)).get("foto");
        // do Picasso

        Picasso.with(v.getContext()).load(url).into(img);

        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mcon, ProdutoActivity.class);

                cliente = new Cliente();

                cliente = (Cliente) intentData.getSerializableExtra("cliente");

                HashMap<String, Object> map = listData.get(position);

                int value = (int) map.get("idproduto");

                cliente.setIdproduto(value);

                intent.putExtra("cliente", cliente);

                mcon.startActivity(intent);
            }
        });


        ///////////////////////////////////////////////////////////////////////
        ///////////////////////////////////////////////////////////////////////

        final TextView quantidade = (TextView) v.findViewById(R.id.qty);

        final TextView subtotal = (TextView) v.findViewById(R.id.subtotal);

        int qty = (int) ((Map)getItem(position)).get("quantidade");
        int estoque = (int) ((Map)getItem(position)).get("estoque");

        quantidade.setText(""+qty);

        return v;
    }
}