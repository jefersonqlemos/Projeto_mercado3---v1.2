package com.example.clienet.projeto_mercado3;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jefer on 15/01/2018.
 */

public class MyAdapter2 extends SimpleAdapter{

    private LayoutInflater mInflater;

    private Context mcon;

    ArrayList<HashMap<String,Object>> listData = new ArrayList<HashMap<String,Object>>();

    Intent intentData;

    public MyAdapter2(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to, Intent intent) {
        super(context, data, resource, from, to);
        mInflater = LayoutInflater.from(context);
        mcon=context;
        listData = (ArrayList<HashMap<String, Object>>) data;
        intentData=intent;

    }

    public View getView(final int position, View convertView, ViewGroup parent){
        // here you let SimpleAdapter built the view normally.
        View v = super.getView(position, convertView, parent);

        // Then we get reference for Picasso
        ImageButton img = (ImageButton) v.findViewById(R.id.carrinho);
        //if(img == null){
        //    img = (ImageButton) v.findViewById(R.id.carrinho);
        //    v.setTag(img); // <<< THIS LINE !!!!
        //}

        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mcon, CarrinhoActivity.class);

                Cliente cliente = new Cliente();

                cliente = (Cliente) intentData.getSerializableExtra("cliente");

                HashMap<String, Object> map = listData.get(position);

                int value = (int) map.get("idmercado");

                String nome = (String) map.get("nome");

                String bairro = (String) map.get("bairro");

                cliente.setIdmercado(value);

                //cliente.setNome_mercado(nome);

                //cliente.setBairro_mercado(bairro);

                // 3. put person in intent data
                intent.putExtra("cliente", cliente);

                // 4. start the activity
                mcon.startActivity(intent);
            }
        });

        // get the url from the data you passed to the `Map`
        //String url = (String) ((Map)getItem(position)).get("foto");
        // do Picasso
        //Picasso.with(v.getContext()).load(url).into(img);

        // return the view
        return v;
    }
}