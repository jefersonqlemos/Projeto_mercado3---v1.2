package com.example.clienet.projeto_mercado3;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import br.com.uol.pslibs.checkout_in_app.PSCheckout;
import br.com.uol.pslibs.checkout_in_app.transparent.listener.PSBilletListener;
import br.com.uol.pslibs.checkout_in_app.transparent.vo.PSBilletRequest;
import br.com.uol.pslibs.checkout_in_app.wallet.util.PSCheckoutConfig;
import br.com.uol.pslibs.checkout_in_app.wallet.view.components.PaymentButton;
import br.com.uol.pslibs.checkout_in_app.wallet.vo.PagSeguroResponse;
import butterknife.BindView;
import butterknife.ButterKnife;

public class BotaoPagseguroFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String SELLER_EMAIL = "jefersonquagliottolemos2@yahoo.com";
    private static final String SELLER_TOKEN = " 696D039970304550B75BB71D7E187A85";
    private final String NOTIFICATION_URL_PAYMENT = "https://pagseguro.uol.com.br/lojamodelo-qa/RetornoAutomatico-OK.jsp";

    SQLiteDatabaseDao dao;
    SQLiteDatabase mDb;
    ProgressDialog mProgressDialog;
    ArrayList<HashMap<String, Object>> listData;
    ArrayList<HashMap<String, Object>> listdatasqlite;
    Carrinho carrinho = new Carrinho();
    Mercado mercado = new Mercado();
    private Cliente cliente;
    private Context mcon;
    private InformacoesPagamento informacoesPagamento = new InformacoesPagamento();

    @BindView(R.id.wallet_payment_button)
    PaymentButton cardWallet;

    public static BotaoPagseguroFragment newInstance(Cliente mcliente) {
        BotaoPagseguroFragment fragment = new BotaoPagseguroFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("cliente", mcliente);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_botao_pagseguro, container, false);
        ButterKnife.bind(this, view);

        cliente = (Cliente) getArguments().getSerializable("cliente");
        mcon = getActivity();

        initWallet();
        initTransparent();
        configurePayment();
        return view;

    }

    private void initWallet(){
        //Inicialização a lib com parametros necessarios
        PSCheckoutConfig psCheckoutConfig = new PSCheckoutConfig();
        psCheckoutConfig.setSellerEmail(SELLER_EMAIL);
        psCheckoutConfig.setSellerToken(SELLER_TOKEN);
        //Informe o fragment container
        psCheckoutConfig.setContainer(R.id.fragment_container);

        //Inicializa apenas os recursos da carteira
        PSCheckout.initWallet(getActivity(), psCheckoutConfig);
    }

    private void initTransparent(){
        PSCheckoutConfig psCheckoutConfig = new PSCheckoutConfig();
        psCheckoutConfig.setSellerEmail(SELLER_EMAIL);
        psCheckoutConfig.setSellerToken(SELLER_TOKEN);
        //Informe o fragment container
        psCheckoutConfig.setContainer(R.id.fragment_container);

        //Inicializa apenas os recursos de pagamento transparente e boleto
        PSCheckout.initTransparent(getActivity(), psCheckoutConfig);
    }

    public void configurePayment() {
        dao = new SQLiteDatabaseDao();
    }

    br.com.uol.pslibs.checkout_in_app.wallet.listener.PSCheckoutListener psCheckoutListener = new br.com.uol.pslibs.checkout_in_app.wallet.listener.PSCheckoutListener() {
        @Override
        public void onSuccess(PagSeguroResponse pagSeguroResponse, Context context) {
            Toast.makeText(getActivity(), "Sucesso de pagamento",Toast.LENGTH_LONG).show();

            /////////////////////////////////////////////////////////////////////////
            //intent temporaria até q ative a funcionalidade do pagamento
            Intent intent = new Intent(mcon, FinalActivity.class);

            // 3. put person in intent data
            intent.putExtra("cliente", cliente);
            intent.putExtra("idpedido", informacoesPagamento.getIdpedido());

            // 4. start the activity
            startActivity(intent);
        }

        @Override
        public void onFailure(PagSeguroResponse pagSeguroResponse, Context context) {
            Toast.makeText(getActivity(), "Falha no pagamento",Toast.LENGTH_LONG).show();
        }

        @Override
        public void onProgress(Context context) {
            Toast.makeText(getActivity(), "Pagamento em andamento",Toast.LENGTH_LONG).show();
        }

        @Override
        public void onCloseProgress(Context context) {

        }
    };

    private class FetchSQL extends AsyncTask<Void,Void,String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = new ProgressDialog(mcon);

            mProgressDialog.setTitle("Por Favor Aguarde");
            // Set progressdialog message
            mProgressDialog.setMessage("Loading...");
            mProgressDialog.setIndeterminate(false);
            // Show progressdialog
            mProgressDialog.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            String result = null;

            listData = new ArrayList<HashMap<String, Object>>();

            ArrayList idprodutos = new ArrayList();
            ArrayList quantidades = new ArrayList();
            float subtotal_produtos = 0;
            int quantidade_total = 0;

            for(int i=0; i<listdatasqlite.size(); i++){
                idprodutos.add(listdatasqlite.get(i).get("idproduto"));
                quantidades.add(listdatasqlite.get(i).get("quantidade"));
            }

            URL url;
            HttpURLConnection urlConnection = null;

            try {

                url = new URL(getString(R.string.ipnovo)+"pagamento_cartao_credito");

                urlConnection = (HttpURLConnection) url.openConnection();

                urlConnection.setReadTimeout(15000 /* milliseconds */);
                urlConnection.setConnectTimeout(15000 /* milliseconds */);
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty ("Authorization", "Bearer "+cliente.getToken());
                urlConnection.setRequestProperty ("Content-Type", "application/x-www-form-urlencoded");
                urlConnection.setRequestProperty ("Accept", "application/json");
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);

                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("idprodutos", idprodutos.toString().replace("[", "").replace("]", ""))
                        .appendQueryParameter("quantidades", quantidades.toString().replace("[", "").replace("]", ""))
                        .appendQueryParameter("idendereco", String.valueOf(cliente.getIdendereco()))
                        .appendQueryParameter("idcliente_comprador", String.valueOf(cliente.getId()))
                        .appendQueryParameter("email", cliente.getEmail())
                        .appendQueryParameter("idmercado", String.valueOf(cliente.getIdmercado()));
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

                    Log.e("aaaastring builder",""+sb);

                    //JSONObject json = new JSONObject(sb.toString());

                    //Log.e("idmercado", json.getString("idmercado"));

                    JSONArray jsonArray = new JSONArray(sb.toString());

                    JSONObject json_data = jsonArray.getJSONObject(0);

                    if(json_data.getInt("pessoa_fisica")==1){
                        informacoesPagamento.setIdpedido(Integer.parseInt(json_data.getString("idpedido")));
                        informacoesPagamento.setIdmercado(json_data.getString("idmercado"));
                        informacoesPagamento.setNome_mercado(json_data.getString("nome_mercado"));
                        informacoesPagamento.setDdd(json_data.getString("ddd"));
                        informacoesPagamento.setTelefone(json_data.getString("telefone"));
                        informacoesPagamento.setValor(json_data.getString("valor"));
                        informacoesPagamento.setCpf(json_data.getString("cpf"));
                        informacoesPagamento.setData_de_nascimento(json_data.getString("data_de_nascimento"));
                        informacoesPagamento.setNome(json_data.getString("nome"));
                        informacoesPagamento.setEndereco(json_data.getString("endereco"));
                        informacoesPagamento.setNumero(json_data.getString("numero"));
                        informacoesPagamento.setComplemento(json_data.getString("complemento"));
                        informacoesPagamento.setBairro(json_data.getString("bairro"));
                        informacoesPagamento.setCidade(json_data.getString("cidade"));
                        informacoesPagamento.setEstado(json_data.getString("estado"));
                        informacoesPagamento.setPais(json_data.getString("pais"));
                        informacoesPagamento.setPessoa_fisica(json_data.getInt("pessoa_fisica"));
                    }else{
                        informacoesPagamento.setIdpedido(Integer.parseInt(json_data.getString("idpedido")));
                        informacoesPagamento.setIdmercado(json_data.getString("idmercado"));
                        informacoesPagamento.setNome_mercado(json_data.getString("nome_mercado"));
                        informacoesPagamento.setDdd(json_data.getString("ddd"));
                        informacoesPagamento.setTelefone(json_data.getString("telefone"));
                        informacoesPagamento.setValor(json_data.getString("valor"));
                        informacoesPagamento.setCnpj(json_data.getString("cnpj"));
                        informacoesPagamento.setRazao_social(json_data.getString("razao_social"));
                        informacoesPagamento.setEndereco(json_data.getString("endereco"));
                        informacoesPagamento.setNumero(json_data.getString("numero"));
                        informacoesPagamento.setComplemento(json_data.getString("complemento"));
                        informacoesPagamento.setBairro(json_data.getString("bairro"));
                        informacoesPagamento.setCidade(json_data.getString("cidade"));
                        informacoesPagamento.setEstado(json_data.getString("estado"));
                        informacoesPagamento.setPais(json_data.getString("pais"));
                        informacoesPagamento.setPessoa_fisica(json_data.getInt("pessoa_fisica"));
                    }

                    result = json_data.getString("idpedido");

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

            cardWallet.configurePayment(String.valueOf(informacoesPagamento.getIdpedido()), "Pedido do Mercado Código "+informacoesPagamento.getIdmercado(), Double.parseDouble(informacoesPagamento.getValor()), 1, R.id.fragment_container, getActivity(),
                    SELLER_EMAIL, SELLER_TOKEN, psCheckoutListener);

            if(value!=null){
                Toast.makeText(getActivity(),"Numero do Pedido: "+value,Toast.LENGTH_LONG).show();
                //pedido.setText("Seu pedido foi finalizado, Numero do pedido: "+value);
                Log.e("JSON", String.valueOf("Seu pedido foi finalizado, Numero do pedido: "+value));
            }

            Log.e("JSON", String.valueOf("aaaaaaaaaaaaaaaa"+value));

            mProgressDialog.dismiss();

        }
    }

    class SQLiteDatabaseDao {

        @SuppressLint("WrongConstant")
        public SQLiteDatabaseDao() {
            mDb = SQLiteDatabase.openDatabase("/data/data/com.example.clienet.projeto_mercado3/databases/Carrinhos.db", null, 0);

            getAllData("produto");

            new BotaoPagseguroFragment.FetchSQL().execute();

        }

        public void getAllData(String table) {
            Cursor c = mDb.rawQuery("select * from " + table + " where mercado_idmercado='"+ cliente.getIdmercado() +"'", null);
            //int columnsSize = c.getColumnCount();
            listdatasqlite = new ArrayList<HashMap<String, Object>>();
            // ??????
            while (c.moveToNext()) {
                HashMap<String, Object> map = new HashMap<String, Object>();
                //for (int i = 0; i < columnsSize; i++) {
                map.put("id", c.getInt(0));
                map.put("idproduto", c.getInt(1));
                map.put("quantidade", c.getInt(2));
                //}
                listdatasqlite.add(map);
            }
        }
    }
}
