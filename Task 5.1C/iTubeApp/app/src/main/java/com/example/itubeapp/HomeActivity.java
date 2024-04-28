package com.example.itubeapp;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity
{
    private UserDAO userDAO;
    private UserEntity currUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.home_layout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initDatabaseAndUser();
        setupUI();
    }
    private void initDatabaseAndUser()
    {
        userDAO = AppDatabase.getInstance(this).userDAO();
        currUser = getIntent().getParcelableExtra("USER_ENTITY");

        if (currUser != null) {
            Toast.makeText(this, String.format("Welcome %s!", currUser.fullName), Toast.LENGTH_SHORT).show();
        }
    }
    private void setupUI()
    {
        EditText urlET = findViewById(R.id.youtube_url);

        getSupportFragmentManager().setFragmentResultListener("YOUTUBE_URL", this,
                (requestKey, bundle) ->
                {
                    urlET.setText(bundle.getString(requestKey));
                });

        Button addBtn = findViewById(R.id.addToPlaylistButton);
        Button playlistBtn = findViewById(R.id.myPlaylistButton);
        Button playBtn = findViewById(R.id.playButton);

        addBtn.setOnClickListener(v -> addUrlToPlaylist(urlET.getText().toString().trim()));
        playlistBtn.setOnClickListener(v -> showPlaylistFragment());
        playBtn.setOnClickListener(v -> showVideoFragment(urlET.getText().toString().trim()));
    }

    private void addUrlToPlaylist(String url)
    {
        if (!url.isEmpty()) {
            List<String> playlist = currUser.getPlaylist();
            if(playlist == null) { playlist = new ArrayList<>(); }

            playlist.add(url);
            currUser.setPlaylist(playlist);

            new Thread(() -> {
                userDAO.update(currUser);

                runOnUiThread(() -> Toast.makeText(this, "URL added to playlist", Toast.LENGTH_SHORT).show());
            }).start();
        } else {
            Toast.makeText(this, "Please enter a URL", Toast.LENGTH_SHORT).show();
        }

    }

    private void showPlaylistFragment()
    {
        PlaylistFragment playlistFragment = new PlaylistFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable("CURR_USER", currUser);
        playlistFragment.setArguments(bundle);

        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                .replace(R.id.home_layout, playlistFragment, "playlist")
                .addToBackStack("playlist")
                .commit();
    }
    private void showVideoFragment(String url)
    {
        if (!url.isEmpty()) {
            YoutubePlayerFragment ytFragment = new YoutubePlayerFragment();

            Bundle bundle = new Bundle();
            bundle.putString("VIDEO_URL", url);
            ytFragment.setArguments(bundle);

            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                    .replace(R.id.home_layout, ytFragment, "youtube_player")
                    .addToBackStack(null)
                    .commit();
        } else {
            Toast.makeText(this, "Please enter a URL", Toast.LENGTH_SHORT).show();
        }
    }
}
