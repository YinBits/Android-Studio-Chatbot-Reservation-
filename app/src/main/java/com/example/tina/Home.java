package com.example.tina;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Home extends Fragment {

    private DatabaseReference databaseReference;
    private FirebaseUser currentUser;
    private ImageView imgProfile;
    private TextView txtNome;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        imgProfile = view.findViewById(R.id.imgProfile);
        txtNome = view.findViewById(R.id.textNome);

        // Inicialize o Firebase
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // Se o usuário está logado, obtenha a referência do nó de dados do usuário
            String user = currentUser.getEmail().substring(0, currentUser.getEmail().indexOf('@')).replace(".", "-");
            databaseReference = FirebaseDatabase.getInstance().getReference("Usuário").child(user);
            fetchUserData();
        }

        return view;
    }

    private void fetchUserData() {
        if (databaseReference != null) {
            // Consulta de dados no Firebase
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        // Verifica se o nó de dados existe

                        // Obtenha a URL da imagem do perfil
                        String imageURL = dataSnapshot.child("ProfileImage").child("imageURL").getValue(String.class);
                        if (imageURL != null) {
                            // Use o Glide para carregar a imagem
                            Glide.with(requireContext()).load(imageURL).into(imgProfile);
                        }

                        // Obtenha o nome do usuário e defina-o no TextView
                        String nome = dataSnapshot.child("nome").getValue(String.class);
                        txtNome.setText(nome);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Trate os erros de consulta, se necessário
                }
            });
        }
    }
}