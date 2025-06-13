package com.example.af_app_venda_cone;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    public void cadastrarUsuario(View v)
    {
        EditText edtEmail = findViewById(R.id.edtEmail);
        EditText edtSenha = findViewById(R.id.edtSenha);
        mAuth.createUserWithEmailAndPassword(edtEmail.getText().toString(), edtSenha.getText().toString())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Usuário criado com sucesso", Toast.LENGTH_LONG).show();
                        Log.d("FIREBASE", "Usuário criado com sucesso");
                    } else {
                        if (edtSenha.length() < 6) {
                            Toast.makeText(this, "A senha deve ter no mínimo 6 caracteres", Toast.LENGTH_SHORT).show();
                            return;
                        }else{
                        Toast.makeText(this, "Erro ao criar usuário: " + task.getException(), Toast.LENGTH_LONG).show();
                        }
                        Log.e("FIREBASE", "Erro ao criar usuário", task.getException());
                    }
                });
    }

    public void logarUsuario(View v)
    {
        EditText edtEmail = findViewById(R.id.edtEmail);
        EditText edtSenha = findViewById(R.id.edtSenha);
        String email = edtEmail.getText().toString();
        String senha = edtSenha.getText().toString();

        mAuth.signInWithEmailAndPassword(email, senha)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Login bem-sucedido", Toast.LENGTH_LONG).show();
                        Log.d("FIREBASE", "Login bem-sucedido");

                        if (email.equals("admin@gmail.com") && senha.equals("admin123")) {
                            startActivity(new Intent(this, PedidosFeitos.class));
                        } else {
                            startActivity(new Intent(this, ComprarCones.class));
                        }

                    } else {
                        Toast.makeText(this, "Erro no login: " + task.getException(), Toast.LENGTH_LONG).show();
                        Log.e("FIREBASE", "Erro no login", task.getException());
                    }
                });
    }

}