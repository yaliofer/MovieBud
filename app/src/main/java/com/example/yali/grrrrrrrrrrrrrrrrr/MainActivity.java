package com.example.yali.grrrrrrrrrrrrrrrrr;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.yuyakaido.android.cardstackview.CardStackView;
import com.yuyakaido.android.cardstackview.SwipeDirection;

import org.json.JSONArray;
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
    ImageView imageView;
    ProgressBar progressBar;
    CardStackView cardStackView;
    MediaCardAdapter adapter;

    public static void updateList (ArrayList<Media> media)
    {
        MainActivity.list.addAll(media);
        media.clear();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MainActivity.list = new ArrayList<>(30);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        cardStackView = (CardStackView)findViewById(R.id.cardStackView);
        setup();
        reload();
    }

    public void setup ()
    {
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
                if (cardStackView.getTopIndex() == adapter.getCount() - 5) {
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
        cardStackView.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run()
            {
                adapter = createMediaCardAdapter();
                cardStackView.setAdapter(adapter);
                cardStackView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }
        }, 1000);
    }

    private void paginate ()
    {
        cardStackView.setPaginationReserved();
        GetMediaTask mediaTask = new GetMediaTask (progressBar);
        mediaTask.execute(Media.getPopularMovieQuery(), Media.getPopularTVQuery(), Media.getConfigurationQuery());
        adapter.addAll(MainActivity.list);
        adapter.notifyDataSetChanged();
    }

    private MediaCardAdapter createMediaCardAdapter ()
    {
        final MediaCardAdapter adapter = new MediaCardAdapter(getApplicationContext());
        GetMediaTask mediaTask =  new GetMediaTask(progressBar);
        mediaTask.execute(Media.getPopularMovieQuery(), Media.getPopularTVQuery(), Media.getConfigurationQuery());
        adapter.addAll(MainActivity.list);
        return adapter;
    }

}

class GetMediaTask extends AsyncTask <String, Integer, ArrayList<Media>>
{
    private ProgressBar progressBar;

     GetMediaTask(ProgressBar pb)
     {
        super();
        this.progressBar = pb;
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
                    //Building Poster URL
                    String query;
                    query = Media.getBaseURL()+Media.getPosterSize()+temp.getPosterPath();
                    //Downloading the Image
                    bitmap = downlaodBitmap(query);
                    //Adding image to object
                    temp.setPoster(bitmap);
                    Log.i("doInBackground in [GetMediaTask]", "Downloaded Poster for "+temp.getTitle());
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
                    //Building Poster URL
                    String query;
                    query = Media.getBaseURL()+Media.getPosterSize()+temp.getPosterPath();
                    //Downloading the Image
                    bitmap = downlaodBitmap(query);
                    //Adding image to object
                    temp.setPoster(bitmap);
                    Log.i("doInBackground in [GetMediaTask]", "Downloaded Poster for "+temp.getTitle());
                    //Add To ArrayList
                    ret.add(temp);
                }
                Log.i("doInBackground in [GetMediaTask]", "Done");
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
        Media temp = media.get(4);
        MainActivity.updateList(media);
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
}

