package com.example.tina;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class Bebidas extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_pratos, container, false);

        // Configurações e lógica específicas do PratosFragment

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
                .replace(R.id.fragment_container, cardapio) // assuming 'fragment_container' is the container for your fragments
                .addToBackStack(null)
                .commit();
    }
}