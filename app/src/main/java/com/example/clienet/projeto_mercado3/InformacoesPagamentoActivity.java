package com.example.clienet.projeto_mercado3;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
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

public class InformacoesPagamentoActivity extends AppCompatActivity {

    Intent intent;
    Cliente cliente;
    InformacoesPagamento informacoesPagamento = new InformacoesPagamento();
    WebView myWebView;
    final Handler handler = new Handler();
    ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_informacoes_pagamento);

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

        myWebView.setWebChromeClient(new WebChromeClient());

        setDesktopMode(myWebView, false);

        new InformacoesPagamentoActivity.FetchSQL().execute();
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

                Intent intent = new Intent(InformacoesPagamentoActivity.this, MainActivity.class);

                finishAffinity();

                startActivity(intent);

                return true;

            case R.id.seus_pedidos:

                Intent intent_pedidos = new Intent(InformacoesPagamentoActivity.this, PedidosActivity.class);

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

        Intent intent = new Intent(InformacoesPagamentoActivity.this, ListaProdutoPedidosActivity.class);

        intent.putExtra("cliente", cliente);

        startActivity(intent);

    }

    @Override
    public boolean onSupportNavigateUp(){
        Intent intent = new Intent(InformacoesPagamentoActivity.this, ListaProdutoPedidosActivity.class);

        intent.putExtra("cliente", cliente);

        startActivity(intent);

        return true;
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

    private class FetchSQL extends AsyncTask<Void,Void,String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

            mProgressDialog = new ProgressDialog(InformacoesPagamentoActivity.this);

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

                url = new URL(getString(R.string.ipnovo)+"informacoes_pagamento");

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

                    if(json.getString("rows")!="null"){

                        JSONObject json_row = new JSONObject(json.getString("rows"));

                        informacoesPagamento.setIdtransacao(json_row.getString("idtransacao"));
                        informacoesPagamento.setCode(json_row.getString("code"));

                    }else{

                        //json = new JSONObject(json.getString("code"));

                        //informacoesPagamento.setIdtransacao(json.getString("idtransacao"));
                        informacoesPagamento.setCode(json.getString("code"));

                    }

                }else{

                    return("unsuccessful");
                }

            }
            catch(Exception e){
                Log.e("JSON", String.valueOf("aaaaaaaaaaaaaaaa"));
                //Toast.makeText(InformacoesPagamentoActivity.this,"Numero do Pedido: "+informacoesPagamento.getCode(),Toast.LENGTH_LONG).show();
                //final WebAppInterface webAppInterface = new WebAppInterface(InformacoesPagamentoActivity.this);
                //myWebView.addJavascriptInterface(webAppInterface,"android");
                //myWebView.loadUrl("https://pagseguro.uol.com.br/v2/checkout/payment.html?code="+informacoesPagamento.getCode());

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

            if(informacoesPagamento.getIdtransacao()==null){
                //Toast.makeText(InformacoesPagamentoActivity.this,"Numero do Pedido: "+informacoesPagamento.getCode(),Toast.LENGTH_LONG).show();
                final WebAppInterface webAppInterface = new WebAppInterface(InformacoesPagamentoActivity.this);
                myWebView.addJavascriptInterface(webAppInterface,"android");
                myWebView.loadUrl("https://pagseguro.uol.com.br/v2/checkout/payment.html?code="+informacoesPagamento.getCode());
            }else{

                Intent intent = new Intent(InformacoesPagamentoActivity.this, InformacoesTransacaoActivity.class);

                // 3. put person in intent data
                intent.putExtra("cliente", cliente);

                // 4. start the activity
                startActivity(intent);
                //Toast.makeText(InformacoesPagamentoActivity.this,"Numero do Pedido: "+informacoesPagamento.getIdtransacao(),Toast.LENGTH_LONG).show();

            }

            mProgressDialog.dismiss();

            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        }
    }
}
