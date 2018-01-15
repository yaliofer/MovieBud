package com.example.yali.grrrrrrrrrrrrrrrrr;


import android.util.Log;

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
    private ArrayList <Long> userLikedMedia;
    private ArrayList <Long> userDislikedMedia;
    private ArrayList <Long> userUnseenMedia;

    //Constructors
    public MatchMaker (FirebaseUser user)
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

        //Single Value Listener - to fill the arraylist with pre-inserted values
        this.userLikedRef.addListenerForSingleValueEvent(new ValueEventListener() {
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
        });
    }

    public String check ()
    {
        return this.userLikedMedia.get(this.userLikedMedia.size()-1).toString();
    }
}