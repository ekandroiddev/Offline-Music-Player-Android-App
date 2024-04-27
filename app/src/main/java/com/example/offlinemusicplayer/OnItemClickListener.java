package com.example.offlinemusicplayer;


public interface OnItemClickListener {
    void onItemClick(int position, String songTitle,long songDuration, String songFilePath);
}
