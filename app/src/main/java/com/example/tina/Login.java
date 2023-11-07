package com.example.tina;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class Login extends AppCompatActivity {

    private EditText edt_email;
    private EditText edt_senha;
    private FirebaseAuth mAuth;

    TextView forgotPassword;

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
        forgotPassword = findViewById(R.id.forgot_password);





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

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);
                View dialogView = getLayoutInflater().inflate(R.layout.dialog_forgot, null);
                EditText emailBox = dialogView.findViewById(R.id.emailBox);

                builder.setView(dialogView);
                AlertDialog dialog = builder.create();

                dialogView.findViewById(R.id.btnReset).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String userEmail = emailBox.getText().toString();

                        if (TextUtils.isEmpty(userEmail) && !Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()){
                            Toast.makeText(Login.this, "Entre com seu email registrado", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        mAuth.sendPasswordResetEmail(userEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    Toast.makeText(Login.this, "Olhe seu email", Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                } else {
                                    Toast.makeText(Login.this, "Falho", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
                dialogView.findViewById(R.id.btnCancel).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                if (dialog.getWindow() !=null){
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                }
                dialog.show();
            }
        });

        btn_cadastrar.setOnClickListener(view -> abrirTelaRegistro());



    }

    private void abrirTelaRegistro() {
        Intent intent = new Intent(Login.this, Registro.class);
        startActivity(intent);
        finish();
    }

    private void abrirTelaPrincipal() {
        Intent intent = new Intent(Login.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}