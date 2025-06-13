package com.example.af_app_venda_cone;

public class ItemPedido {
    private String nomeCone;
    private double preco;
    private int quantidade;

    public ItemPedido() {}

    public ItemPedido(String nomeCone, double preco, int quantidade) {
        this.nomeCone = nomeCone;
        this.preco = preco;
        this.quantidade = quantidade;
    }

    public String getNomeCone() { return nomeCone; }
    public double getPreco() { return preco; }
    public int getQuantidade() { return quantidade; }

    public void setNomeCone(String nomeCone) { this.nomeCone = nomeCone; }
    public void setPreco(double preco) { this.preco = preco; }
    public void setQuantidade(int quantidade) { this.quantidade = quantidade;}
}