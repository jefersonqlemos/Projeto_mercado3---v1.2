package com.example.clienet.projeto_mercado3;

/**
 * Created by jefer on 24/02/2018.
 */

public class Mercado {
    private String nome_mercado = null;
    private String foto_mercado = null;
    private String bairro_mercado = null;
    private String endereco_mercado = null;
    private String cidade = null;
    private String email = null;
    private String preco_entrega;
    private String telefone1;
    private String telefone2;

    public String getEndereco_mercado() {
        return endereco_mercado;
    }

    public void setEndereco_mercado(String endereco_mercado) {
        this.endereco_mercado = endereco_mercado;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPreco_entrega() {
        return preco_entrega;
    }

    public void setPreco_entrega(String preco_entrega) {
        this.preco_entrega = preco_entrega;
    }

    public String getTelefone1() {
        return telefone1;
    }

    public void setTelefone1(String telefone1) {
        this.telefone1 = telefone1;
    }

    public String getTelefone2() {
        return telefone2;
    }

    public void setTelefone2(String telefone2) {
        this.telefone2 = telefone2;
    }

    public String getNome_mercado() {
        return nome_mercado;
    }

    public void setNome_mercado(String nome_mercado) {
        this.nome_mercado = nome_mercado;
    }

    public String getFoto_mercado() {
        return foto_mercado;
    }

    public void setFoto_mercado(String foto_mercado) {
        this.foto_mercado = foto_mercado;
    }

    public String getBairro_mercado() {
        return bairro_mercado;
    }

    public void setBairro_mercado(String bairro_mercado) {
        this.bairro_mercado = bairro_mercado;
    }
}
