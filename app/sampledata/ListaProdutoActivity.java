package com.example.clienet.projeto_mercado3;

import android.app.ProgressDialog;
import android.app.SearchManager;
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
import java.util.ArrayList;
import java.util.HashMap;

public class ListaProdutoActivity extends AppCompatActivity {

    TextView firstLine;
    TextView secondLine;
    ArrayList<HashMap<String,Object>> listdata = new ArrayList<HashMap<String,Object>>();

    ProgressDialog mProgressDialog;

    Cliente cliente;
    ListView list;

    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_produto);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        intent = getIntent();
        cliente = (Cliente) intent.getSerializableExtra("cliente");

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            //doMySearch(query);

        }

        getSupportActionBar().setSubtitle("Login: "+cliente.getEmail());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setImageResource(R.drawable.ic_local_grocery_store_black);

        fab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(ListaProdutoActivity.this, ListaCarrinhosActivity.class);

                // 3. put person in intent data
                intent.putExtra("cliente", cliente);

                // 4. start the activity
                startActivity(intent);
            }
        });

        list = (ListView) findViewById(R.id.list_items);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
                HashMap<String, Object> map = listdata.get(pos-1);

                int value = (int) map.get("idproduto");

                cliente.setIdproduto(value);

                Intent intent = new Intent(ListaProdutoActivity.this, ProdutoActivity.class);

                // 3. put person in intent data
                intent.putExtra("cliente", cliente);

                // 4. start the activity
                startActivity(intent);

            }
        });

        ListAdapter adapter =
                new MyAdapter(
                        ListaProdutoActivity.this,
                        listdata,
                        R.layout.item,
                        new String[]{"nome","preco"},
                        new int[]{R.id.firstLine,R.id.thirdLine});
        list.setAdapter(adapter);

        new ListaProdutoActivity.FetchSQL().execute();


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

                SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();

                editor.clear();
                editor.commit();

                Intent intent = new Intent(ListaProdutoActivity.this, MainActivity.class);

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

        Intent intent = new Intent(ListaProdutoActivity.this, ListaMercadoActivity.class);

        intent.putExtra("cliente", cliente);

        startActivity(intent);

    }

    private class FetchSQL extends AsyncTask<Void,Void,String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            mProgressDialog = new ProgressDialog(ListaProdutoActivity.this);

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
                sql = "SELECT idproduto, nome, marca, preco, quantidade_unidade, unidade, foto FROM produtos WHERE mercado_idmercado='"+cliente.getIdmercado()+"'";
                ResultSet rs = st.executeQuery(sql);

                while(rs.next()) {
                    HashMap<String, Object> meMap = new HashMap<String, Object>();
                    meMap.put("idproduto", rs.getInt(1));
                    meMap.put("nome", rs.getString(2)+" "+rs.getString(3)+" " +rs.getString(5)+" "+rs.getString(6));
                    meMap.put("foto", "http://192.168.1.104"+rs.getString(7));
                    meMap.put("preco", " R$ " + rs.getString(4));
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

            View header = getLayoutInflater().inflate(R.layout.listview_lista_produto_header,null);

            ImageButton imageButton = (ImageButton) header.findViewById(R.id.imageButton);

            Picasso.with(ListaProdutoActivity.this).load(cliente.getFoto_mercado()).into(imageButton);

            imageButton.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(ListaProdutoActivity.this, MercadoActivity.class);

                    // 3. put person in intent data
                    intent.putExtra("cliente", cliente);

                    // 4. start the activity
                    startActivity(intent);
                }
            });

            ((TextView) header.findViewById(R.id.mercado)).setText(cliente.getNome_mercado());

            list.addHeaderView(header);

            ListAdapter adapter =
                    new MyAdapter(
                            ListaProdutoActivity.this,
                            listdata,
                            R.layout.item,
                            new String[]{"nome","preco"},
                            new int[]{R.id.firstLine,R.id.thirdLine});
            list.setAdapter(adapter);

            mProgressDialog.dismiss();

        }
    }

}
