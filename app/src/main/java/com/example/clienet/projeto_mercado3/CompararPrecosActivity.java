package com.example.clienet.projeto_mercado3;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class CompararPrecosActivity extends AppCompatActivity {

    Cliente cliente;
    TextView numero;
    SearchView searchView;
    Mercado mercado = new Mercado();
    Produto produto = new Produto();
    TextView cidade;
    ProgressDialog mProgressDialog;

    ArrayList<HashMap<String,Object>> listdata = new ArrayList<HashMap<String,Object>>();
    ArrayList<HashMap<String,Object>> listmercado= new ArrayList<HashMap<String,Object>>();

    ListView list;
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comparar_precos);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        cliente = (Cliente) bundle.getSerializable("cliente");

        getSupportActionBar().setSubtitle("Login: " + cliente.getEmail());

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        numero = (TextView) findViewById(R.id.numero);

        numero.setText("" + cliente.getQuantidade());

        list = (ListView) findViewById(R.id.list_items);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setImageResource(R.drawable.ic_local_grocery_store_black);

        fab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (cliente.getQuantidade() > 0) {
                    Intent intent = new Intent(CompararPrecosActivity.this, ListaCarrinhosActivity.class);

                    // 3. put person in intent data
                    intent.putExtra("cliente", cliente);

                    // 4. start the activity
                    startActivity(intent);
                }
            }
        });

        cidade = (TextView) findViewById(R.id.cidade);

        cidade.setText(cliente.getNome_cidade());

        button = (Button) findViewById(R.id.escolhercidade);

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(CompararPrecosActivity.this, Maps2Activity.class);

                intent.putExtra("cliente", cliente);

                startActivity(intent);
                // Code here executes on main thread after user presses button
            }
        });

        searchView = (SearchView) findViewById(R.id.search);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                View view = CompararPrecosActivity.this.getCurrentFocus();
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager)getSystemService(CompararPrecosActivity.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
                listdata=null;
                listdata=new ArrayList<HashMap<String,Object>>();
                listmercado = null;
                listmercado=new ArrayList<HashMap<String,Object>>();
                list.setAdapter(null);
                produto.setNome(query);
                new FetchSQL().execute();
                //Toast toast = Toast.makeText(CompararPrecosActivity.this, query, Toast.LENGTH_LONG);
                //toast.show();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                listdata=null;
                listdata=new ArrayList<HashMap<String,Object>>();
                listmercado = null;
                listmercado=new ArrayList<HashMap<String,Object>>();
                list.setAdapter(null);
                produto.setNome(newText);
                new FetchSQL().execute();
                //Toast toast = Toast.makeText(CompararPrecosActivity.this, newText, Toast.LENGTH_LONG);
                //toast.show();
                return true;
            }
        });

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {

                HashMap<String, Object> map = (HashMap<String, Object>)list.getItemAtPosition(pos);

                int value = (int) map.get("idproduto");

                int idmercado = (int) map.get("mercado_idmercado");
                cliente.setIdmercado(idmercado);

                cliente.setIdproduto(value);

                Intent intent = new Intent(CompararPrecosActivity.this, ProdutoActivity.class);

                // 3. put person in intent data
                intent.putExtra("cliente", cliente);

                // 4. start the activity
                startActivity(intent);

            }
        });

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

                Intent intent = new Intent(CompararPrecosActivity.this, MainActivity.class);

                finishAffinity();

                startActivity(intent);

                return true;

            case R.id.seus_pedidos:

                Intent intent_pedidos = new Intent(CompararPrecosActivity.this, PedidosActivity.class);

                Bundle bundle = new Bundle();

                bundle.putSerializable("cliente", cliente);

                intent_pedidos.putExtras(bundle);

                startActivity(intent_pedidos);

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed()
    {
        Intent intent = new Intent(CompararPrecosActivity.this, MenuActivity.class);

        intent.putExtra("cliente", cliente);

        startActivity(intent);
    }

    @Override
    public boolean onSupportNavigateUp(){
        Intent intent = new Intent(CompararPrecosActivity.this, MenuActivity.class);

        intent.putExtra("cliente", cliente);

        startActivity(intent);

        return true;
    }


    private class FetchSQL extends AsyncTask<Void,Void,String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

            mProgressDialog = new ProgressDialog(CompararPrecosActivity.this);

            mProgressDialog.setTitle("Por Favor Aguarde");
            // Set progressdialog message
            mProgressDialog.setMessage("Loading...");
            mProgressDialog.setIndeterminate(false);
            // Show progressdialog

            mProgressDialog.show();

        }

        @Override
        protected String doInBackground(Void... params) {

            URL url;
            HttpURLConnection urlConnection = null;
            String result = null;

            try {

                url = new URL(getString(R.string.ipnovo)+"comparar_precos");

                urlConnection = (HttpURLConnection) url.openConnection();

                urlConnection.setReadTimeout(30000 /* milliseconds */);
                urlConnection.setConnectTimeout(30000 /* milliseconds */);
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty ("Authorization", "Bearer "+cliente.getToken());
                urlConnection.setRequestProperty ("Content-Type", "application/x-www-form-urlencoded");
                urlConnection.setRequestProperty ("Accept", "application/json");
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);

                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("idcidade", String.valueOf(cliente.getIdcidade()))
                        .appendQueryParameter("produto", produto.getNome());
                String query = builder.build().getEncodedQuery();

                // Open connection for sending data
                OutputStream os = urlConnection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(query);
                writer.flush();
                writer.close();
                os.close();
                urlConnection.connect();

            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
                return "exception";
            }

            try {

                int response_code = urlConnection.getResponseCode();

                // Check if successful connection made
                if (response_code == HttpURLConnection.HTTP_OK) {

                    InputStream in = urlConnection.getInputStream();

                    InputStreamReader isw = new InputStreamReader(in);

                    StringBuilder sb = new StringBuilder();

                    String line;

                    BufferedReader br = new BufferedReader(isw);

                    while ((line = br.readLine()) != null) {
                        sb.append(line);
                    }

                    Log.e("tt", "tttttttttttttttttttttttttttttttttttttttttttttttttttttttttt "+sb);

                    JSONObject json = new JSONObject(sb.toString());

                    JSONArray jsonArray = new JSONArray(json.getString("rows"));

                    for(int i=0; i<jsonArray.length(); i++){
                        JSONObject json_data = jsonArray.getJSONObject(i);

                        HashMap<String, Object> meMap = new HashMap<String, Object>();
                        meMap.put("idmercado", json_data.getInt("idmercado"));
                        meMap.put("nome", json_data.getString("nome"));
                        meMap.put("preco_entrega", " R$ " + json_data.getString("preco_entrega"));
                        listmercado.add(meMap);

                    }

                    jsonArray = new JSONArray(json.getString("rows2"));

                    for(int i=0; i<jsonArray.length(); i++){
                        JSONObject json_data = jsonArray.getJSONObject(i);

                        HashMap<String, Object> meMap = new HashMap<String, Object>();
                        meMap.put("idproduto", json_data.getInt("idproduto"));
                        meMap.put("nome", json_data.getString("nome")+" "+json_data.getString("marca")+" " +json_data.getString("quantidade_unidade")+" "+json_data.getString("unidade"));
                        meMap.put("foto", getString(R.string.ip)+getString(R.string.endereco_imagem)+json_data.getString("foto"));
                        meMap.put("preco", " R$ " + json_data.getString("preco"));
                        meMap.put("mercado_idmercado", json_data.getInt("mercado_idmercado"));
                        int idmercado = json_data.getInt("mercado_idmercado");
                        for(int j=0; j<listmercado.size(); j++) {
                            HashMap<String, Object> map = listmercado.get(j);
                            int value = (int) map.get("idmercado");
                            if(idmercado==value) {
                                String nome = (String) map.get("nome");
                                String preco_entrega = (String) map.get("preco_entrega");
                                meMap.put("nome_mercado", nome);
                                meMap.put("preco_entrega", preco_entrega);
                            }
                        }

                        listdata.add(meMap);

                    }

                }else{
                    return("unsuccessful");
                }

            }
            catch(Exception e){
                Log.e("JSON", String.valueOf("aaaaaaaaaaaaaaaa"));
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }

            return result;

        }
        @Override
        protected void onPostExecute(String value) {
            //

            ListAdapter adapter =
                    new MyAdapter4(
                            CompararPrecosActivity.this,
                            listdata,
                            R.layout.comparar_precos,
                            new String[]{"nome","preco","nome_mercado", "preco_entrega"},
                            new int[]{R.id.nome,R.id.preco,R.id.mercado, R.id.preco_entrega});
            list.setAdapter(adapter);

            mProgressDialog.dismiss();

            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        }
    }
}
