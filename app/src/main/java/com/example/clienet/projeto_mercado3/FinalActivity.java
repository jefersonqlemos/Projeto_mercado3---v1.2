package com.example.clienet.projeto_mercado3;

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
import android.widget.Button;
import android.widget.TextView;

public class FinalActivity extends AppCompatActivity {

    Intent intent;
    Cliente cliente;
    TextView pedido;
    Button ir_menu;
    Button ir_pedidos;
    private String idpedido;
    SQLiteDatabase mDb;
    SQLiteDatabaseDao2 dao2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final);

        ir_menu = findViewById(R.id.ir_para_menu);

        ir_pedidos = findViewById(R.id.ir_para_pedidos);

        intent = getIntent();
        Bundle bundle = intent.getExtras();
        cliente = (Cliente)bundle.getSerializable("cliente");
        //idpedido = bundle.getString("idpedido");

        pedido = findViewById(R.id.pedido);

        pedido.setText("Numero do Pedido: "+idpedido);

        getSupportActionBar().setSubtitle("Login: "+cliente.getEmail());

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ir_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FinalActivity.this, MenuActivity.class);

                intent.putExtra("cliente", cliente);

                startActivity(intent);
            }
        });

        ir_pedidos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FinalActivity.this, PedidosActivity.class);

                intent.putExtra("cliente", cliente);

                startActivity(intent);
            }
        });

        dao2 = new SQLiteDatabaseDao2();

    }

    @Override
    public void onBackPressed()
    {

        Intent intent = new Intent(FinalActivity.this, MenuActivity.class);

        intent.putExtra("cliente", cliente);

        startActivity(intent);

    }

    @Override
    public boolean onSupportNavigateUp(){
        Intent intent = new Intent(FinalActivity.this, MenuActivity.class);

        intent.putExtra("cliente", cliente);

        startActivity(intent);

        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
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

                Intent intent = new Intent(FinalActivity.this, MainActivity.class);

                finishAffinity();

                startActivity(intent);

                return true;

            case R.id.seus_pedidos:

                Intent intent_pedidos = new Intent(FinalActivity.this, PedidosActivity.class);

                Bundle bundle = new Bundle();

                bundle.putSerializable("cliente", cliente);

                intent_pedidos.putExtras(bundle);

                startActivity(intent_pedidos);

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    class SQLiteDatabaseDao2 {

        public SQLiteDatabaseDao2() {

            mDb = SQLiteDatabase.openDatabase("/data/data/com.example.clienet.projeto_mercado3/databases/Carrinhos.db", null, 0);
            deleteData("mercado");
            getQuantidade("produto");

        }

        public void deleteData(String table) {

            try {
                mDb.execSQL("PRAGMA foreign_keys=ON");
            }catch (Exception e) {
                e.printStackTrace();
            }

            mDb.delete(table, "idmercado='"+cliente.getIdmercado()+"'",null);
        }

        public void getQuantidade(String table) {
            Cursor c = mDb.rawQuery("select quantidade from " + table, null);
            //int columnsSize = c.getColumnCount();
            // ??????
            int quantidade=0;

            while (c.moveToNext()) {
                //for (int i = 0; i < columnsSize; i++) {
                quantidade = quantidade + c.getInt(0);

            }

            cliente.setQuantidade(quantidade);
        }
    }
}
