package com.namerami.livetvtower.model;

import java.util.ArrayList;

public class Channel {
    private final String id;
    private final String name;
    private final String country;
    private final String flag;
    private final String logo;
    private final String group;
    private final ArrayList<String> urls;

    public Channel(
            String id,
            String name,
            String country,
            String flag,
            String logo,
            String group,
            ArrayList<String> urls
    ) {
        this.id = id;
        this.name = name;
        this.country = country;
        this.flag = flag;
        this.logo = logo;
        this.group = group;
        this.urls = urls;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCountry() {
        return country;
    }

    public String getFlag() {
        return flag;
    }

    public String getLogo() {
        return logo;
    }

    public String getGroup() {
        return group;
    }

    public ArrayList<String> getUrls() {
        return urls;
    }

    public String getUrl() {
        if (urls == null || urls.isEmpty()) {
            return "";
        }

        return urls.get(0);
    }
}