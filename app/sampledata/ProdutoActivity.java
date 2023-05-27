package com.example.clienet.projeto_mercado3;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ProdutoActivity extends AppCompatActivity {

    SQLiteDatabase mDb;
    SQLiteDatabaseDao dao;

    Cliente cliente;
    ImageView imageView;
    TextView nome;
    TextView preco;
    TextView descricao;
    TextView tipo_unidade;
    TextView tipo_unidade2;
    TextView unidade2;
    TextView quantidade_unidade2;
    TextView estoque;
    TextView falta_estoque;
    NumberPicker qty;
    TextView mercado;

    Produto pr = new Produto();

    private static final String TAG = ProdutoActivity.class.getSimpleName();

    ProgressDialog mProgressDialog;

    Button button;
    ImageButton imageButton;

    Intent intent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_produto);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        intent = getIntent();
        cliente = (Cliente) intent.getSerializableExtra("cliente");

        getSupportActionBar().setSubtitle("Login: "+cliente.getEmail());

        imageView = (ImageView) findViewById(R.id.imageView);
        nome = (TextView) findViewById(R.id.nome);
        preco = (TextView) findViewById(R.id.preco);
        descricao = (TextView) findViewById(R.id.descricao);
        tipo_unidade = (TextView) findViewById(R.id.tipo_unidade);
        tipo_unidade2 = (TextView) findViewById(R.id.tipo_unidade2);
        unidade2 = (TextView) findViewById(R.id.unidade2);
        quantidade_unidade2 = (TextView) findViewById(R.id.quantidade_unidade2);
        estoque = (TextView) findViewById(R.id.estoque);
        falta_estoque = (TextView) findViewById(R.id.falta_estoque);
        qty = (NumberPicker) findViewById(R.id.quantidade);
        button =(Button)findViewById(R.id.comprar);
        imageButton =(ImageButton)findViewById(R.id.imageButton);
        mercado = (TextView) findViewById(R.id.mercado);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //new SQLiteDatabaseDao(ProdutoActivity.this);

                dao = new SQLiteDatabaseDao();

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
                Intent intent = new Intent(ProdutoActivity.this, ListaCarrinhosActivity.class);

                // 3. put person in intent data
                intent.putExtra("cliente", cliente);

                // 4. start the activity
                startActivity(intent);
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

        }

        // 创建一个数据库
        public void createTable(SQLiteDatabase mDb, String table) {
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
                        + "mercado_idmercado integer)");
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
            values.put("nome", cliente.getNome_mercado());
            values.put("bairro", cliente.getBairro_mercado());
            mDb.insert(table, null, values);

        }

        public void insertProduto(SQLiteDatabase sqLiteDatabase, String table) {
            ContentValues values = new ContentValues();
            values.put("idproduto", cliente.getIdproduto());
            values.put("quantidade", qty.getValue());
            values.put("mercado_idmercado", cliente.getIdmercado());
            sqLiteDatabase.insert(table, null, values);
        }

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
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.logout:

                SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();

                editor.clear();
                editor.commit();

                Intent intent = new Intent(ProdutoActivity.this, MainActivity.class);

                finishAffinity();

                startActivity(intent);

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private class FetchSQL extends AsyncTask<Void,Void,String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
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
            String imagem = "";

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
                String sql;
                sql = "SELECT * FROM produtos WHERE idproduto='"+cliente.getIdproduto()+"'";
                ResultSet rs = st.executeQuery(sql);
                while(rs.next()) {
                    pr.setPreco(rs.getFloat(2));
                    pr.setNome(rs.getString(3));
                    pr.setMarca(rs.getString(4));
                    pr.setTipo_unidade(rs.getString(5));
                    pr.setUnidade(rs.getString(6));
                    pr.setEstoque(rs.getInt(7));
                    pr.setDescricao(rs.getString(8));
                    pr.setFoto(rs.getString(9));
                    pr.setQuantidade_unidade(rs.getFloat(12));
                    pr.setTipo_unidade2(rs.getString(15));
                    pr.setUnidade2(rs.getString(16));
                    pr.setQuantidade_unidade2(rs.getFloat(17));
                }

                rs.close();
                st.close();
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            return imagem;
        }
        @Override
        protected void onPostExecute(String value) {
            //

            Picasso.with(ProdutoActivity.this).load(cliente.getFoto_mercado()).into(imageButton);
            mercado.setText(cliente.getNome_mercado());

            nome.setText(pr.getNome()+" "+pr.getMarca()+" "+pr.getQuantidade_unidade()+" "+pr.getUnidade());
            preco.setText("R$"+pr.getPreco());
            descricao.setText("Descrição: "+pr.getDescricao());
            tipo_unidade.setText("Tipo unidade: "+pr.getTipo_unidade());
            tipo_unidade2.setText("Tipo unidade: "+pr.getTipo_unidade2());
            unidade2.setText("Unidade: "+pr.getTipo_unidade2());
            quantidade_unidade2.setText("Qty unidade: "+pr.getQuantidade_unidade2());
            estoque.setText("Em Estoque: "+pr.getEstoque());

            if(pr.getEstoque()==0){
                button.setVisibility(View.INVISIBLE);
                falta_estoque.setText("O produto que você escolheu não está no estoque agora");
            }

            qty.setMinValue(0);
            qty.setMaxValue(pr.getEstoque());

            Picasso.with(ProdutoActivity.this).load("http://192.168.1.104"+pr.getFoto()).into(imageView);

            mProgressDialog.dismiss();

        }
    }

}
