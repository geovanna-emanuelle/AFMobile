package com.example.af_app_venda_cone;

import java.util.Date;
import java.util.List;

public class Pedido {
    private String id;
    private String userID;
//    private String nomeCone;
//    private double preco;
//    private int quantidade;

    private List<ItemPedido> itens;
    private String status;
    private Date dataHora;
    private int numero;

    public Pedido() {}

    public Pedido(String id, String userID, List<ItemPedido> itens, String status, Date dataHora, int numero) {
        this.id = id;
        this.userID = userID;
        this.itens = itens;
        this.status = status;
        this.dataHora = dataHora;
        this.numero = numero;
    }

    public String getId() { return id; }
    public String getUserID() { return userID; }
//    public String getNomeCone() { return nomeCone; }
//    public double getPreco() { return preco; }
//    public int getQuantidade() { return quantidade; }
    public List<ItemPedido> getItens() { return itens; }
    public String getStatus() { return status; }
    public Date getDataHora() { return dataHora; }
    public int getNumero() { return numero; }

    public void setId(String id) { this.id = id; }
    public void setUserID(String userID) { this.userID = userID; }
    public void setItens(List<ItemPedido> itens) { this.itens = itens; }
    public void setStatus(String status) { this.status = status; }
    public void setDataHora(Date dataHora) { this.dataHora = dataHora; }
    public void setNumero(int numero) { this.numero = numero; }
}
