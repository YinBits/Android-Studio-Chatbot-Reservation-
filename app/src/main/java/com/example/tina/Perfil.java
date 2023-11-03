package com.example.tina;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class Perfil extends Fragment {

    private FirebaseAuth mAuth;
    private Uri imageUri;
    private StorageReference storageReference;
    private DatabaseReference databaseReference;
    private ImageView imgProfile;
    private TextView txtNome, txtEmail, txtData, txtTelefone;
    private Context context;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_perfil, container, false);

        mAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference().child("ProfileImage");
        String userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        String user = userEmail.substring(0, userEmail.indexOf('@')).replace(".", "-");
        databaseReference = FirebaseDatabase.getInstance().getReference("Usuário").child(user);

        imgProfile = view.findViewById(R.id.imgProfile);
        txtNome = view.findViewById(R.id.textNome);
        txtEmail = view.findViewById(R.id.textEmail);
        txtData = view.findViewById(R.id.textDate);
        txtTelefone = view.findViewById(R.id.textTelefone);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && context != null) {
                    String nome = Codex.decode(dataSnapshot.child("nome").getValue(String.class));
                    String email = Codex.decode(dataSnapshot.child("email").getValue(String.class));
                    String dataNascimento = Codex.decode(dataSnapshot.child("dataNascimento").getValue(String.class));
                    String telefone = Codex.decode(dataSnapshot.child("telefone").getValue(String.class));

                    Log.i("Decode", "Decoded values:"
                            + "\nNome: " + nome
                            + "\nEmail: " + email
                            + "\nData de nascimento: " + dataNascimento
                            + "\nTelefone: " + telefone);

                    txtNome.setText(nome != null ? nome : "");
                    txtEmail.setText(email != null ? email : "");
                    txtData.setText(dataNascimento != null ? dataNascimento : "");
                    txtTelefone.setText(telefone != null ? telefone : "");

                    Object imageURL = dataSnapshot.child("ProfileImage").child("imageURL").getValue();
                    if (imageURL != null) {
                        Glide.with(context).load(imageURL.toString()).into(imgProfile);
                    }
                } else {
                    // O nó no banco de dados não existe ou está vazio
                    // Trate esse caso de acordo com a lógica do seu aplicativo
                    // Aqui, os campos de texto serão deixados em branco e a imagem será mantida como está
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Erro", "Falha ao acessar o banco de dados: " + databaseError.getMessage());
            }
        });

        Button btnLogout = view.findViewById(R.id.btn_logout);
        Button btnSave = view.findViewById(R.id.btn_save);

        btnLogout.setOnClickListener(v -> logoutUser());
        imgProfile.setOnClickListener(v -> openImagePicker());
        btnSave.setOnClickListener(v -> saveProfileImage());

        return view;
    }

    private void logoutUser() {
        mAuth.signOut();
        Intent intent = new Intent(requireActivity(), Login.class);
        startActivity(intent);
        requireActivity().finish();
    }

    private void openImagePicker() {
        Intent photoPicker = new Intent(Intent.ACTION_GET_CONTENT);
        photoPicker.setType("image/*");
        activityResultLauncher.launch(photoPicker);
    }

    private final ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == Activity.RESULT_OK) {
            Intent data = result.getData();
            imageUri = data.getData();
            imgProfile.setImageURI(imageUri);
        } else {
            Log.i("erro", "erro");
        }
    });

    private void saveProfileImage() {
        if (imageUri != null) {
            // Adicione logs para verificar se esse bloco está sendo executado
            Log.d("saveProfileImage", "Iniciando upload da imagem");

            final StorageReference imageReference = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(imageUri));
            imageReference.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
                imageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                    DataClass dataClass = new DataClass(null, null, null, null, null, uri.toString());
                    databaseReference.child("ProfileImage").setValue(dataClass);
                    Log.d("saveProfileImage", "Imagem enviada e URL salva");
                });
            }).addOnFailureListener(e -> {
                Log.e("erro upload", e.toString());
            });
        }
    }

    private String getFileExtension(Uri fileUri) {
        ContentResolver contentResolver = context.getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(fileUri));
    }
}
