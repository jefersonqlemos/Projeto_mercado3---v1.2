package com.example.clienet.projeto_mercado3;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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

public class PagamentoCartaoDebitoActivity extends AppCompatActivity {

    Intent intent;
    Cliente cliente;
    Button continuar;

    SQLiteDatabaseDao dao;
    SQLiteDatabase mDb;
    ProgressDialog mProgressDialog;
    ArrayList<HashMap<String, Object>> listData;
    ArrayList<HashMap<String, Object>> listdatasqlite;
    Carrinho carrinho = new Carrinho();
    Mercado mercado = new Mercado();
    private InformacoesPagamento informacoesPagamento = new InformacoesPagamento();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pagamento_cartao_debito);

        intent = getIntent();
        Bundle bundle = intent.getExtras();
        cliente = (Cliente) bundle.getSerializable("cliente");

        getSupportActionBar().setSubtitle("Login: " + cliente.getEmail());

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        continuar = findViewById(R.id.continuar);

        continuar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //new Cielo().execute();

                dao = new SQLiteDatabaseDao();

                //Intent intent = new Intent(PagamentoCartaoDebitoActivity.this, FinalActivity.class);

                //intent.putExtra("cliente", cliente);

                //startActivity(intent);
            }
        });

    }

    @Override
    public void onBackPressed() {

        Intent intent = new Intent(PagamentoCartaoDebitoActivity.this, FormaPagamentoActivity.class);

        intent.putExtra("cliente", cliente);

        startActivity(intent);

    }

    @Override
    public boolean onSupportNavigateUp() {
        Intent intent = new Intent(PagamentoCartaoDebitoActivity.this, FormaPagamentoActivity.class);

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

                Intent intent = new Intent(PagamentoCartaoDebitoActivity.this, MainActivity.class);

                finishAffinity();

                startActivity(intent);

                return true;

            case R.id.seus_pedidos:

                Intent intent_pedidos = new Intent(PagamentoCartaoDebitoActivity.this, PedidosActivity.class);

                Bundle bundle = new Bundle();

                bundle.putSerializable("cliente", cliente);

                intent_pedidos.putExtras(bundle);

                startActivity(intent_pedidos);

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /*private class Cielo extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... voids) {

            // ...
            // Configure seu merchant
            Merchant merchant = new Merchant("MERCHANT ID", "MERCHANT KEY");

            // Crie uma instância de Sale informando o ID do pagamento
            Sale sale = new Sale("ID do pagamento");

            UpdateSaleResponse updateSaleResponse = new UpdateSaleResponse();

            // Crie uma instância de Customer informando o nome do cliente
            Customer customer = sale.customer("Comprador Teste");

            // Crie uma instância de Payment informando o valor do pagamento
            Payment payment = sale.payment(15700);

            // Crie  uma instância de Credit Card utilizando os dados de teste
            // esses dados estão disponíveis no manual de integração

            payment.setAuthenticate(true).debitCard("123", "Visa").setExpirationDate("12/2020")
                    .setCardNumber("0000000000000001")
                    .setHolder("Fulano de Tal");

            // Crie o pagamento na Cielo
            try {
                // Configure o SDK com seu merchant e o ambiente apropriado para criar a venda
                sale = new CieloEcommerce(merchant, Environment.SANDBOX).createSale(sale);

                // Com a venda criada na Cielo, já temos o ID do pagamento, TID e demais
                // dados retornados pela Cielo
                String paymentId = sale.getPayment().getPaymentId();

                // Com o ID do pagamento, podemos fazer sua captura, se ela não tiver sido capturada ainda
                updateSaleResponse = new CieloEcommerce(merchant, Environment.SANDBOX).captureSale(paymentId, 15700, 0);

                // E também podemos fazer seu cancelamento, se for o caso
                updateSaleResponse = new CieloEcommerce(merchant, Environment.SANDBOX).cancelSale(paymentId, 15700);

            } catch (ExecutionException | InterruptedException e) {
                // Como se trata de uma AsyncTask, será preciso tratar ExecutionException e InterruptedException
                e.printStackTrace();
            } catch (CieloRequestException e) {
                // Em caso de erros de integração, podemos tratar o erro aqui.
                // os códigos de erro estão todos disponíveis no manual de integração.
                CieloError error = e.getError();

                Log.v("Cielo", error.getCode().toString());
                Log.v("Cielo", error.getMessage());

                if (error.getCode() != 404) {
                    Log.e("Cielo", null, e);
                }
            }
            // ...
            return null;
        }
    }*/

    private class FetchSQL extends AsyncTask<Void,Void,String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = new ProgressDialog(PagamentoCartaoDebitoActivity.this);

            mProgressDialog.setTitle("Por Favor Aguarde");
            // Set progressdialog message
            mProgressDialog.setMessage("Loading...");
            mProgressDialog.setIndeterminate(false);
            // Show progressdialog
            mProgressDialog.show();
        }

        @Override
        protected String doInBackground(Void... params) {

            String result = null;

            listData = new ArrayList<HashMap<String, Object>>();

            ArrayList idprodutos = new ArrayList();
            ArrayList quantidades = new ArrayList();
            float subtotal_produtos = 0;
            int quantidade_total = 0;

            for(int i=0; i<listdatasqlite.size(); i++){
                idprodutos.add(listdatasqlite.get(i).get("idproduto"));
                quantidades.add(listdatasqlite.get(i).get("quantidade"));
            }

            URL url;
            HttpURLConnection urlConnection = null;

            try {

                url = new URL(getString(R.string.ipnovo)+"pagamento_cartao_debito");

                urlConnection = (HttpURLConnection) url.openConnection();

                urlConnection.setReadTimeout(15000);//milisegundos
                urlConnection.setConnectTimeout(15000);
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty ("Authorization", "Bearer "+cliente.getToken());
                urlConnection.setRequestProperty ("Content-Type", "application/x-www-form-urlencoded");
                urlConnection.setRequestProperty ("Accept", "application/json");
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);

                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("idprodutos", idprodutos.toString().replace("[", "").replace("]", ""))
                        .appendQueryParameter("quantidades", quantidades.toString().replace("[", "").replace("]", ""))
                        .appendQueryParameter("idendereco", String.valueOf(cliente.getIdendereco()))
                        .appendQueryParameter("idcliente_comprador", String.valueOf(cliente.getId()))
                        .appendQueryParameter("email", cliente.getEmail())
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

                Log.e("JSON", String.valueOf("Seu pedido foi finalizado, Numero do pedido: "+response_code));

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

                    Log.e("aaaastring builder",""+sb);

                    //JSONObject json = new JSONObject(sb.toString());

                    Log.e("idmercado", sb+"");

                    JSONArray jsonArray = new JSONArray(sb.toString());

                    JSONObject json_data = jsonArray.getJSONObject(0);

                    informacoesPagamento.setIdpedido(Integer.parseInt(json_data.getString("idpedido")));

                    result = json_data.getString("url");

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

            if(value!=null){
                Toast.makeText(PagamentoCartaoDebitoActivity.this,"Numero do Pedido: "+value,Toast.LENGTH_LONG).show();
                //pedido.setText("Seu pedido foi finalizado, Numero do pedido: "+value);
                Log.e("JSON", String.valueOf("Seu pedido foi finalizado, Numero do pedido: "+value));
            }

            Log.e("JSON", String.valueOf("aaaakkkkkkkkkaaaaaaaaaaaa"+value));

            mProgressDialog.dismiss();

            /////////////////////////////////////////////////////////////////////////
            //intent temporaria até q ative a funcionalidade do pagamento
            /*Intent intent = new Intent(PagamentoCartaoDebitoActivity.this, FinalActivity.class);

            // 3. put person in intent data
            intent.putExtra("cliente", cliente);
            intent.putExtra("idpedido", informacoesPagamento.getIdpedido());

            // 4. start the activity
            startActivity(intent);*/
        }
    }

    class SQLiteDatabaseDao {

        @SuppressLint("WrongConstant")
        public SQLiteDatabaseDao() {
            mDb = SQLiteDatabase.openDatabase("/data/data/com.example.clienet.projeto_mercado3/databases/Carrinhos.db", null, 0);

            getAllData("produto");

            new FetchSQL().execute();
        }

        public void getAllData(String table) {
            Cursor c = mDb.rawQuery("select * from " + table + " where mercado_idmercado='"+ cliente.getIdmercado() +"'", null);
            //int columnsSize = c.getColumnCount();
            listdatasqlite = new ArrayList<HashMap<String, Object>>();
            // ??????
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
}
