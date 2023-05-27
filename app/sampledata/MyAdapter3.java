package com.example.clienet.projeto_mercado3;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.database.sqlite.SQLiteDatabase.openOrCreateDatabase;

/**
 * Created by jefer on 15/01/2018.
 */

public class MyAdapter3 extends SimpleAdapter{

    private LayoutInflater mInflater;

    private Context mcon;

    SQLiteDatabase db;
    SQLiteDatabaseDao dao;
    SQLiteDatabaseDao2 dao2;

    Cliente cliente;

    Carrinho carrinho = new Carrinho();

    ArrayList<HashMap<String,Object>> listData = new ArrayList<HashMap<String,Object>>();

    Intent intentData;

    private static final String TAG = MainActivity.class.getSimpleName();

    public MyAdapter3(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to, Intent intent) {
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


        /////////////////////////////////////////////////////////////////////////
        /////////////////////////////////////////////////////////////////////////

        ImageButton img2 = (ImageButton) v.findViewById(R.id.imageButton2);

        img2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mcon, CarrinhoActivity.class);

                cliente = new Cliente();

                cliente = (Cliente) intentData.getSerializableExtra("cliente");

                HashMap<String, Object> map = listData.get(position);

                int value = (int) map.get("idproduto");

                cliente.setIdproduto(value);

                // 3. put person in intent data
                intent.putExtra("cliente", cliente);

                dao = new SQLiteDatabaseDao();

                // 4. start the activity
                mcon.startActivity(intent);
            }
        });

        ///////////////////////////////////////////////////////////////////////
        ///////////////////////////////////////////////////////////////////////

        final NumberPicker quantidade = (NumberPicker) v.findViewById(R.id.numberPicker);

        final TextView subtotal = (TextView) v.findViewById(R.id.subtotal);

        quantidade.setOnScrollListener(new NumberPicker.OnScrollListener() {

            @Override
            public void onScrollStateChange(NumberPicker numberPicker, int scrollState) {
                if (scrollState == NumberPicker.OnScrollListener.SCROLL_STATE_IDLE) {
                    String pr = String.valueOf(((Map)getItem(position)).get("preco"));
                    float preco = Float.parseFloat(pr);
                    float qtd = quantidade.getValue();
                    subtotal.setText(String.valueOf(new DecimalFormat("######0.00").format(qtd*preco)));

                    cliente = new Cliente();

                    cliente = (Cliente) intentData.getSerializableExtra("cliente");

                    HashMap<String, Object> map = listData.get(position);

                    int value = (int) map.get("idproduto");

                    cliente.setIdproduto(value);

                    int q = (int) map.get("quantidade");

                    carrinho.setQuantidade(q);

                    dao2 = new SQLiteDatabaseDao2();

                    HashMap<String, Object> meMap = new HashMap<String, Object>();

                    meMap.put("idproduto", ((Map)getItem(position)).get("idproduto"));
                    meMap.put("nome", ((Map)getItem(position)).get("nome"));
                    meMap.put("foto", ((Map)getItem(position)).get("foto"));
                    meMap.put("preco", ((Map)getItem(position)).get("preco"));
                    meMap.put("estoque", ((Map)getItem(position)).get("estoque"));
                    meMap.put("quantidade", ((Map)getItem(position)).get("quantidade"));
                    meMap.put("subtotal", String.valueOf(new DecimalFormat("######0.00").format(qtd*preco)));

                    listData.set(position, meMap);

                }
            }
        });

        quantidade.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {

            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                // TODO Auto-generated method stub
                String pr = String.valueOf(((Map)getItem(position)).get("preco"));
                float preco = Float.parseFloat(pr);
                float qtd = quantidade.getValue();
                subtotal.setText(String.valueOf(new DecimalFormat("######0.00").format(qtd*preco)));

                cliente = new Cliente();

                cliente = (Cliente) intentData.getSerializableExtra("cliente");

                HashMap<String, Object> map = listData.get(position);

                int value = (int) map.get("idproduto");

                cliente.setIdproduto(value);

                int q = (int) map.get("quantidade");

                carrinho.setQuantidade(q);

                dao2 = new SQLiteDatabaseDao2();

                HashMap<String, Object> meMap = new HashMap<String, Object>();

                meMap.put("idproduto", ((Map)getItem(position)).get("idproduto"));
                meMap.put("nome", ((Map)getItem(position)).get("nome"));
                meMap.put("foto", ((Map)getItem(position)).get("foto"));
                meMap.put("preco", ((Map)getItem(position)).get("preco"));
                meMap.put("estoque", ((Map)getItem(position)).get("estoque"));
                meMap.put("quantidade", ((Map)getItem(position)).get("quantidade"));
                meMap.put("subtotal", String.valueOf(new DecimalFormat("######0.00").format(qtd*preco)));

                listData.set(position, meMap);

            }
        });


        int qty = (int) ((Map)getItem(position)).get("quantidade");
        int estoque = (int) ((Map)getItem(position)).get("estoque");

        quantidade.setMinValue(1);
        quantidade.setMaxValue(estoque);
        quantidade.setValue(qty);

        return v;
    }

    class SQLiteDatabaseDao {

        public SQLiteDatabaseDao() {
            db = SQLiteDatabase.openDatabase("/data/data/com.example.clienet.projeto_mercado3/databases/Carrinhos.db", null, 0);
            deleteData("produto");
        }

        public void deleteData(String table) {
            db.delete(table, "idproduto='"+cliente.getIdproduto()+"'",null);
            Log.d(TAG, "Deu cerrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrtodelete");
        }
    }

    class SQLiteDatabaseDao2 {

        public SQLiteDatabaseDao2() {
            db = SQLiteDatabase.openDatabase("/data/data/com.example.clienet.projeto_mercado3/databases/Carrinhos.db", null, 0);
            updateData("produto");
        }

        public void updateData(String table) {
            ContentValues cv = new ContentValues();
            cv.put("quantidade", carrinho.getQuantidade());
            db.update(table, cv,"idproduto='"+cliente.getIdproduto()+"'",null);
            Log.d(TAG, "Deu cerrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrtoupdate");
        }
    }
}


