package com.example.clienet.projeto_mercado3;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

public class ListaProdutoPedidosActivity extends AppCompatActivity {

    Cliente cliente;
    Pedido pedido = new Pedido();
    LocalEntrega localEntrega = new LocalEntrega();
    ListView list;
    Intent intent;
    TextView numero;
    ArrayList<HashMap<String,Object>> listdata = new ArrayList<HashMap<String,Object>>();
    ProgressDialog mProgressDialog;
    Button informacoes_de_pagamento;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_produto_pedidos);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        intent = getIntent();
        cliente = (Cliente) intent.getSerializableExtra("cliente");

        getSupportActionBar().setSubtitle("Login: "+cliente.getEmail());

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setImageResource(R.drawable.ic_local_grocery_store_black);

        list = (ListView) findViewById(R.id.list_items);

        fab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(cliente.getQuantidade()>0) {
                    Intent intent = new Intent(ListaProdutoPedidosActivity.this, ListaCarrinhosActivity.class);

                    // 3. put person in intent data
                    intent.putExtra("cliente", cliente);

                    // 4. start the activity
                    startActivity(intent);
                }
            }
        });

        numero = (TextView) findViewById(R.id.numero);

        numero.setText(""+cliente.getQuantidade());

        new ListaProdutoPedidosActivity.FetchSQL().execute();

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

                Intent intent = new Intent(ListaProdutoPedidosActivity.this, MainActivity.class);

                finishAffinity();

                startActivity(intent);

                return true;

            case R.id.seus_pedidos:

                Intent intent_pedidos = new Intent(ListaProdutoPedidosActivity.this, PedidosActivity.class);

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

        Intent intent = new Intent(ListaProdutoPedidosActivity.this, PedidosActivity.class);

        intent.putExtra("cliente", cliente);

        startActivity(intent);

    }

    @Override
    public boolean onSupportNavigateUp(){
        Intent intent = new Intent(ListaProdutoPedidosActivity.this, PedidosActivity.class);

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

            mProgressDialog = new ProgressDialog(ListaProdutoPedidosActivity.this);

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

                url = new URL(getString(R.string.ipnovo)+"lista_produto_pedidos");

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
                        .appendQueryParameter("idpedidos_confirmados", String.valueOf(cliente.getIdpedido()));
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

                    Log.e("JSON", String.valueOf("bbbbbbbbbbbbbbb"+sb));

                    JSONObject json = new JSONObject(sb.toString());

                    JSONArray jsonArray = new JSONArray(json.getString("rows"));

                    for(int i=0; i<jsonArray.length(); i++){
                        JSONObject json_data = jsonArray.getJSONObject(i);

                        HashMap<String, Object> meMap = new HashMap<String, Object>();
                        meMap.put("produtos_idprodutos", json_data.getInt("produtos_idprodutos"));
                        meMap.put("idproduto_comprado", json_data.getInt("idproduto_comprado"));
                        meMap.put("quantidade", "Quantidade: " + json_data.getString("quantidade"));
                        meMap.put("pedidos_confirmados_idpedidos_confirmados", json_data.getInt("pedidos_confirmados_idpedidos_confirmados"));
                        meMap.put("nome", json_data.getString("nome"));
                        meMap.put("foto", getString(R.string.ip)+getString(R.string.endereco_imagem)+json_data.getString("foto"));
                        listdata.add(meMap);

                    }

                    jsonArray = new JSONArray(json.getString("rows2"));

                    for(int i=0; i<jsonArray.length(); i++){
                        JSONObject json_data = jsonArray.getJSONObject(i);

                        pedido.setIdpedido(json_data.getInt("idpedidos_confirmados"));
                        pedido.setIdstatus(json_data.getInt("idstatus"));
                        pedido.setStatus(json_data.getString("status_do_pedido"));
                        cliente.setIdmercado(json_data.getInt("mercado_idmercado"));
                        pedido.setValor(json_data.getString("valor"));
                        cliente.setIdendereco(json_data.getInt("endereco_idendereco"));
                        pedido.setNome_mercado(json_data.getString("nome_mercado"));
                        pedido.setData(json_data.getString("created_at"));

                    }

                    jsonArray = new JSONArray(json.getString("rows3"));

                    for(int i=0; i<jsonArray.length(); i++){
                        JSONObject json_data = jsonArray.getJSONObject(i);

                        pedido.setNome_cidade(json_data.getString("nome"));

                    }

                    jsonArray = new JSONArray(json.getString("rows4"));

                    for(int i=0; i<jsonArray.length(); i++){
                        JSONObject json_data = jsonArray.getJSONObject(i);

                        localEntrega.setIdentificacao(json_data.getString("identificacao"));
                        localEntrega.setEndereco(json_data.getString("endereco"));
                        localEntrega.setNumero(json_data.getString("numero"));
                        localEntrega.setComplemento(json_data.getString("complemento"));
                        localEntrega.setBairro(json_data.getString("bairro"));
                        localEntrega.setDestinatario(json_data.getString("nome_destinatario"));

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

            list = (ListView) findViewById(R.id.list_items);

            View header = getLayoutInflater().inflate(R.layout.produto_pedidos_header,null);

            if(pedido.getIdstatus()== 1) {
                ImageView img = (ImageView) header.findViewById(R.id.payment);
                img.setImageResource(R.mipmap.ic_launcher_payment);
            }else if(pedido.getIdstatus()== 2){
                ImageView img = (ImageView) header.findViewById(R.id.payment_accept);
                img.setImageResource(R.mipmap.ic_launcher_payment_accept);
            }else if(pedido.getIdstatus()== 3){
                ImageView img = (ImageView) header.findViewById(R.id.transport);
                img.setImageResource(R.mipmap.ic_launcher_transport);
            }else if(pedido.getIdstatus()== 4){
                ImageView img = (ImageView) header.findViewById(R.id.delivered);
                img.setImageResource(R.mipmap.ic_launcher_delivered);
            }



            ((TextView) header.findViewById(R.id.status)).setText(pedido.getStatus());

            ((TextView) header.findViewById(R.id.identificacao)).setText(localEntrega.getIdentificacao());
            ((TextView) header.findViewById(R.id.nome_destinatario)).setText("Destinátario: "+localEntrega.getDestinatario());
            ((TextView) header.findViewById(R.id.endereco)).setText("Endereço: "+localEntrega.getEndereco()+" "+localEntrega.getNumero()+", "+localEntrega.getComplemento());
            ((TextView) header.findViewById(R.id.bairro)).setText("Bairro: "+localEntrega.getBairro());
            ((TextView) header.findViewById(R.id.nome_cidade)).setText("Cidade: "+ pedido.getNome_cidade());
            ((TextView) header.findViewById(R.id.idpedido)).setText("Numero do pedido: "+pedido.getIdpedido());
            ((TextView) header.findViewById(R.id.valor)).setText("Valor da Compra: R$ "+pedido.getValor());
            ((TextView) header.findViewById(R.id.nome_mercado)).setText("Supermercado: "+pedido.getNome_mercado());
            ((TextView) header.findViewById(R.id.data)).setText("Data do pedido: "+pedido.getData());
            informacoes_de_pagamento = (Button) header.findViewById(R.id.informacoes_de_pagamento);

            informacoes_de_pagamento.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(ListaProdutoPedidosActivity.this, InformacoesPagamentoActivity.class);

                    intent.putExtra("cliente", cliente);

                    startActivity(intent);
                }
            });

            list.addHeaderView(header);

            ListAdapter adapter =
                    new MyAdapter6(
                            ListaProdutoPedidosActivity.this,
                            listdata,
                            R.layout.produto_pedidos,
                            new String[]{"nome","quantidade"},
                            new int[]{R.id.nome,R.id.quantidade},
                            intent);
            list.setAdapter(adapter);

            mProgressDialog.dismiss();

            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        }
    }

}
