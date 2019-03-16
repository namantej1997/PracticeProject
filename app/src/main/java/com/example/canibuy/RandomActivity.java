package com.example.canibuy;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class RandomActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.random_activity);

    }

    public void show(View view){
        AlertDialog.Builder d = new AlertDialog.Builder(RandomActivity.this);

        d.setTitle("Manual expense Input");

//        Switch sw = new Switch(DashBoard.this);
//
//        EditText et1 = new EditText(DashBoard.this);
//        EditText et2 = new EditText(DashBoard.this);
//        EditText et3 = new EditText(DashBoard.this);

        d.setView(R.layout.ledger_input);

        AlertDialog a = d.create();
        a.show();
    }
}
