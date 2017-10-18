package com.example.yali.grrrrrrrrrrrrrrrrr;
import android.content.Context;
import android.media.Image;
import android.os.AsyncTask;
import android.util.JsonReader;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.Toast;

import java.net.MalformedURLException;
import java.util.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import org.json.*;

import static android.widget.Toast.LENGTH_SHORT;


public class Media {
    //Setting Values
    private String type;
    private String title;
    private long id;
    private String posterPath;
    private double rating;
    private String language;
    private Image poster;
    //Static Parameters
    private static String apiKey =
            "eac80b61f4b6d4103fab65bae01e44ac";
    private static String popularMovieQuery =
            "https://api.themoviedb.org/3/movie/popular?api_key=eac80b61f4b6d4103fab65bae01e44ac&language=en-US";
    private static String popularTVQuery =
            "https://api.themoviedb.org/3/tv/popular?api_key=eac80b61f4b6d4103fab65bae01e44ac&language=en-US";
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
    /*
    //Static method to get 30 random movies or tv shows
    public static ArrayList<Media> randomiseMedia ()
    {
        ArrayList<Media> res = new ArrayList<>(30);
        try
        {
            int numMovies, numTV;
            numMovies = (int)(Math.random()*30+1);
            numTV = 30-numMovies;
            //Handle Movies
            String fq = Media.popularMovieQuery;
            String ans = "";
            //String ans = readURL(fq); //Asynctask this
            new getJsonTask().execute(ans);
            /*
            getJsonTask g = new getJsonTask();
            g.doInBackground();
             */
            /*
            Log.i("Fine till", "Here");
            Log.i("Result:", "Ans:"+ans);
            JSONObject obj = new JSONObject(ans);
            JSONArray result = obj.getJSONArray("results");
            Log.i("Done", "Done");
        }
        catch (Exception e)
        {
            Log.e("Error", "Shit");
        }
        return res;
    }

}


class getJsonTask extends AsyncTask <String, Integer, String>
{

    @Override
    protected String doInBackground(String... params)
    {
        BufferedReader read = null;
        try
        {
            URL url = new URL (params[0]);
            read = new BufferedReader(new InputStreamReader(url.openStream()));
            StringBuffer buffer = new StringBuffer();
            int c;
            char [] chars = new char [1024];
            while ((c=read.read(chars))!=-1)
            {
                buffer.append(chars, 0, c);
            }
            return buffer.toString();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally
        {
            if (read!=null)
            {
                try {
                    read.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

}
            */