package com.example.offlinemusicplayer;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Music_Playing_activity extends AppCompatActivity implements  MediaPlayer.OnCompletionListener {

    ImageButton downBtn, previousBtn,nextBtn,playPauseBtn;
    ImageView musicAlbum;
    TextView musicTitle,currentPosition,fullDuration;
    SeekBar seekBar;

    MediaPlayer mediaPlayer;

    ArrayList<SongRowModel> songList;
    String title;
    int duration;
    int position;
    String filePath;
    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_music_playing);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        downBtn=findViewById(R.id.downBtn);
        previousBtn=findViewById(R.id.previousBtn);
        nextBtn=findViewById(R.id.nextBtn);
        playPauseBtn=findViewById(R.id.playPauseBtn);
        musicAlbum=findViewById(R.id.musicAlbum);
        musicTitle=findViewById(R.id.musicTitle);
        seekBar=findViewById(R.id.seekBar);
        currentPosition=findViewById(R.id.currentPosition);
        fullDuration=findViewById(R.id.fullDuration);

        songList = getIntent().getParcelableExtra("allSongs");
        title = getIntent().getStringExtra("title");
        duration = getIntent().getIntExtra("duration", 0);
        filePath = getIntent().getStringExtra("filePath");
        position = getIntent().getIntExtra("position",0);

        setData();
        updateSeekBar();

        mediaPlayer = new MediaPlayer();

        playMusic(position);

        playPauseBtn.setOnClickListener(v -> {
            playPause();
        });
        previousBtn.setOnClickListener(v -> previous());
        nextBtn.setOnClickListener(v -> next());
        downBtn.setOnClickListener(v -> {
            Intent intent = new Intent(Music_Playing_activity.this, MainActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_down_enter, R.anim.slide_down_exit);
        });


        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // Handle progress change
                if (fromUser) {
                    // Seek to the selected progress
                    mediaPlayer.seekTo(progress);
                    currentPosition.setText(formatDuration(progress));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Called when user starts touching the SeekBar
                mediaPlayer.pause();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Called when user stops touching the SeekBar
                mediaPlayer.start();
            }
        });

    }

    @SuppressLint("DefaultLocale")
    private String formatDuration(int durationInMillis) {
        int seconds = durationInMillis / 1000;
        int minutes = seconds / 60;
        seconds = seconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    private void updateSeekBar() {
        seekBar.setProgress(mediaPlayer.getCurrentPosition());
        if (mediaPlayer.isPlaying()) {
            Runnable runnable = this::updateSeekBar;
            handler.postDelayed(runnable, 1000); // Update every second
        }
    }

    private void playPause() {
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.start();
        }
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }

    private void next() {
        position++;
        if (position >= songList.size()) {
            position = 0;
        }
        playMusic(position);
    }

    private void previous() {
        position--;
        if (position < 0) {
            position = songList.size() - 1;
        }
        playMusic(position);
    }

    private void setData() {
        musicTitle.setText(title);
        seekBar.setMax(duration);

    }

    private void playMusic(int position) {
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(songList.get(position).getData());
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException ignored) {

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
            handler.removeCallbacksAndMessages(null);
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        next();
    }
}