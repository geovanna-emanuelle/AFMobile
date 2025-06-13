package com.example.af_app_venda_cone;

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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.List;

public class MeusPedidos extends AppCompatActivity {
    private RecyclerView recyclerView;
    private PedidoAdapter adapter;
    private List<Pedido> lista = new ArrayList<>();

    private ListenerRegistration listenerRegistration;


    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_meus_pedidos);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Toast.makeText(this, "Carregando pedidos...", Toast.LENGTH_SHORT).show();

        recyclerView = findViewById(R.id.recyclerViewPedidos);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PedidoAdapter(lista, false);
        recyclerView.setAdapter(adapter);

        iniciarListenerPedidos();

    }

    private void iniciarListenerPedidos() {
        String userId = mAuth.getCurrentUser().getUid();

        List<String> statusAceitosCliente = new ArrayList<>();
        statusAceitosCliente.add("Em preparo");
        statusAceitosCliente.add("Pronto");
        statusAceitosCliente.add("Entregue");
        statusAceitosCliente.add("Cancelado");

        listenerRegistration = db.collection("pedido")
                .whereEqualTo("userID", userId)
                .whereIn("status", statusAceitosCliente)
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

                        adapter.notifyDataSetChanged();
                    }
                });
    }
    @Override
    protected void onStop() {
        super.onStop();
        if (listenerRegistration != null) {
            listenerRegistration.remove();
        }
    }


}