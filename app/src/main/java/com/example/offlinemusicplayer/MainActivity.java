package com.example.offlinemusicplayer;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.Manifest;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnItemClickListener  {

    RecyclerView recycler;
    MusicAdapter musicAdapter;

    ArrayList<SongRowModel> allSonglist = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        recycler=findViewById(R.id.recyclerView);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recycler.setLayoutManager(layoutManager);

        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.teal_200));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_MEDIA_AUDIO)!= PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_MEDIA_AUDIO},123);
            }
            else {
                findSong();
            }
        }
        else{
            if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},123);
            }
            else {
                findSong();
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 123) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                findSong();
                Toast.makeText(this, "granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "permition is require, Allow us from setting", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void findSong() {
        Uri musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.DATA
        };
        String selection = MediaStore.Audio.Media.IS_MUSIC + "!= 0";
        try (Cursor cursor = getContentResolver().query(musicUri, projection, selection, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {

                do {
                    String title = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
                    Log.d(TAG, "Found song title: " + title);
                    int duration = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));
                    String artist = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
                    String pathData = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));

                    SongRowModel songRowModel = new SongRowModel(title, artist, duration, pathData);
                    if (new File(songRowModel.getData()).exists()) {
                        allSonglist.add(songRowModel);
                    }
                } while (cursor.moveToNext());
                displaysong(allSonglist);
            } else {
                Log.w(TAG, "Cursor is null or empty, no songs found!");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error finding songs: " + e.getMessage());
        }
    }

    private void displaysong(List<SongRowModel> musicList) {
        if (musicList.isEmpty()){
            Toast.makeText(this, "No songs", Toast.LENGTH_SHORT).show();
        } else {
            musicAdapter = new MusicAdapter(getApplicationContext(), musicList, this);
            recycler.setAdapter(musicAdapter);
        }
    }


    @Override
    public void onItemClick(int position, String songTitle,long songDuration, String songFilePath) {
        Toast.makeText(this, "Item clicked at position: " + position, Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(MainActivity.this, Music_Playing_activity.class);
        intent.putExtra("songList", allSonglist);
        intent.putExtra("position", position);
        intent.putExtra("title", songTitle);
        intent.putExtra("duration", songDuration);
        intent.putExtra("filePath", songFilePath);
        startActivity(intent);
    }
}