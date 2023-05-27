package com.example.clienet.projeto_mercado3;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class MenuActivity extends AppCompatActivity {

    ImageButton button;
    ImageButton button2;
    ImageButton button3;
    ImageButton button4;
    Cliente cliente;
    TextView numero;
    String cidade;
    String estado;
    String pais;
    ProgressDialog mProgressDialog;
    private int idcidade = 1;

    private static final int MY_PERMISSION_REQUEST_FINE_LOCATION = 101;

    private FusedLocationProviderClient mFusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        button = (ImageButton) findViewById(R.id.irascompras);

        button2 = (ImageButton) findViewById(R.id.irascomprasusandominhalocalizacao);

        button3 = (ImageButton) findViewById(R.id.compararprecos);

        button4 = (ImageButton) findViewById(R.id.compararprecosusandominhalocalizacao);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        cliente = (Cliente) bundle.getSerializable("cliente");

        getSupportActionBar().setSubtitle("Login: " + cliente.getEmail());

        numero = (TextView) findViewById(R.id.numero);

        numero.setText("" + cliente.getQuantidade());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setImageResource(R.drawable.ic_local_grocery_store_black);

        fab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (cliente.getQuantidade() > 0) {
                    Intent intent = new Intent(MenuActivity.this, ListaCarrinhosActivity.class);

                    // 3. put person in intent data
                    intent.putExtra("cliente", cliente);

                    // 4. start the activity
                    startActivity(intent);
                }
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, MapsActivity.class);

                Bundle bundle = new Bundle();

                bundle.putSerializable("cliente", cliente);

                intent.putExtras(bundle);

                startActivity(intent);

                finish();
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast toast = Toast.makeText(MenuActivity.this, cidade, Toast.LENGTH_LONG);
                toast.show();

                new FetchSQL().execute();
            }
        });

        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, Maps2Activity.class);

                Bundle bundle = new Bundle();

                bundle.putSerializable("cliente", cliente);

                intent.putExtras(bundle);

                startActivity(intent);

                finish();
            }
        });

        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast toast = Toast.makeText(MenuActivity.this, cidade, Toast.LENGTH_LONG);
                toast.show();

                new FetchSQL2().execute();
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSION_REQUEST_FINE_LOCATION);
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(MenuActivity.this);

        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {

                            Geocoder gc = new Geocoder(MenuActivity.this);
                            double lat =  location.getLatitude();
                            double lng =  location.getLongitude();
                            List<Address> list = null;
                            try {
                                list = gc.getFromLocation(lat, lng, 1);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            Address add = list.get(0);
                            cidade=add.getLocality();
                            estado=add.getAdminArea();
                            pais=add.getCountryName();
                            cliente.setNome_cidade(cidade);

                        }
                    }
                });




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

                Intent intent = new Intent(MenuActivity.this, MainActivity.class);

                finishAffinity();

                startActivity(intent);

                return true;

            case R.id.seus_pedidos:

                Intent intent_pedidos = new Intent(MenuActivity.this, PedidosActivity.class);

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
    public void onBackPressed() {
        finishAffinity();
    }

    private class FetchSQL extends AsyncTask<Void, Void, Integer> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

            mProgressDialog = new ProgressDialog(MenuActivity.this);

            mProgressDialog.setTitle("Por Favor Aguarde");
            // Set progressdialog message
            mProgressDialog.setMessage("Loading...");
            mProgressDialog.setIndeterminate(false);
            // Show progressdialog
            mProgressDialog.show();

        }


        @Override
        protected Integer doInBackground(Void... params) {

            if(cidade!=null) {

                URL url;
                HttpURLConnection urlConnection = null;

                try {

                    url = new URL(getString(R.string.ipnovo)+"mapa");

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
                            .appendQueryParameter("nome_pais", pais)
                            .appendQueryParameter("nome_estado", estado)
                            .appendQueryParameter("nome_cidade", cidade);
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

                        Log.e("JSON", sb.toString());

                        idcidade = Integer.parseInt(sb.toString().replace(" ", ""));

                    }

                }
                catch(Exception e){
                    Log.e("JSON", String.valueOf("aaaaaaaaaaaaaaaa"));
                } finally {
                    if (urlConnection != null) {
                        urlConnection.disconnect();
                    }
                }

            }
            return idcidade;
        }
        @Override
        protected void onPostExecute(Integer value) {
            //

            cliente.setIdcidade(value);

            Intent intent = new Intent(MenuActivity.this, ListaMercadoActivity.class);

            // 3. put person in intent data
            intent.putExtra("cliente", cliente);

            mProgressDialog.dismiss();

            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

            // 4. start the activity
            startActivity(intent);

        }
    }

    private class FetchSQL2 extends AsyncTask<Void, Void, Integer> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

            mProgressDialog = new ProgressDialog(MenuActivity.this);

            mProgressDialog.setTitle("Por Favor Aguarde");
            // Set progressdialog message
            mProgressDialog.setMessage("Loading...");
            mProgressDialog.setIndeterminate(false);
            // Show progressdialog
            mProgressDialog.show();

        }


        @Override
        protected Integer doInBackground(Void... params) {

            if(cidade!=null) {

                URL url;
                HttpURLConnection urlConnection = null;

                try {

                    url = new URL(getString(R.string.ipnovo)+"mapa");

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
                            .appendQueryParameter("nome_pais", pais)
                            .appendQueryParameter("nome_estado", estado)
                            .appendQueryParameter("nome_cidade", cidade);
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

                        Log.e("JSON", sb.toString());

                        idcidade = Integer.parseInt(sb.toString().replace(" ", ""));

                    }

                }
                catch(Exception e){
                    Log.e("JSON", String.valueOf("aaaaaaaaaaaaaaaa"));
                } finally {
                    if (urlConnection != null) {
                        urlConnection.disconnect();
                    }
                }

            }
            return idcidade;
        }
        @Override
        protected void onPostExecute(Integer value) {
            //

            cliente.setIdcidade(value);

            Intent intent = new Intent(MenuActivity.this, CompararPrecosActivity.class);

            // 3. put person in intent data
            intent.putExtra("cliente", cliente);

            mProgressDialog.dismiss();

            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

            // 4. start the activity
            startActivity(intent);

        }
    }

}
