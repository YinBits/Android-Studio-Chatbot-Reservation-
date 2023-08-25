package com.example.tina;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


public class Cardapio extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_cardapio, container, false);

        Button buttonPratos = rootView.findViewById(R.id.btn_pratos);
        buttonPratos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFragment(new Pratos());
            }
        });

        Button buttonPorcoes = rootView.findViewById(R.id.btn_porcoes);
        buttonPorcoes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFragment(new Porcoes());
            }
        });

        Button buttonPizzas = rootView.findViewById(R.id.btn_pizzas);
        buttonPizzas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFragment(new Pizzas());
            }
        });

        Button buttonBebidas = rootView.findViewById(R.id.btn_bebidas);
        buttonBebidas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFragment(new Bebidas());
            }
        });

        Button buttonDoces = rootView.findViewById(R.id.btn_doces);
        buttonDoces.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFragment(new Doces());
            }
        });

        return rootView;
    }

    private void openFragment(Fragment fragment) {
        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment) // assuming 'fragment_container' is the container for your fragments
                .addToBackStack(null)
                .commit();
    }
}