package com.example.kevin.watsoninnovation;

public class DBQuest {
    public String description;
    public String places;
    public String teaser;
    public String title;
    public long time;
    public double lat;
    public double lon;

    public DBQuest(String description, String places, String teaser, String title, long time, double lat, double lon) {
        this.description = description;
        this.places = places;
        this.teaser = teaser;
        this.title = title;
        this.time = time;
        this.lat = lat;
        this.lon = lon;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPlaces() {
        return places;
    }

    public void setPlaces(String places) {
        this.places = places;
    }

    public String getTeaser() {
        return teaser;
    }

    public void setTeaser(String teaser) {
        this.teaser = teaser;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }
}
