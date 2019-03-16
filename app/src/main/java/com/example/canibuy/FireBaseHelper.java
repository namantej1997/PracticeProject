package com.example.canibuy;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.example.canibuy.Ledger;

import java.util.ArrayList;

public class FireBaseHelper {

    DatabaseReference db;
    Boolean saved;
    ArrayList<Ledger> ledgerList = new ArrayList<>();

    public FireBaseHelper(DatabaseReference db) {
        this.db = db;
    }

    public Boolean saveLedger(Ledger ledger) {
        if (ledger == null) {
            saved = false;
        } else {
            try {
                db.child("Ledger").push().setValue(ledger);
                saved = true;
            }catch(DatabaseException e){
                e.printStackTrace();
                saved=false;
            }
        }
        return saved;
    }

    private void fetchLedger(DataSnapshot dataSnapshot){
        ledgerList.clear();
        for(DataSnapshot ds : dataSnapshot.getChildren()){
            Ledger ledger = ds.getValue(Ledger.class);
            ledgerList.add(ledger);
        }
    }

    public ArrayList<Ledger> retrieveLedger(){
        db.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                fetchLedger(dataSnapshot);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                fetchLedger(dataSnapshot);
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
        return ledgerList;
    }
}
