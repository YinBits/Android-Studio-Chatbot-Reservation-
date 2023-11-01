package com.example.tina;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.Locale;

public class Eventos extends Fragment {
    private ListView mListView;
    private EventosAdapter mAdapter;
    private TextView mEmptyView;
    private ArrayList<EventoItem> todosEventos; // Lista de todos os eventos
    private ArrayList<EventoItem> eventosFiltrados; // Eventos filtrados pela data selecionada

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_eventos, container, false);

        // Inicializar o Firebase
        FirebaseApp.initializeApp(getContext());

        // Criar a referência ao banco de dados Firebase
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

        // Criar a lista de eventos
        todosEventos = new ArrayList<>(); // Mantenha uma cópia de todos os eventos
        eventosFiltrados = new ArrayList<>(); // Inicialize a lista de eventos filtrados

        // Configurar o adaptador
        mAdapter = new EventosAdapter(getContext(), eventosFiltrados);

        // Vincular o adaptador ao ListView
        mListView = view.findViewById(R.id.listView);
        mEmptyView = view.findViewById(R.id.empty_view); // Obtém a referência ao TextView de visualização vazia
        mListView.setEmptyView(mEmptyView); // Define o TextView de visualização vazia para o ListView
        mListView.setAdapter(mAdapter);

        // Configurar o CalendarView
        CalendarView calendarView = view.findViewById(R.id.calendarView);

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                // Aqui, você obtém a data selecionada pelo usuário no formato padrão "yyyy-MM-dd"
                String dataSelecionada = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, dayOfMonth);

                // Verifica se já existem eventos filtrados para a data selecionada
                if (eventosFiltrados.isEmpty() || !eventosFiltrados.get(0).getData().equals(dataSelecionada)) {
                    // Filtrar os eventos com base na data selecionada
                    filtrarEventosPorData(dataSelecionada);
                }
            }
        });

        // Carregar todos os eventos do Firebase inicialmente
        DatabaseReference eventosRef = mDatabase.child("Eventos");
        eventosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                todosEventos.clear(); // Limpar a lista de todos os eventos

                for (DataSnapshot eventSnapshot : dataSnapshot.getChildren()) {
                    // Obter os dados do evento
                    String nome = eventSnapshot.child("nome").getValue(String.class);
                    String data = eventSnapshot.child("data").getValue(String.class);
                    String descricao = eventSnapshot.child("descricao").getValue(String.class);

                    // Verificar se os valores não são nulos antes de criar uma instância de Evento
                    if (nome != null && data != null && descricao != null) {
                        EventoItem evento = new EventoItem(nome, data, descricao);
                        todosEventos.add(evento);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Tratar erros ao carregar os eventos
                Log.e("Firebase", "Erro ao carregar os eventos: " + databaseError.getMessage());
            }
        });

        return view;
    }

    // Método para filtrar eventos por data
    private void filtrarEventosPorData(String dataSelecionada) {
        eventosFiltrados.clear(); // Limpe os eventos filtrados existentes

        for (EventoItem evento : todosEventos) {
            if (evento.getData().equals(dataSelecionada)) {
                eventosFiltrados.add(evento);
            }
        }

        if (eventosFiltrados.isEmpty()) { // Verifica se a lista de eventos filtrados está vazia
            mEmptyView.setText("Sem eventos nesta data"); // Define o texto para o TextView de visualização vazia
        } else {
            mEmptyView.setText(""); // Limpa o texto do TextView de visualização vazia
        }

        mAdapter.clear();
        mAdapter.addAll(eventosFiltrados);
        mAdapter.notifyDataSetChanged();
    }
}
