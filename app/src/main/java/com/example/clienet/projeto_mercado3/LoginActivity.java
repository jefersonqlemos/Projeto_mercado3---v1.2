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
import android.view.WindowManager;
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

public class LoginActivity extends AppCompatActivity {

    EditText editText;
    EditText editText2;
    Button button;
    ProgressDialog mProgressDialog;
    private String email = null;
    private String token = null;
    SharedPreferences pref;
    SharedPreferences.Editor editor;

    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

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
                email = editText.getText().toString();
                new FetchSQL().execute();
                // Code here executes on main thread after user presses button
            }
        });

        /*if(pref.getString("token", null)!=null) {

            token = pref.getString("token", null);
            email = pref.getString("email", null);

            new FetchSQL().execute();

        }*/

    }



    @Override
    public void onBackPressed()
    {
        finishAffinity();
    }

    private class FetchSQL extends AsyncTask<Void,Void,String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

            mProgressDialog = new ProgressDialog(LoginActivity.this);

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

                url = new URL(getString(R.string.ipnovo)+"login");

                urlConnection = (HttpURLConnection) url.openConnection();

                urlConnection.setReadTimeout(30000 /* milliseconds */);
                urlConnection.setConnectTimeout(30000 /* milliseconds */);
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);

                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("email", email)
                        .appendQueryParameter("password", editText2.getText().toString());
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

                    Log.e("JSON", String.valueOf("bbbbbbbbbbbbbbbbbbbbbbbbbbbbb"+sb.toString()));

                    JSONObject json = new JSONObject(sb.toString());

                    /*JSONArray jsonArray = new JSONArray(json.getString("rows"));

                    for(int i=0; i<jsonArray.length(); i++) {
                        JSONObject json_data = jsonArray.getJSONObject(i);
                        result =  json_data.getString("iduser");
                    }*/

                    token = json.getString("access_token");

                    json = new JSONObject(json.getString("iduser"));

                    result = json.getString("iduser");

                }

            }
            catch(Exception e){
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

            if(value!=null) {

                if(token!=null) {

                    Cliente cliente = new Cliente();

                    cliente.setId(Integer.parseInt(value));

                    cliente.setToken(token);

                    cliente.setEmail(email);

                    cliente.setQuantidade(pref.getInt("quantidade", 0));

                    if (pref.getString("email", null) == null) {

                        editor.putString("email", email);

                        editor.putString("token", token);

                        editor.commit();
                    }

                    Intent intent = new Intent(LoginActivity.this, MenuActivity.class);

                    Bundle bundle = new Bundle();

                    bundle.putSerializable("cliente", cliente);

                    intent.putExtras(bundle);

                    mProgressDialog.dismiss();

                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                    Toast toast = Toast.makeText(LoginActivity.this, "Erro de conexão ou autenticação", Toast.LENGTH_SHORT);
                    toast.show();

                    startActivity(intent);

                    finish();

                    System.exit(0);
                }

            }else{
                mProgressDialog.dismiss();

                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                Toast toast = Toast.makeText(LoginActivity.this, "Erro de conexão ou autenticação", Toast.LENGTH_SHORT);
                toast.show();
            }

        }
    }
}


