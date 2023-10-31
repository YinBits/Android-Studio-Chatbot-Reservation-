package com.example.tina;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide; // Importe a classe Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy; // Importe essa classe para otimização de cache

import java.util.List;

public class CardapioAdapter extends RecyclerView.Adapter<CardapioAdapter.ViewHolder> {

    private List<MenuItem> menuItems;

    public CardapioAdapter(List<MenuItem> menuItems) {
        this.menuItems = menuItems;
    }

    public void addItem(MenuItem menuItem) {
        menuItems.add(menuItem);
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView itemNameTextView;
        public TextView itemDescriptionTextView;
        public TextView itemPriceTextView;
        public ImageView itemImageView;

        public ViewHolder(View itemView) {
            super(itemView);
            itemNameTextView = itemView.findViewById(R.id.itemNameTextView);
            itemDescriptionTextView = itemView.findViewById(R.id.itemDescriptionTextView);
            itemPriceTextView = itemView.findViewById(R.id.itemPriceTextView);
            itemImageView = itemView.findViewById(R.id.itemImageView);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cardapio, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MenuItem menuItem = menuItems.get(position);

        holder.itemNameTextView.setText(menuItem.getNome());
        holder.itemDescriptionTextView.setText(menuItem.getDescricao());
        holder.itemPriceTextView.setText(String.format("R$ %.2f", menuItem.getPreco()));

        // Use Glide para carregar a imagem da variável "imagem"
        Glide.with(holder.itemImageView.getContext())
                .load(menuItem.getImagem()) // Passe a variável de imagem
                .diskCacheStrategy(DiskCacheStrategy.ALL) // Otimização de cache
                .into(holder.itemImageView);
    }

    @Override
    public int getItemCount() {
        return menuItems.size();
    }
}
