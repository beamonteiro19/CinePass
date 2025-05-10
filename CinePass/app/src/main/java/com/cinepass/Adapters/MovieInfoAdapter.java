package com.cinepass.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.cinepass.R;
import java.util.List;

public class MovieInfoAdapter extends RecyclerView.Adapter<MovieInfoAdapter.MovieInfoViewHolder> {

    private List<MovieInfo> movieInfoList;
    private OnTimeClickListener onTimeClickListener;

    public interface OnTimeClickListener {
        void onTimeClick(String time);
    }

    public MovieInfoAdapter(List<MovieInfo> movieInfoList, OnTimeClickListener onTimeClickListener) {
        this.movieInfoList = movieInfoList;
        this.onTimeClickListener = onTimeClickListener;
    }

    @NonNull
    @Override
    public MovieInfoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_info_movie, parent, false);
        return new MovieInfoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieInfoViewHolder holder, int position) {
        MovieInfo movieInfo = movieInfoList.get(position);
        holder.textInfoTypeMovie.setText(movieInfo.getType());
        holder.textInfoAudio.setText(movieInfo.getAudio());
        holder.textMovieSchedule1.setText(movieInfo.getSchedule1());
        holder.textMovieSchedule2.setText(movieInfo.getSchedule2());

        holder.textMovieSchedule1.setOnClickListener(v -> onTimeClickListener.onTimeClick(movieInfo.getSchedule1()));
        holder.textMovieSchedule2.setOnClickListener(v -> onTimeClickListener.onTimeClick(movieInfo.getSchedule2()));
    }

    @Override
    public int getItemCount() {
        return movieInfoList.size();
    }

    public static class MovieInfoViewHolder extends RecyclerView.ViewHolder {

        TextView textInfoTypeMovie;
        TextView textInfoAudio;
        TextView textMovieSchedule1;
        TextView textMovieSchedule2;

        public MovieInfoViewHolder(@NonNull View itemView) {
            super(itemView);
            textInfoTypeMovie = itemView.findViewById(R.id.textInfoTypeMovie);
            textInfoAudio = itemView.findViewById(R.id.textInfoAudio);
            textMovieSchedule1 = itemView.findViewById(R.id.textMovieSchedule1);
            textMovieSchedule2 = itemView.findViewById(R.id.textMovieSchedule2);
        }
    }

    public static class MovieInfo {
        private String type;
        private String audio;
        private String schedule1;
        private String schedule2;

        public MovieInfo(String type, String audio, String schedule1, String schedule2) {
            this.type = type;
            this.audio = audio;
            this.schedule1 = schedule1;
            this.schedule2 = schedule2;
        }

        public String getType() {
            return type;
        }

        public String getAudio() {
            return audio;
        }

        public String getSchedule1() {
            return schedule1;
        }

        public String getSchedule2() {
            return schedule2;
        }
    }
}
