package com.example.clienet.projeto_mercado3;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

public class CarrinhoActivity extends AppCompatActivity {

    Cliente cliente;
    Carrinho carrinho = new Carrinho();

    SQLiteDatabase mDb;
    SQLiteDatabaseDao dao;
    SQLiteDatabaseDao2 dao2;

    Intent intent;

    ListView list;

    ArrayList<HashMap<String, Object>> listData;

    ArrayList<HashMap<String, Object>> listdatasqlite;

    ProgressDialog mProgressDialog;

    ImageButton imageButton;

    View header;

    View footer;

    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carrinho);

        list = (ListView) findViewById(R.id.list_items);

        intent = getIntent();
        Bundle bundle = intent.getExtras();
        cliente =(Cliente)bundle.getSerializable("cliente");

        imageButton =(ImageButton)findViewById(R.id.imageButton);

        getSupportActionBar().setSubtitle("Login: "+cliente.getEmail());

        dao = new SQLiteDatabaseDao();

    }

    @Override
    public void onBackPressed()
    {

        Intent intent = new Intent(CarrinhoActivity.this, ListaCarrinhosActivity.class);

        intent.putExtra("cliente", cliente);

        startActivity(intent);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        MenuItem register = menu.findItem(R.id.search);

        register.setVisible(false);

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.logout:

                SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();

                editor.clear();
                editor.commit();

                Intent intent = new Intent(CarrinhoActivity.this, MainActivity.class);

                finishAffinity();

                startActivity(intent);

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    class SQLiteDatabaseDao {

        @SuppressLint("WrongConstant")
        public SQLiteDatabaseDao() {
            mDb = openOrCreateDatabase("Carrinhos.db",
                    SQLiteDatabase.CREATE_IF_NECESSARY, null);

            getAllData("produto");

            new CarrinhoActivity.FetchSQL().execute();

        }

        public void getAllData(String table) {
            Cursor c = mDb.rawQuery("select * from " + table + " where mercado_idmercado='"+ cliente.getIdmercado() +"'", null);
            //int columnsSize = c.getColumnCount();
            listdatasqlite = new ArrayList<HashMap<String, Object>>();
            // 获取表的内容
            while (c.moveToNext()) {
                HashMap<String, Object> map = new HashMap<String, Object>();
                //for (int i = 0; i < columnsSize; i++) {
                map.put("id", c.getInt(0));
                map.put("idproduto", c.getInt(1));
                map.put("quantidade", c.getInt(2));
                //}
                listdatasqlite.add(map);
            }
        }
    }

    private class FetchSQL extends AsyncTask<Void,Void,String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = new ProgressDialog(CarrinhoActivity.this);

            mProgressDialog.setTitle("Por Favor Aguarde");
            // Set progressdialog message
            mProgressDialog.setMessage("Loading...");
            mProgressDialog.setIndeterminate(false);
            // Show progressdialog
            mProgressDialog.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            String senha = "";

            listData = new ArrayList<HashMap<String, Object>>();

            try {
                Class.forName("com.mysql.jdbc.Driver");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            String url = "jdbc:mysql://192.168.1.104/projeto_mercado1?user=root&password=spfc";
            Connection conn;
            try {
                DriverManager.setLoginTimeout(5);
                conn = DriverManager.getConnection(url);
                Statement st = conn.createStatement();
                String sql;
                ArrayList idprodutos = new ArrayList();
                ArrayList quantidades = new ArrayList();
                float subtotal_produtos = 0;
                int quantidade_total = 0;

                for(int i=0; i<listdatasqlite.size(); i++){
                     idprodutos.add(listdatasqlite.get(i).get("idproduto"));
                     quantidades.add(listdatasqlite.get(i).get("quantidade"));
                }
                int i=0;

                Log.d(TAG, "Deu cerrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrto"+quantidades);

                sql = "SELECT idproduto, foto, nome, marca, quantidade_unidade, unidade,  preco, estoque  FROM produtos WHERE idproduto IN ("+idprodutos.toString().replace("[", "").replace("]", "")+")";

                ResultSet rs = st.executeQuery(sql);

                while(rs.next()) {
                    HashMap<String, Object> meMap = new HashMap<String, Object>();
                    meMap.put("idproduto", rs.getInt(1));
                    meMap.put("nome", rs.getString(3)+" "+rs.getString(4)+" " +rs.getString(5)+" "+rs.getString(6));
                    meMap.put("foto", "http://192.168.1.104"+rs.getString(2));
                    meMap.put("preco", rs.getString(7));
                    meMap.put("estoque", rs.getInt(8));
                    meMap.put("quantidade", quantidades.get(i));
                    float qtd = Float.parseFloat(String.valueOf(quantidades.get(i)));
                    float preco = rs.getFloat(7);
                    subtotal_produtos = subtotal_produtos + (qtd*preco);
                    meMap.put("subtotal", String.valueOf(new DecimalFormat("######0.00").format(preco * qtd)));
                    listData.add(meMap);
                    quantidade_total = quantidade_total + (int) qtd;
                    i++;
                }

                carrinho.setSubtotal(subtotal_produtos);
                carrinho.setQuantidade_total(quantidade_total);

                sql = "SELECT preco_entrega FROM mercados WHERE idmercado='"+cliente.getIdmercado()+"'";

                rs = st.executeQuery(sql);

                while(rs.next()) {
                    carrinho.setPreco_entrega(rs.getFloat(1));
                }

                rs.close();
                st.close();
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return senha;
        }
        @Override
        protected void onPostExecute(String value) {
            //

            header = getLayoutInflater().inflate(R.layout.listview_header,null);

            ((TextView) header.findViewById(R.id.mercado)).setText(cliente.getNome_mercado());
            ((TextView) header.findViewById(R.id.bairro)).setText(cliente.getBairro_mercado());
            ((TextView) header.findViewById(R.id.numero_de_itens)).setText(Integer.toString(carrinho.getQuantidade_total())+" itens");

            carrinho.setTotal(carrinho.getSubtotal()+carrinho.getPreco_entrega());

            ((TextView) header.findViewById(R.id.total)).setText("Total: R$ "+String.valueOf(new DecimalFormat("######0.00").format(carrinho.getTotal())));

            ImageButton imgexcluir = (ImageButton) header.findViewById(R.id.imgexcluir);
            imgexcluir.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dao2 = new SQLiteDatabaseDao2();
                }
            });

            list.addHeaderView(header);

            footer = getLayoutInflater().inflate(R.layout.listview_footer, null);

            ((TextView) footer.findViewById(R.id.preco_entrega)).setText("Preço da entrega: R$ "+String.valueOf(new DecimalFormat("######0.00").format(carrinho.getPreco_entrega())));

            ((TextView) footer.findViewById(R.id.subtotal_produtos)).setText("Subtotal dos produtos: R$ "+String.valueOf(new DecimalFormat("######0.00").format(carrinho.getSubtotal())));

            ((TextView) footer.findViewById(R.id.total2)).setText("Valor total da compra: R$ "+String.valueOf(new DecimalFormat("######0.00").format(carrinho.getTotal())));

            list.addFooterView(footer);

            ListAdapter adapter =
                    new MyAdapter3(
                            CarrinhoActivity.this,
                            listData,
                            R.layout.carrinho,
                            new String[]{"nome", "preco", "subtotal"},
                            new int[]{R.id.nome, R.id.valor_unitario, R.id.subtotal},
                            intent);
            list.setAdapter(adapter);

            mProgressDialog.dismiss();

        }
    }

    class SQLiteDatabaseDao2 {

        public SQLiteDatabaseDao2() {
            mDb = SQLiteDatabase.openDatabase("/data/data/com.example.clienet.projeto_mercado3/databases/Carrinhos.db", null, 0);
            deleteData("mercado");

            Intent intent = new Intent(CarrinhoActivity.this, ListaCarrinhosActivity.class);

            intent.putExtra("cliente", cliente);

            finishAffinity();

            startActivity(intent);
        }

        public void deleteData(String table) {
            mDb.delete(table, "idmercado='"+cliente.getIdmercado()+"'",null);
            Log.d(TAG, "Deu cerrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrtodelete");
        }
    }

}
