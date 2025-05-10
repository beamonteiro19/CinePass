package com.cinepass.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cinepass.R;

import java.util.List;

public class SessionDateAdapter extends RecyclerView.Adapter<SessionDateAdapter.ViewHolder> {

    private List<String> sessionDates;
    private OnDateClickListener onDateClickListener;

    public interface OnDateClickListener {
        void onDateClick(String date);
    }

    public SessionDateAdapter(List<String> sessionDates, OnDateClickListener onDateClickListener) {
        this.sessionDates = sessionDates;
        this.onDateClickListener = onDateClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_session, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String sessionDate = sessionDates.get(position);
        holder.bind(sessionDate);
        holder.itemView.setOnClickListener(v -> onDateClickListener.onDateClick(sessionDate));
    }

    @Override
    public int getItemCount() {
        return sessionDates.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView textSessionDate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textSessionDate = itemView.findViewById(R.id.textSessionDate);
        }

        public void bind(String sessionDate) {
            textSessionDate.setText(sessionDate);
        }
    }
}
