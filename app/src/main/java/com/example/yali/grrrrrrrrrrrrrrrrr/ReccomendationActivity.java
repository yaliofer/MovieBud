package com.example.yali.grrrrrrrrrrrrrrrrr;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import java.util.ArrayList;

public class ReccomendationActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private ListView listView;
    private DataSnapshot dataSnapshot;
    private FirebaseUser user;
    private FirebaseDatabase mDatabase;
    private DatabaseReference dbRef;
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
        dbRef = mDatabase.getReference();
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
            media.add("Nothing");
        }
        ArrayAdapter <String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, media);
        listView.setAdapter(adapter);
    }
}
