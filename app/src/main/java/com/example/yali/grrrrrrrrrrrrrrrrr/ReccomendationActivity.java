package com.example.yali.grrrrrrrrrrrrrrrrr;

import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

public class ReccomendationActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private ListView listView;
    private FirebaseUser user;
    private FirebaseDatabase mDatabase;
    private MatchMaker matchMaker;
    private TextView recText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reccomendation);

        progressBar = (ProgressBar) findViewById(R.id.reccomendationProgressBar);
        listView = (ListView) findViewById(R.id.reccomendationListView);
        recText = (TextView) findViewById(R.id.reccomendationTextView);

        recText.setVisibility(View.GONE);
        user = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance();
        matchMaker = new MatchMaker(user, this);
        matchMaker.makeMatch();

        progressBar.setVisibility(View.VISIBLE);
        mDatabase.goOnline();//Go Online For Sure
    }

    public void updateListView (ArrayList<String> media)
    {
        progressBar.setVisibility(View.GONE);
        recText.setVisibility(View.VISIBLE);
        if (media.size()==0)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Reccomendations");
            builder.setMessage("There are currently no media reccomended to you. \n" +
                    "Please play with the UI for a bit longer or wait fro new reccomendations to pop up");
            builder.setPositiveButton("Got it!", null);

            AlertDialog dialog = builder.create();
            dialog.show();
        }
        else
        {
            ArrayAdapter <String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, media);
            listView.setAdapter(adapter);
            /*GetTrailerLink task = new GetTrailerLink();
            task.execute();*/
        }
    }
}

class GetTrailerLink extends AsyncTask <HashMap<Long, String>, Integer, HashMap<String, URL>>
{
    private String link;
    private String apiLink;
    private String apiLink2;

    public GetTrailerLink ()
    {
        this.link = "https://www.youtube.com/watch?v=";
        this.apiLink = "https://api.themoviedb.org/3/movie/";
        this.apiLink2 = "/videos?api_key=eac80b61f4b6d4103fab65bae01e44ac&language=en-US";
    }

    @Override
    protected HashMap<String, URL> doInBackground(HashMap<Long, String>[] hashMaps) {
        HashMap <String, URL> retMap = new HashMap<String, URL>();
        HashMap <Long, String> map = hashMaps[0];
        /*Collection <Long> co = map.values();
        Object [] longs = co.toArray();
        Long [] lo = Arrays.copyOf(longs, longs.length, Long[].class);*/
        Set<Long> set =  map.keySet();
        Object [] longs = set.toArray();
        Long [] lo = Arrays.copyOf(longs, longs.length, Long[].class);
        ArrayList <Long> ids = new ArrayList<Long>(Arrays.asList(lo));
        for (Long id: ids)
        {
            String tempLink = apiLink+id+apiLink2;
            try
            {
                String ans = readURL(tempLink);
                JSONObject parentObject = new JSONObject(ans);
                if (parentObject.has("results"))
                {
                    JSONArray results = parentObject.getJSONArray("results");
                    JSONObject object = (JSONObject) results.get(0);
                    String key = object.getString("key");
                    String mediaName = map.get(id);
                    URL temp = new URL(link+key);
                    retMap.put(mediaName, temp);
                }

            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        return retMap;
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