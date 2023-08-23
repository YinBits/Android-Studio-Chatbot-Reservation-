package com.example.tina;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

import java.util.Objects;

public class Registro extends AppCompatActivity {

    private EditText edt_nome;
    private EditText edt_telefone;
    private EditText edt_data_nascimento;
    private EditText edt_CPF;
    private EditText edt_email_registro;
    private EditText edt_senha_registro;
    private EditText edt_confirmar_senha;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        mAuth = FirebaseAuth.getInstance();

        edt_nome = findViewById(R.id.edt_nome);
        edt_telefone = findViewById(R.id.edt_telefone);
        edt_data_nascimento = findViewById(R.id.edt_data_nascimento);
        edt_CPF = findViewById(R.id.edt_CPF);
        edt_email_registro = findViewById(R.id.edt_email_registro);
        edt_senha_registro = findViewById(R.id.edt_senha_registro);
        edt_confirmar_senha = findViewById(R.id.edt_confirmar_senha);
        CheckBox ckb_mostrar_senha_registro = findViewById(R.id.ckb_mostrar_senha_registro);
        Button btn_entrar_registro = findViewById(R.id.btn_entrar_registro);
        TextView voltar = findViewById(R.id.voltar);
        ImageView seta = findViewById(R.id.seta);


        ckb_mostrar_senha_registro.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                edt_senha_registro.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                edt_confirmar_senha.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            } else {
                edt_senha_registro.setTransformationMethod(PasswordTransformationMethod.getInstance());
                edt_confirmar_senha.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
        });

        btn_entrar_registro.setOnClickListener(view -> {
            String RegisterEmail = edt_email_registro.getText().toString();
            String Senha = edt_senha_registro.getText().toString();
            String ConfirmarSenha = edt_confirmar_senha.getText().toString();

            String nome = edt_nome.getText().toString();
            String telefone = edt_telefone.getText().toString();
            String dataNascimento = edt_data_nascimento.getText().toString();
            String cpf = edt_CPF.getText().toString();
            String email = edt_email_registro.getText().toString();

            if (!TextUtils.isEmpty(RegisterEmail) || !TextUtils.isEmpty(ConfirmarSenha) || !TextUtils.isEmpty(Senha)) {
                if (Senha.equals(ConfirmarSenha)) {
                    mAuth.createUserWithEmailAndPassword(RegisterEmail, Senha).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            uploadData(nome, telefone, dataNascimento, cpf, email);
                            abrirTelaPrincipal();
                        } else {
                            String error = Objects.requireNonNull(task.getException()).getMessage();
                            Toast.makeText(Registro.this, "" + error, Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(Registro.this, "A senha deve ser a mesma em ambos os campos!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void uploadData(String nome, String telefone, String dataNascimento, String cpf, String email) {

        DataClass dataClass = new DataClass(nome, telefone, dataNascimento, cpf, email);

        int indiceArroba = email.indexOf('@');

        if (indiceArroba != -1) {
            String novoEmail = email.substring(0, indiceArroba).replace(".", "-");

            FirebaseDatabase.getInstance().getReference("Tina").child(novoEmail).setValue(dataClass).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(Registro.this, "Usuário cadastrado com sucesso!", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(e -> Toast.makeText(Registro.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show());
        } else {
            System.out.println("O email não contém um '@'.");
        }


    }

    private void abrirTelaPrincipal() {
        Intent intent = new Intent(Registro.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}