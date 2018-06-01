package com.example.yali.grrrrrrrrrrrrrrrrr;


import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MatchMaker
{
    //Setting Attributes
    private FirebaseUser user;
    private String match;
    private DatabaseReference userRef;
    private DatabaseReference matchRef;
    private DatabaseReference userLikedRef;
    private DatabaseReference userDislikedRef;
    private DatabaseReference userUnseenRef;
    private DataSnapshot dbDataSnapshot;
    private ArrayList <Long> userLikedMedia;
    private ArrayList <Long> userDislikedMedia;
    private ArrayList <Long> userUnseenMedia;
    private ArrayList <String> potentialUsers;
    private ArrayList <String> mediaToWatch;
    private ReccomendationActivity activity;

    //Constructors
    public MatchMaker (final FirebaseUser user, ReccomendationActivity activity)
    {
        this.user = user;
        FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
        DatabaseReference dbRef = mDatabase.getReference();
        this.userRef = dbRef.child("users").child(user.getUid());
        this.userLikedRef = this.userRef.child("Liked Media");
        this.userDislikedRef = this.userRef.child("Disliked Media");
        this.userUnseenRef = this.userRef.child("Unseen Media");
        this.userLikedMedia = new ArrayList<>();
        this.userDislikedMedia = new ArrayList<>();
        this.userUnseenMedia = new ArrayList<>();
        this.potentialUsers = new ArrayList<>();
        this.mediaToWatch = new ArrayList<>();
        this.activity = activity;
    }

    public void makeMatch ()
    {
        //Make Getting the value in here and triggering at the end the potential matches and then the full matching
        //getPotentialMatches(this.userLikedMedia);
        updateInfo();
    }

    private void updateInfo ()
    {
        FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
        DatabaseReference dbRef = mDatabase.getReference();
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                MatchMaker.this.dbDataSnapshot = dataSnapshot;
                dataSnapshot = dataSnapshot.child("users");
                DataSnapshot userLikedSnapshot = dataSnapshot.child(user.getUid()).child("Liked Media");
                DataSnapshot userDisikedSnapshot = dataSnapshot.child(user.getUid()).child("Disliked Media");
                DataSnapshot userUnseenSnapshot = dataSnapshot.child(user.getUid()).child("Unseen Media");

                for (DataSnapshot snap: userLikedSnapshot.getChildren())
                {
                    Long id = Long.parseLong(snap.getKey());
                    MatchMaker.this.userLikedMedia.add(id);
                }

                for (DataSnapshot snap: userDisikedSnapshot.getChildren())
                {
                    Long id = Long.parseLong(snap.getKey());
                    MatchMaker.this.userDislikedMedia.add(id);
                }

                for (DataSnapshot snap: userUnseenSnapshot.getChildren())
                {
                    Long id = Long.parseLong(snap.getKey());
                    MatchMaker.this.userUnseenMedia.add(id);
                }

                Log.i("updateInfo", "Starting getPotentialMatches");
                MatchMaker.this.getPotentialMatches(MatchMaker.this.userLikedMedia);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.i("Error in [updateInfo]", databaseError.getDetails());
            }
        });
    }

    private void getPotentialMatches (final ArrayList<Long> userLikedMedia)
    {//ADD MINIMUM OF 20 MOVIES
        Log.i("getPotentialMatches", "Started");
        DataSnapshot dataSnapshot = this.dbDataSnapshot.child("users");
        for (DataSnapshot snap : dataSnapshot.getChildren())
        {
            boolean inserted = false;
            DataSnapshot shot = snap.child("Liked Media");
            for (DataSnapshot dataSnap : shot.getChildren())
            {
                if (MatchMaker.this.userLikedMedia.contains(Long.parseLong(dataSnap.getKey()))&&inserted==false)//Maybe Like Example
                {
                    MatchMaker.this.potentialUsers.add(snap.getKey());
                    Log.i("Potential User", snap.getKey());
                    inserted = true;
                }
            }
        }
        //Do this with OrderByKey and then checking, limitToFirst(10)
        if (this.potentialUsers.size()>0)
        {
            this.getBestMatch();
        }
        else
        {
            ArrayList <String> insert = new ArrayList<>();
            insert.add("There isn't enough media in your profile to match you to someone!");
            insert.add("Please add more media to your profile to get matches");
            this.activity.updateListView(insert);
        }
        //IF THERE IS NO POTENTIAL MATCHES, GO STRAIGHT TO THE LIST VIEW UPDATE AND ADD AN ITEM THAT SAYS THAT
        //THERE ISNT ENOUGH MATERIALS AND THE USER SHOULD PLAY WITH IT MORE
    }

    private void getBestMatch ()
    {
        double tempMatchPercent;
        double maxMatchPercent = 0;
        String bestMatchID = "";
        DataSnapshot snapshot = this.dbDataSnapshot.child("users");
        for (String currentUser : this.potentialUsers)
        {
            if (!currentUser.equals(this.user.getUid()))
            {
                DataSnapshot snap = snapshot.child(currentUser); //snap - Temp User's Data Snapshot
                DataSnapshot likedRef = snap.child("Liked Media");
                DataSnapshot dislikedRef = snap.child("Disliked Media");
                ArrayList <Long> liked = new ArrayList<>();
                ArrayList <Long> disliked = new ArrayList<>();
                for (DataSnapshot shot: likedRef.getChildren())
                {
                    Long id = Long.parseLong(shot.getKey());
                /*MatchMaker.this.userLikedMedia.add(id);*/
                    liked.add(id);
                }
                for (DataSnapshot shot: dislikedRef.getChildren())
                {
                    Long id = Long.parseLong(shot.getKey());
                /*MatchMaker.this.userDislikedMedia.add(id);*/
                    disliked.add(id);
                }
                tempMatchPercent = getPercentOfMatch(liked, disliked);
                Log.i("Matching", "User Match Percent with ["+currentUser+"] is "+tempMatchPercent);
                if (tempMatchPercent>maxMatchPercent)
                {
                    maxMatchPercent = tempMatchPercent;
                    bestMatchID = currentUser;
                }
            }
        }
        this.match = bestMatchID;
        MatchMaker.this.userRef.child("Match").setValue(bestMatchID);
        MatchMaker.this.userRef.child("Match Percent").setValue(maxMatchPercent);
        this.getMoviesToWatch();
    }

    private double getPercentOfMatch (ArrayList<Long> liked, ArrayList<Long> disliked)
    {
        double percent = 0;
        int percentFrom = this.getPotentialMatchPercent(liked, disliked);
        double sameLiked = this.getSameLiked(liked);
        double sameDisliked = this.getSameDisliked(disliked);
        //percent = (((double)((sameLiked*percentFrom/100)*70/100)+(double)((sameDisliked*percentFrom/100)*30/100))*100);//Gives 0.0 percent matching
        percent = (double)((sameLiked/percentFrom*100)*70/100);
        percent = percent + (double)((sameDisliked/percentFrom*100)*30/100);
        /*percent = (double)((sameLiked*percentFrom/100.0)*70.0/100.0);
        percent = percent + (double)((sameDisliked*percentFrom/100)*30/100);*/
        return percent;
    }

    private int getPotentialMatchPercent (ArrayList <Long> liked, ArrayList <Long> disliked)
    {//Gets the checked user's liked and disliked and return the number of movies both users has seen
        int sameMedia = 0;
        for (Long lng : liked)
        {
            if (this.userLikedMedia.contains(lng)||this.userDislikedMedia.contains(lng))
            {
                sameMedia++;
            }
        }
        for (Long lng : disliked)
        {
            if (this.userLikedMedia.contains(lng)||this.userDislikedMedia.contains(lng))
            {
                sameMedia++;
            }
        }
        return sameMedia;
    }

    private double getSameLiked (ArrayList <Long> liked)
    {//Gets the checked user's liked movies and check how many of them match the user's liked movies
        double sameLiked = 0;
        for (Long lng : liked)
        {
            if (this.userLikedMedia.contains(lng))
            {
                sameLiked++;
            }
        }
        return sameLiked;
    }

    private double getSameDisliked (ArrayList <Long> disliked)
    {//Gets the checked user's disliked movies and check how many of them match the user's disliked movies
        double sameDisliked = 0;
        for (Long lng : disliked)
        {
            if (this.userDislikedMedia.contains(lng))
            {
                sameDisliked++;
            }
        }
        return sameDisliked;
    }

    private void getMoviesToWatch ()
    {
        ArrayList <String> media = new ArrayList<>();
        ArrayList <Long> userViewed = new ArrayList<>();
        DataSnapshot matchLikedShot = dbDataSnapshot.child("users").child(this.match).child("Liked Media");
        userViewed.addAll(this.userLikedMedia);
        userViewed.addAll(this.userDislikedMedia);
        for (DataSnapshot snap : matchLikedShot.getChildren())
        {
            Long id = Long.parseLong(snap.getKey());
            if (!userViewed.contains(id))
            {
                media.add(snap.getValue(String.class));
            }
        }
        this.mediaToWatch = media;
        this.activity.updateListView(media);
    }
}