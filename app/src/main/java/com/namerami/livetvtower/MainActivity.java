package com.namerami.livetvtower;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.namerami.livetvtower.adapter.ChannelAdapter;
import com.namerami.livetvtower.data.ChannelCatalogLoader;
import com.namerami.livetvtower.model.Channel;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

    private TextView appTitle;
    private TextView statusText;
    private EditText searchInput;
    private RecyclerView channelRecyclerView;

    private final List<Channel> allChannels = new ArrayList<>();
    private final List<Channel> visibleChannels = new ArrayList<>();

    private ChannelAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        appTitle = findViewById(R.id.appTitle);
        statusText = findViewById(R.id.statusText);
        searchInput = findViewById(R.id.searchInput);
        channelRecyclerView = findViewById(R.id.channelRecyclerView);

        setupRecyclerView();
        setupSearch();
        loadChannels();
    }

    private void setupRecyclerView() {
        adapter = new ChannelAdapter(visibleChannels, channel -> {
            Intent intent = new Intent(MainActivity.this, PlayerActivity.class);
            intent.putExtra("channelName", channel.getName());
            intent.putStringArrayListExtra("streamUrls", channel.getUrls());
            startActivity(intent);
        });

        channelRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        channelRecyclerView.setAdapter(adapter);
    }

    private void loadChannels() {
        appTitle.setText("Live TV Tower");
        statusText.setText("Loading channel catalogue...");

        ChannelCatalogLoader.loadChannels(new ChannelCatalogLoader.Callback() {
            @Override
            public void onSuccess(ArrayList<Channel> channels) {
                allChannels.clear();
                allChannels.addAll(channels);

                visibleChannels.clear();
                visibleChannels.addAll(channels);

                adapter.notifyDataSetChanged();
                statusText.setText(channels.size() + " channels ready");

                Toast.makeText(
                        MainActivity.this,
                        "Loaded " + channels.size() + " channels",
                        Toast.LENGTH_SHORT
                ).show();
            }

            @Override
            public void onError(String error) {
                statusText.setText("Failed to load channels");
                Toast.makeText(
                        MainActivity.this,
                        "Load failed: " + error,
                        Toast.LENGTH_LONG
                ).show();
            }
        });
    }

    private void setupSearch() {
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterChannels(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void filterChannels(String query) {
        visibleChannels.clear();

        String lowerQuery = query.toLowerCase().trim();

        if (lowerQuery.isEmpty()) {
            visibleChannels.addAll(allChannels);
        } else {
            for (Channel channel : allChannels) {
                String name = safeLower(channel.getName());
                String country = safeLower(channel.getCountry());
                String group = safeLower(channel.getGroup());

                if (name.contains(lowerQuery)
                        || country.contains(lowerQuery)
                        || group.contains(lowerQuery)) {
                    visibleChannels.add(channel);
                }
            }
        }

        adapter.notifyDataSetChanged();
        statusText.setText(visibleChannels.size() + " / " + allChannels.size() + " channels shown");
    }

    private String safeLower(String value) {
        if (value == null) {
            return "";
        }
        return value.toLowerCase();
    }
}
