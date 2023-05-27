package com.example.clienet.projeto_mercado3;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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

public class MainActivity extends AppCompatActivity {

    private int id = 0;
    private String email = null;
    private String token = null;

    Cliente cliente = new Cliente();

    SQLiteDatabase mDb;
    SQLiteDatabaseDao dao;

    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences pref;
        SharedPreferences.Editor editor;

        pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        editor = pref.edit();

        if (pref.getString("email", null) != null) {

            email = pref.getString("email", null);
            token = pref.getString("token", null);

            new FetchSQL().execute();

        } else {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        }

    }


    @Override
    public void onBackPressed() {
        finish();
        System.exit(0);
    }

    private class FetchSQL extends AsyncTask<Void, Void, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(Void... params) {

            URL url;
            HttpURLConnection urlConnection = null;
            String result = null;

            try {

                url = new URL(getString(R.string.ipnovo) + "main");

                urlConnection = (HttpURLConnection) url.openConnection();

                urlConnection.setReadTimeout(30000 /* milliseconds */);
                urlConnection.setConnectTimeout(30000 /* milliseconds */);
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty ("Authorization", "Bearer "+token);
                urlConnection.setRequestProperty ("Content-Type", "application/x-www-form-urlencoded");
                urlConnection.setRequestProperty ("Accept", "application/json");
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);

                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("email", email);
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

                    Log.e("JSON", String.valueOf("bbbbbbbbbbbbbbbbbbbbbbbbbbbbb" + sb.toString()));

                    JSONObject json = new JSONObject(sb.toString());

                    json = new JSONObject(json.getString("iduser"));

                    result = json.getString("iduser");

                }

            } catch (Exception e) {
                e.printStackTrace();
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

            if (value != null) {

                if (Integer.parseInt(value) != 0) {

                    cliente.setId(Integer.parseInt(value));

                    cliente.setEmail(email);

                    cliente.setToken(token);

                    dao = new SQLiteDatabaseDao();

                    Intent intent = new Intent(MainActivity.this, MenuActivity.class);

                    Bundle bundle = new Bundle();

                    bundle.putSerializable("cliente", cliente);

                    intent.putExtras(bundle);

                    startActivity(intent);

                    finish();

                    System.exit(0);

                }

            } else {

                Toast toast = Toast.makeText(MainActivity.this, "Erro de conexão ou autenticação", Toast.LENGTH_SHORT);
                toast.show();
            }

        }
    }

    class SQLiteDatabaseDao {

        @SuppressLint("WrongConstant")
        public SQLiteDatabaseDao() {
            mDb = openOrCreateDatabase("Carrinhos.db",
                    SQLiteDatabase.CREATE_IF_NECESSARY, null);
            // 初始化创建表
            createTable(mDb, "mercado");
            // 初始化插入数据
            getQuantidade("produto");

        }

        // 创建一个数据库
        public void createTable(SQLiteDatabase mDb, String table) {

            try {
                mDb.execSQL("PRAGMA foreign_keys=ON");
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Ocorreu 1 erro",
                        Toast.LENGTH_LONG).show();
            }

            try {
                mDb.execSQL("create table if not exists "
                        + "mercado"
                        + " (id integer primary key autoincrement, "
                        + "idmercado integer unique, "
                        + "nome text not null, "
                        + "bairro text not null)");
                //Toast.makeText(getApplicationContext(), "Produto Adicionado ao carrinho",
                //        Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Ocorreu 1 erro",
                        Toast.LENGTH_LONG).show();
            }

            try {
                mDb.execSQL("create table if not exists "
                        + "produto"
                        + "(id integer primary key autoincrement, "
                        + "idproduto integer unique, "
                        + "quantidade integer,"
                        + "mercado_idmercado integer,"
                        + "foreign key (mercado_idmercado) references mercado(idmercado) on delete cascade)");
                Toast.makeText(getApplicationContext(), "Produto Adicionado ao Carrinho",
                        Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Ocorreu um erro",
                        Toast.LENGTH_LONG).show();
            }

        }

        public void getQuantidade(String table) {
            Cursor c = mDb.rawQuery("select quantidade from " + table, null);
            //int columnsSize = c.getColumnCount();
            // 获取表的内容
            int quantidade=0;

            while (c.moveToNext()) {
                //for (int i = 0; i < columnsSize; i++) {
                quantidade = quantidade + c.getInt(0);

            }

            cliente.setQuantidade(quantidade);
        }
    }

}
