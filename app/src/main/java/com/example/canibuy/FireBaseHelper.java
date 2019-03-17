package com.example.canibuy;

import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.example.canibuy.Ledger;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

import static android.webkit.ConsoleMessage.MessageLevel.LOG;

public class FireBaseHelper {
    //TODO: make a function iterating ledgerList and category percentages for pie

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
            } catch (DatabaseException e) {
                e.printStackTrace();
                saved = false;
            }
        }
        return saved;
    }

    private void fetchLedger(DataSnapshot dataSnapshot) {
        ledgerList.clear();
        for (DataSnapshot ds : dataSnapshot.getChildren()) {
            Ledger ledger = ds.getValue(Ledger.class);
            ledgerList.add(ledger);
        }
    }

    public ArrayList<Ledger> retrieveLedger() {
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

    public String getTotalBalance() {
        int total = 0;
        if (ledgerList != null) {
            for (Ledger ledger : ledgerList) {
                int amount = Integer.parseInt(ledger.getAmmount().replaceAll("[^0-9]", ""));
                if (ledger.isDebited()) {
                    total -= amount;
                } else {
                    total += amount;
                }
            }
        }
        return String.valueOf(total);
    }

    public void getPieValues() {
//        HashMap<String, Float> pieValues = new HashMap<>();
//        int total = Integer.parseInt(getTotalBalance());
//        if (ledgerList != null) {
//            for (Ledger ledger : ledgerList) {
//                if (pieValues.get(ledger.getCategory()) == null) {
//                    pieValues.put(ledger.getCategory(), Float.parseFloat(ledger.getAmmount()) / total);
//                } else {
//                    pieValues.put(ledger.category, pieValues.get(ledger.getCategory()) + Float.parseFloat(ledger.getAmmount()) / total);
//                }
//            }
//        }
//        return pieValues;


    }
}
