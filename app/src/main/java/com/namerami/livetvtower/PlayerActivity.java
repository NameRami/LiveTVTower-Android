package com.namerami.livetvtower;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.media3.common.MediaItem;
import androidx.media3.common.PlaybackException;
import androidx.media3.common.Player;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.PlayerView;

import java.util.ArrayList;

public class PlayerActivity extends Activity {

    private PlayerView playerView;
    private ExoPlayer player;

    private ArrayList<String> streamUrls;
    private String channelName;
    private int currentUrlIndex = 0;
    private boolean isTryingFallback = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );

        setContentView(R.layout.activity_player);

        playerView = findViewById(R.id.playerView);
        channelName = getIntent().getStringExtra("channelName");
        streamUrls = getIntent().getStringArrayListExtra("streamUrls");

        if (streamUrls == null || streamUrls.isEmpty()) {
            Toast.makeText(this, "No stream found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Toast.makeText(this, channelName, Toast.LENGTH_SHORT).show();
        startPlayer();
    }

    private void startPlayer() {
        player = new ExoPlayer.Builder(this).build();
        playerView.setPlayer(player);

        player.addListener(new Player.Listener() {
            @Override
            public void onPlayerError(PlaybackException error) {
                tryNextStream();
            }
        });

        playCurrentStream();
    }

    private void playCurrentStream() {
        if (player == null) {
            return;
        }

        if (currentUrlIndex >= streamUrls.size()) {
            Toast.makeText(this, "Channel offline or unsupported", Toast.LENGTH_LONG).show();
            return;
        }

        String url = streamUrls.get(currentUrlIndex);

        MediaItem mediaItem = new MediaItem.Builder()
                .setUri(Uri.parse(url))
                .build();

        player.stop();
        player.clearMediaItems();
        player.setMediaItem(mediaItem);
        player.prepare();
        player.play();
        isTryingFallback = false;
    }

    private void tryNextStream() {
        if (isTryingFallback) {
            return;
        }

        isTryingFallback = true;
        currentUrlIndex++;

        if (currentUrlIndex < streamUrls.size()) {
            Toast.makeText(this, "Trying backup stream...", Toast.LENGTH_SHORT).show();
            playCurrentStream();
        } else {
            Toast.makeText(this, "All streams failed", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        releasePlayer();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releasePlayer();
    }

    private void releasePlayer() {
        if (player != null) {
            player.release();
            player = null;
        }
    }
}
