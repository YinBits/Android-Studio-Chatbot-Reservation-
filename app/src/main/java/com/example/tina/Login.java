package com.example.tina;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class Login extends AppCompatActivity {

    private EditText edt_email;
    private EditText edt_senha;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        edt_email = findViewById(R.id.edt_email);
        edt_senha = findViewById(R.id.edt_senha);
        Button btn_entrar = findViewById(R.id.btn_entrar);
        Button btn_cadastrar = findViewById(R.id.btn_cadastrar);
        CheckBox ckb_mostrar_senha = findViewById(R.id.ckb_mostrar_senha);

        // Verificar se o usuário já está logado
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            abrirTelaPrincipal();
            finish(); // Feche a LoginActivity para que o usuário não possa voltar para ela pressionando o botão "Voltar".
        }

        btn_entrar.setOnClickListener(view -> {
            String loginEmail = edt_email.getText().toString();
            String loginSenha = edt_senha.getText().toString();

            if (!TextUtils.isEmpty(loginEmail) || !TextUtils.isEmpty(loginSenha)) {
                mAuth.signInWithEmailAndPassword(loginEmail, loginSenha).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        abrirTelaPrincipal();
                    } else {
                        String error = Objects.requireNonNull(task.getException()).getMessage();
                        Toast.makeText(Login.this, "" + error, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        ckb_mostrar_senha.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                edt_senha.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            } else {
                edt_senha.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
        });

        btn_cadastrar.setOnClickListener(view -> abrirTelaRegistro());
    }

    private void abrirTelaRegistro() {
        Intent intent = new Intent(Login.this, Registro.c  lass);
        startActivity(intent);
        finish();
    }

    private void abrirTelaPrincipal() {
        Intent intent = new Intent(Login.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}