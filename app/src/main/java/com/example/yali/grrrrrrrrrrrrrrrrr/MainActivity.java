package com.example.yali.grrrrrrrrrrrrrrrrr;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
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

    private static ArrayList<Media> list = null;
    private ProgressBar progressBar;
    private CardStackView cardStackView;
    private MediaCardAdapter adapter;
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private FirebaseUser user;
    private long currentPage;
    protected boolean inPagination = false;
    private boolean downloadMore = true;
    private MatchMaker matcher;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        this.user = mAuth.getCurrentUser();
        mAuth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser cUser = firebaseAuth.getCurrentUser();
                if (cUser==null)
                {
                    currentPage = 1;
                }
                if (cUser.isAnonymous())
                {
                    currentPage = 7;
                }
            }
        });

        final String s = getString(R.string.firebasePageNumber);
        this.mDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = this.mDatabase.getReference();
        DatabaseReference userRef = databaseReference.child("users").child(user.getUid());
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DataSnapshot snap =  dataSnapshot.child(s);
                currentPage = snap.getValue(long.class);
                Log.i("Value Event Listener at [userRef]", "Changed Data");
                //Listener starts only AFTER the mediatask already started
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.i("Value Event Listener at [userRef]", "Error");
                Log.i("Value Event Listener at [userRef] Error", databaseError.toException().toString());
            }
        });

        setup();
        reload();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem checkable = menu.findItem(R.id.mainMenuDownload);
        checkable.setChecked(downloadMore);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.mainMenuReverse:
                cardStackView.reverse();
                break;
            case R.id.mainMenuAccount:
                askToLogOut();
                break;
            case R.id.mainMenuDownload:
                downloadMore = !downloadMore;
                break;
            case R.id.mainMenuReccomendations:
                matcher = new MatchMaker(this.user);
                matcher.makeMatch();
                break;
        }
        return true;
    }

    public void updatePageNumber (long num)
    {
        long toSet = num+1;
        FirebaseUser user = mAuth.getCurrentUser();
        DatabaseReference userRef = mDatabase.getReference().child("users").child(user.getUid());
        DatabaseReference pageRef = userRef.child("Page Number");
        pageRef.setValue(toSet);
    }

    public void finalize (ArrayList <Media> media)
    {
        if (this.adapter==null)
        {
            this.adapter = new MediaCardAdapter(getApplicationContext());
        }
        this.adapter.addAll(media);
        this.adapter.notifyDataSetChanged();
        cardStackView.setAdapter(this.adapter);
    }

    public void askToLogOut ()
    {
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(this);
        builder.setTitle("Log Out");
        builder.setCancelable(false);
        builder.setMessage("Are you sure you want to log out?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mAuth.signOut();
                cardStackView.setEnabled(false);
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
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
                FirebaseUser user = mAuth.getCurrentUser();
                String userID = user.getUid();
                DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
                DatabaseReference userRef = rootRef.child("users").child(userID);
                DatabaseReference likedMedia = userRef.child("Liked Media");
                DatabaseReference dislikedMedia = userRef.child("Disliked Media");
                DatabaseReference unseenMedia = userRef.child("Unseen Media");
                Log.d("CardStackView", "onCardSwiped: " + direction.toString());
                Log.d("CardStackView", "topIndex: " + cardStackView.getTopIndex());
                Media swiped = adapter.getItem(0);
                adapter.remove(swiped);
                cardStackView.setAdapter(adapter);
                if (direction.equals(SwipeDirection.Right))
                {
                    likedMedia.child(swiped.getId()+"").setValue(swiped.getTitle()+"");
                }
                if (direction.equals(SwipeDirection.Left))
                {
                    dislikedMedia.child(swiped.getId()+"").setValue(swiped.getTitle()+"");
                }
                if (direction.equals(SwipeDirection.Top))
                {
                    unseenMedia.child(swiped.getId()+"").setValue(swiped.getTitle()+"");
                }
                if (adapter.getCount()<10&&!inPagination&&downloadMore)
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
                downloadForFirstUse();
            }
        }, 4000);
    }

    private void paginate ()
    {
        cardStackView.setPaginationReserved();
        this.inPagination = true;
        GetMediaTask mediaTask = new GetMediaTask (progressBar, cardStackView, this, currentPage);
        mediaTask.execute(Media.getPopularMovieQuery(), Media.getPopularTVQuery(), Media.getConfigurationQuery());
        Log.i("Media starting", "Paginate");
    }

    public void badLink ()
    {
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(this);
        builder.setTitle("Error");
        builder.setCancelable(false);
        builder.setMessage("Error in retrieveing the media. please try again soon");
        builder.setPositiveButton("Try Again", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                paginate();
            }
        });
        builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void downloadForFirstUse()
    {
        GetMediaTask mediaTask =  new GetMediaTask(progressBar, cardStackView, this, currentPage);
        mediaTask.execute(Media.getPopularMovieQuery(), Media.getPopularTVQuery(), Media.getConfigurationQuery());
    }


}

class GetMediaTask extends AsyncTask <String, Integer, ArrayList<Media>>
{
    private ProgressBar progressBar;
    private CardStackView cardStackView;
    private MainActivity mainActivity;
    private long currentPage;

     GetMediaTask(ProgressBar pb, CardStackView csv, MainActivity ma, long page)
     {
        super();
        this.progressBar = pb;
        this.cardStackView = csv;
        this.mainActivity = ma;
        this.currentPage = page;
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
            String ans = GetMediaTask.readURL(params[2]);//Params[2] is Configurations Query
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
            String link = prepLink(params[0], this.currentPage);
            if (link.endsWith("0"))
            {
                this.mainActivity.badLink();
            }
            ans = readURL(link);//params[0]
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
            }
            //Handle TV
            ans = readURL(prepLink(params[1], this.currentPage));//params[1]
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
    {//Change so it wont replace the adapter but update it
        super.onPostExecute(media);
        //MainActivity.updateList(media);
        //Add Methods to Filter Already Seen Movies
        mainActivity.finalize(media);
        mainActivity.updatePageNumber(this.currentPage);
        mainActivity.inPagination = false;
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
                return (JSONArray) obj.get("genres");
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

    private static String prepLink (String baseLink, long pageNumber)
    {
        String link;
        link = baseLink;
        link = link+"&page="+pageNumber;
        return link;
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

