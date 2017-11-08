package com.example.yali.grrrrrrrrrrrrrrrrr;
import android.graphics.Bitmap;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;


public class Media {
    //Setting Values
    private String type;
    private String title;
    private long id;
    private String posterPath;
    private double rating;
    //Add Genre
    private String language;
    private Bitmap poster;
    //Static Parameters
    private static String apiKey =
            "eac80b61f4b6d4103fab65bae01e44ac";
    private static String popularMovieQuery =
            "https://api.themoviedb.org/3/movie/popular?api_key=eac80b61f4b6d4103fab65bae01e44ac&language=en-US";
    private static String popularTVQuery =
            "https://api.themoviedb.org/3/tv/popular?api_key=eac80b61f4b6d4103fab65bae01e44ac&language=en-US";
    private static String configurationQuery =
            "https://api.themoviedb.org/3/configuration?api_key=eac80b61f4b6d4103fab65bae01e44ac";
    private static String baseURL = "";
    private static String posterSize = "";
    //Constructors

    public Media(String type, String title, long id, String posterPath, double rating, String language) {
        this.type = type;
        this.title = title;
        this.id = id;
        this.posterPath = posterPath;
        this.rating = rating;
        this.language = language;
    }

    public Media(Media media) {
        this.type = media.type;
        this.title = media.title;
        this.id = media.id;
        this.posterPath = media.posterPath;
        this.rating = media.rating;
        this.language = media.language;
    }
    //GETS AND SETS

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public static String getApiKey() {
        return apiKey;
    }

    public static void setApiKey(String apiKey) {
        Media.apiKey = apiKey;
    }

    public static String getPopularMovieQuery() {
        return popularMovieQuery;
    }

    public static void setPopularMovieQuery(String popularMovieQuery) {
        Media.popularMovieQuery = popularMovieQuery;
    }

    public static String getPopularTVQuery() {
        return popularTVQuery;
    }

    public static void setPopularTVQuery(String popularTVQuery) {
        Media.popularTVQuery = popularTVQuery;
    }

    public static String getConfigurationQuery() {
        return configurationQuery;
    }

    public static String getBaseURL() {
        return baseURL;
    }

    public static String getPosterSize() {
        return posterSize;
    }

    public static void setBaseURL(String baseURL) {
        Media.baseURL = baseURL;
    }

    public static void setPosterSize(String posterSize) {
        Media.posterSize = posterSize;
    }

    public Bitmap getPoster() {
        return poster;
    }

    public void setPoster(Bitmap poster) {
        this.poster = poster;
    }

    //Other Methods

    @Override
    public String toString() {
        return "Media{" +
                "type='" + type + '\'' +
                ", title='" + title + '\'' +
                ", id=" + id +
                ", posterPath='" + posterPath + '\'' +
                ", rating=" + rating +
                ", language='" + language + '\'' +
                '}';
    }

    //Static Method to read URL
    private static String readURL(String urlString) throws IOException {
        BufferedReader read = null;
        try {
            URL url = new URL(urlString);
            read = new BufferedReader(new InputStreamReader(url.openStream()));
            StringBuffer buffer = new StringBuffer();
            int c;
            char[] chars = new char[1024];
            while ((c = read.read(chars)) != -1) {
                buffer.append(chars, 0, c);
            }
            return buffer.toString();
        } finally {
            if (read != null) {
                read.close();
            }
        }
    }
}