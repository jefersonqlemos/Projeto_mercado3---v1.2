package com.example.clienet.projeto_mercado3;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

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

public class Maps2Activity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    Button button;
    private String cidade = null;
    private String estado = null;
    private String pais = null;
    private int idcidade = 1;
    ProgressDialog mProgressDialog;
    Cliente cliente;
    TextView numero;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps2);

        button = (Button) findViewById(R.id.button);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        cliente =(Cliente)bundle.getSerializable("cliente");

        numero = (TextView) findViewById(R.id.numero);

        numero.setText(""+cliente.getQuantidade());

        getSupportActionBar().setSubtitle("Login: "+cliente.getEmail());

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new Maps2Activity.FetchSQL().execute();
                // Code here executes on main thread after user presses button
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setImageResource(R.drawable.ic_local_grocery_store_black);

        fab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(cliente.getQuantidade()>0) {
                    Intent intent = new Intent(Maps2Activity.this, CompararPrecosActivity.class);

                    // 3. put person in intent data
                    intent.putExtra("cliente", cliente);

                    // 4. start the activity
                    startActivity(intent);
                }
            }
        });

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public void onBackPressed()
    {
        Intent intent = new Intent(Maps2Activity.this, MenuActivity.class);

        intent.putExtra("cliente", cliente);

        startActivity(intent);
    }

    @Override
    public boolean onSupportNavigateUp(){
        Intent intent = new Intent(Maps2Activity.this, MenuActivity.class);

        intent.putExtra("cliente", cliente);

        startActivity(intent);

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

                Intent intent = new Intent(Maps2Activity.this, MainActivity.class);

                finishAffinity();

                startActivity(intent);

                return true;

            case R.id.seus_pedidos:

                Intent intent_pedidos = new Intent(Maps2Activity.this, PedidosActivity.class);

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
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        final LatLng sydney = new LatLng(-27.0052, -51.1544);

        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        final Marker marker = mMap.addMarker(new MarkerOptions().position(sydney).draggable(true));

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                idcidade=0;
                marker.setPosition(latLng);
                Geocoder gc = new Geocoder(Maps2Activity.this);
                double lat = latLng.latitude;
                double lng = latLng.longitude;
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

                marker.setTitle(cidade+" "+estado+" "+pais);
                marker.showInfoWindow();
            }
        });

        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {

            }

            @Override
            public void onMarkerDrag(Marker marker) {

            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                idcidade=0;
                Geocoder gc = new Geocoder(Maps2Activity.this);
                LatLng ll = marker.getPosition();
                double lat = ll.latitude;
                double lng = ll.longitude;
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

                marker.setTitle(cidade+" "+estado+" "+pais);
                marker.showInfoWindow();

            }
        });

    }

    private class FetchSQL extends AsyncTask<Void, Void, Integer> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

            mProgressDialog = new ProgressDialog(Maps2Activity.this);

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

            Intent intent = new Intent(Maps2Activity.this, CompararPrecosActivity.class);

            // 3. put person in intent data
            intent.putExtra("cliente", cliente);

            mProgressDialog.dismiss();

            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

            // 4. start the activity
            startActivity(intent);

        }
    }
}
