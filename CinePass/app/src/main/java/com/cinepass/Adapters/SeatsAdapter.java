package com.cinepass.Adapters;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cinepass.Activities.DetailActivity;
import com.cinepass.Activities.MainActivity;
import com.cinepass.Activities.Profile;
import com.cinepass.Activities.SeatSelection;
import com.cinepass.R;

import java.util.List;


public class SeatsAdapter extends RecyclerView.Adapter<SeatsAdapter.SeatsViewHolder> {

    private List<String> seats;
    private OnSeatClickListener onSeatClickListener;
    private int selectedItem = -1;

    public SeatsAdapter(List<String> seats, OnSeatClickListener onSeatClickListener) {
        this.seats = seats;
        this.onSeatClickListener = onSeatClickListener;
    }

    @NonNull
    @Override
    public SeatsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_seats, parent, false);
        return new SeatsViewHolder(view, onSeatClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull SeatsViewHolder holder, int position) {
        String seat = seats.get(position);
        holder.seatNumber.setText(seat);

        //pra mudar a cor apos o clique
        if (selectedItem == position) {
            holder.itemView.setSelected(true);
        } else {
            holder.itemView.setSelected(false);
        }
    }


    @Override
    public int getItemCount() {
        return seats.size();
    }

    public List<String> getSeats() {
        return seats;
    }

    class SeatsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView seatNumber;
        OnSeatClickListener onSeatClickListener;

        public SeatsViewHolder(@NonNull View itemView, OnSeatClickListener onSeatClickListener) {
            super(itemView);
            seatNumber = itemView.findViewById(R.id.idSeat);
            this.onSeatClickListener = onSeatClickListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onSeatClickListener.onSeatClick(getAdapterPosition());

            // Atualiza o item selecionado e notifica o RecyclerView
            int previousSelectedItem = selectedItem;
            selectedItem = getAdapterPosition();
            notifyItemChanged(previousSelectedItem);
            notifyItemChanged(selectedItem);
        }
    }

    public interface OnSeatClickListener {
        void onSeatClick(int position);
    }
}