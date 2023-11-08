package com.example.tina;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
    private TextView txtReserva; // TextView para exibir informações da reserva
    private Button btnCancelarReserva; // Botão para cancelar a reserva

    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);
        recyclerView = view.findViewById(R.id.recycler);
        imgProfile = view.findViewById(R.id.imgProfile);
        txtNome = view.findViewById(R.id.textNome);
        txtReserva = view.findViewById(R.id.reservationText);
        btnCancelarReserva = view.findViewById(R.id.cancelReservation); // Botão de cancelar a reserva

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String user = currentUser.getEmail().substring(0, currentUser.getEmail().indexOf('@')).replace(".", "-");
            databaseReference = FirebaseDatabase.getInstance().getReference("Usuário").child(user);
            fetchUserData();
            checkReservation(); // Verifique se o usuário tem uma reserva
        }

        // Inicialize o ArrayList com as URLs do banco de dados "ImagensBanner"
        ArrayList<String> arrayList = new ArrayList<>();

        // Obtenha uma referência ao banco de dados "ImagensBanner"
        DatabaseReference imagensBannerRef = FirebaseDatabase.getInstance().getReference("Banners");
        DatabaseReference reservaRef = FirebaseDatabase.getInstance().getReference("Reservas");

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

        // Configurar o botão de cancelar a reserva
        btnCancelarReserva.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Adicione o código para cancelar a reserva aqui
                // Certifique-se de que o usuário tenha uma reserva antes de permitir o cancelamento

                if (databaseReference != null) {
                    reservaRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                // Se o usuário tiver uma reserva, remova-a do banco de dados
                                dataSnapshot.getRef().removeValue()
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                // Reserva cancelada com sucesso
                                                txtReserva.setText("Nenhuma reserva em seu nome");
                                                btnCancelarReserva.setVisibility(View.GONE);
                                                Toast.makeText(getActivity(), "Reserva cancelada com sucesso", Toast.LENGTH_SHORT).show();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(Exception e) {
                                                // Trate o erro, caso ocorra
                                                Toast.makeText(getActivity(), "Erro ao cancelar a reserva", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            // Trate os erros de consulta, se necessário
                        }
                    });
                }
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
                        // Divida a string do nome no primeiro espaço em branco
                        String[] nomeParts = nome.split(" ", 2);
                        if (nomeParts.length > 0) {
                            txtNome.setText(nomeParts[0]);
                        } else {
                            txtNome.setText(nome); // Caso não haja espaços na string
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Trate os erros de consulta, se necessário
                }
            });
        }
    }

    // Verifique se o usuário tem uma reserva
    private void checkReservation() {
        if (currentUser != null) {
            String userId = currentUser.getEmail().substring(0, currentUser.getEmail().indexOf('@'));
            DatabaseReference reservaRef = FirebaseDatabase.getInstance().getReference("Reservas").child(userId);

            reservaRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        // As informações da reserva existem no banco de dados
                        String data = dataSnapshot.child("dataReserva").getValue(String.class);
                        String horario = dataSnapshot.child("horarioReserva").getValue(String.class);
                        String numeroDaMesa = dataSnapshot.child("numeroMesa").getValue(String.class);
                        String numeroDePessoas = dataSnapshot.child("numeroPessoas").getValue(String.class);

                        // Atualize sua interface com as informações da reserva
                        String reservaInfo = "Na data "+data + " no horario:" + horario + ", na mesa " + numeroDaMesa + " para " + numeroDePessoas + " pessoas";
                        txtReserva.setText(reservaInfo);
                        btnCancelarReserva.setVisibility(View.VISIBLE);
                    } else {
                        // Nenhuma reserva encontrada para o usuário
                        txtReserva.setText("Nenhuma reserva em seu nome");
                        btnCancelarReserva.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Lidar com erros de consulta, se necessário
                }
            });
        }
    }

}