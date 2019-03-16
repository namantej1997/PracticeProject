package com.example.canibuy;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

public class FireBaseAmtExHelper {

    DatabaseReference db;
    Boolean saved;
    ArrayList<AmountExchange> amountExchangeArrayList = new ArrayList<>();
    public FireBaseAmtExHelper(DatabaseReference db) {
        this.db = db;
    }

    public Boolean saveAmountExchange(AmountExchange amountExchange) {
        if (amountExchange == null) {
            saved = false;
        } else {
            try {
                db.child("AmountExchange").push().setValue(amountExchange);
                saved = true;
            }catch(DatabaseException e){
                e.printStackTrace();
                saved=false;
            }
        }
        return saved;
    }

    private void fetchAmountExchange(DataSnapshot dataSnapshot){
        amountExchangeArrayList.clear();
        for(DataSnapshot ds : dataSnapshot.getChildren()){
            AmountExchange amountExchange = ds.getValue(AmountExchange.class);
            amountExchangeArrayList.add(amountExchange);
        }
    }


    public ArrayList<AmountExchange> retrieveAmountExchange(){
        db.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                fetchAmountExchange(dataSnapshot);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                fetchAmountExchange(dataSnapshot);
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
        return amountExchangeArrayList;
    }



}
