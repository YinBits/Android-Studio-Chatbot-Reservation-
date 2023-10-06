package com.example.tina;

import android.content.ContentResolver;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class Home extends Fragment {

    final private StorageReference storageReference = FirebaseStorage.getInstance().getReference();
    final private String userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
    final private String user = userEmail.substring(0, userEmail.indexOf('@')).replace(".", "-");
    final private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Usuário").child(user);

    //Botão Logout
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        ImageView imgProfile = view.findViewById(R.id.imgProfile);

        databaseReference.get().addOnSuccessListener(dataSnapshot -> {
            Object imageURL = dataSnapshot.child("ProfileImage").child("imageURL").getValue();
            if (imageURL != null) {
                Glide.with(this).load(imageURL.toString()).into(imgProfile);
            }
            String nome = dataSnapshot.child("nome").getValue().toString();


            TextView txtNome = view.findViewById(R.id.textNome);

            txtNome.setText(nome);


            if (imageURL != null) {
                Glide.with(this).load(imageURL.toString()).into(imgProfile);
            }
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
