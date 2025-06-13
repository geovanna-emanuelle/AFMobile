package com.example.af_app_venda_cone;

import android.app.AlertDialog;
import android.content.Context;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ItemPedidoAdapter extends RecyclerView.Adapter<ItemPedidoAdapter.ViewHolder> {
    private List<ItemPedido> itens;

    private boolean modoAdm;
    private Pedido pedido;

    public ItemPedidoAdapter(List<ItemPedido> itens, Pedido pedido, boolean modoAdm) {
        this.itens = (itens != null) ? itens : new ArrayList<>();
        this.pedido = pedido;
        this.modoAdm = modoAdm;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pedido_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ItemPedido item = itens.get(position);
        Log.d("ITEM_DEBUG", item.getNomeCone() + " x" + item.getQuantidade());

        holder.txtNomeQuantidade.setText(item.getNomeCone() + " x" + item.getQuantidade());

        double total = item.getPreco() * item.getQuantidade();

        NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
        String totalFormatado = format.format(total);

        holder.txtPrecoTotal.setText(totalFormatado);

        if (!modoAdm && "Em preparo".equals(pedido.getStatus())) {
            holder.itemView.setOnClickListener(v -> exibirDialogEditarQuantidade(v, item, position));
            holder.itemView.setOnLongClickListener(v -> {
                excluirItemDoPedido(v.getContext(), position);
                return true;
            });
        }
    }

    @Override
    public int getItemCount() {
        return (itens != null) ? itens.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtNomeQuantidade, txtPrecoTotal;


        public ViewHolder(View itemView) {
            super(itemView);
            txtNomeQuantidade = itemView.findViewById(R.id.txtNomeQuantidade);
            txtPrecoTotal = itemView.findViewById(R.id.txtPrecoTotal);
        }
    }

    private void exibirDialogEditarQuantidade(View v, ItemPedido item, int position) {
        Context context = v.getContext();
        EditText input = new EditText(context);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        input.setText(String.valueOf(item.getQuantidade()));
        input.setHint("Nova quantidade");

        new AlertDialog.Builder(context)
                .setTitle("Editar Quantidade")
                .setView(input)
                .setPositiveButton("Salvar", (dialog, which) -> {
                    int novaQtd;
                    try {
                        novaQtd = Integer.parseInt(input.getText().toString());
                    } catch (NumberFormatException e) {
                        Toast.makeText(context, "Quantidade inválida", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    item.setQuantidade(novaQtd);
                    pedido.setItens(itens);

                    FirebaseFirestore.getInstance()
                            .collection("pedido")
                            .document(pedido.getId())
                            .update("itens", itens)
                            .addOnSuccessListener(aVoid -> {
                                notifyItemChanged(position);
                                Toast.makeText(context, "Item atualizado!", Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(context, "Erro ao atualizar item", Toast.LENGTH_SHORT).show();
                            });

                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
    private void excluirItemDoPedido(Context context, int position) {
        itens.remove(position);
        pedido.setItens(itens);

        FirebaseFirestore.getInstance()
                .collection("pedido")
                .document(pedido.getId())
                .update("itens", itens)
                .addOnSuccessListener(aVoid -> {
                    notifyItemRemoved(position);
                    Toast.makeText(context, "Item removido!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Erro ao remover item", Toast.LENGTH_SHORT).show();
                });
    }

}


