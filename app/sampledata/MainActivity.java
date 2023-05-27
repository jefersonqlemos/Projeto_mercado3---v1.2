package com.example.clienet.projeto_mercado3;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import main.java.org.mindrot.jbcrypt.BCrypt;

public class MainActivity extends AppCompatActivity {

    EditText editText;
    EditText editText2;
    Button button;
    ProgressDialog mProgressDialog;
    private int id=0;

    SharedPreferences pref;
    SharedPreferences.Editor editor;

    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText   = (EditText)findViewById(R.id.editText);
        editText2   = (EditText)findViewById(R.id.editText2);
        button = (Button) findViewById(R.id.button);

        TextView cc = (TextView) findViewById(R.id.criar_conta);
        cc.setMovementMethod(LinkMovementMethod.getInstance());

        TextView es = (TextView) findViewById(R.id.esqueci_senha);
        es.setMovementMethod(LinkMovementMethod.getInstance());

        pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        editor = pref.edit();

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new FetchSQL().execute();
                // Code here executes on main thread after user presses button
            }
        });

        if(pref.getInt("id", 0)!=0) {

            //Log.d(TAG, "Deu cerrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrto");
            //ir para próximo Activity

            Intent intent = new Intent(MainActivity.this, MapsActivity.class);

            // 2. create person object
            Cliente cliente = new Cliente();

            cliente.setId(pref.getInt("id", 0));
            cliente.setEmail(pref.getString("email", null));

            // 3. put person in intent data
            intent.putExtra("cliente", cliente);

            // 4. start the activity
            startActivity(intent);

            finish();
            System.exit(0);

        }

    }



    @Override
    public void onBackPressed() {
        finish();
        System.exit(0);
    }

    private class FetchSQL extends AsyncTask<Void,Void,String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            mProgressDialog = new ProgressDialog(MainActivity.this);

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
            String email=editText.getText().toString();
            String senha=editText2.getText().toString();

            try {

                url = new URL("http://192.168.1.103/login");

                urlConnection = (HttpURLConnection) url.openConnection();

                urlConnection.setReadTimeout(15000 /* milliseconds */);
                urlConnection.setConnectTimeout(15000 /* milliseconds */);
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);

                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("email", email)
                        .appendQueryParameter("senha", senha);
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
                Log.e("JSON2", String.valueOf("aaaaaaaaaaaaaaaa2"));
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

                    Log.e("JSON", String.valueOf("bbbbbbbbbbbbbbbbbbbbbbbbbbbbb"+sb.toString()));

                    if(sb.toString()!="0") {

                        Cliente cliente = new Cliente();

                        cliente.setId(id);

                        cliente.setEmail(editText.getText().toString());

                        editor.putInt("id", Integer.parseInt(sb.toString()));

                        editor.putString("email", editText.getText().toString());

                        editor.commit();

                        Intent intent = new Intent(MainActivity.this, MapsActivity.class);

                        Bundle bundle = new Bundle();

                        bundle.putSerializable("cliente", cliente);

                        intent.putExtras(bundle);

                        mProgressDialog.dismiss();

                        startActivity(intent);

                        finish();

                        System.exit(0);

                    }else{
                        mProgressDialog.dismiss();

                        Toast toast = Toast.makeText(MainActivity.this, "Você errou seu e-mail ou senha", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                    //JSONObject json = new JSONObject(sb.toString());

                    /*JSONArray jsonArray = new JSONArray(json.getString("rows"));

                    for(int i=0; i<jsonArray.length(); i++){
                        JSONObject json_data = jsonArray.getJSONObject(i);

                        Log.i("log_tag", "id mercado" + json_data.getInt("idmercado") +
                                ", nome " + json_data.getString("nome") +
                                ", preco entrega" + json_data.getString("preco_entrega")
                        );
                    }*/

                    //Log.e("JSON", String.valueOf("bbbbbbbbbbbbbbbbbbbbbbbbbbbbb10"));

                }else{
                    return("Deu merda");
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

            /*
            String senha = null;

            try {
                Class.forName("com.mysql.jdbc.Driver");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                senha = e.toString();
            }
            String url = "jdbc:mysql://192.168.1.104/projeto_mercado1?user=root&password=spfc";
            Connection conn;
            try {
                DriverManager.setLoginTimeout(5);
                conn = DriverManager.getConnection(url);
                Statement st = conn.createStatement();
                String sql;
                sql = "SELECT id, password FROM users WHERE email='"+editText.getText().toString()+"'";
                ResultSet rs = st.executeQuery(sql);

                while(rs.next()) {
                    id =  rs.getInt(1);
                    senha =  rs.getString(2);
                }

                rs.close();
                st.close();
                conn.close();
            } catch (Exception e) {
                e.printStackTrace();
                mProgressDialog.dismiss();
                Toast toast = Toast.makeText(MainActivity.this, "Você não esta conectado a rede ou nosso servidor ", Toast.LENGTH_SHORT);
                toast.show();
            }

            return senha;
            */
        }
        @Override
        protected void onPostExecute(String value) {
            //
            /*if(value!=null) {

                if (BCrypt.checkpw(editText2.getText().toString(), value)) {

                    //Log.d(TAG, "Deu cerrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrto");
                    //ir para próximo Activity

                    Cliente cliente = new Cliente();

                    cliente.setId(id);

                    cliente.setEmail(editText.getText().toString());

                    editor.putInt("id", id);

                    editor.putString("email", editText.getText().toString());

                    editor.commit();

                    Intent intent = new Intent(MainActivity.this, MapsActivity.class);

                    Bundle bundle = new Bundle();

                    bundle.putSerializable("cliente", cliente);

                    intent.putExtras(bundle);

                    mProgressDialog.dismiss();

                    startActivity(intent);

                    finish();

                    System.exit(0);


                } else {
                    //mensagem de erro

                    mProgressDialog.dismiss();

                    Toast toast = Toast.makeText(MainActivity.this, "Você errou sua senha", Toast.LENGTH_SHORT);
                    toast.show();

                }
            }else {

                mProgressDialog.dismiss();
                Toast toast = Toast.makeText(MainActivity.this, "Você errou seu email ", Toast.LENGTH_SHORT);
                toast.show();
            }
            */
        }
    }
}
