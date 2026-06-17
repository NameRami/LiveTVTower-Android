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

public class IptvOrgLoader {

    private static final String CHANNELS_URL = "https://iptv-org.github.io/api/channels.json";
    private static final String STREAMS_URL = "https://iptv-org.github.io/api/streams.json";

    public interface Callback {
        void onSuccess(ArrayList<Channel> channels);
        void onError(String error);
    }

    public static void loadChannels(Callback callback) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler mainHandler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            try {
                String channelsText = fetchText(CHANNELS_URL);
                String streamsText = fetchText(STREAMS_URL);

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

                    streamMap.get(channelId).add(streamUrl);
                }

                ArrayList<Channel> result = new ArrayList<>();

                for (int i = 0; i < channelsJson.length(); i++) {
                    JSONObject channelObj = channelsJson.getJSONObject(i);

                    String id = channelObj.optString("id", "");
                    boolean isNsfw = channelObj.optBoolean("is_nsfw", false);

                    if (id.isEmpty()) {
                        continue;
                    }

                    if (isNsfw) {
                        continue;
                    }

                    if (!streamMap.containsKey(id)) {
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

                    String flag = countryFlag(country);

                    ArrayList<String> urls = streamMap.get(id);

                    Channel channel = new Channel(
                            id,
                            name,
                            country,
                            flag,
                            logo,
                            group,
                            urls
                    );

                    result.add(channel);
                }

                Collections.sort(result, new Comparator<Channel>() {
                    @Override
                    public int compare(Channel c1, Channel c2) {
                        return c1.getName().compareToIgnoreCase(c2.getName());
                    }
                });

                mainHandler.post(() -> callback.onSuccess(result));

            } catch (Exception e) {
                mainHandler.post(() -> callback.onError(e.getMessage()));
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
            connection.setReadTimeout(20000);
            connection.setRequestProperty("User-Agent", "LiveTVTower-Android");

            int responseCode = connection.getResponseCode();

            if (responseCode < 200 || responseCode >= 300) {
                throw new Exception("HTTP error: " + responseCode);
            }

            InputStream inputStream = connection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            StringBuilder builder = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                builder.append(line);
                builder.append("\n");
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

        code = code.toUpperCase();

        char first = code.charAt(0);
        char second = code.charAt(1);

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