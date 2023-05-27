package com.example.clienet.projeto_mercado3;

/**
 * Created by jefer on 26/01/2018.
 */

public class Carrinho {

    private int quantidade = 0;
    private float total = 0;
    private float subtotal = 0;
    private float preco_entrega = 0;
    private int quantidade_total = 0;

    public int getQuantidade_total() {
        return quantidade_total;
    }

    public void setQuantidade_total(int quantidade_total) {
        this.quantidade_total = quantidade_total;
    }

    public float getPreco_entrega() {
        return preco_entrega;
    }

    public void setPreco_entrega(float preco_entrega) {
        this.preco_entrega = preco_entrega;
    }

    public float getTotal() {
        return total;
    }

    public void setTotal(float total) {
        this.total = total;
    }

    public float getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(float subtotal) {
        this.subtotal = subtotal;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }

}
