package com.example.clienet.projeto_mercado3;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class FormaPagamentoActivity extends AppCompatActivity {

    Button continuar;

    Cliente cliente;

    Intent intent;

    RadioGroup radioGroup;

    RadioButton radioButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forma_pagamento);

        intent = getIntent();
        Bundle bundle = intent.getExtras();
        cliente = (Cliente)bundle.getSerializable("cliente");

        getSupportActionBar().setSubtitle("Login: "+cliente.getEmail());

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        radioGroup = findViewById(R.id.radioGroup);

        continuar = findViewById(R.id.continuar);

        continuar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int selectedId = radioGroup.getCheckedRadioButtonId();

                radioButton = (RadioButton) findViewById(selectedId);

                Log.e("aa", "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"+selectedId);

                int id = radioGroup.indexOfChild(radioButton);

                cliente.setForma_pagamento(id);

                if(id==0) {
                    Intent intent = new Intent(FormaPagamentoActivity.this, PagamentoActivity.class);

                    // 3. put person in intent data
                    intent.putExtra("cliente", cliente);

                    // 4. start the activity
                    startActivity(intent);
                }else if(id==1) {
                    Intent intent = new Intent(FormaPagamentoActivity.this, PagamentoActivity.class);

                    // 3. put person in intent data
                    intent.putExtra("cliente", cliente);

                    // 4. start the activity
                    startActivity(intent);
                }else if(id==2){
                    Intent intent = new Intent(FormaPagamentoActivity.this, PagamentoActivity.class);

                    // 3. put person in intent data
                    intent.putExtra("cliente", cliente);

                    // 4. start the activity
                    startActivity(intent);
                }/*else if(id==3){
                    Intent intent = new Intent(FormaPagamentoActivity.this, PagamentoBotaoPagseguroActivity.class);

                    // 3. put person in intent data
                    intent.putExtra("cliente", cliente);

                    // 4. start the activity
                    startActivity(intent);
                }*/
            }
        });
    }

    @Override
    public void onBackPressed()
    {

        Intent intent = new Intent(FormaPagamentoActivity.this, ResumoActivity.class);

        intent.putExtra("cliente", cliente);

        startActivity(intent);

    }

    @Override
    public boolean onSupportNavigateUp(){
        Intent intent = new Intent(FormaPagamentoActivity.this, ResumoActivity.class);

        intent.putExtra("cliente", cliente);

        startActivity(intent);

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

                Intent intent = new Intent(FormaPagamentoActivity.this, CarrinhoActivity.class);

                finishAffinity();

                startActivity(intent);

                return true;

            case R.id.seus_pedidos:

                Intent intent_pedidos = new Intent(FormaPagamentoActivity.this, PedidosActivity.class);

                Bundle bundle = new Bundle();

                bundle.putSerializable("cliente", cliente);

                intent_pedidos.putExtras(bundle);

                startActivity(intent_pedidos);

                return true;


            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
