package com.example.yali.grrrrrrrrrrrrrrrrr;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.yuyakaido.android.cardstackview.CardStackView;
import com.yuyakaido.android.cardstackview.SwipeDirection;

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

public class MainActivity extends AppCompatActivity {

    static ArrayList<Media> list = null;
    ProgressBar progressBar;
    CardStackView cardStackView;
    MediaCardAdapter adapter;
    //Color Palette and stuff

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setup();
        reload();
    }

    public void setup ()
    {
        MainActivity.list = new ArrayList<>(30);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        cardStackView = (CardStackView)findViewById(R.id.cardStackView);
        cardStackView.setCardEventListener(new CardStackView.CardEventListener()
        {
            @Override
            public void onCardDragging(float percentX, float percentY)
            {
                Log.d("CardStackView", "onCardDragging");
            }

            @Override
            public void onCardSwiped(SwipeDirection direction)
            {
                Log.d("CardStackView", "onCardSwiped: " + direction.toString());
                Log.d("CardStackView", "topIndex: " + cardStackView.getTopIndex());
                if (direction.equals(SwipeDirection.Right))
                {
                    Toast.makeText(getApplicationContext(), "You Like This Movie", Toast.LENGTH_SHORT).show();
                }
                if (direction.equals(SwipeDirection.Left))
                {
                    Toast.makeText(getApplicationContext(), "You Dislike This Movie", Toast.LENGTH_SHORT).show();
                }
                if (cardStackView.getTopIndex() == adapter.getCount() - 5)
                {//If Paginate needs to work, change the cardStackView adapter to the main in the MainActivity
                    Log.d("CardStackView", "Paginate: " + cardStackView.getTopIndex());
                    paginate();
                }

            }

            @Override
            public void onCardReversed()
            {
                Log.d("CardStackView", "onCardReversed");
            }

            @Override
            public void onCardMovedToOrigin()
            {
                Log.d("CardStackView", "onCardMovedToOrigin");
            }

            @Override
            public void onCardClicked(int index)
            {
                Log.d("CardStackView", "onCardClicked: " + index);
            }
        });
    }

    public void reload ()
    {
        Log.i("Reload", "Starting");
        cardStackView.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run()
            {
                adapter = null;
                createMediaCardAdapter();
                /*cardStackView.setAdapter(adapter);
                cardStackView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);*/
            }
        }, 1000);
    }

    private void paginate ()
    {
        cardStackView.setPaginationReserved();
        GetMediaTask mediaTask = new GetMediaTask (progressBar, cardStackView, adapter, this);
        //mediaTask.execute(Media.getPopularMovieQuery(), Media.getPopularTVQuery(), Media.getConfigurationQuery());
        /*adapter.addAll(MainActivity.list);
        adapter.notifyDataSetChanged();*/
    }

    private void createMediaCardAdapter ()
    {
        final MediaCardAdapter adapter = new MediaCardAdapter(getApplicationContext());
        GetMediaTask mediaTask =  new GetMediaTask(progressBar, cardStackView, adapter, this);
        mediaTask.execute(Media.getPopularMovieQuery(), Media.getPopularTVQuery(), Media.getConfigurationQuery());
    }

    public void setAdapter (MediaCardAdapter a)
    {
        this.adapter = a;
    }

}

class GetMediaTask extends AsyncTask <String, Integer, ArrayList<Media>>
{
    private ProgressBar progressBar;
    private CardStackView cardStackView;
    private MediaCardAdapter adapter;
    private MainActivity mainActivity;

     GetMediaTask(ProgressBar pb, CardStackView csv, MediaCardAdapter ad, MainActivity ma)
     {
        super();
        this.progressBar = pb;
        this.cardStackView = csv;
        this.adapter = ad;
        this.mainActivity = ma;
     }

