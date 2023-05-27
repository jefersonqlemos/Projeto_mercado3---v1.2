package com.example.clienet.projeto_mercado3;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
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
import java.util.HashMap;

public class LocalEntregaActivity extends AppCompatActivity {

    Cliente cliente;

    ProgressDialog mProgressDialog;

    Intent intent;

    TextView endereco;
    TextView bairro;
    TextView cidade;
    TextView destinatario;

    Spinner spinner;

    Button continuar;

    Button adicionar_endereco;

    private String[] enderecos;

    private HashMap<Integer,String> spinnerMap;

    LocalEntrega localEntrega = new LocalEntrega();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_entrega);

        intent = getIntent();
        Bundle bundle = intent.getExtras();
        cliente = (Cliente)bundle.getSerializable("cliente");

        endereco = (TextView) findViewById(R.id.endereco);

        bairro = (TextView) findViewById(R.id.bairro);

        cidade = (TextView) findViewById(R.id.cidade);

        destinatario = (TextView) findViewById(R.id.destinatario);

        spinner = (Spinner) findViewById(R.id.spinner);

        getSupportActionBar().setSubtitle("Login: "+cliente.getEmail());

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        continuar = (Button) findViewById(R.id.continuar);

        adicionar_endereco = findViewById(R.id.adicionar_endereco);

        new FetchSQL().execute();

        continuar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(cliente.getIdcidade_carrinho()==localEntrega.getIdcidade()){
                    int id = Integer.parseInt(spinnerMap.get(spinner.getSelectedItemPosition()));
                    cliente.setIdendereco(id);

                    Intent intent = new Intent(LocalEntregaActivity.this, ResumoActivity.class);

                    // 3. put person in intent data
                    intent.putExtra("cliente", cliente);

                    // 4. start the activity
                    startActivity(intent);

                }else{
                    Toast.makeText(getApplicationContext(), "A cidade do endereço precisa ser a mesma que a do mercado ",
                            Toast.LENGTH_LONG).show();
                }
            }
        });

        adicionar_endereco.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LocalEntregaActivity.this, EnderecoActivity.class);

                // 3. put person in intent data
                intent.putExtra("cliente", cliente);

                // 4. start the activity
                startActivity(intent);
            }
        });


    }

    @Override
    public void onBackPressed()
    {

        Intent intent = new Intent(LocalEntregaActivity.this, CarrinhoActivity.class);

        intent.putExtra("cliente", cliente);

        startActivity(intent);

    }

    @Override
    public boolean onSupportNavigateUp(){
        Intent intent = new Intent(LocalEntregaActivity.this, CarrinhoActivity.class);

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

                Intent intent = new Intent(LocalEntregaActivity.this, CarrinhoActivity.class);

                finishAffinity();

                startActivity(intent);

                return true;

            case R.id.seus_pedidos:

                Intent intent_pedidos = new Intent(LocalEntregaActivity.this, PedidosActivity.class);

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

            mProgressDialog = new ProgressDialog(LocalEntregaActivity.this);

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

                url = new URL(getString(R.string.ipnovo)+"local_entrega");

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
                        .appendQueryParameter("idcliente_comprador", String.valueOf(cliente.getId()));
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



                    JSONObject json = new JSONObject(sb.toString());

                    JSONArray jsonArray = new JSONArray(json.getString("rows"));

                    for(int i=0; i<jsonArray.length(); i++) {
                        JSONObject json_data = jsonArray.getJSONObject(i);
                        localEntrega.setEndereco(json_data.getString("endereco"));
                        localEntrega.setNumero(json_data.getString("numero"));
                        localEntrega.setComplemento(json_data.getString("complemento"));
                        localEntrega.setBairro(json_data.getString("bairro"));
                        localEntrega.setDestinatario(json_data.getString("nome"));
                    }

                    jsonArray = new JSONArray(json.getString("rows2"));

                    for(int i=0; i<jsonArray.length(); i++) {
                        JSONObject json_data = jsonArray.getJSONObject(i);
                        localEntrega.setNome_cidade(json_data.getString("nome"));
                        localEntrega.setIdcidade(json_data.getInt("idcidade"));
                    }

                    jsonArray = new JSONArray(json.getString("rows3"));

                    enderecos = new String[jsonArray.length()];
                    spinnerMap = new HashMap<Integer, String>();

                    //spinnerMap.put(0, "0");
                    //enderecos[0] = "Endereço padrão";

                    for(int i=0; i<jsonArray.length(); i++) {
                        JSONObject json_data = jsonArray.getJSONObject(i);
                        spinnerMap.put(i, json_data.getString("idenderecos"));
                        enderecos[i]=json_data.getString("identificacao");
                    }

                    Log.e("add", "rrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrr"+sb.toString());

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

            endereco.setText(localEntrega.getEndereco()+", "+localEntrega.getNumero()+", "+localEntrega.getComplemento());
            bairro.setText("Bairro: "+localEntrega.getBairro());
            cidade.setText("Cidade: "+localEntrega.getNome_cidade()+localEntrega.getDestinatario());
            destinatario.setText("Destinatario: "+localEntrega.getDestinatario());

            //String colors[] = {"Red","Blue","White","Yellow","Black", "Green","Purple","Orange","Grey"};

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(LocalEntregaActivity.this,
                    android.R.layout.simple_spinner_item, enderecos);

            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            spinner.setAdapter(adapter);

            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                    localEntrega.setIdendereco(Integer.parseInt(spinnerMap.get(i)));
                    new FetchSQL2().execute();

                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

            mProgressDialog.dismiss();

            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        }
    }

    private class FetchSQL2 extends AsyncTask<Void,Void,String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

            mProgressDialog = new ProgressDialog(LocalEntregaActivity.this);

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

                url = new URL(getString(R.string.ipnovo)+"endereco");

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
                        .appendQueryParameter("idendereco", String.valueOf(localEntrega.getIdendereco()));
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

                    Log.e("add", "rrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrr"+sb.toString());

                    JSONObject json = new JSONObject(sb.toString());

                    JSONArray jsonArray = new JSONArray(json.getString("rows"));

                    JSONObject json_data = jsonArray.getJSONObject(0);

                    localEntrega.setEndereco(json_data.getString("endereco"));
                    localEntrega.setNumero(json_data.getString("numero"));
                    localEntrega.setComplemento(json_data.getString("complemento"));
                    localEntrega.setBairro(json_data.getString("bairro"));
                    localEntrega.setDestinatario(json_data.getString("nome_destinatario"));

                    jsonArray = new JSONArray(json.getString("rows2"));

                    json_data = jsonArray.getJSONObject(0);

                    localEntrega.setIdcidade(json_data.getInt("idcidade"));
                    //cliente.setIdcidade_carrinho(json_data.getInt("idcidade"));
                    localEntrega.setNome_cidade(json_data.getString("nome"));

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

            endereco.setText(localEntrega.getEndereco()+", "+localEntrega.getNumero()+", "+localEntrega.getComplemento());
            bairro.setText("Bairro: "+localEntrega.getBairro());
            cidade.setText("Cidade: "+localEntrega.getNome_cidade());
            destinatario.setText("Destinatario: "+localEntrega.getDestinatario());

            mProgressDialog.dismiss();

            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        }
    }
}
