package com.example.yali.grrrrrrrrrrrrrrrrr;


import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
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
    private FirebaseUser match;
    private DatabaseReference userRef;
    private DatabaseReference matchRef;
    private DatabaseReference userLikedRef;
    private DatabaseReference userDislikedRef;
    private DatabaseReference userUnseenRef;
    private DataSnapshot userDataSnapshot;
    private ArrayList <Long> userLikedMedia;
    private ArrayList <Long> userDislikedMedia;
    private ArrayList <Long> userUnseenMedia;
    private ArrayList <String> potentialUsers;

    //Constructors
    public MatchMaker (final FirebaseUser user)
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

        /*dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                MatchMaker.this.userDataSnapshot = dataSnapshot;
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
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });*/

        //Single Value Listener - to fill the arraylist with pre-inserted values
        /*this.userLikedRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snap : dataSnapshot.getChildren())
                {
                    Long id = Long.parseLong(snap.getKey());
                    userLikedMedia.add(id);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.i("Error in [addListenerForSingleValueEvent] at [userLikedRef]", databaseError.getDetails());
            }
        });
        this.userDislikedRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snap : dataSnapshot.getChildren())
                {
                    Long id = Long.parseLong(snap.getKey());
                    userDislikedMedia.add(id);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.i("Error in [addListenerForSingleValueEvent] at [userDislikedRef]", databaseError.getDetails());
            }
        });
        this.userUnseenRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snap : dataSnapshot.getChildren())
                {
                    Long id = Long.parseLong(snap.getKey());
                    userUnseenMedia.add(id);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.i("Error in [addListenerForSingleValueEvent] at [userUnseesRef]", databaseError.getDetails());
            }
        });

        //Child Listeners- to add each child when it is added in the main activity live
        this.userLikedRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Long id = Long.parseLong(dataSnapshot.getKey());
                userLikedMedia.add(id);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        this.userDislikedRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                long id = Long.parseLong(dataSnapshot.getKey());
                userDislikedMedia.add(id);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        this.userUnseenRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                long id = Long.parseLong(dataSnapshot.getKey());
                userUnseenMedia.add(id);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });*/
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
                MatchMaker.this.userDataSnapshot = dataSnapshot;
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
    {
        Log.i("getPotentialMatches", "Started");
        DataSnapshot dataSnapshot = this.userDataSnapshot.child("users");
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
            Log.i("[getPotentialMatches]", "Got Matches, "+this.potentialUsers.get(0));
        }

       /* usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (final DataSnapshot snap : dataSnapshot.getChildren())
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
            }*/

            /*Query query = snap.getRef().child("Liked Media").orderByKey().limitToFirst(10);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Log.i("Check", "Entered onDataChanged");
                    for (DataSnapshot shot: dataSnapshot.getChildren())
                    {
                        boolean inserted = false;
                        if (userLikedMedia.contains(Long.parseLong(shot.getKey()))&&!inserted)
                        {
                            MatchMaker.this.potentialUsers.add(snap.getKey());
                            Log.i("[getPotentialUsers]", "Added Potential User");
                            inserted = !inserted;
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.i("Error in [addListenerForSingleValueEvent] at [potentialUsersRef] at Second Inner Class", databaseError.getDetails());
                }
            });*/
            //WHILE!!!!!


           /* @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.i("Error in [addListenerForSingleValueEvent] at [potentialUsersRef]", databaseError.getDetails());
            }
        });*/
    }
    //Do this with OrderByKey and then checking, limitToFirst(10)


    public String check () throws InterruptedException {
        Thread.sleep(3000);
        return this.userLikedMedia.get(this.userLikedMedia.size()-1).toString();
    }
}