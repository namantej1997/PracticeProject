package com.example.canibuy;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.codecrafters.tableview.TableView;
import de.codecrafters.tableview.toolkit.SimpleTableDataAdapter;
import de.codecrafters.tableview.toolkit.SimpleTableHeaderAdapter;

public class MainActivity extends AppCompatActivity {

    String[] tableheaders={"Income","Expense"};
    String[][] tableData;

    private TextView mTextMessage;
    private FirebaseAuth auth;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    Intent display;
                    display = new Intent(getApplicationContext(),displayPie.class);
                    startActivity(display);
                    return true;
                case R.id.navigation_dashboard:
                    Intent scan;
                    scan = new Intent(getApplicationContext(),Scanactivity.class);
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
        startActivity(new Intent(this,LoginActivity.class));

    }
    private void requestSmsPermission() {
        String permission = Manifest.permission.RECEIVE_SMS;
        int grant = ContextCompat.checkSelfPermission(this, permission);
        if ( grant != PackageManager.PERMISSION_GRANTED) {
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
        if(auth.getCurrentUser()==null){
            finish();
            startActivity(new Intent(this,LoginActivity.class));
        }

        FirebaseUser user = auth.getCurrentUser();

        mTextMessage = (TextView) findViewById(R.id.message);
        mTextMessage.setText("Welcome: "+user.getEmail());

        //creating table...

        final TableView<String[]> tb =(TableView<String[]>) findViewById(R.id.tableView);
        tb.setColumnCount(2);
        tb.setHeaderBackgroundColor(Color.parseColor("#2ecc71"));
        tb.setHeaderAdapter(new SimpleTableHeaderAdapter(this,tableheaders));
        //tb.setDataAdapter(new SimpleTableDataAdapter(this,tableData));


        //recieving messages
        SmsReceiver.bindListener(new SmsListener() {
            @Override
            public void messageReceived(String messageText) {
                Log.d("Text",messageText);
                getMoney(messageText);
                //Toast.makeText(MainActivity.this,"Message: "+messageText,Toast.LENGTH_LONG).show();
            }
        });

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    public void getMoney(String messageText) {

        Log.i("Text", messageText);


        String regex = "credited";
        String regex1 = "debited";


        String messageToSearch = messageText;
        int money = Integer.parseInt(messageToSearch.replaceAll("[^0-9]", ""));

        //  Toast.makeText(SMSActivity.this,""+money,Toast.LENGTH_LONG).show();

        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Pattern pattern1 = Pattern.compile(regex1,Pattern.CASE_INSENSITIVE);

        Matcher matcher = pattern.matcher(messageToSearch);
        Matcher matcher1 = pattern1.matcher(messageToSearch);
        Map<String, Integer> moneyleft = new HashMap<>();

        if(matcher.find()){
            moneyleft.put(regex, money);
            Toast.makeText(MainActivity.this,""+regex+"  "+money,Toast.LENGTH_LONG).show();
        }

        else if(matcher1.find()){
            moneyleft.put(regex1, money);
            Toast.makeText(MainActivity.this,""+regex1+"  "+money,Toast.LENGTH_LONG).show();
        }



        //  firebaseFirestore.collection("items").add(moneyleft);
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
        String targetPdf = directory_path+"hello.pdf";
        File filePath = new File(targetPdf);
        try {
            document.writeTo(new FileOutputStream(filePath));
            Toast.makeText(this, "Done", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            Log.e("main", "error "+e.toString());
            Toast.makeText(this, "Something wrong: " + e.toString(),  Toast.LENGTH_LONG).show();
        }
        // close the document
        document.close();
    }

}
