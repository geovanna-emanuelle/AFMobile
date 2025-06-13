package com.example.af_app_venda_cone;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ComprarCones extends AppCompatActivity {


    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    List<Cone> listaCones = new ArrayList<>();
    private RecyclerView recyclerViewCones;
    private ConeAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_comprar_cones);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        recyclerViewCones = findViewById(R.id.recyclerViewCones);
        recyclerViewCones.setLayoutManager(new LinearLayoutManager(this));

        adapter = new ConeAdapter(listaCones);
        recyclerViewCones.setAdapter(adapter);

        findViewById(R.id.btnFinalizarPedido).setOnClickListener(v -> finalizarPedido());

        findViewById(R.id.btnMeusPedidos).setOnClickListener(v -> {
            startActivity(new Intent(ComprarCones.this, MeusPedidos.class));
        });

        carregarCones();
    }

    private void carregarCones() {
        db.collection("cones").get().addOnSuccessListener(docs -> {
            listaCones.clear();
            for (DocumentSnapshot doc : docs) {
                Cone c = doc.toObject(Cone.class);
                c.setId(doc.getId());
                listaCones.add(c);
            }
            adapter.notifyDataSetChanged();
        });
    }
    private void finalizarPedido() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) return;

        Date agora = new Date();

        db.collection("pedido").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        int numeroPedido = task.getResult().size() + 1;

                        List<ItemPedido> itensPedido = new ArrayList<>();
                        for (Cone cone : listaCones) {
                            if (cone.getQuantidadeSelecionada() > 0) {
                                itensPedido.add(new ItemPedido(
                                        cone.getNome(),
                                        cone.getPreco(),
                                        cone.getQuantidadeSelecionada()
                                ));
                            }
                        }

                        if (itensPedido.isEmpty()) {
                            Toast.makeText(this, "Selecione ao menos um cone.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        Pedido pedido = new Pedido(
                                null,
                                user.getUid(),
                                itensPedido,
                                "Em preparo",
                                agora,
                                numeroPedido
                        );

                        db.collection("pedido")
                                .add(pedido)
                                .addOnSuccessListener(documentReference -> {
                                    String pedidoId = documentReference.getId();
                                    pedido.setId(pedidoId);

                                    db.collection("pedido")
                                            .document(pedidoId)
                                            .update("id", pedidoId)
                                            .addOnSuccessListener(aVoid -> {
                                                for (Cone c : listaCones) {
                                                    c.setQuantidadeSelecionada(0);
                                                }
                                                adapter.notifyDataSetChanged();
                                                Toast.makeText(this, "Pedido finalizado! :)", Toast.LENGTH_SHORT).show();
                                            })
                                            .addOnFailureListener(e -> {
                                                Toast.makeText(this, "Erro ao atualizar ID do pedido.", Toast.LENGTH_SHORT).show();
                                            });
                                });

                    } else {
                        Toast.makeText(this, "Erro ao obter pedidos para número do pedido.", Toast.LENGTH_LONG).show();
                    }
                });
    }

}