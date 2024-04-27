package com.example.offlinemusicplayer;


import java.io.Serializable;

public class SongRowModel implements Serializable{
    private final String title;
    private final String artist;
    private final long duration;
    private final String data;

    public SongRowModel(String title, String artist, long duration, String data) {
        this.title = title;
        this.artist = artist;
        this.duration = duration;
        this.data = data;
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public long getDuration() {
        return duration;
    }

    public String getData() {
        return data;
    }


}

