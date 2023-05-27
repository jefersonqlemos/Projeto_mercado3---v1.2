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
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.Context.CONTEXT_IGNORE_SECURITY;
import static android.content.Context.MODE_PRIVATE;

/**
 * Created by jefer on 15/01/2018.
 */

public class MyAdapter3 extends SimpleAdapter{

    private LayoutInflater mInflater;

    private Context mcon;

    SQLiteDatabase db;
    SQLiteDatabaseDao dao;
    SQLiteDatabaseDao2 dao2;
    ImageButton atualizar;

    Cliente cliente;

    SharedPreferences pref;
    SharedPreferences.Editor editor;

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

                dao = new SQLiteDatabaseDao();

                // 3. put person in intent data
                intent.putExtra("cliente", cliente);

                // 4. start the activity
                mcon.startActivity(intent);
            }
        });

        ///////////////////////////////////////////////////////////////////////
        ///////////////////////////////////////////////////////////////////////

        final NumberPicker quantidade = (NumberPicker) v.findViewById(R.id.numberPicker);

        final TextView subtotal = (TextView) v.findViewById(R.id.subtotal);

        atualizar = (ImageButton) v.findViewById(R.id.atualizar);

        atualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mcon, CarrinhoActivity.class);

                cliente = new Cliente();

                cliente = (Cliente) intentData.getSerializableExtra("cliente");

                HashMap<String, Object> map = listData.get(position);

                int value = (int) map.get("idproduto");

                cliente.setIdproduto(value);

                carrinho.setQuantidade(quantidade.getValue());

                dao2 = new SQLiteDatabaseDao2();

                // 3. put person in intent data
                intent.putExtra("cliente", cliente);

                // 4. start the activity
                mcon.startActivity(intent);
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
            getQuantidade("produto");
            Log.d(TAG, "Deu cerrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrtodelete");
        }

        public void getQuantidade(String table) {
            Cursor c = db.rawQuery("select quantidade from " + table, null);
            //int columnsSize = c.getColumnCount();
            // 获取表的内容
            int quantidade=0;

            while (c.moveToNext()) {
                //for (int i = 0; i < columnsSize; i++) {
                quantidade = quantidade + c.getInt(0);

            }

            cliente.setQuantidade(quantidade);
        }
    }

    class SQLiteDatabaseDao2 {

        public SQLiteDatabaseDao2() {
            db = SQLiteDatabase.openDatabase("/data/data/com.example.clienet.projeto_mercado3/databases/Carrinhos.db", null, 0);
            updateData("produto");
            getQuantidade("produto");
        }

        public void updateData(String table) {
            ContentValues values = new ContentValues();
            values.put("idproduto", cliente.getIdproduto());
            values.put("quantidade", carrinho.getQuantidade());
            values.put("mercado_idmercado", cliente.getIdmercado());
            db.update(table, values, "idproduto='"+cliente.getIdproduto()+"'",null);
        }

        public void getQuantidade(String table) {
            Cursor c = db.rawQuery("select quantidade from " + table, null);
            //Cursor c = db.rawQuery("select * from " + table, null);
            //int columnsSize = c.getColumnCount();
            // 获取表的内容
            int quantidade=0;

            while (c.moveToNext()) {
                //Toast.makeText(mcon, " Produto atualizado "+c.getInt(1),
                //        Toast.LENGTH_LONG).show();
                //for (int i = 0; i < columnsSize; i++) {
                quantidade = quantidade + c.getInt(0);

            }

            cliente.setQuantidade(quantidade);
        }
    }
}


