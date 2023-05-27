package com.example.clienet.projeto_mercado3;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    Button button;
    private String cidade = null;
    private String estado = null;
    private String pais = null;
    private int idcidade = 1;
    ProgressDialog mProgressDialog;
    Cliente cliente;

    //Cliente cliente = (Cliente) intent.getSerializableExtra("cliente");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        button = (Button) findViewById(R.id.button);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        cliente =(Cliente)bundle.getSerializable("cliente");

        getSupportActionBar().setSubtitle("Login: "+cliente.getEmail());

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new FetchSQL().execute();
                // Code here executes on main thread after user presses button
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setImageResource(R.drawable.ic_local_grocery_store_black);

        fab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MapsActivity.this, ListaCarrinhosActivity.class);

                // 3. put person in intent data
                intent.putExtra("cliente", cliente);

                // 4. start the activity
                startActivity(intent);
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
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        MenuItem register = menu.findItem(R.id.search);

        register.setVisible(false);

        return true;
    }

    @Override
    public void onBackPressed()
    {
        finishAffinity();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.logout:

                SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();

                editor.clear();
                editor.commit();

                Intent intent = new Intent(MapsActivity.this, MainActivity.class);

                finishAffinity();

                startActivity(intent);

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //@Override
    //public void onBackPressed() {
    //    onDestroy();
    //    finish();
    //}

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        final LatLng sydney = new LatLng(-27.0052, -51.1544);

        //mMap.addMarker(new MarkerOptions().position(sydney).title("Videira State of Santa Catarina Brazil"));

        // Add a marker in Sydney and move the camera

        //Location myLocation = mMap.getMyLocation();

        //LatLng myLatLng = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());

        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        final Marker marker = mMap.addMarker(new MarkerOptions().position(sydney).draggable(true));

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                idcidade=0;
                marker.setPosition(latLng);
                Geocoder gc = new Geocoder(MapsActivity.this);
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
                Geocoder gc = new Geocoder(MapsActivity.this);
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
                marker.setTitle(cidade+" "+estado+" "+pais);
                marker.showInfoWindow();

            }
        });

        /*if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);*/
    }

    private class FetchSQL extends AsyncTask<Void, Void, Integer> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            mProgressDialog = new ProgressDialog(MapsActivity.this);

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

                try {
                    Class.forName("com.mysql.jdbc.Driver");
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                String url = "jdbc:mysql://192.168.1.104/projeto_mercado1?user=root&password=spfc";
                Connection conn;
                try {
                    DriverManager.setLoginTimeout(5);
                    conn = DriverManager.getConnection(url);
                    Statement st = conn.createStatement();
                    String sqlpais, sqlestado, sqlcidade;

                    int idestado = 0;
                    int idpais = 0;

                    sqlpais = "SELECT idpais FROM pais WHERE nome='" + pais + "'";
                    ResultSet rs = st.executeQuery(sqlpais);
                    while (rs.next()) {
                        idpais = rs.getInt(1);
                    }

                    sqlestado = "SELECT idestado FROM estados WHERE nome='" + estado + "' OR nome='"+ "State of " + estado + "'AND pais_idpais='" + idpais + "'";
                    ResultSet rs2 = st.executeQuery(sqlestado);
                    while (rs2.next()) {
                        idestado = rs2.getInt(1);
                    }

                    sqlcidade = "SELECT idcidade FROM cidades WHERE nome='" + cidade + "' AND estado_idestado='" + idestado + "'";
                    ResultSet rs3 = st.executeQuery(sqlcidade);
                    while (rs3.next()) {
                        idcidade = rs3.getInt(1);
                    }

                    rs.close();
                    rs2.close();
                    rs3.close();
                    st.close();
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            return idcidade;
        }
        @Override
        protected void onPostExecute(Integer value) {
            //

            //Log.d(TAG, "Deu cerrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrto");
            //ir para próximo Activity
            cliente.setIdcidade(value);

            Intent intent = new Intent(MapsActivity.this, ListaMercadoActivity.class);

            // 3. put person in intent data
            intent.putExtra("cliente", cliente);

            mProgressDialog.dismiss();

            // 4. start the activity
            startActivity(intent);

        }
    }
}
