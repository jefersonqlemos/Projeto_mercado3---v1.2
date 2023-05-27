package com.example.clienet.projeto_mercado3;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.TextView;

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

public class InformacoesTransacaoActivity extends AppCompatActivity {

    Intent intent;
    Cliente cliente;
    InformacoesTransacao informacoesTransacao = new InformacoesTransacao();
    SQLiteDatabase mDb;
    ProgressDialog mProgressDialog;
    Carrinho carrinho = new Carrinho();
    Mercado mercado = new Mercado();
    TextView date;
    TextView code;
    TextView reference;
    TextView type;
    TextView status;
    TextView lastEventDate;
    TextView paymentMethod;
    TextView paymentLink;
    TextView grossAmount;
    TextView discountAmount;
    TextView feeAmount;
    TextView netAmount;
    TextView extraAmount;
    TextView installmentCount;
    TextView itemCount;
    TextView name;
    TextView email;
    TextView phone;
    TextView street;
    TextView number;
    TextView complement;
    TextView district;
    TextView city;
    TextView state;
    TextView country;
    TextView postalCode;
    TextView shippingType;
    TextView shippingCost;
    TextView itens;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_informacoes_transacao);

        intent = getIntent();
        Bundle bundle = intent.getExtras();
        cliente = (Cliente) bundle.getSerializable("cliente");

        getSupportActionBar().setSubtitle("Login: " + cliente.getEmail());

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        date = findViewById(R.id.date);
        code = findViewById(R.id.code);
        reference = findViewById(R.id.reference);
        type = findViewById(R.id.type);
        status = findViewById(R.id.status);
        lastEventDate = findViewById(R.id.lastEventDate);
        paymentMethod = findViewById(R.id.paymentMethod);
        paymentLink = findViewById(R.id.paymentLink);
        grossAmount = findViewById(R.id.grossAmount);
        discountAmount = findViewById(R.id.discountAmount);
        feeAmount = findViewById(R.id.feeAmount);
        netAmount = findViewById(R.id.netAmount);
        extraAmount = findViewById(R.id.extraAmount);
        installmentCount = findViewById(R.id.installmentCount);
        itemCount = findViewById(R.id.itemCount);
        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        phone = findViewById(R.id.phone);
        street = findViewById(R.id.street);
        number = findViewById(R.id.number);
        complement = findViewById(R.id.complement);
        district = findViewById(R.id.district);
        city = findViewById(R.id.city);
        state = findViewById(R.id.state);
        country = findViewById(R.id.country);
        postalCode = findViewById(R.id.postalCode);
        shippingType = findViewById(R.id.shippingType);
        shippingCost = findViewById(R.id.shippingCost);
        itens = findViewById(R.id.items);

        new InformacoesTransacaoActivity.FetchSQL().execute();

    }

    @Override
    public void onBackPressed() {

        if(cliente.getIdpedido()!=0) {
            Intent intent = new Intent(InformacoesTransacaoActivity.this, ListaProdutoPedidosActivity.class);

            intent.putExtra("cliente", cliente);

            startActivity(intent);
        }else{
            Intent intent = new Intent(InformacoesTransacaoActivity.this, ResumoActivity.class);

            intent.putExtra("cliente", cliente);

            startActivity(intent);
        }

    }

    @Override
    public boolean onSupportNavigateUp() {

        if(cliente.getIdpedido()!=0) {
            Intent intent = new Intent(InformacoesTransacaoActivity.this, ListaProdutoPedidosActivity.class);

            intent.putExtra("cliente", cliente);

            startActivity(intent);
        }else{
            Intent intent = new Intent(InformacoesTransacaoActivity.this, ResumoActivity.class);

            intent.putExtra("cliente", cliente);

            startActivity(intent);
        }

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

                Intent intent = new Intent(InformacoesTransacaoActivity.this, MainActivity.class);

                finishAffinity();

                startActivity(intent);

                return true;

            case R.id.seus_pedidos:

                Intent intent_pedidos = new Intent(InformacoesTransacaoActivity.this, PedidosActivity.class);

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

            mProgressDialog = new ProgressDialog(InformacoesTransacaoActivity.this);

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

                url = new URL(getString(R.string.ipnovo)+"informacoes_transacao");

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

                    JSONArray jsonArray = new JSONArray(sb.toString());

                    Log.e("JSON", String.valueOf("hhhhhhhhhhhhhhhhhhhh"+ jsonArray));

                    JSONObject json_data = jsonArray.getJSONObject(0);

                    json_data = json_data.getJSONObject("respostaPagSeguro");

                    informacoesTransacao.setDate(json_data.getString("date"));
                    informacoesTransacao.setCode(json_data.getString("code"));
                    informacoesTransacao.setReference(json_data.getString("reference"));
                    informacoesTransacao.setType(json_data.getString("type").replace(" ", ""));
                    informacoesTransacao.setStatus(json_data.getString("status"));
                    informacoesTransacao.setLastEventDate(json_data.getString("lastEventDate"));
                    informacoesTransacao.setPaymentLink(json_data.getString("paymentLink"));
                    informacoesTransacao.setGrossAmount(json_data.getString("grossAmount"));
                    informacoesTransacao.setDiscountAmount(json_data.getString("discountAmount"));
                    informacoesTransacao.setFeeAmount(json_data.getString("feeAmount"));
                    informacoesTransacao.setNetAmount(json_data.getString("netAmount"));
                    informacoesTransacao.setExtraAmount(json_data.getString("extraAmount"));
                    informacoesTransacao.setInstallmentCount(json_data.getString("installmentCount"));
                    informacoesTransacao.setItemCount(json_data.getString("itemCount"));

                    JSONObject json_payment = new JSONObject(json_data.getString("paymentMethod"));

                    informacoesTransacao.setPaymentMethodType(json_payment.getString("type"));
                    informacoesTransacao.setPaymentMethodCode(json_payment.getString("code"));

                    JSONObject json_sender = new JSONObject(json_data.getString("sender"));

                    informacoesTransacao.setSenderName(json_sender.getString("name"));
                    informacoesTransacao.setSenderEmail(json_sender.getString("email"));

                    JSONObject json_phone = new JSONObject(json_sender.getString("phone"));

                    informacoesTransacao.setSenderPhoneAreaCode(json_phone.getString("areaCode"));
                    informacoesTransacao.setSenderPhoneNumber(json_phone.getString("number"));

                    JSONObject json_shipping = new JSONObject(json_data.getString("shipping"));

                    informacoesTransacao.setShippingType(json_shipping.getString("type"));
                    informacoesTransacao.setShippingCost(json_shipping.getString("cost"));

                    JSONObject json_address = new JSONObject(json_shipping.getString("address"));

                    informacoesTransacao.setShippingAddressStreet(json_address.getString("street"));
                    informacoesTransacao.setShippingAddressNumber(json_address.getString("number"));
                    informacoesTransacao.setShippingAddressComplement(json_address.getString("complement"));
                    informacoesTransacao.setShippingAddressDistrict(json_address.getString("district"));
                    informacoesTransacao.setShippingAddressCity(json_address.getString("city"));
                    informacoesTransacao.setShippingAddressState(json_address.getString("state"));
                    informacoesTransacao.setShippingAddressCountry(json_address.getString("country"));
                    informacoesTransacao.setShippingAddressPostalCode(json_address.getString("postalCode"));

                    JSONObject json_items = new JSONObject(json_data.getString("items"));

                    JSONArray jsonArray_item = new JSONArray(json_items.getString("item"));

                    String items = null;

                    //Log.e("JSON", String.valueOf("hhhhhhhhhhhhhhhhhhhh"+jsonArray_item.getJSONObject(1)));

                    for(int i=0; i<jsonArray_item.length(); i++){
                        JSONObject json_item = jsonArray_item.getJSONObject(i);

                        items += json_item.getString("description")+"\n"
                                +"Quantidade: "+json_item.getString("quantity")+"\n"
                                +"Valor: "+json_item.getString("amount")+"\n"
                                +"\n";

                        /*HashMap<String, Object> meMap = new HashMap<String, Object>();
                        meMap.put("id", json_item.getString("id"));
                        meMap.put("description", json_item.getString("description"));
                        meMap.put("quantity", json_item.getString("quantity"));
                        meMap.put("amount", json_item.getString("amount"));
                        listdata.add(meMap);*/

                    }
                    //Log.e("JSON", String.valueOf("hhhhhhhhhhhhhhhhhhhhoi"));

                    informacoesTransacao.setItems(items);

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

            date.setText("Data da efetuação do pedido: "+informacoesTransacao.getDate());
            code.setText("Código da transação: "+informacoesTransacao.getCode());
            reference.setText("Id do pedido: "+informacoesTransacao.getReference());
            if(Integer.parseInt(informacoesTransacao.getType())==1){
                type.setText("Tipo: Pagamento");
            }

            //type.setText(informacoesTransacao.getType());
            if(Integer.parseInt(informacoesTransacao.getStatus())==1) {
                status.setText("Status: Aguardando pagamento");
            }else if(Integer.parseInt(informacoesTransacao.getStatus())==2){
                status.setText("Status: Em análise");
            }else if(Integer.parseInt(informacoesTransacao.getStatus())==3){
                status.setText("Status: Pagamento aprovado");
            }else if(Integer.parseInt(informacoesTransacao.getStatus())==4){
                status.setText("Status: Disponível");
            }else if(Integer.parseInt(informacoesTransacao.getStatus())==5){
                status.setText("Status: Em disputa");
            }else if(Integer.parseInt(informacoesTransacao.getStatus())==6){
                status.setText("Status: Devolvido");
            }else if(Integer.parseInt(informacoesTransacao.getStatus())==7){
                status.setText("Status: Cancelado");
            }

            lastEventDate.setText("Ultima atualização: "+informacoesTransacao.getLastEventDate());

            if(Integer.parseInt(informacoesTransacao.getPaymentMethodType())==1){
                paymentMethod.setText("Método de pagamento: Cartão de Crédito  \nCódigo: "+informacoesTransacao.getPaymentMethodCode());
            }else if(Integer.parseInt(informacoesTransacao.getPaymentMethodType())==2){
                paymentMethod.setText("Método de pagamento: Boleto Bancário \nCódigo: "+informacoesTransacao.getPaymentMethodCode());
                paymentLink.setText(informacoesTransacao.getPaymentLink());
            }else if(Integer.parseInt(informacoesTransacao.getPaymentMethodType())==3){
                paymentMethod.setText("Método de pagamento: Débito Online \nCódigo: "+informacoesTransacao.getPaymentMethodCode());
            }else if(Integer.parseInt(informacoesTransacao.getPaymentMethodType())==4){
                paymentMethod.setText("Método de pagamento: Saldo PagSeguro \nCódigo: "+informacoesTransacao.getPaymentMethodCode());
            }else if(Integer.parseInt(informacoesTransacao.getPaymentMethodType())==5){
                paymentMethod.setText("Método de pagamento: Oi Paggo \nCódigo: "+informacoesTransacao.getPaymentMethodCode());
            }else if(Integer.parseInt(informacoesTransacao.getPaymentMethodType())==6){
                paymentMethod.setText("Método de pagamento: \nCódigo: "+informacoesTransacao.getPaymentMethodCode());
            }else if(Integer.parseInt(informacoesTransacao.getPaymentMethodType())==7){
                paymentMethod.setText("Método de pagamento: Débito em Conta \nCódigo: "+informacoesTransacao.getPaymentMethodCode());
            }

            grossAmount.setText("Valor bruto: "+informacoesTransacao.getGrossAmount());
            discountAmount.setText("Desconto: "+informacoesTransacao.getDiscountAmount());
            feeAmount.setText("Valor da taxa bancaria: "+informacoesTransacao.getFeeAmount());
            netAmount.setText("Valor líquido: "+informacoesTransacao.getNetAmount());
            extraAmount.setText("Valor extra: "+informacoesTransacao.getExtraAmount());
            installmentCount.setText("Numero de parcelas: "+informacoesTransacao.getInstallmentCount());
            itemCount.setText("Numero de itens: "+informacoesTransacao.getItemCount());
            name.setText("Nome: "+informacoesTransacao.getSenderName());
            email.setText("Email: "+informacoesTransacao.getSenderEmail());
            phone.setText("Telefone: "+informacoesTransacao.getSenderPhoneAreaCode()+" "+informacoesTransacao.getSenderPhoneNumber());
            street.setText("Rua: "+informacoesTransacao.getShippingAddressStreet());
            number.setText("Numero: "+informacoesTransacao.getShippingAddressNumber());
            complement.setText("Complemento: "+informacoesTransacao.getShippingAddressComplement());
            district.setText("Bairro: "+informacoesTransacao.getShippingAddressDistrict());
            city.setText("Cidade: "+informacoesTransacao.getShippingAddressCity());
            state.setText("Estado: "+informacoesTransacao.getShippingAddressState());
            country.setText("País: "+informacoesTransacao.getShippingAddressCountry());
            postalCode.setText("Código postal: "+informacoesTransacao.getShippingAddressPostalCode());


            if(Integer.parseInt(informacoesTransacao.getShippingType())==3){
                shippingType.setText("Tipo de transporte: Particular");
            }else{
                shippingType.setText("Tipo de transporte: "+informacoesTransacao.getShippingType());
            }

            shippingCost.setText("Custo de transporte: "+informacoesTransacao.getShippingCost());

            itens.setText(informacoesTransacao.getItems());

            mProgressDialog.dismiss();

            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        }
    }
}
