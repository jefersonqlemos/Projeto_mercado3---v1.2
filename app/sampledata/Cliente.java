package com.example.clienet.projeto_mercado3;

/**
 * Created by jefer on 09/01/2018.
 */
import java.io.Serializable;

public class Cliente implements Serializable{

    private static final long serialVersionUID = 1L;
    private int id = 0;
    private int idcidade = 0;
    private int idmercado = 0;
    private int idproduto = 0;
    private String email = null;
    private String nome_mercado = null;
    private String foto_mercado = null;
    private String bairro_mercado = null;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdcidade() {
        return idcidade;
    }

    public void setIdcidade(int idcidade) {
        this.idcidade = idcidade;
    }

    public int getIdmercado() {
        return idmercado;
    }

    public void setIdmercado(int idmercado) {
        this.idmercado = idmercado;
    }

    public int getIdproduto() {
        return idproduto;
    }

    public void setIdproduto(int idproduto) {
        this.idproduto = idproduto;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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
