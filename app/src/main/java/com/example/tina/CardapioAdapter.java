package com.example.tina;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CardapioAdapter extends RecyclerView.Adapter<CardapioAdapter.ViewHolder> {

    private List<MenuItem> menuItems; // Substitua MenuItem pelo nome da sua classe de dados

    // Construtor para o adaptador
    public CardapioAdapter(List<MenuItem> menuItems) {
        this.menuItems = menuItems; // Inicialize a lista com a lista fornecida
    }

    public void addItem(MenuItem menuItem) {
        menuItems.add(menuItem);
        notifyDataSetChanged(); // Notifique o RecyclerView que os dados foram alterados
    }

    // Classe ViewHolder que representa cada item na lista
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView itemNameTextView;
        public TextView itemDescriptionTextView;
        public TextView itemPriceTextView;
        public ImageView itemImageView;

        public ViewHolder(View itemView) {
            super(itemView);
            itemNameTextView = itemView.findViewById(R.id.itemNameTextView); // Substitua pelo ID correto
            itemDescriptionTextView = itemView.findViewById(R.id.itemDescriptionTextView); // Substitua pelo ID correto
            itemPriceTextView = itemView.findViewById(R.id.itemPriceTextView); // Substitua pelo ID correto
            itemImageView = itemView.findViewById(R.id.itemImageView); // Substitua pelo ID correto


        }
    }

    // Método para criar novas visualizações (invocado pelo layout manager)
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cardapio, parent, false); // Substitua R.layout.item_cardapio pelo layout do seu item de cardápio
        return new ViewHolder(view);
    }

    // Método para substituir o conteúdo de uma visualização (invocado pelo layout manager)
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MenuItem menuItem = menuItems.get(position);

        // Configure os elementos da interface com os dados do menuItem
        holder.itemNameTextView.setText(menuItem.getName()); // Corrija para o campo correto
        holder.itemDescriptionTextView.setText(menuItem.getDescription());
        holder.itemPriceTextView.setText(String.format("R$ %.2f", menuItem.getPrice())); // Formate o preço como desejado
        // Carregue a imagem usando uma biblioteca como Picasso ou Glide (não incluída aqui)
        // Picasso.get().load(menuItem.getImageUrl()).into(holder.itemImageView);
    }

    // Método para obter o tamanho dos dados (invocado pelo layout manager)
    @Override
    public int getItemCount() {
        return menuItems.size();
    }
}
