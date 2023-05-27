package com.example.clienet.projeto_mercado3;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SimpleAdapter;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jefer on 16/03/2018.
 */

public class MyAdapter6  extends SimpleAdapter {

    private LayoutInflater mInflater;

    private Context mcon;

    SQLiteDatabase db;

    Cliente cliente;

    ArrayList<HashMap<String,Object>> listData = new ArrayList<HashMap<String,Object>>();

    Intent intentData;

    Button adicionar_comentario;

    public MyAdapter6(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to, Intent intent) {
        super(context, data, resource, from, to);

        mInflater = LayoutInflater.from(context);
        mcon=context;
        listData = (ArrayList<HashMap<String, Object>>) data;
        intentData=intent;

    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        // here you let SimpleAdapter built the view normally.
        View v = super.getView(position, convertView, parent);

        // Then we get reference for Picasso
        ImageButton img = (ImageButton) v.getTag();
        if (img == null) {
            img = (ImageButton) v.findViewById(R.id.foto);
            v.setTag(img); // <<< THIS LINE !!!!
        }

        // get the url from the data you passed to the `Map`

        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                cliente = new Cliente();

                cliente = (Cliente) intentData.getSerializableExtra("cliente");

                HashMap<String, Object> map = listData.get(position);

                int value = (int) map.get("produtos_idprodutos");

                cliente.setIdproduto(value);

                Intent intent = new Intent(mcon, ProdutoActivity.class);

                // 3. put person in intent data
                intent.putExtra("cliente", cliente);

                // 4. start the activity
                mcon.startActivity(intent);
            }
        });

        adicionar_comentario = (Button) v.findViewById(R.id.adicionar_comentario);

        adicionar_comentario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cliente = new Cliente();

                cliente = (Cliente) intentData.getSerializableExtra("cliente");

                HashMap<String, Object> map = listData.get(position);

                int value = (int) map.get("produtos_idprodutos");

                cliente.setIdproduto(value);

                Intent intent = new Intent(mcon, ComentarioActivity.class);

                // 3. put person in intent data
                intent.putExtra("cliente", cliente);

                // 4. start the activity
                mcon.startActivity(intent);
            }
        });

        String url = (String) ((Map) getItem(position)).get("foto");
        // do Picasso
        Picasso.with(v.getContext()).load(url).into(img);
        // return the view
        return v;
    }
}