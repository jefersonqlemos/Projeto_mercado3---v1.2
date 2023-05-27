package com.example.clienet.projeto_mercado3;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

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
import java.util.HashMap;

public class MercadoActivity extends AppCompatActivity {

    Cliente cliente;
    Mercado mercado = new Mercado();
    TextView numero;

    TextView nome;
    TextView endereco;
    TextView bairro;
    TextView cidade;
    TextView email;
    TextView preco_entrega;
    TextView telefone1;
    TextView telefone2;
    ImageView foto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mercado);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        cliente = (Cliente) bundle.getSerializable("cliente");

        getSupportActionBar().setSubtitle("Login: " + cliente.getEmail());

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        numero = (TextView) findViewById(R.id.numero);

        numero.setText("" + cliente.getQuantidade());

        nome = findViewById(R.id.mercado);
        endereco = findViewById(R.id.endereco);
        bairro = findViewById(R.id.bairro);
        cidade = findViewById(R.id.cidade);
        email = findViewById(R.id.email);
        preco_entrega = findViewById(R.id.preco_entrega);
        telefone1 = findViewById(R.id.telefone1);
        telefone2 = findViewById(R.id.telefone2);
        foto = findViewById(R.id.imageView);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setImageResource(R.drawable.ic_local_grocery_store_black);

        fab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (cliente.getQuantidade() > 0) {
                    Intent intent = new Intent(MercadoActivity.this, ListaCarrinhosActivity.class);

                    // 3. put person in intent data
                    intent.putExtra("cliente", cliente);

                    // 4. start the activity
                    startActivity(intent);
                }
            }
        });

        new FetchSQL().execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }


    @Override
    public void onBackPressed()
    {
        Intent intent = new Intent(MercadoActivity.this, ListaProdutoActivity.class);

        intent.putExtra("cliente", cliente);

        startActivity(intent);
    }

    @Override
    public boolean onSupportNavigateUp(){
        Intent intent = new Intent(MercadoActivity.this, ListaProdutoActivity.class);

        intent.putExtra("cliente", cliente);

        startActivity(intent);

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

                Intent intent = new Intent(MercadoActivity.this, MainActivity.class);

                finishAffinity();

                startActivity(intent);

                return true;

            case R.id.seus_pedidos:

                Intent intent_pedidos = new Intent(MercadoActivity.this, PedidosActivity.class);

                Bundle bundle = new Bundle();

                bundle.putSerializable("cliente", cliente);

                intent_pedidos.putExtras(bundle);

                startActivity(intent_pedidos);

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private class FetchSQL extends AsyncTask<Void,Void,String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        }

        @Override
        protected String doInBackground(Void... params) {

            URL url;
            HttpURLConnection urlConnection = null;
            String result = null;

            try {

                url = new URL(getString(R.string.ipnovo)+"mercado");

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
                        .appendQueryParameter("idmercado", String.valueOf(cliente.getIdmercado()));
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

                    JSONArray jsonArray = new JSONArray(json.getString("rows2"));

                    for(int i=0; i<jsonArray.length(); i++){
                        JSONObject json_data = jsonArray.getJSONObject(i);

                        cliente.setNome_cidade(json_data.getString("nome"));

                    }

                    jsonArray = new JSONArray(json.getString("rows"));

                    for(int i=0; i<jsonArray.length(); i++){
                        JSONObject json_data = jsonArray.getJSONObject(i);

                        mercado.setNome_mercado(json_data.getString("nome"));
                        mercado.setFoto_mercado(json_data.getString("foto"));
                        mercado.setBairro_mercado(json_data.getString("bairro"));
                        mercado.setEndereco_mercado(json_data.getString("endereco"));
                        mercado.setEmail(json_data.getString("email"));
                        mercado.setPreco_entrega(json_data.getString("preco_entrega"));
                        mercado.setTelefone1(json_data.getString("telefone1"));
                        mercado.setTelefone2(json_data.getString("telefone2"));

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

            nome.setText(mercado.getNome_mercado());
            endereco.setText(mercado.getEndereco_mercado());
            bairro.setText("Bairro: "+mercado.getBairro_mercado());
            cidade.setText(cliente.getNome_cidade());
            email.setText("Email: "+mercado.getEmail());
            preco_entrega.setText("Preço da Entrega: R$"+mercado.getPreco_entrega());
            telefone1.setText(mercado.getTelefone1());
            telefone2.setText(mercado.getTelefone2());

            Picasso.with(MercadoActivity.this).load(getString(R.string.ip)+getString(R.string.endereco_imagem)+mercado.getFoto_mercado()).into(foto);

            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }
    }

}
