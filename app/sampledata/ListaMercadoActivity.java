package com.example.clienet.projeto_mercado3;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

public class ListaMercadoActivity extends AppCompatActivity {

    ArrayList<HashMap<String,Object>> listdata = new ArrayList<HashMap<String,Object>>();
    SimpleAdapter listItemAdapter;
    ProgressDialog mProgressDialog;

    Cliente cliente;
    ListView list;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_mercado);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        intent = getIntent();
        cliente = (Cliente) intent.getSerializableExtra("cliente");

        getSupportActionBar().setSubtitle("Login: "+cliente.getEmail());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setImageResource(R.drawable.ic_local_grocery_store_black);

        fab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(ListaMercadoActivity.this, ListaCarrinhosActivity.class);

                // 3. put person in intent data
                intent.putExtra("cliente", cliente);

                // 4. start the activity
                startActivity(intent);
            }
        });

        list = (ListView) findViewById(R.id.list_items);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
                HashMap<String, Object> map = listdata.get(pos);

                int value = (int) map.get("idmercado");

                String nome_mercado = (String) map.get("nome");

                String foto_mercado = (String) map.get("foto");

                String bairro = (String) map.get("bairro");

                cliente.setIdmercado(value);
                cliente.setNome_mercado(nome_mercado);
                cliente.setFoto_mercado(foto_mercado);
                cliente.setBairro_mercado(bairro);

                Intent intent = new Intent(ListaMercadoActivity.this, ListaProdutoActivity.class);

                // 3. put person in intent data
                intent.putExtra("cliente", cliente);

                // 4. start the activity
                startActivity(intent);

            }
        });

        /*listItemAdapter = new SimpleAdapter(ListaMercadoActivity.this,
                listdata,
                R.layout.item,
                new String[] { "foto", "nome", "preco_entrega" },
                new int[] { R.id.icon, R.id.firstLine, R.id.secondLine });
        list.setAdapter(listItemAdapter);
        */

        ListAdapter adapter =
                new MyAdapter(
                        ListaMercadoActivity.this,
                        listdata,
                        R.layout.mercado,
                        new String[]{"nome","bairro", "preco_entrega"},
                        new int[]{R.id.firstLine,R.id.secondLine, R.id.thirdLine});
        list.setAdapter(adapter);

        new FetchSQL().execute();

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

                Intent intent = new Intent(ListaMercadoActivity.this, MainActivity.class);

                finishAffinity();

                startActivity(intent);

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed()
    {

        Intent intent = new Intent(ListaMercadoActivity.this, MapsActivity.class);

        intent.putExtra("cliente", cliente);

        startActivity(intent);

    }

    private class FetchSQL extends AsyncTask<Void,Void,String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            mProgressDialog = new ProgressDialog(ListaMercadoActivity.this);

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
                sql = "SELECT idmercado, nome, foto, preco_entrega, bairro FROM mercados WHERE cidade_idcidade='"+cliente.getIdcidade()+"'";
                ResultSet rs = st.executeQuery(sql);
                while(rs.next()) {
                    HashMap<String, Object> meMap = new HashMap<String, Object>();
                    meMap.put("idmercado", rs.getInt(1));
                    meMap.put("nome", rs.getString(2));
                    meMap.put("bairro", "Bairro: "+rs.getString(5));
                    meMap.put("foto", "http://192.168.1.104"+rs.getString(3));
                    meMap.put("preco_entrega", "R$ " + rs.getString(4));
                    listdata.add(meMap);
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

            list = (ListView) findViewById(R.id.list_items);
            /*listItemAdapter = new SimpleAdapter(ListaMercadoActivity.this,
                    listdata,
                    R.layout.item,
                    new String[] { "foto", "nome", "preco_entrega" },
                    new int[] { R.id.icon, R.id.firstLine, R.id.secondLine });
            list.setAdapter(listItemAdapter);*/
            ListAdapter adapter =
                    new MyAdapter(
                            ListaMercadoActivity.this,
                            listdata,
                            R.layout.mercado,
                            new String[]{"nome","bairro","preco_entrega"},
                            new int[]{R.id.firstLine,R.id.secondLine, R.id.thirdLine});
            list.setAdapter(adapter);

            mProgressDialog.dismiss();

        }
    }

}
