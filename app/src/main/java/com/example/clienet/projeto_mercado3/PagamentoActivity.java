package com.example.clienet.projeto_mercado3;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
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

public class PagamentoActivity extends AppCompatActivity {

    Intent intent;
    Cliente cliente;
    InformacoesPagamento informacoesPagamento = new InformacoesPagamento();
    SQLiteDatabaseDao dao;
    SQLiteDatabase mDb;
    ProgressDialog mProgressDialog;
    ArrayList<HashMap<String, Object>> listData;
    ArrayList<HashMap<String, Object>> listdatasqlite;
    Carrinho carrinho = new Carrinho();
    Mercado mercado = new Mercado();
    WebView myWebView;
    final Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pagamento);

        intent = getIntent();
        Bundle bundle = intent.getExtras();
        cliente = (Cliente) bundle.getSerializable("cliente");

        getSupportActionBar().setSubtitle("Login: " + cliente.getEmail());

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        myWebView = findViewById(R.id.webview);

        myWebView.setWebViewClient(new WebViewClient());

        myWebView.getSettings().setJavaScriptEnabled(true);
        myWebView.getSettings().setLoadWithOverviewMode(true);
        myWebView.getSettings().setUseWideViewPort(true);

        myWebView.getSettings().setSupportZoom(true);
        myWebView.getSettings().setBuiltInZoomControls(true);
        myWebView.getSettings().setDisplayZoomControls(false);

        myWebView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        myWebView.setScrollbarFadingEnabled(false);

        final WebAppInterface webAppInterface = new WebAppInterface(this);

        myWebView.setWebChromeClient(new WebChromeClient());
        myWebView.addJavascriptInterface(webAppInterface,"android");

        setDesktopMode(myWebView, false);

        dao = new SQLiteDatabaseDao();

    }

    private class WebAppInterface {
        Context mContext;
        WebAppInterface(Context c) {
            mContext = c;
        }
        @JavascriptInterface
        public int showToast(String toast) {
            final String string = toast;
            handler.post(new Runnable() {
                @Override
                public void run() {
                    //Toast.makeText(mContext, string, Toast.LENGTH_SHORT).show();
                    /*Intent intent = new Intent(mContext, FinalActivity.class);

                    // 3. put person in intent data
                    intent.putExtra("cliente", cliente);
                    intent.putExtra("idpedido", informacoesPagamento.getIdpedido());

                    // 4. start the activity
                    startActivity(intent);*/
                }
            });
            return cliente.getIdpedido();
        }

        @JavascriptInterface
        public void showToast2(String toast) {
            final String string = toast;
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(mContext, string, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(mContext, FinalActivity.class);

                    // 3. put person in intent data
                    intent.putExtra("cliente", cliente);

                    // 4. start the activity
                    startActivity(intent);
                }
            });
        }
    }

    public void setDesktopMode(WebView webView,boolean enabled) {
        String newUserAgent = webView.getSettings().getUserAgentString();
        if (enabled) {
            try {
                String ua = webView.getSettings().getUserAgentString();
                String androidOSString = webView.getSettings().getUserAgentString().substring(ua.indexOf("("), ua.indexOf(")") + 1);
                newUserAgent = webView.getSettings().getUserAgentString().replace(androidOSString, "(X11; Linux x86_64)");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            newUserAgent = null;
        }

        webView.getSettings().setUserAgentString(newUserAgent);
        webView.getSettings().setUseWideViewPort(enabled);
        webView.getSettings().setLoadWithOverviewMode(enabled);
        webView.reload();
    }


    @Override
    public void onBackPressed() {

        Intent intent = new Intent(PagamentoActivity.this,  ListaProdutoPedidosActivity.class);

        intent.putExtra("cliente", cliente);

        startActivity(intent);

    }

    @Override
    public boolean onSupportNavigateUp() {

        if(cliente.getIdpedido()!=0) {
            Intent intent = new Intent(PagamentoActivity.this, ListaProdutoPedidosActivity.class);

            intent.putExtra("cliente", cliente);

            startActivity(intent);
        }else{
            Intent intent = new Intent(PagamentoActivity.this, ResumoActivity.class);

            intent.putExtra("cliente", cliente);

            startActivity(intent);
        }

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

                Intent intent = new Intent(PagamentoActivity.this, MainActivity.class);

                finishAffinity();

                startActivity(intent);

                return true;

            case R.id.seus_pedidos:

                Intent intent_pedidos = new Intent(PagamentoActivity.this, PedidosActivity.class);

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

            mProgressDialog = new ProgressDialog(PagamentoActivity.this);

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

                url = new URL(getString(R.string.ipnovo)+"pagamento");

                urlConnection = (HttpURLConnection) url.openConnection();

                //urlConnection.setReadTimeout(30000);//milisegundos
                urlConnection.setConnectTimeout(30000);
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

                    informacoesPagamento.setIdpedido(json_data.getInt("idpedido"));

                    json_data = json_data.getJSONObject("respostaPagSeguro");

                    informacoesPagamento.setCode(json_data.getString("code"));
                    informacoesPagamento.setDate(json_data.getString("date"));

                    result = String.valueOf(informacoesPagamento.getIdpedido());

                }else{

                    return("não obteve sucesso na transação");
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
                Toast.makeText(PagamentoActivity.this,"Numero do Pedido: "+value,Toast.LENGTH_LONG).show();
                cliente.setIdpedido(informacoesPagamento.getIdpedido());
                //pedido.setText("Seu pedido foi finalizado, Numero do pedido: "+value);
                Log.e("JSON", String.valueOf("Seu pedido foi finalizado, Numero do pedido AA: "+informacoesPagamento.getCode()));
            }

            Log.e("JSON", String.valueOf("aaaakkkkkkkkkaaaaaaaaaaaa"+value));

            mProgressDialog.dismiss();

            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

            myWebView.loadUrl("https://pagseguro.uol.com.br/v2/checkout/payment.html?code="+informacoesPagamento.getCode());

            //myWebView.loadUrl("https://pagseguro.uol.com.br/v2/checkout/payment.html?code="+"590E03E20D0D6747744BFF89A700A709");

            //myWebView.loadUrl(getString(R.string.ip)+"redirecionamento"+"?transaction_id=123");

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

            new PagamentoActivity.FetchSQL().execute();
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
