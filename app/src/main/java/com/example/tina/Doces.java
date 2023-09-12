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

import java.util.ArrayList;
import java.util.List;

public class Doces extends Fragment {

    private RecyclerView recyclerView;
    private CardapioAdapter cardapioAdapter;
    private List<MenuItem> docesList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_doces, container, false);

        recyclerView = rootView.findViewById(R.id.gridlayout); // Certifique-se de substituir pelo ID correto

        docesList = new ArrayList<>();
        docesList.add(new MenuItem("Pratos 2", "Descrição da pratos 2", 4.99, ""));
        docesList.add(new MenuItem("Pratos 2", "Descrição da pratos 2", 4.99, ""));
        docesList.add(new MenuItem("Pratos 1", "Descrição da pratos 1", 5.99, ""));
        docesList.add(new MenuItem("Pratos 2", "Descrição da pratos 2", 4.99, ""));
        docesList.add(new MenuItem("Pratos 2", "Descrição da pratos 2", 4.99, ""));

        cardapioAdapter = new CardapioAdapter(docesList);
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

        return rootView;
    }

    private void openCardapio() {
        Cardapio cardapio = new Cardapio();
        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, cardapio)
                .addToBackStack(null)
                .commit();
    }
}
