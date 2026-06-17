package com.namerami.livetvtower.data;

import android.os.Handler;
import android.os.Looper;

import com.namerami.livetvtower.model.Channel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChannelCatalogLoader {

    private static final String CHANNELS_ENDPOINT = "https://iptv-org.github.io/api/channels.json";
    private static final String STREAMS_ENDPOINT = "https://iptv-org.github.io/api/streams.json";

    public interface Callback {
        void onSuccess(ArrayList<Channel> channels);
        void onError(String error);
    }

    public static void loadChannels(Callback callback) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler mainHandler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            try {
                String channelsText = fetchText(CHANNELS_ENDPOINT);
                String streamsText = fetchText(STREAMS_ENDPOINT);

                JSONArray channelsJson = new JSONArray(channelsText);
                JSONArray streamsJson = new JSONArray(streamsText);

                Map<String, ArrayList<String>> streamMap = new HashMap<>();

                for (int i = 0; i < streamsJson.length(); i++) {
                    JSONObject streamObj = streamsJson.getJSONObject(i);

                    String channelId = streamObj.optString("channel", "");
                    String streamUrl = streamObj.optString("url", "");

                    if (channelId.isEmpty() || streamUrl.isEmpty()) {
                        continue;
                    }

                    if (!streamMap.containsKey(channelId)) {
                        streamMap.put(channelId, new ArrayList<>());
                    }

                    ArrayList<String> urls = streamMap.get(channelId);
                    if (urls != null && !urls.contains(streamUrl)) {
                        urls.add(streamUrl);
                    }
                }

                ArrayList<Channel> result = new ArrayList<>();

                for (int i = 0; i < channelsJson.length(); i++) {
                    JSONObject channelObj = channelsJson.getJSONObject(i);

                    String id = channelObj.optString("id", "");
                    boolean isNsfw = channelObj.optBoolean("is_nsfw", false);

                    if (id.isEmpty() || isNsfw || !streamMap.containsKey(id)) {
                        continue;
                    }

                    String name = channelObj.optString("name", id);
                    String country = channelObj.optString("country", "");
                    String logo = channelObj.optString("logo", "");
                    String group = "TV";

                    JSONArray categories = channelObj.optJSONArray("categories");
                    if (categories != null && categories.length() > 0) {
                        group = categories.optString(0, "TV");
                    }

                    ArrayList<String> urls = streamMap.get(id);
                    if (urls == null || urls.isEmpty()) {
                        continue;
                    }

                    result.add(new Channel(
                            id,
                            name,
                            country,
                            countryFlag(country),
                            logo,
                            group,
                            urls
                    ));
                }

                Collections.sort(result, new Comparator<Channel>() {
                    @Override
                    public int compare(Channel first, Channel second) {
                        return first.getName().compareToIgnoreCase(second.getName());
                    }
                });

                mainHandler.post(() -> callback.onSuccess(result));
            } catch (Exception e) {
                mainHandler.post(() -> callback.onError(e.getMessage() == null ? "Unknown error" : e.getMessage()));
            } finally {
                executor.shutdown();
            }
        });
    }

    private static String fetchText(String urlString) throws Exception {
        HttpURLConnection connection = null;

        try {
            URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(15000);
            connection.setReadTimeout(25000);
            connection.setRequestProperty("User-Agent", "LiveTVTower-Android/0.1");

            int responseCode = connection.getResponseCode();
            if (responseCode < 200 || responseCode >= 300) {
                throw new Exception("HTTP error " + responseCode);
            }

            InputStream inputStream = connection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line).append('\n');
            }

            reader.close();
            return builder.toString();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private static String countryFlag(String code) {
        if (code == null || code.length() != 2) {
            return "📺";
        }

        String upper = code.toUpperCase();
        char first = upper.charAt(0);
        char second = upper.charAt(1);

        if (first < 'A' || first > 'Z' || second < 'A' || second > 'Z') {
            return "📺";
        }

        int base = 0x1F1E6;
        StringBuilder flag = new StringBuilder();
        flag.appendCodePoint(base + first - 'A');
        flag.appendCodePoint(base + second - 'A');
        return flag.toString();
    }
}
