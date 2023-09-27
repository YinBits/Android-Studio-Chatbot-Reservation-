package com.example.tina;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
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
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class Perfil extends Fragment {

    private FirebaseAuth mAuth;
    private Uri imageUri;
    final private StorageReference storageReference = FirebaseStorage.getInstance().getReference();
    final private String userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
    final private String user = userEmail.substring(0, userEmail.indexOf('@')).replace(".", "-");
    final private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Tina").child(user);

    //Botão Logout
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_perfil, container, false);

        mAuth = FirebaseAuth.getInstance();
        Button btn_logout = view.findViewById(R.id.btn_logout); // Use view.findViewById
        Button btn_save = view.findViewById(R.id.btn_save); // Use view.findViewById
        ImageView imgProfile = view.findViewById(R.id.imgProfile);

        databaseReference.get().addOnSuccessListener(dataSnapshot -> {
            Object imageURL = dataSnapshot.child("ProfileImage").child("imageURL").getValue();
            String nome = dataSnapshot.child("nome").getValue().toString();
            String email = dataSnapshot.child("email").getValue().toString();
            String dataNascimento = dataSnapshot.child("dataNascimento").getValue().toString();
            String telefone = dataSnapshot.child("telefone").getValue().toString();

            TextView txtNome = view.findViewById(R.id.textNome);
            TextView txtEmail = view.findViewById(R.id.textEmail);
            TextView txtData = view.findViewById(R.id.textDate);
            TextView txtTelefone = view.findViewById(R.id.textTelefone);

            txtNome.setText(nome);
            txtEmail.setText(email);
            txtData.setText(dataNascimento);
            txtTelefone.setText(telefone);

            if (imageURL != null) {
                Glide.with(this).load(imageURL.toString()).into(imgProfile);
            }
        });

        btn_logout.setOnClickListener(v -> {
            mAuth.signOut();
            Intent intent = new Intent(getActivity(), Login.class); // Use getActivity() para obter a atividade
            startActivity(intent);
            getActivity().finish(); // Finaliza a atividade atual
        });

        ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK) {
                Intent data = result.getData();
                imageUri = data.getData();
                imgProfile.setImageURI(imageUri);
            } else {
                Log.i("erro", "erro");
            }
        });

        imgProfile.setOnClickListener(view1 -> {
            Intent photoPicker = new Intent();
            photoPicker.setAction(Intent.ACTION_GET_CONTENT);
            photoPicker.setType("image/*");
            activityResultLauncher.launch(photoPicker);
        });

        btn_save.setOnClickListener(a -> {
            uploadToFirebase(imageUri);
        });

        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void uploadToFirebase(Uri uri) {
        final StorageReference imageReference = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(uri));
        imageReference.putFile(uri).addOnSuccessListener(taskSnapshot -> imageReference.getDownloadUrl().addOnSuccessListener(uri1 -> {
            DataClass dataClass = new DataClass(null, null, null, null, null, uri1.toString());
            databaseReference.child("ProfileImage").setValue(dataClass);
        })).addOnFailureListener(e -> {
            Log.i("erro upload", e.toString());
        });
    }

    private String getFileExtension(Uri fileUri) {
        ContentResolver contentResolver = this.getContext().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(fileUri));
    }
}
