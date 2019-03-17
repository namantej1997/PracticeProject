package com.example.canibuy;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.codecrafters.tableview.TableView;
import de.codecrafters.tableview.toolkit.SimpleTableDataAdapter;
import de.codecrafters.tableview.toolkit.SimpleTableHeaderAdapter;

public class MainActivity extends AppCompatActivity {

    String[] tableheaders = {"Income            Expense"};
    private TextView mTextMessage;
    private FirebaseAuth auth;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    Intent display;
                    display = new Intent(getApplicationContext(), DashBoard.class);
                    startActivity(display);
                    return true;
                case R.id.navigation_dashboard:
                    Intent scan;
                    scan = new Intent(getApplicationContext(), Scanactivity.class);
                    startActivity(scan);
                    return true;
                case R.id.navigation_notifications:
                    mTextMessage.setText("Saved");
                    generatePDF();
                    return true;
                case R.id.navigation_settting:
                    logout();
                    return true;
            }
            return false;
        }
    };

    private void logout() {
        auth.signOut();
        finish();
        startActivity(new Intent(this, LoginActivity.class));

    }

    private void requestSmsPermission() {
        String permission = Manifest.permission.RECEIVE_SMS;
        int grant = ContextCompat.checkSelfPermission(this, permission);
        if (grant != PackageManager.PERMISSION_GRANTED) {
            String[] permission_list = new String[1];
            permission_list[0] = permission;
            ActivityCompat.requestPermissions(this, permission_list, 1);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        System.out.println("jhgjyvs");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestSmsPermission();

        auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() == null) {
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }

        FirebaseUser user = auth.getCurrentUser();

        mTextMessage = (TextView) findViewById(R.id.message);
        mTextMessage.setText("Welcome: " + user.getEmail());

        //creating table...

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        FireBaseHelper helper = new FireBaseHelper(mDatabase);

        Toast.makeText(this, "" + helper.getTotalBalance(), Toast.LENGTH_LONG).show();

        updateTable(helper);


        //recieving messages
        SmsReceiver.bindListener(new SmsListener() {
            @Override
            public void messageReceived(String messageText) {
                Log.d("Text", messageText);
                parseSMS(messageText, helper);
                //Toast.makeText(MainActivity.this,"Message: "+messageText,Toast.LENGTH_LONG).show();
            }
        });

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    public void parseSMS(String messageText, FireBaseHelper helper) {

        String category;
        String ammount;
        String itemName;
        boolean debited;

        //New sms parser
        messageText = messageText.toLowerCase();
        if (messageText.contains("credited")) {
            debited = false;
            category = "Bank";
            if (messageText.contains("interest"))
                itemName = "Interest";
            else
                itemName = "Salary";
            ammount = messageText.replaceAll("[^0-9]", "");
        } else {
            debited = true;
            if (messageText.contains("ola")) {
                category = "Travel";
                itemName = "Ola";
            } else if (messageText.contains("metro")) {
                category = "Travel";
                itemName = "Metro";
            } else if (messageText.contains("domino's")) {
                category = "Food";
                itemName = "Domino's";
            } else if (messageText.contains("bookmyshow")) {
                category = "Entertainment";
                itemName = "Book My Show";
            } else {
                category = "Others";
                itemName = "Miscellaneous";
            }
            ammount = messageText.replaceAll("[^0-9]", "");
        }
        Ledger smsLedger = new Ledger(category, ammount, itemName, debited);
        Toast.makeText(MainActivity.this, (smsLedger.isDebited()) ? "Debited: " + ammount : "Credited: " + ammount, Toast.LENGTH_LONG).show();

        //Firebase Stuff

        helper.saveLedger(smsLedger);
        updateTable(helper);
    }

    private void updateTable(FireBaseHelper helper) {

        ProgressBar progressBar;
        TextView balance;

        progressBar = findViewById(R.id.progressBar2);
        balance = findViewById(R.id.balance);

        final TableView<String[]> tb = (TableView<String[]>) findViewById(R.id.tableView);
        tb.setColumnCount(1);
        tb.setHeaderBackgroundColor(Color.parseColor("#2ecc71"));
        tb.setHeaderAdapter(new SimpleTableHeaderAdapter(this, tableheaders));
        progressBar.setProgress(10);
        balance.setText("0");
        ArrayList<Ledger> ledgerList = helper.retrieveLedger();
        int credit = 0;
        if (ledgerList != null) {
            ArrayList<String> expenseArr = new ArrayList<>();
            for (Ledger ledger : ledgerList) {
                if (ledger.isDebited()) {
                    expenseArr.add("                    Rs. " + ledger.getAmmount() + " (" + ledger.getItemName() + ")");
                    balance.setText(String.valueOf(Integer.parseInt(balance.getText().toString()) - Integer.parseInt(ledger.getAmmount())));
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        progressBar.setProgress(Integer.parseInt(balance.getText().toString()) * 100/credit);
                    }
                } else {
                    credit += Integer.parseInt(ledger.getAmmount());
                    expenseArr.add("Rs. " + ledger.getAmmount() + " (" + ledger.getItemName() + ")");
                    balance.setText(String.valueOf(Integer.parseInt(balance.getText().toString()) + Integer.parseInt(ledger.getAmmount())));
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        progressBar.setProgress(Integer.parseInt(balance.getText().toString()) * 100/credit);
                    }
                }
            }
            // Toast.makeText(this,"I am a disco dancer",Toast.LENGTH_LONG).show();
            String[][] tableData = new String[expenseArr.size()][1];
            for (int i = 0; i < expenseArr.size(); i++) {
                tableData[i][0] = expenseArr.get(i);
            }
            //  Toast.makeText(this,"I am a disco dancer",Toast.LENGTH_LONG).show();
            tb.setDataAdapter(new SimpleTableDataAdapter(this, tableData));
        }

//        int totalBalance = Integer.parseInt(helper.getTotalBalance());
//        ProgressBar progressBar = findViewById(R.id.progressBar2);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            progressBar.setProgress(totalBalance,true);
//        }
//        TextView balance = findViewById(R.id.balance);
//        balance.setText(String.valueOf(balance));
    }

    public void generatePDF() {

        String sometext = "Naman Tejwani";

        PdfDocument document = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(300, 600, 1).create();

        // start a page
        PdfDocument.Page page = document.startPage(pageInfo);

        Canvas canvas = page.getCanvas();
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        canvas.drawCircle(30, 30, 30, paint);
        paint.setColor(Color.BLACK);
        canvas.drawText(sometext, 80, 50, paint);
        document.finishPage(page);


        String directory_path = Environment.getExternalStorageDirectory().getPath() + "/mypdf/";
        File file = new File(directory_path);
        if (!file.exists()) {
            file.mkdirs();
        }
        String targetPdf = directory_path + "hello.pdf";
        File filePath = new File(targetPdf);
        try {
            document.writeTo(new FileOutputStream(filePath));
            Toast.makeText(this, "Done", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            Log.e("main", "error " + e.toString());
            Toast.makeText(this, "Something wrong: " + e.toString(), Toast.LENGTH_LONG).show();
        }
        // close the document
        document.close();
    }

}
