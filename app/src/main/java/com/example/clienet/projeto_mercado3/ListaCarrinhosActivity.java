package com.example.clienet.projeto_mercado3;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;

public class ListaCarrinhosActivity extends AppCompatActivity {

    SQLiteDatabase mDb;
    SQLiteDatabaseDao dao;

    Cliente cliente;

    ListView list;

    SimpleAdapter listItemAdapter;

    ArrayList<HashMap<String, Object>> listData;

    ImageButton imageButton;

    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_carrinhos);

        list = (ListView) findViewById(R.id.list_items);

        intent = getIntent();
        Bundle bundle = intent.getExtras();
        cliente =(Cliente)bundle.getSerializable("cliente");

        getSupportActionBar().setSubtitle("Login: "+cliente.getEmail());

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dao = new SQLiteDatabaseDao();

    }

    @Override
    public void onBackPressed()
    {
        if(cliente.getIdcidade()==0){
            Intent intent = new Intent(ListaCarrinhosActivity.this, MenuActivity.class);

            intent.putExtra("cliente", cliente);

            startActivity(intent);
        }else {

            Intent intent = new Intent(ListaCarrinhosActivity.this, ListaMercadoActivity.class);

            intent.putExtra("cliente", cliente);

            startActivity(intent);

        }
    }

    @Override
    public boolean onSupportNavigateUp(){
        if(cliente.getIdcidade()==0){
            Intent intent = new Intent(ListaCarrinhosActivity.this, MenuActivity.class);

            intent.putExtra("cliente", cliente);

            startActivity(intent);
        }else {

            Intent intent = new Intent(ListaCarrinhosActivity.this, ListaMercadoActivity.class);

            intent.putExtra("cliente", cliente);

            startActivity(intent);

        }

        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    class SQLiteDatabaseDao {

        @SuppressLint("WrongConstant")
        public SQLiteDatabaseDao() {
            mDb = openOrCreateDatabase("Carrinhos.db",
                    SQLiteDatabase.CREATE_IF_NECESSARY, null);

            getAllData("mercado");

            /*listItemAdapter = new SimpleAdapter(ListaCarrinhosActivity.this,
                    listData,
                    R.layout.carrinhos,
                    new String[] { "nome" },
                    new int[] { R.id.nome });
            list.setAdapter(listItemAdapter);
            */

            ListAdapter adapter =
                    new MyAdapter2(
                            ListaCarrinhosActivity.this,
                            listData,
                            R.layout.carrinhos,
                            new String[]{"nome", "bairro"},
                            new int[]{R.id.nome, R.id.bairro},
                            intent);
            list.setAdapter(adapter);

        }


        public void getAllData(String table) {
            Cursor c = mDb.rawQuery("select * from " + table, null);
            //int columnsSize = c.getColumnCount();
            listData = new ArrayList<HashMap<String, Object>>();
            // 获取表的内容
            while (c.moveToNext()) {
                HashMap<String, Object> map = new HashMap<String, Object>();
                //for (int i = 0; i < columnsSize; i++) {
                    map.put("id", c.getInt(0));
                    map.put("idmercado", c.getInt(1));
                    map.put("nome", c.getString(2));
                    map.put("bairro", c.getString(3));
                //}
                listData.add(map);
            }
        }


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.logout:

                this.deleteDatabase("/data/data/com.example.clienet.projeto_mercado3/databases/Carrinhos.db");

                SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();

                editor.clear();
                editor.commit();

                Intent intent = new Intent(ListaCarrinhosActivity.this, MainActivity.class);

                finishAffinity();

                startActivity(intent);

                return true;

            case R.id.seus_pedidos:

                Intent intent_pedidos = new Intent(ListaCarrinhosActivity.this, PedidosActivity.class);

                Bundle bundle = new Bundle();

                bundle.putSerializable("cliente", cliente);

                intent_pedidos.putExtras(bundle);

                startActivity(intent_pedidos);

                return true;


            default:
                return super.onOptionsItemSelected(item);
        }
    }



}
