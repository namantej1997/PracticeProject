package com.example.canibuy;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Scanactivity extends AppCompatActivity {

    private TextView textView;

    private IntentIntegrator qrScan;

    private FirebaseFirestore firebaseFirestore;


    private EditText editText;

    private Button insert;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
//

//

        firebaseFirestore = FirebaseFirestore.getInstance();

        qrScan = new IntentIntegrator(this);

    }

    public void scanQRCode(View view) {
        qrScan.initiateScan();
    }


    public void scanBills(View view) {
        startActivity(new Intent(this, LaunchCameraActivity.class));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "Result not found", Toast.LENGTH_SHORT).show();
            } else {
                try {
                    JSONObject obj = new JSONObject(result.getContents());
                    textView.setText(obj.getString("address"));
                } catch (JSONException e) {
                    e.printStackTrace();
                    String code = result.getContents().trim();
                    int price = 2000;
                    Map<String, Integer> itemval = new HashMap<>();

                    itemval.put(code, price);

                    firebaseFirestore.collection("items").add(itemval);

                    Toast.makeText(this, result.getContents(), Toast.LENGTH_SHORT).show();


                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
