package com.namerami.livetvtower;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.namerami.livetvtower.adapter.ChannelAdapter;
import com.namerami.livetvtower.data.IptvOrgLoader;
import com.namerami.livetvtower.model.Channel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class MainActivity extends Activity {

    private TextView appTitle;
    private RecyclerView channelRecyclerView;
    private EditText searchInput;
    private LinearLayout countriesChipsLayout;
    private TextView channelCountBadge;

    private final List<Channel> allChannels = new ArrayList<>();
    private final List<Channel> visibleChannels = new ArrayList<>();
    private final Set<String> uniqueCountries = new TreeSet<>();

    private String currentSearchQuery = "";
    private String selectedCountry = null;

    private ChannelAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        appTitle = findViewById(R.id.appTitle);
        channelRecyclerView = findViewById(R.id.channelRecyclerView);
        searchInput = findViewById(R.id.searchInput);
        countriesChipsLayout = findViewById(R.id.countriesChipsLayout);
        channelCountBadge = findViewById(R.id.channelCountBadge);

        setupRecyclerView();
        setupSearch();
        loadChannelsFromIptvOrg();
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

    private void loadChannelsFromIptvOrg() {
        appTitle.setText("🎬 LiveTV Tower — loading...");

        IptvOrgLoader.loadChannels(new IptvOrgLoader.Callback() {
            @Override
            public void onSuccess(ArrayList<Channel> channels) {
                allChannels.clear();
                allChannels.addAll(channels);

                // Extract unique countries
                uniqueCountries.clear();
                for (Channel channel : allChannels) {
                    if (channel.getCountry() != null && !channel.getCountry().isEmpty()) {
                        uniqueCountries.add(channel.getCountry());
                    }
                }

                // Create country filter chips
                createCountryChips();

                visibleChannels.clear();
                visibleChannels.addAll(channels);

                adapter.notifyDataSetChanged();

                appTitle.setText("🎬 LiveTV Tower");
                updateChannelCountBadge();

                Toast.makeText(
                        MainActivity.this,
                        "Loaded " + channels.size() + " channels",
                        Toast.LENGTH_SHORT
                ).show();
            }

            @Override
            public void onError(String error) {
                appTitle.setText("🎬 LiveTV Tower — failed");
                Toast.makeText(
                        MainActivity.this,
                        "Failed to load channels: " + error,
                        Toast.LENGTH_LONG
                ).show();
            }
        });
    }

    private void createCountryChips() {
        countriesChipsLayout.removeAllViews();

        // Add "All" chip
        addCountryChip("All", null, true);

        // Add country chips
        for (String country : uniqueCountries) {
            addCountryChip(country, country, false);
        }
    }

    private void addCountryChip(String label, String countryCode, boolean isSelected) {
        TextView chip = new TextView(this);
        chip.setText(label);
        chip.setTextColor(isSelected ? 0xFFFFFFFF : 0xFF9CA3AF);
        chip.setTextSize(12);
        chip.setPadding(20, 8, 20, 8);
        chip.setBackground(getDrawable(isSelected ? R.drawable.chip_bg_selected : R.drawable.chip_bg));

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(6, 0, 6, 0);
        chip.setLayoutParams(params);

        chip.setOnClickListener(v -> {
            selectedCountry = countryCode;
            applyFilters();
            updateChipSelection();
        });

        countriesChipsLayout.addView(chip);
    }

    private void updateChipSelection() {
        for (int i = 0; i < countriesChipsLayout.getChildCount(); i++) {
            TextView chip = (TextView) countriesChipsLayout.getChildAt(i);
            boolean isSelected = (i == 0 && selectedCountry == null) ||
                    (i > 0 && selectedCountry != null && chip.getText().toString().equals(selectedCountry));

            chip.setTextColor(isSelected ? 0xFFFFFFFF : 0xFF9CA3AF);
            chip.setBackground(getDrawable(isSelected ? R.drawable.chip_bg_selected : R.drawable.chip_bg));
        }
    }

    private void setupSearch() {
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                currentSearchQuery = s.toString();
                applyFilters();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void applyFilters() {
        visibleChannels.clear();

        String lowerQuery = currentSearchQuery.toLowerCase().trim();

        for (Channel channel : allChannels) {
            // Apply country filter
            if (selectedCountry != null && !selectedCountry.equals(channel.getCountry())) {
                continue;
            }

            // Apply search filter
            if (!lowerQuery.isEmpty()) {
                String name = channel.getName() == null ? "" : channel.getName().toLowerCase();
                String group = channel.getGroup() == null ? "" : channel.getGroup().toLowerCase();
                String country = channel.getCountry() == null ? "" : channel.getCountry().toLowerCase();

                if (!name.contains(lowerQuery)
                        && !group.contains(lowerQuery)
                        && !country.contains(lowerQuery)) {
                    continue;
                }
            }

            visibleChannels.add(channel);
        }

        adapter.notifyDataSetChanged();
        updateChannelCountBadge();
    }

    private void updateChannelCountBadge() {
        String text = String.format("Showing %d of %d channels", visibleChannels.size(), allChannels.size());
        if (selectedCountry != null) {
            text += " • " + selectedCountry;
        }
        channelCountBadge.setText(text);
    }
}
