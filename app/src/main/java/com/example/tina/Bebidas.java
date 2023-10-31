package com.example.tina;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Bebidas extends Fragment {

    private RecyclerView recyclerView;
    private CardapioAdapter cardapioAdapter;
    private List<MenuItem> pratosList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_pratos, container, false);

        recyclerView = rootView.findViewById(R.id.gridlayout);

        pratosList = new ArrayList<>();

        // Inicialize o adaptador com a lista vazia
        cardapioAdapter = new CardapioAdapter(pratosList);
        recyclerView.setAdapter(cardapioAdapter);

        // Configurando um GridLayoutManager com 2 colunas
        int numberOfColumns = 2;
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), numberOfColumns));

        Button buttonVoltarCardapio = rootView.findViewById(R.id.btn_voltar);
        buttonVoltarCardapio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCardapio();
            }
        });

        // Carregue os itens do Firebase Realtime Database
        loadItemsFromFirebase();

        return rootView;
    }

    private void loadItemsFromFirebase() {
        DatabaseReference cardapioRef = FirebaseDatabase.getInstance().getReference("Cardapio");
        cardapioRef.orderByChild("categoria").equalTo("Bebidas").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                pratosList.clear(); // Limpe a lista existente

                for (DataSnapshot itemSnapshot : dataSnapshot.getChildren()) {
                    String nome = itemSnapshot.child("nome").getValue(String.class);
                    String descricao = itemSnapshot.child("descricao").getValue(String.class);
                    double preco = itemSnapshot.child("preco").getValue(Double.class);
                    String imagem = itemSnapshot.child("imagem").getValue(String.class);

                    if (nome != null && descricao != null &&  imagem != null) {
                        pratosList.add(new MenuItem(nome, descricao, preco, imagem));
                    }
                }

                // Notificar o adapter de que os dados foram atualizados
                cardapioAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Lida com erros ao acessar o banco de dados
                // Aqui você pode tratar erros, como exibir uma mensagem de erro para o usuário
            }
        });
    }

    private void openCardapio() {
        Cardapio cardapio = new Cardapio();
        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, cardapio)
                .addToBackStack(null)
                .commit();
    }
}
