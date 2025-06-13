package com.example.af_app_venda_cone;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PedidosFeitos extends AppCompatActivity {

    private RecyclerView recyclerView;
    private PedidoAdapter adapter;
    private List<Pedido> lista = new ArrayList<>();

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ListenerRegistration listenerRegistration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pedidos_feitos);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Collections.sort(lista, (p1, p2) -> p2.getDataHora().compareTo(p1.getDataHora()));

        recyclerView = findViewById(R.id.recyclerViewPedidos);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PedidoAdapter(lista, true);
        recyclerView.setAdapter(adapter);

        findViewById(R.id.btnCadastrarCones).setOnClickListener(v -> {
            startActivity(new Intent(PedidosFeitos.this, CadastroCones.class));
        });

    }
    private void iniciarListenerPedidos() {
        List<String> statusAceitos = new ArrayList<>();
        statusAceitos.add("Em preparo");
        statusAceitos.add("Pronto");
        statusAceitos.add("Cancelado");

        listenerRegistration = db.collection("pedido")
                .whereIn("status", statusAceitos)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Toast.makeText(this, "Erro ao carregar pedidos: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (value != null) {
                        lista.clear();
                        for (var doc : value.getDocuments()) {
                            Pedido p = doc.toObject(Pedido.class);
                            p.setId(doc.getId());
                            lista.add(p);
                        }

                        Collections.sort(lista, (p1, p2) -> p2.getDataHora().compareTo(p1.getDataHora()));
                        adapter.notifyDataSetChanged();
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        iniciarListenerPedidos();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (listenerRegistration != null) {
            listenerRegistration.remove();
        }
    }
}
