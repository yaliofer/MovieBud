package com.example.yali.grrrrrrrrrrrrrrrrr;
import android.media.Image;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public TextView tv;
    private static String poster = "/9E2y5Q7WlCVNEhP5GiVTjhEhx1o.jpg";

    public static void setPoster (String set)
    {
        MainActivity.poster = set;
    }

    public static String getPoster ()
    {
        return MainActivity.poster;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv = (TextView) findViewById(R.id.tv);
        new GetMediaTask().execute(Media.getPopularMovieQuery(), Media.getPopularTVQuery());
        new GetPhotoTask().execute(Media.getConfigurationQuery(), MainActivity.poster);
        //Toast.makeText(getApplicationContext(), GetMediaTask.text, Toast.LENGTH_SHORT).show();
    }


}

class GetMediaTask extends AsyncTask <String, Integer, ArrayList<Media>>
{
    @Override
    protected ArrayList<Media> doInBackground(String... params)
    {
        ArrayList<Media> ret = new ArrayList<Media>();
        try
        {
            Log.i("Starting task", "Now");
            int numMovies, numTV;
            numMovies = 15;
            numTV = 15;
            //Handle Movies
            String ans;
            Log.i("Attempting to read URL", "Now");
            ans = readURL(params[0]);
            Log.i("Success! ", "Data successfully recovered from TMDb");
            Log.i("Answer: ", ans);
            //Parse the JSON
            JSONObject parentObject = new JSONObject(ans);
            JSONArray results;
            JSONObject obj;
            Media temp;
            int i, idRaw, ratingRaw;
            String title, path;
            String language;
            double rating;
            Object oof;
            if (parentObject.has("results"))
            {
                results = parentObject.getJSONArray("results");
                for (i=0;i<30&&results.length()>0&&i<numMovies;i++)
                {
                    obj = (JSONObject) results.get(i);
                    language = (String)obj.get("original_language");
                    if (language.equals("en"))
                    {
                        language = "English";
                    }
                    //Build Media
                    Log.i("Obj:", obj.toString());
                    title = (String)obj.get("original_title");
                    idRaw = (int)obj.get("id");
                    path = (String)obj.get("poster_path");
                    oof = obj.get("vote_average");
                    if (oof instanceof Integer)
                    {
                        ratingRaw = (int)obj.get("vote_average");
                        temp = new Media ("movie", title, (long)idRaw, path, (double)ratingRaw, language);
                    }
                    else
                    {
                        rating = (double)obj.get("vote_average");
                        temp = new Media ("movie", title, (long)idRaw, path, rating, language);
                    }
                    //rating = (double)obj.get("vote_average");
                    ret.add(temp);
                }
                Log.i("Success! ", "Successfully added all the movies");
                Media t = ret.get(7);
                Log.i("Prove: ", t.toString());
                Log.i("Number: ", ret.size()+"");
            }
            //Handle TV
            ans = readURL(params[1]);
            Log.i("Success! ", "Data successfully recovered from TMDb");
            Log.i("Answer: ", ans);
            //Parse the JSON
            parentObject = new JSONObject(ans);
            results = null;
            if (parentObject.has("results"))
            {
                results = parentObject.getJSONArray("results");
                for (i=0;i<30&&results.length()>0&&i<numTV;i++)
                {
                    obj = (JSONObject)results.get(i);
                    language = (String)obj.get("original_language");
                    if (language.equals("en"))
                    {
                        language = "English";
                    }
                    //Build Media
                    title = (String)obj.get("original_name");
                    idRaw = (int)obj.get("id");
                    path = (String)obj.get("poster_path");
                    oof = obj.get("vote_average");
                    if (oof instanceof Integer)
                    {
                        ratingRaw = (int)obj.get("vote_average");
                        temp = new Media ("tv", title, (long)idRaw, path, (double)ratingRaw, language);
                    }
                    else
                    {
                        rating = (double)obj.get("vote_average");
                        temp = new Media ("tv", title, (long)idRaw, path, rating, language);
                    }
                    ret.add(temp);
                }
                Log.i("Success!", "Added TV Shows");
                Media t = ret.get(17);
                Log.i("Prove:" , t.toString());
                Log.i("Number: ", ret.size()+"");
                t = ret.get(7);
                Log.i("Prove:" , t.toString());
                MainActivity.setPoster(t.getPosterPath());
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return ret;
    }


    public static String readURL(String urlString) throws IOException {
        BufferedReader read = null;
        try {
            URL url = new URL(urlString);
            Log.i("URL", "Fine");
            URLConnection urlConnection = url.openConnection();
            Log.i("Open C0nnection", "Successfully");
            InputStream gett = urlConnection.getInputStream();
            Log.i("Got Input Stream", "Successfully");
            InputStreamReader is = new InputStreamReader(gett);
            Log.i("IS built", "!");
            read = new BufferedReader(is);
            Log.i("Opened", "Stream");
            StringBuffer buffer = new StringBuffer();
            int c;
            char[] chars = new char[1024];
            while ((c = read.read(chars)) != -1) {
                buffer.append(chars, 0, c);
            }
            return buffer.toString();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }


        finally {
            if (read != null) {
                read.close();
                Log.i("Error in getting the JSON", "Try again");
            }
        }
        return "";
    }
}

class GetPhotoTask extends AsyncTask <String, Integer, Image>
{

    @Override
    protected Image doInBackground(String... params)
    {
        Image image = null;
        String baseURL, posterSize;
        JSONArray sizes;
        try
        {
            Log.i("Downloading Poster Starting", "Now");
            String ans = GetMediaTask.readURL(params[0]);
            //GET URLs
            Log.i("Success! ", "Data successfully recovered from TMDb");
            Log.i("JSON:", ans);
            JSONObject object = new JSONObject(ans);
            JSONObject images;
            if (object.has("images"))
            {
                images = object.getJSONObject("images");
                Log.i("Retrieved", "JSON images file");
                Log.i("JSON:", images.toString());
                baseURL = images.getString("secure_base_url");
                sizes = images.getJSONArray("poster_sizes");
                posterSize = sizes.get(sizes.length()-2).toString();
                Log.i("Base URL", baseURL);
                Log.i("Size", posterSize);
                Media.setBaseURL(baseURL);
                Media.setPosterSize(posterSize);
            }
            //Build Poster URL
            String query;
            query = Media.getBaseURL()+Media.getPosterSize()+params[1];
            Log.i("Poster URL", query);
            //Downloading the Image
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        return image;
    }
}
