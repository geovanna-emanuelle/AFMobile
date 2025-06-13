package com.example.af_app_venda_cone;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class ConeAdapter extends RecyclerView.Adapter<ConeAdapter.ViewHolder> {
    private List<Cone> lista;
    private OnItemClickListener listener;
    private boolean mostrarControlesQuantidade;

    public interface OnItemClickListener {
        void onItemClick(Cone cone);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public ConeAdapter(List<Cone> lista) {
        this(lista, true);
    }

    public ConeAdapter(List<Cone> lista, boolean mostrarControlesQuantidade) {
        this.lista = lista;
        this.mostrarControlesQuantidade = mostrarControlesQuantidade;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cone, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int pos) {
        Cone cone = lista.get(pos);
        holder.txt1.setText(cone.getNome());
        holder.txt2.setText("Preço: R$ " + cone.getPreco());

        if (mostrarControlesQuantidade) {
            holder.txtQuantidade.setVisibility(View.VISIBLE);
            holder.btnMais.setVisibility(View.VISIBLE);
            holder.btnMenos.setVisibility(View.VISIBLE);

            holder.txtQuantidade.setText(String.valueOf(cone.getQuantidadeSelecionada()));

            holder.btnMais.setOnClickListener(v -> {
                int novaQuantidade = cone.getQuantidadeSelecionada() + 1;
                cone.setQuantidadeSelecionada(novaQuantidade);
                holder.txtQuantidade.setText(String.valueOf(novaQuantidade));
            });

            holder.btnMenos.setOnClickListener(v -> {
                int atual = cone.getQuantidadeSelecionada();
                if (atual > 0) {
                    cone.setQuantidadeSelecionada(atual - 1);
                    holder.txtQuantidade.setText(String.valueOf(atual - 1));
                }
            });

        } else {
            holder.txtQuantidade.setVisibility(View.GONE);
            holder.btnMais.setVisibility(View.GONE);
            holder.btnMenos.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(cone);
            }
        });

        holder.itemView.setOnTouchListener(new View.OnTouchListener() {
            private long lastClickTime = 0;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    long currentTime = System.currentTimeMillis();
                    if (currentTime - lastClickTime < 300) {
                        deletarCone(cone.getId(), holder.getAdapterPosition(), v);
                    }
                    lastClickTime = currentTime;
                }
                return false;
            }
        });
    }

    private void deletarCone(String idDocumento, int position, View view) {
        FirebaseFirestore.getInstance().collection("cones")
                .document(idDocumento)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    lista.remove(position);
                    notifyItemRemoved(position);
                    Toast.makeText(view.getContext(), "Cone deletado!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(view.getContext(), "Erro ao deletar cone", Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txt1, txt2, txtQuantidade;
        Button btnMais, btnMenos;

        public ViewHolder(View itemView) {
            super(itemView);
            txt1 = itemView.findViewById(R.id.txtNomeCone);
            txt2 = itemView.findViewById(R.id.txtPrecoCone);
            txtQuantidade = itemView.findViewById(R.id.txtQuantidade);
            btnMais = itemView.findViewById(R.id.btnMais);
            btnMenos = itemView.findViewById(R.id.btnMenos);
        }
    }
}