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
import android.widget.EditText;
import android.widget.Spinner;
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

public class EnderecoActivity extends AppCompatActivity {

    Cliente cliente;

    Endereco end = new Endereco();

    Intent intent;

    ProgressDialog mProgressDialog;

    Button concluir;

    EditText identificacao;
    EditText nome_destinatario;
    EditText endereco;
    EditText numero;
    EditText complemento;
    EditText bairro;
    EditText cep;

    Spinner spinnerPais;
    Spinner spinnerEstado;
    Spinner spinnerCidade;

    private String[] paises;
    private String[] estados;
    private String[] cidades;

    private HashMap<Integer,String> spinnerMapPais;
    private HashMap<Integer,String> spinnerMapEstado;
    private HashMap<Integer,String> spinnerMapCidade;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_endereco);

        intent = getIntent();
        Bundle bundle = intent.getExtras();
        cliente = (Cliente)bundle.getSerializable("cliente");

        getSupportActionBar().setSubtitle("Login: "+cliente.getEmail());

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        identificacao = findViewById(R.id.identificacao);
        nome_destinatario = findViewById(R.id.nome_destinatario);
        endereco = findViewById(R.id.endereco);
        numero = findViewById(R.id.numero);
        complemento = findViewById(R.id.complemento);
        bairro = findViewById(R.id.bairro);
        cep = findViewById(R.id.cep);

        spinnerPais = (Spinner) findViewById(R.id.spinner2);
        spinnerEstado = (Spinner) findViewById(R.id.spinner3);
        spinnerCidade = (Spinner) findViewById(R.id.spinner4);

        concluir = (Button) findViewById(R.id.concluir);

        concluir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new FetchSQL4().execute();

                Intent intent = new Intent(EnderecoActivity.this, LocalEntregaActivity.class);

                // 3. put person in intent data
                intent.putExtra("cliente", cliente);

                // 4. start the activity
                startActivity(intent);
            }
        });

        new FetchSQL().execute();

    }


    @Override
    public void onBackPressed()
    {

        Intent intent = new Intent(EnderecoActivity.this, LocalEntregaActivity.class);

        intent.putExtra("cliente", cliente);

        startActivity(intent);

    }

    @Override
    public boolean onSupportNavigateUp(){
        Intent intent = new Intent(EnderecoActivity.this, LocalEntregaActivity.class);

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

                Intent intent = new Intent(EnderecoActivity.this, CarrinhoActivity.class);

                finishAffinity();

                startActivity(intent);

                return true;

            case R.id.seus_pedidos:

                Intent intent_pedidos = new Intent(EnderecoActivity.this, PedidosActivity.class);

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

            mProgressDialog = new ProgressDialog(EnderecoActivity.this);

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

                url = new URL(getString(R.string.ipnovo)+"buscar_pais");

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

                    paises = new String[jsonArray.length()];
                    spinnerMapPais = new HashMap<Integer, String>();

                    for(int i=0; i<jsonArray.length(); i++) {
                        JSONObject json_data = jsonArray.getJSONObject(i);
                        spinnerMapPais.put(i, json_data.getString("idpais"));
                        paises[i]=json_data.getString("nome");
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

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(EnderecoActivity.this,
                    android.R.layout.simple_spinner_item, paises);

            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            spinnerPais.setAdapter(adapter);

            spinnerPais.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                    end.setPais(Integer.parseInt(spinnerMapPais.get(i)));
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

            mProgressDialog = new ProgressDialog(EnderecoActivity.this);

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

                url = new URL(getString(R.string.ipnovo)+"buscar_estado");

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
                        .appendQueryParameter("idpais", String.valueOf(end.getPais()));
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

                    estados = new String[jsonArray.length()];
                    spinnerMapEstado = new HashMap<Integer, String>();

                    for(int i=0; i<jsonArray.length(); i++) {
                        JSONObject json_data = jsonArray.getJSONObject(i);
                        spinnerMapEstado.put(i, json_data.getString("idestado"));
                        estados[i]=json_data.getString("nome");
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

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(EnderecoActivity.this,
                    android.R.layout.simple_spinner_item, estados);

            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            spinnerEstado.setAdapter(adapter);

            spinnerEstado.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                    end.setEstado(Integer.parseInt(spinnerMapEstado.get(i)));
                    new FetchSQL3().execute();

                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

            mProgressDialog.dismiss();

            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        }
    }

    private class FetchSQL3 extends AsyncTask<Void,Void,String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

            mProgressDialog = new ProgressDialog(EnderecoActivity.this);

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

                url = new URL(getString(R.string.ipnovo)+"buscar_cidade");

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
                        .appendQueryParameter("idestado", String.valueOf(end.getEstado()));
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

                    cidades = new String[jsonArray.length()];
                    spinnerMapCidade = new HashMap<Integer, String>();

                    for(int i=0; i<jsonArray.length(); i++) {
                        JSONObject json_data = jsonArray.getJSONObject(i);
                        spinnerMapCidade.put(i, json_data.getString("idcidade"));
                        cidades[i]=json_data.getString("nome");
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

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(EnderecoActivity.this,
                    android.R.layout.simple_spinner_item, cidades);

            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            spinnerCidade.setAdapter(adapter);

            spinnerCidade.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                    end.setCidade(Integer.parseInt(spinnerMapCidade.get(i)));

                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

            mProgressDialog.dismiss();

            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        }
    }

    private class FetchSQL4 extends AsyncTask<Void,Void,String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

            mProgressDialog = new ProgressDialog(EnderecoActivity.this);

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

                url = new URL(getString(R.string.ipnovo)+"adicionar_endereco");

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
                        .appendQueryParameter("identificacao", String.valueOf(identificacao.getText()))
                        .appendQueryParameter("nome_destinatario", String.valueOf(nome_destinatario.getText()))
                        .appendQueryParameter("endereco", String.valueOf(endereco.getText()))
                        .appendQueryParameter("numero", String.valueOf(numero.getText()))
                        .appendQueryParameter("complemento", String.valueOf(complemento.getText()))
                        .appendQueryParameter("bairro", String.valueOf(bairro.getText()))
                        .appendQueryParameter("cep", String.valueOf(cep.getText()))
                        .appendQueryParameter("idcliente_comprador", String.valueOf(cliente.getId()))
                        .appendQueryParameter("idcidade", String.valueOf(end.getCidade()));
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

                    result = sb.toString();

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

            mProgressDialog.dismiss();

            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

            Toast toast = Toast.makeText(EnderecoActivity.this, value, Toast.LENGTH_LONG);
            toast.show();

        }
    }

}
