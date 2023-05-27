package com.example.clienet.projeto_mercado3;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.ScrollView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

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

public class ProdutoActivity extends AppCompatActivity {

    SQLiteDatabase mDb;
    SQLiteDatabaseDao dao;

    Cliente cliente;
    ImageView imageView;
    TextView nome;
    TextView preco;
    TextView descricao;
    TextView tipo_unidade;
    //TextView tipo_unidade2;
    //TextView unidade2;
    //TextView quantidade_unidade2;
    TextView estoque;
    TextView falta_estoque;
    NumberPicker qty;
    TextView mercadotv;

    Mercado mercado = new Mercado();

    ArrayList<HashMap<String,Object>> listdata = new ArrayList<HashMap<String,Object>>();

    Produto pr = new Produto();

    private static final String TAG = ProdutoActivity.class.getSimpleName();

    ProgressDialog mProgressDialog;

    Button button;
    ImageButton imageButton;

    Intent intent;

    TextView numero;

    ListView list;

    ScrollView sv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_produto);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        intent = getIntent();
        cliente = (Cliente) intent.getSerializableExtra("cliente");

        getSupportActionBar().setSubtitle("Login: "+cliente.getEmail());

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        imageView = (ImageView) findViewById(R.id.imageView);
        nome = (TextView) findViewById(R.id.nome);
        preco = (TextView) findViewById(R.id.preco);
        descricao = (TextView) findViewById(R.id.descricao);
        tipo_unidade = (TextView) findViewById(R.id.tipo_unidade);
        //tipo_unidade2 = (TextView) findViewById(R.id.tipo_unidade2);
        //unidade2 = (TextView) findViewById(R.id.unidade2);
        //quantidade_unidade2 = (TextView) findViewById(R.id.quantidade_unidade2);
        estoque = (TextView) findViewById(R.id.estoque);
        falta_estoque = (TextView) findViewById(R.id.falta_estoque);
        qty = (NumberPicker) findViewById(R.id.quantidade);
        button =(Button)findViewById(R.id.comprar);
        imageButton =(ImageButton)findViewById(R.id.imageButton);
        mercadotv = (TextView) findViewById(R.id.mercado);

        numero = (TextView) findViewById(R.id.numero);

        numero.setText(""+cliente.getQuantidade());

        list = (ListView) findViewById(R.id.comentarios);

        sv = (ScrollView) findViewById(R.id.scrollView);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //new SQLiteDatabaseDao(ProdutoActivity.this);

                dao = new SQLiteDatabaseDao();

                numero.setText(cliente.getQuantidade()+"");

            }
        });

        imageButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProdutoActivity.this, MercadoActivity.class);

                // 3. put person in intent data
                intent.putExtra("cliente", cliente);

                // 4. start the activity
                startActivity(intent);

            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setImageResource(R.drawable.ic_local_grocery_store_black);

        fab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(cliente.getQuantidade()>0) {
                    Intent intent = new Intent(ProdutoActivity.this, ListaCarrinhosActivity.class);

                    // 3. put person in intent data
                    intent.putExtra("cliente", cliente);

                    // 4. start the activity
                    startActivity(intent);
                }
            }
        });

        new ProdutoActivity.FetchSQL().execute();
    }

    @Override
    public void onBackPressed()
    {

        Intent intent = new Intent(ProdutoActivity.this, ListaProdutoActivity.class);

        intent.putExtra("cliente", cliente);

        startActivity(intent);

    }

    @Override
    public boolean onSupportNavigateUp(){
        Intent intent = new Intent(ProdutoActivity.this, ListaProdutoActivity.class);

        intent.putExtra("cliente", cliente);

        startActivity(intent);

        return true;
    }

    class SQLiteDatabaseDao {

        @SuppressLint("WrongConstant")
        public SQLiteDatabaseDao() {
            mDb = openOrCreateDatabase("Carrinhos.db",
                    SQLiteDatabase.CREATE_IF_NECESSARY, null);
            // 初始化创建表
            createTable(mDb, "mercado");
            // 初始化插入数据
            insertMercado(mDb, "mercado");
            // 初始化获取所有数据表数据
            insertProduto(mDb, "produto");

            getQuantidade("produto");

        }

        // 创建一个数据库
        public void createTable(SQLiteDatabase mDb, String table) {

            try {
                mDb.execSQL("PRAGMA foreign_keys=ON");
            }catch (Exception e) {
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

        public void insertMercado(SQLiteDatabase mDb, String table) {
            ContentValues values = new ContentValues();
            values.put("idmercado", cliente.getIdmercado());
            values.put("nome", mercado.getNome_mercado());
            values.put("bairro", mercado.getBairro_mercado());
            mDb.insert(table, null, values);

        }

        public void insertProduto(SQLiteDatabase sqLiteDatabase, String table) {
            ContentValues values = new ContentValues();
            values.put("idproduto", cliente.getIdproduto());
            values.put("quantidade", qty.getValue());
            values.put("mercado_idmercado", cliente.getIdmercado());
            int id = (int) sqLiteDatabase.insertWithOnConflict(table, null, values, SQLiteDatabase.CONFLICT_REPLACE);
            if(id==0){
                sqLiteDatabase.update(table, values, null,null);
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

                Intent intent = new Intent(ProdutoActivity.this, MainActivity.class);

                finishAffinity();

                startActivity(intent);

                return true;

            case R.id.seus_pedidos:

                Intent intent_pedidos = new Intent(ProdutoActivity.this, PedidosActivity.class);

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

            mProgressDialog = new ProgressDialog(ProdutoActivity.this);

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

                url = new URL(getString(R.string.ipnovo)+"produto");

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
                        .appendQueryParameter("idmercado", String.valueOf(cliente.getIdmercado()))
                        .appendQueryParameter("idproduto", String.valueOf(cliente.getIdproduto()));
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

                    Log.e("JSON", "ddddddddddddddddddd"+sb.toString());

                    JSONObject json = new JSONObject(sb.toString());

                    JSONArray jsonArray = new JSONArray(json.getString("rows"));

                    for(int i=0; i<jsonArray.length(); i++) {

                        JSONObject json_data = jsonArray.getJSONObject(i);

                        pr.setPreco(Float.parseFloat(json_data.getString("preco")));
                        pr.setNome(json_data.getString("nome"));
                        pr.setMarca(json_data.getString("marca"));
                        pr.setTipo_unidade(json_data.getString("tipo_unidade"));
                        pr.setUnidade(json_data.getString("unidade"));
                        pr.setEstoque(json_data.getInt("estoque"));
                        pr.setDescricao(json_data.getString("descricao"));
                        pr.setFoto(json_data.getString("foto"));
                        pr.setQuantidade_unidade(Float.parseFloat(json_data.getString("quantidade_unidade")));
                        //pr.setTipo_unidade2(json_data.getString("tipo_unidade2"));
                        //pr.setUnidade2(json_data.getString("unidade2"));
                        //pr.setQuantidade_unidade2(Float.parseFloat(json_data.getString("quantidade_unidade2")));

                    }

                    Log.e("a","aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa1"+mercado.getFoto_mercado());

                    jsonArray = new JSONArray(json.getString("rows2"));

                    for(int i=0; i<jsonArray.length(); i++) {
                        JSONObject json_data = jsonArray.getJSONObject(i);
                        mercado.setNome_mercado(json_data.getString("nome"));
                        mercado.setFoto_mercado(json_data.getString("foto"));
                        mercado.setBairro_mercado(json_data.getString("bairro"));
                    }

                    jsonArray = new JSONArray(json.getString("rows3"));

                    for(int i=0; i<jsonArray.length(); i++){
                        JSONObject json_data = jsonArray.getJSONObject(i);
                        HashMap<String, Object> meMap = new HashMap<String, Object>();
                        meMap.put("nome_cliente_comprador", json_data.getString("nome_cliente_comprador"));
                        meMap.put("comentario", json_data.getString("comentarios"));
                        meMap.put("data", json_data.getString("created_at"));
                        listdata.add(meMap);
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

            Log.e("a","aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"+mercado.getFoto_mercado());

            Picasso.with(ProdutoActivity.this).load(getString(R.string.ip)+getString(R.string.endereco_imagem)+mercado.getFoto_mercado()).into(imageButton);
            mercadotv.setText(mercado.getNome_mercado());

            nome.setText(pr.getNome()+" "+pr.getMarca()+" "+pr.getQuantidade_unidade()+" "+pr.getUnidade());
            preco.setText("R$"+pr.getPreco());
            descricao.setText("Descrição: "+pr.getDescricao());
            tipo_unidade.setText("Tipo unidade: "+pr.getTipo_unidade());
            //tipo_unidade2.setText("Tipo unidade: "+pr.getTipo_unidade2());
            //unidade2.setText("Unidade: "+pr.getUnidade2());
            //quantidade_unidade2.setText("Qty unidade: "+pr.getQuantidade_unidade2());
            estoque.setText("Em Estoque: "+pr.getEstoque());

            if(pr.getEstoque()==0){
                button.setVisibility(View.GONE);
                falta_estoque.setText("O produto que você escolheu não está no estoque agora");
                qty.setMinValue(0);
            }else{
                button.setVisibility(View.VISIBLE);
                qty.setMinValue(1);
            }

            qty.setMaxValue(pr.getEstoque());

            Picasso.with(ProdutoActivity.this).load(getString(R.string.ip)+getString(R.string.endereco_imagem)+pr.getFoto()).into(imageView);

            list = (ListView) findViewById(R.id.comentarios);

            ListAdapter adapter =
                    new SimpleAdapter(
                            ProdutoActivity.this,
                            listdata,
                            R.layout.comentario,
                            new String[]{"nome_cliente_comprador","comentario", "data"},
                            new int[]{R.id.nome_cliente, R.id.comentario, R.id.data});
            list.setAdapter(adapter);

            mProgressDialog.dismiss();

            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

            sv.smoothScrollTo(0, 0);

        }
    }

}
