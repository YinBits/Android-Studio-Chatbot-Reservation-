package com.example.tina;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class Home extends Fragment {

    private RecyclerView recyclerView;

    private DatabaseReference databaseReference;
    private FirebaseUser currentUser;
    private ImageView imgProfile;
    private ImageView imgCarousel; // ImageView para a imagem do carrossel
    private TextView txtNome;

    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);
        recyclerView= view.findViewById(R.id.recycler);
        imgProfile = view.findViewById(R.id.imgProfile);
        txtNome = view.findViewById(R.id.textNome);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String user = currentUser.getEmail().substring(0, currentUser.getEmail().indexOf('@')).replace(".", "-");
            databaseReference = FirebaseDatabase.getInstance().getReference("Usuário").child(user);
            fetchUserData();
        }

        // Inicialize o ArrayList com as URLs do banco de dados "ImagensBanner"
        ArrayList<String> arrayList = new ArrayList<>();

        // Obtenha uma referência ao banco de dados "ImagensBanner"
        DatabaseReference imagensBannerRef = FirebaseDatabase.getInstance().getReference("Banners");

        imagensBannerRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot imageSnapshot : dataSnapshot.getChildren()) {
                        String imageURL = imageSnapshot.child("imageUrl").getValue(String.class);
                        if (imageURL != null && !imageURL.isEmpty()) {
                            arrayList.add(imageURL);
                        }
                    }

                    // Atualize o adaptador com as novas URLs
                    ImageAdapter adapter = new ImageAdapter(getActivity(), arrayList);
                    adapter.setOnItemClickListener(new ImageAdapter.onItemClickListener() {
                        @Override
                        public void onClick(ImageView imageView, String url) {
                            // Crie uma intenção para iniciar a atividade de exibição da imagem
                            Intent intent = new Intent(getActivity(), ImageViewActivity.class);
                            intent.putExtra("image", url);
                            startActivity(intent);
                        }
                    });

                    recyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Lidar com erros de consulta, se necessário
            }
        });

        return view;
    }

    private void fetchUserData() {
        if (databaseReference != null) {
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String imageURL = dataSnapshot.child("ProfileImage").child("imageURL").getValue(String.class);
                        if (imageURL != null) {
                            Glide.with(requireContext()).load(imageURL).into(imgProfile);
                        }

                        String nome = Codex.decode(dataSnapshot.child("nome").getValue(String.class));
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
