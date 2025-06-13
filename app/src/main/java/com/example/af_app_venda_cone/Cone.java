package com.example.af_app_venda_cone;

import com.google.firebase.firestore.Exclude;

public class Cone {
    private String id;
    private String nome;
    private double preco;
    @Exclude
    private int quantidadeSelecionada = 0;
    public Cone() {}
    public Cone(String id, String nome, double preco) {
        this.id = id;
        this.nome = nome;
        this.preco = preco;

    }

    public String getId() { return id; }
    public String getNome() { return nome; }
    public double getPreco() { return preco; }

    public void setId(String id) { this.id = id; }
    public void setNome(String nome) { this.nome = nome; }
    public void setPreco(double preco) { this.preco = preco; }
    public int getQuantidadeSelecionada() {
        return quantidadeSelecionada;
    }

    public void setQuantidadeSelecionada(int quantidadeSelecionada) {
        this.quantidadeSelecionada = quantidadeSelecionada;
    }
}
