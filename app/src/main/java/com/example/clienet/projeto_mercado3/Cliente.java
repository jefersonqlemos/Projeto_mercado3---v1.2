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
    private int quantidade = 0;
    private int Idcategoria = 0;
    private int Idendereco = 0;
    private int Idcidade_carrinho=0;
    private String nome_cidade="Videira";
    private int forma_pagamento=1;
    private int idpedido=0;
    private int quantidadeTabelas=0;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    private String token = null;

    public int getQuantidadeTabelas() {
        return quantidadeTabelas;
    }

    public void setQuantidadeTabelas(int quantidadeTabelas) {
        this.quantidadeTabelas = quantidadeTabelas;
    }

    public int getIdpedido() {
        return idpedido;
    }

    public void setIdpedido(int idpedido) {
        this.idpedido = idpedido;
    }

    public int getForma_pagamento() {
        return forma_pagamento;
    }

    public void setForma_pagamento(int forma_pagamento) {
        this.forma_pagamento = forma_pagamento;
    }

    public String getNome_cidade() {
        return nome_cidade;
    }

    public void setNome_cidade(String nome_cidade) {
        this.nome_cidade = nome_cidade;
    }

    public int getIdcidade_carrinho() {
        return Idcidade_carrinho;
    }

    public void setIdcidade_carrinho(int idcidade_carrinho) {
        Idcidade_carrinho = idcidade_carrinho;
    }

    public int getIdendereco() {
        return Idendereco;
    }

    public void setIdendereco(int idendereco) {
        Idendereco = idendereco;
    }

    public int getIdcategoria() {
        return Idcategoria;
    }

    public void setIdcategoria(int idcategoria) {
        Idcategoria = idcategoria;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }

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

}
