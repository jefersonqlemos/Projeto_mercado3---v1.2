package com.example.clienet.projeto_mercado3;

/**
 * Created by jefer on 16/03/2018.
 */

public class Pedido {

    private int idpedido;
    private String status;
    private int idstatus;
    private String valor;
    private String data;
    private String nome_mercado;
    private String nome_cidade;

    public String getNome_cidade() {
        return nome_cidade;
    }

    public void setNome_cidade(String nome_cidade) {
        this.nome_cidade = nome_cidade;
    }

    public String getNome_mercado() {
        return nome_mercado;
    }

    public void setNome_mercado(String nome_mercado) {
        this.nome_mercado = nome_mercado;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    public int getIdstatus() {
        return idstatus;
    }

    public void setIdstatus(int idstatus) {
        this.idstatus = idstatus;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getIdpedido() {
        return idpedido;
    }

    public void setIdpedido(int idpedido) {
        this.idpedido = idpedido;
    }
}