    @Override
    protected void onPreExecute()
    {
        super.onPreExecute();
        this.progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    protected ArrayList<Media> doInBackground(String... params)
    {
        ArrayList<Media> ret = new ArrayList<>();
        String baseURL, posterSize;
        JSONArray sizes;
        Bitmap bitmap;
        try
        {
            Log.i("doInBackground in [GetMediaTask]", "Staring now");
            //Handle Configurations
            String ans = GetMediaTask.readURL(params[2]);
            //Handle Genres
            JSONArray genres = downloadGenreKeys();
            //GET URLs
            Log.i("doInBackground in [GetMediaTask]", "Downloaded Configurations JSON");
            JSONObject object = new JSONObject(ans);
            JSONObject images;
            if (object.has("images"))
            {
                images = object.getJSONObject("images");
                Log.i("JSON:", images.toString());
                baseURL = images.getString("secure_base_url");
                sizes = images.getJSONArray("poster_sizes");
                posterSize = sizes.get(sizes.length()-2).toString();
                Log.i("doInBackground in [GetMediaTask]", "Retrieved Configuration");
                Media.setBaseURL(baseURL);
                Media.setPosterSize(posterSize);
            }
            //Handle Media
            Log.i("doInBackground in [GetMediaTask]", "Starting Media Handling");
            int numMovies, numTV;
            numMovies = 15;
            numTV = 15;
            //Handle Movies
            ans = readURL(params[0]);
            Log.i("doInBackground in [GetMediaTask]", "Downloaded Movies JSON");
            //Parse the JSON
            JSONObject parentObject = new JSONObject(ans);
            JSONArray results;
            JSONObject obj;
            Media temp;
            int i, idRaw, ratingRaw;
            String title, path;
            String language;
            String genre;
            double rating;
            JSONArray ids;
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
                    title = (String)obj.get("title");
                    idRaw = (int)obj.get("id");
                    path = (String)obj.get("poster_path");
                    oof = obj.get("vote_average");
                    ids = (JSONArray) obj.get("genre_ids");
                    genre = setGenre(ids, genres);
                    if (oof instanceof Integer)
                    {
                        ratingRaw = (int)obj.get("vote_average");
                        temp = new Media ("movie", title, (long)idRaw, path, (double)ratingRaw, genre,language);
                    }
                    else
                    {
                        rating = (double)obj.get("vote_average");
                        temp = new Media ("movie", title, (long)idRaw, path, rating, genre, language);
                    }
                    Log.i("doInBackground in [GetMediaTask]", "genre-"+genre);
                    //Building Poster URL
                    String query;
                    query = Media.getBaseURL()+Media.getPosterSize()+temp.getPosterPath();
                    //Downloading the Image
                    bitmap = downlaodBitmap(query);
                    //Adding image to object
                    temp.setPoster(bitmap);
                    Log.i("doInBackground in [GetMediaTask]", "Downloaded Poster for "+temp.getTitle()+ " in place "+(ret.size()+1));
                    //Add To ArrayList
                    ret.add(temp);
                }
                Log.i("doInBackground in [GetMediaTask]", "Added all the Movies");
                Media t = ret.get(7);
            }
            //Handle TV
            ans = readURL(params[1]);
            Log.i("doInBackground in [GetMediaTask]", "Downloaded TV JSON");
            //Parse the JSON
            parentObject = new JSONObject(ans);
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
                    title = (String)obj.get("name");
                    idRaw = (int)obj.get("id");
                    path = (String)obj.get("poster_path");
                    oof = obj.get("vote_average");
                    ids = (JSONArray) obj.get("genre_ids");
                    genre = setGenre(ids, genres);
                    if (oof instanceof Integer)
                    {
                        ratingRaw = (int)obj.get("vote_average");
                        temp = new Media ("tv", title, (long)idRaw, path, (double)ratingRaw, genre, language);
                    }
                    else
                    {
                        rating = (double)obj.get("vote_average");
                        temp = new Media ("tv", title, (long)idRaw, path, rating, genre,  language);
                    }
                    //Building Poster URL
                    String query;
                    query = Media.getBaseURL()+Media.getPosterSize()+temp.getPosterPath();
                    //Downloading the Image
                    bitmap = downlaodBitmap(query);
                    //Adding image to object
                    temp.setPoster(bitmap);
                    Log.i("doInBackground in [GetMediaTask]", "Downloaded Poster for "+temp.getTitle()+" in place "+(ret.size()+1));
                    //Add To ArrayList
                    ret.add(temp);
                }
                Log.i("doInBackground in [GetMediaTask]", "Done");
                Log.i("DoInBackground", ""+ret.size());
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return ret;
    }

    @Override
    protected void onPostExecute(ArrayList<Media> media)
    {
        super.onPostExecute(media);
        //MainActivity.updateList(media);
        adapter.addAll(media);
        cardStackView.setAdapter(adapter);
        mainActivity.setAdapter(adapter);
        cardStackView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
    }

    private static Bitmap downlaodBitmap (String url)
    {
        Bitmap bitmap = null;
        try
        {
            //Downloading the Image
            URL imageURL = new URL (url);
            HttpURLConnection connection = (HttpURLConnection) imageURL.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            bitmap = BitmapFactory.decodeStream(input);
            return bitmap;
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        return bitmap;
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
            if (read != null)
            {
                read.close();
            }
        }
        return "";
    }

    private static JSONArray downloadGenreKeys ()
    {
        try
        {
            String ans = readURL(Media.genreKey);
            JSONObject obj = new JSONObject(ans);
            if (obj.has("genres"))
            {
                JSONObject object;
                JSONArray array = (JSONArray) obj.get("genres");
                return array;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }

    private static String convertGenre (JSONArray array, int id)
    {
        JSONObject object;
        try
        {
            for (int i=0; i<array.length();i++)
            {
                object = (JSONObject) array.get(i);
                if ((int)(object.get("id"))==id)
                {
                    return object.get("name").toString();
                }
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        return "None";
    }

    private static String setGenre (JSONArray ids, JSONArray genreKey)
    {
        String genre = "";
        try
        {
            if (ids.length()==1)
            {
                return convertGenre(genreKey, ids.getInt(0));
            }
            if (ids.length()>1)
            {
                return convertGenre(genreKey, ids.getInt(0))+", "+convertGenre(genreKey, ids.getInt(1));
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        return genre;
    }

}

