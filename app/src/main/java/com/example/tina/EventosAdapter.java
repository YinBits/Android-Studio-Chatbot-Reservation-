package com.example.tina;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class EventosAdapter extends ArrayAdapter<EventoItem> {
    private Context mContext;

    public EventosAdapter(Context context, ArrayList<EventoItem> eventos) {
        super(context, R.layout.item_evento, eventos);
        mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Obter o evento
        EventoItem evento = getItem(position);

        // Criar a view
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.item_evento, parent, false);

        // Atualizar a view
        TextView tituloView = view.findViewById(R.id.titulo);
        tituloView.setText(evento.getNome());
        TextView dataView = view.findViewById(R.id.data);
        dataView.setText(evento.getData());
        TextView descricaoView = view.findViewById(R.id.descricao);
        descricaoView.setText(evento.getDescricao());

        return view;
    }
}
