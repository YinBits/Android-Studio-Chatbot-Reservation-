package layout.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.example.tina.DataClass;
import com.example.tina.MainActivity;
import com.example.tina.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

public class Registro extends AppCompatActivity {

private EditText edt_nome;
private EditText edt_telefone;
private EditText edt_data_nascimento;
private EditText edt_CPF;
private EditText edt_email_registro;
private EditText edt_senha_registro;
private EditText edt_confirmar_senha;
private CheckBox ckb_mostrar_senha_registro;
private Button btn_entrar_registro;
private FirebaseAuth mAuth;

FirebaseFirestore db = FirebaseFirestore.getInstance();

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
    ckb_mostrar_senha_registro = findViewById(R.id.ckb_mostrar_senha_registro);
    btn_entrar_registro = findViewById(R.id.btn_entrar_registro);


    ckb_mostrar_senha_registro.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            if (b){
                edt_senha_registro.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                edt_confirmar_senha.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            }else{
                edt_senha_registro.setTransformationMethod(PasswordTransformationMethod.getInstance());
                edt_confirmar_senha.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
        }
    });

    btn_entrar_registro.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String RegisterEmail = edt_email_registro.getText().toString();
            String Senha = edt_senha_registro.getText().toString();
            String ConfirmarSenha = edt_confirmar_senha.getText().toString();
            String NomeCadastro = edt_nome.getText().toString();
            String Telefone = edt_telefone.getText().toString();
            String DataNascimento = edt_data_nascimento.getText().toString();
            String Cpf = edt_CPF.getText().toString();


            if (!TextUtils.isEmpty(RegisterEmail) || !TextUtils.isEmpty(ConfirmarSenha) || !TextUtils.isEmpty(Senha) || !TextUtils.isEmpty(NomeCadastro)|| !TextUtils.isEmpty(Telefone) || !TextUtils.isEmpty(DataNascimento) || !TextUtils.isEmpty(Cpf)){

                if(Senha.equals(ConfirmarSenha)){
                    mAuth.createUserWithEmailAndPassword(RegisterEmail,Senha).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                uploadData(NomeCadastro, Telefone, DataNascimento, Cpf, RegisterEmail);
                                abrirTelaPrincipal();
                            }else {
                                String error = task.getException().getMessage();
                                Toast.makeText(Registro.this, ""+error, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }else {
                    Toast.makeText(Registro.this, "A senha deve ser a mesma em ambos os campos!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    });
    }

    private void uploadData(String nome, String telefone, String dataNascimento, String cpf, String email) {

        DataClass dataClass = new DataClass(nome, telefone, dataNascimento, cpf, email);

        email = email.replace(".", "-");

        FirebaseDatabase.getInstance().getReference("Tina").child(email)
                .setValue(dataClass).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });
    }
    private void abrirTelaPrincipal() {
        Intent intent = new Intent(Registro.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}