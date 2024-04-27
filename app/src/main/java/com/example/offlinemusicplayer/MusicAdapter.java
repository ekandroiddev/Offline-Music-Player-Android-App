package com.example.offlinemusicplayer;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MusicAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    List<SongRowModel> songRowModels;

    private final OnItemClickListener listener;

    public MusicAdapter(Context context, List<SongRowModel> songRowModels, OnItemClickListener listener ) {
        this.context = context;
        this.songRowModels = songRowModels;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.song_item_view,parent,false);
        return new MusicViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        SongRowModel songRowModel = songRowModels.get(position);
        MusicViewHolder musicViewHolder = (MusicViewHolder) holder;

        musicViewHolder.MusicName.setText(String.valueOf(songRowModel.getTitle()));
        musicViewHolder.MusicDuration.setText(String.valueOf(songRowModel.getDuration()));

        musicViewHolder.itemView.setOnClickListener(view -> {
            Toast.makeText(context, songRowModel.getTitle(), Toast.LENGTH_SHORT).show();
            if (listener != null) {
                listener.onItemClick(position,songRowModel.getTitle(),songRowModel.getDuration(),songRowModel.getData() );
            }
        });
    }

    @Override
    public int getItemCount() {
        return songRowModels.size();
    }

    public static class MusicViewHolder extends RecyclerView.ViewHolder{

        TextView MusicName, MusicDuration;

        public MusicViewHolder(@NonNull View itemView) {
            super(itemView);
            MusicName = itemView.findViewById(R.id.song_name);
            MusicDuration = itemView.findViewById(R.id.duration);
        }
    }
}

