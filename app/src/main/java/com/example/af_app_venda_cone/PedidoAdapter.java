package com.example.af_app_venda_cone;

import android.app.AlertDialog;
import android.content.Context;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class PedidoAdapter extends RecyclerView.Adapter<PedidoAdapter.ViewHolder> {
    private List<Pedido> pedidos;
    private boolean modoAdm;

    public PedidoAdapter(List<Pedido> pedidos, boolean modoAdm) {
        this.pedidos = pedidos;
        this.modoAdm = modoAdm;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pedido, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Pedido p = pedidos.get(position);

        holder.txtNumeroPedido.setText("Pedido #" + p.getNumero());

        if (modoAdm && !p.getStatus().equalsIgnoreCase("Cancelado")) {
            holder.txtStatus.setVisibility(View.GONE);
            holder.spinnerStatus.setVisibility(View.VISIBLE);

            String[] statusOpcoes = new String[]{"Em preparo", "Pronto", "Entregue"};

            ArrayAdapter<String> adapterSpinner = new ArrayAdapter<>(holder.itemView.getContext(),
                    android.R.layout.simple_spinner_item, statusOpcoes);
            adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            holder.spinnerStatus.setAdapter(adapterSpinner);

            int posSelecionada = adapterSpinner.getPosition(p.getStatus());
            if (posSelecionada >= 0) {
                holder.spinnerStatus.setSelection(posSelecionada);
            }

            holder.spinnerStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                boolean inicializado = false;

                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                    if (!inicializado) {
                        inicializado = true;
                        return;
                    }

                    int adapterPos = holder.getAdapterPosition();
                    if (adapterPos == RecyclerView.NO_POSITION) return;

                    Pedido pedidoAtual = pedidos.get(adapterPos);
                    String novoStatus = statusOpcoes[pos];
                    if (!novoStatus.equals(pedidoAtual.getStatus())) {
                        FirebaseFirestore.getInstance()
                                .collection("pedido")
                                .document(pedidoAtual.getId())
                                .update("status", novoStatus)
                                .addOnSuccessListener(aVoid -> {
                                    pedidoAtual.setStatus(novoStatus);
                                    notifyItemChanged(adapterPos);
                                    Toast.makeText(holder.itemView.getContext(), "Status atualizado para: " + novoStatus, Toast.LENGTH_SHORT).show();
                                });
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {}
            });

        } else {
            holder.spinnerStatus.setVisibility(View.GONE);
            holder.txtStatus.setVisibility(View.VISIBLE);
            holder.txtStatus.setText("Status: " + p.getStatus());
        }

        if (p.getDataHora() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            holder.txtData.setText("Data: " + sdf.format(p.getDataHora()));
        } else {
            holder.txtData.setText("Data: --/--/---- --:--");
        }

        List<ItemPedido> itensPedido = (p.getItens() != null) ? p.getItens() : new java.util.ArrayList<>();

        ItemPedidoAdapter itemAdapter = new ItemPedidoAdapter(
                itensPedido,
                p,
                modoAdm
        );

        holder.recyclerViewItens.setLayoutManager(
                new LinearLayoutManager(holder.itemView.getContext())
        );
        holder.recyclerViewItens.setAdapter(itemAdapter);

        double total = 0.0;
        for (ItemPedido item : itensPedido) {
            total += item.getPreco() * item.getQuantidade();
        }

        NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
        String totalFormatado = format.format(total);

        holder.txtTotalPedido.setText("Total: " + totalFormatado);

        holder.txtNumeroPedido.setOnClickListener(v -> {
            Context context = v.getContext();
            //para o cliente
            if (!modoAdm) {
                if (p.getStatus().equalsIgnoreCase("Em preparo")) {
                    new AlertDialog.Builder(context)
                            .setTitle("Cancelar Pedido")
                            .setMessage("Deseja cancelar o pedido #" + p.getNumero() + "?")
                            .setPositiveButton("Sim", (dialog, which) -> {
                                FirebaseFirestore.getInstance()
                                        .collection("pedido")
                                        .document(p.getId())
                                        .update("status", "Cancelado")
                                        .addOnSuccessListener(aVoid -> {
                                            p.setStatus("Cancelado");
                                            notifyItemChanged(holder.getAdapterPosition());
                                            Toast.makeText(context, "Pedido cancelado", Toast.LENGTH_SHORT).show();
                                        });
                            })
                            .setNegativeButton("Não", null)
                            .show();
                }
            }
        });

        holder.txtNumeroPedido.setOnLongClickListener(v -> {
            Context context = v.getContext();
            if (!modoAdm && p.getStatus().equalsIgnoreCase("Entregue")) {
                new AlertDialog.Builder(context)
                        .setTitle("Concluir Pedido")
                        .setMessage("Deseja concluir o pedido #" + p.getNumero() + "?")
                        .setPositiveButton("Sim", (dialog, which) -> {
                            int pos = holder.getAdapterPosition();
                            if (pos != RecyclerView.NO_POSITION) {
                                pedidos.remove(pos);
                                notifyItemRemoved(pos);
                                Toast.makeText(context, "Pedido concluído", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("Não", null)
                        .show();

                return true;
            }
            if (p.getStatus().equalsIgnoreCase("Cancelado")) {
                new AlertDialog.Builder(context)
                        .setTitle("Excluir Pedido")
                        .setMessage("Deseja excluir o pedido #" + p.getNumero() + " permanentemente?")
                        .setPositiveButton("Sim", (dialog, which) -> {
                            if (modoAdm) {
                                FirebaseFirestore.getInstance()
                                        .collection("pedido")
                                        .document(p.getId())
                                        .delete()
                                        .addOnSuccessListener(aVoid -> {
                                            int pos = holder.getAdapterPosition();
                                            if (pos != RecyclerView.NO_POSITION) {
                                                pedidos.remove(pos);
                                                notifyItemRemoved(pos);
                                            }
                                            Toast.makeText(context, "Pedido excluído permanentemente", Toast.LENGTH_SHORT).show();
                                        });
                            } else {
                                int pos = holder.getAdapterPosition();
                                if (pos != RecyclerView.NO_POSITION) {
                                    pedidos.remove(pos);
                                    notifyItemRemoved(pos);
                                }
                                Toast.makeText(context, "Pedido removido da sua lista", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("Não", null)
                        .show();
            } else {
                if (!modoAdm) {
                    Toast.makeText(context, "Só é possível excluir pedidos cancelados.", Toast.LENGTH_SHORT).show();
                }
            }

            return true;
        });

    }


    @Override
    public int getItemCount() {
        return pedidos.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtNumeroPedido, txtStatus, txtData, txtTotalPedido;
        Spinner spinnerStatus;
        RecyclerView recyclerViewItens;

        public ViewHolder(View itemView) {
            super(itemView);
            txtNumeroPedido = itemView.findViewById(R.id.txtNumeroPedido);
            txtStatus = itemView.findViewById(R.id.txtStatus);
            spinnerStatus = itemView.findViewById(R.id.spinnerStatus);
            txtData = itemView.findViewById(R.id.txtData);
            txtTotalPedido = itemView.findViewById(R.id.txtTotalPedido);
            recyclerViewItens = itemView.findViewById(R.id.recyclerViewItens);
        }
    }

}
