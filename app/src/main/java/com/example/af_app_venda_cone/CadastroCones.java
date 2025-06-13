package com.example.af_app_venda_cone;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class CadastroCones extends AppCompatActivity {

    private EditText edtNome, edtPreco;
    private RecyclerView recyclerView;
    private List<Cone> lista = new ArrayList<>();
    private ConeAdapter adapter;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cadastro_cones);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        edtNome = findViewById(R.id.edtNomeCone);
        edtPreco = findViewById(R.id.edtPrecoCone);
        recyclerView = findViewById(R.id.recyclerView);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ConeAdapter(lista, false);
        recyclerView.setAdapter(adapter);

        carregarCones();
    }

    public void cadastrarCone(View view) {
        String nome = edtNome.getText().toString();
        double preco = Double.parseDouble(edtPreco.getText().toString());

        Cone cone = new Cone(null, nome, preco);

        db.collection("cones").add(cone)
                .addOnSuccessListener(doc -> {
                    Toast.makeText(this, "Cone cadastrado", Toast.LENGTH_SHORT).show();
                    edtNome.setText("");
                    edtPreco.setText("");
                    carregarCones();
                });
    }
    private void carregarCones() {
        db.collection("cones").get().addOnSuccessListener(docs -> {
            lista.clear();
            for (DocumentSnapshot doc : docs) {
                Cone c = doc.toObject(Cone.class);
                c.setId(doc.getId());
                lista.add(c);
            }
            adapter.notifyDataSetChanged();
        });
    }
}