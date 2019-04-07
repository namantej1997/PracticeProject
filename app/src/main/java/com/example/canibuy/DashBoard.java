package com.example.canibuy;


import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DashBoard extends AppCompatActivity {

    DatabaseReference db;
    FireBaseHelper helper;
    CustomAdapter adapter;
    ListView lv;
    Switch debitedBool;
    EditText itemTxt, ammountTxt, categoryTxt;
    PieChart investment;
    private Bitmap bitmap;
    ConstraintLayout llScroll;
    int other =1,invest =0,trav =0,credit,bkshare,ibmshare,totalPackage;
    int totalExpense = 1;
    FloatingActionButton fab,refresh;
    int totalshareExpense=1;
    SeekBar other1;
    SeekBar travel1;
    SeekBar investment1;
    TextView insight1,insight2,insight3,balance;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.displaypie);

        investment = findViewById(R.id.piechart2);
        fab = findViewById(R.id.fab);
        refresh = findViewById(R.id.refresh);
        other1 = findViewById(R.id.other);
        travel1 = findViewById(R.id.travel);
        investment1 = findViewById(R.id.investments);
        insight1 = findViewById(R.id.insightCat1);
        insight2 = findViewById(R.id.insightCat2);
        insight3 = findViewById(R.id.insightCat3);
        balance = findViewById(R.id.textView4);



        //List View
        //Firebase
        lv = (ListView) findViewById(R.id.lv);

        //Init Firebase db
        db = FirebaseDatabase.getInstance().getReference();
        helper = new FireBaseHelper(db);

        //Adapter
        adapter = new CustomAdapter(this, helper.retrieveLedger());
        lv.setAdapter(adapter);
        // Setting on Touch Listener for handling the touch inside ScrollView
        lv.setOnTouchListener((v, event) -> {
            // Disallow the touch request for parent scroll on touch of child view
            v.getParent().requestDisallowInterceptTouchEvent(true);
            return false;
        });
        setListViewHeightBasedOnChildren(lv);

        llScroll = findViewById(R.id.llScroll);
        FloatingActionButton pdf = findViewById(R.id.pdf);
        pdf.setOnClickListener(v -> {
            Log.d("size", " " + llScroll.getWidth() + "  " + llScroll.getWidth());
            bitmap = loadBitmapFromView(llScroll, llScroll.getWidth(), llScroll.getHeight());
            createPdf();
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayInputDialog();


            }
        });

        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makepie();
                makepieInvest();
            }
        });


        //Pie Chart
        PieChart pieChart = findViewById(R.id.piechart1);
        pieChart.setUsePercentValues(true);

        Description desc = new Description();
        desc.setText("Estimated Graph");
        desc.setTextSize(10f);

        pieChart.setDescription(desc);
        pieChart.setHoleRadius(20f);
        pieChart.setTransparentCircleRadius(20f);

        List<PieEntry> value = new ArrayList<>();

        View v;
        TextView category;
        TextView amount;

//        HashMap<String, Float> pieValues = helper.getPieValues();
        setValues();

//        for (Map.Entry<String, Float> e : pieValues.entrySet()) {
//            value.add(new PieEntry(e.getValue(), e.getKey()));
//        }


        int travelpercent = trav*100/totalExpense;
        int investmentpercent = invest*100/totalExpense;
        int otherpercent = other*100/totalExpense;


        Log.i("travel cost",""+travelpercent);
        Log.i("invested cost",""+investmentpercent);
        Log.i("other cost",""+otherpercent);

        value.add(new PieEntry((float)travelpercent, "Travel"));
        value.add(new PieEntry((float)otherpercent, "Miscellaneous"));
       value.add(new PieEntry((float)investmentpercent, "Investments"));
//        value.add(new PieEntry(5f, "Travel"));

        PieDataSet pieDataSet = new PieDataSet(value, "Spending");
        PieData pieData = new PieData(pieDataSet);
        pieChart.setData(pieData);

        pieDataSet.setColors(ColorTemplate.JOYFUL_COLORS);
        pieChart.animateXY(1400, 1400);


    }



    private void makepieInvest() {
        PieChart pieChart = findViewById(R.id.piechart2);
        pieChart.setUsePercentValues(true);

        Description desc = new Description();
        desc.setText("Estimated Graph");
        desc.setTextSize(10f);

        pieChart.setDescription(desc);
        pieChart.setHoleRadius(20f);
        pieChart.setTransparentCircleRadius(20f);

        List<PieEntry> value = new ArrayList<>();

        ArrayList<Ledger> ledgerList = helper.retrieveLedger();
        if (ledgerList != null) {

            for (Ledger ledger : ledgerList) {
                if (ledger.isDebited()) {



                    switch (ledger.getItemName()){
                        case "Blackrock shares":
                            bkshare+=Integer.parseInt(ledger.getAmmount());
                            break;
                        case "IBM shares":
                            ibmshare+=Integer.parseInt(ledger.getAmmount());
                            break;

                    }


                }


            }

            totalshareExpense = bkshare+ibmshare;
        }

        if(totalshareExpense==0){
            totalshareExpense=1;
        }

        int percentblkshare = bkshare*100/totalshareExpense;
        int percentibmshare = ibmshare*100/totalshareExpense;

        value.add(new PieEntry((float)percentblkshare, "Blackrock"));
        value.add(new PieEntry((float)percentibmshare, "IBM"));


        PieDataSet pieDataSet = new PieDataSet(value, "Spending");
        PieData pieData = new PieData(pieDataSet);
        pieChart.setData(pieData);

        pieDataSet.setColors(ColorTemplate.JOYFUL_COLORS);
        pieChart.animateXY(1400, 1400);



    }

    private void makepie() {
        PieChart pieChart = findViewById(R.id.piechart1);
        pieChart.setUsePercentValues(true);

        Description desc = new Description();
        desc.setText("Estimated Graph");
        desc.setTextSize(10f);

        pieChart.setDescription(desc);
        pieChart.setHoleRadius(20f);
        pieChart.setTransparentCircleRadius(20f);

        List<PieEntry> value = new ArrayList<>();

        View v;
        TextView category;
        TextView amount;

//        HashMap<String, Float> pieValues = helper.getPieValues();
        setValues();

//        for (Map.Entry<String, Float> e : pieValues.entrySet()) {
//            value.add(new PieEntry(e.getValue(), e.getKey()));
//        }


        int travelpercent = trav*100/totalExpense;
        int investmentpercent = invest*100/totalExpense;
        int otherpercent = other*100/totalExpense;

        travel1.setProgress(travelpercent);
        investment1.setProgress(investmentpercent);
        other1.setProgress(otherpercent);


        if(travelpercent>25){
            insight1.setText("You should cut down the expense on Travelling");
        }else{
            insight1.setText("Travelling Expense is under control");
        }
        if(investmentpercent<5){
            insight3.setText("You should start investing more percent of your income");
        }else{
            insight3.setText("Money goes in investments looks good");
        }
        if(otherpercent>75){
            insight2.setText("Please cut down the Miscellaneous Costs and start saving");
        }else{
            insight2.setText("Miscellaneous expense under control");
        }

        value.add(new PieEntry((float)travelpercent, "Travel"));
        value.add(new PieEntry((float)otherpercent, "Miscellaneous"));
        value.add(new PieEntry((float)investmentpercent, "Investments"));
//        value.add(new PieEntry(5f, "Travel"));

        PieDataSet pieDataSet = new PieDataSet(value, "Spending");
        PieData pieData = new PieData(pieDataSet);
        pieChart.setData(pieData);

        pieDataSet.setColors(ColorTemplate.JOYFUL_COLORS);
        pieChart.animateXY(1400, 1400);
    }

    private void setValues() {

        ArrayList<Ledger> ledgerList = helper.retrieveLedger();
        if (ledgerList != null) {

            for (Ledger ledger : ledgerList) {
                if (ledger.isDebited()) {

                    totalPackage+=Integer.parseInt(ledger.getAmmount());

                    switch (ledger.getCategory()){
                       case "Others":
                            other+=Integer.parseInt(ledger.getAmmount());
                            break;
                        case "Travel":
                            trav+=Integer.parseInt(ledger.getAmmount());
                            break;
                        case "Investments":
                            invest+=Integer.parseInt(ledger.getAmmount());
                            break;
                    }


                }
                else {
                    credit += Integer.parseInt(ledger.getAmmount());


                }

            }

            totalExpense = other+invest+trav;
        }
        Log.i("Total amount creditted",""+credit);
        Log.i("Total amount debitted",""+totalExpense);

        int b=credit-totalPackage;
        balance.setText(""+b);


    }

    public static Bitmap loadBitmapFromView(View v, int width, int height) {
        Bitmap b = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        v.draw(c);

        return b;
    }

    //Display Ledger input
    public void displayInputDialog() {

        Dialog d = new Dialog(this);
        d.setTitle("Save To Firebase");
        d.setContentView(R.layout.ledger_input);

        debitedBool = (Switch) d.findViewById(R.id.debitedBool);
        itemTxt = (EditText) d.findViewById(R.id.Item);
        ammountTxt = (EditText) d.findViewById(R.id.amount);
        categoryTxt = d.findViewById(R.id.category);
//
        Button saveBtn = d.findViewById(R.id.saveBtn);

        //Save
        saveBtn.setOnClickListener((v) -> {

            //Get data
            Boolean debited = !debitedBool.isChecked();
            String item = itemTxt.getText().toString();
            String amount = ammountTxt.getText().toString();
            String category = categoryTxt.getText().toString();

            Ledger ledger = new Ledger();
            ledger.setDebited(debited);
            ledger.setItemName(item);
            ledger.setAmmount(amount);
            ledger.setCategory(category);
//
            if (item != null && amount != null && category != null && item.length() > 0 && amount.length() > 0 && category.length() > 0) {
//                Ledger ledger = new Ledger(category, amount, item, debited);
                if (helper.saveLedger(ledger)) {
//
                    debitedBool.setChecked(false);
                    itemTxt.setText("");
                    ammountTxt.setText("");
                    categoryTxt.setText("");
//
                    adapter = new CustomAdapter(DashBoard.this, helper.retrieveLedger());
                    lv.setAdapter(adapter);
                }
            } else {
                Toast.makeText(DashBoard.this, "Fields are empty", Toast.LENGTH_SHORT).show();
            }


        });


        d.show();
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0) {
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, ViewGroup.LayoutParams.WRAP_CONTENT));

            }

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }


    private void createPdf() {
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        //  Display display = wm.getDefaultDisplay();
        DisplayMetrics displaymetrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        float hight = displaymetrics.heightPixels;
        float width = displaymetrics.widthPixels;

        int convertHighet = (int) hight, convertWidth = (int) width;

//        Resources mResources = getResources();
//        Bitmap bitmap = BitmapFactory.decodeResource(mResources, R.drawable.screenshot);

        PdfDocument document = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(convertWidth, convertHighet, 2).create();
        PdfDocument.Page page = document.startPage(pageInfo);

        Canvas canvas = page.getCanvas();

        Paint paint = new Paint();
        canvas.drawPaint(paint);

        bitmap = Bitmap.createScaledBitmap(bitmap, convertWidth, convertHighet, true);

        paint.setColor(Color.BLUE);
        canvas.drawBitmap(bitmap, 0, 0, null);
        document.finishPage(page);

        // write the document content
        String targetPdf = "/sdcard/pdff.pdf";
        File filePath;
        filePath = new File(targetPdf);
        try {
            document.writeTo(new FileOutputStream(filePath));

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Something wrong: " + e.toString(), Toast.LENGTH_LONG).show();
        }

        // close the document
        document.close();
        Toast.makeText(this, "PDF of Scroll is created!!!", Toast.LENGTH_SHORT).show();

        openGeneratedPDF();

    }

    private void openGeneratedPDF() {
        File file = new File("/sdcard/pdff.pdf");
        if (file.exists()) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            Uri uri = FileProvider.getUriForFile(DashBoard.this, BuildConfig.APPLICATION_ID + ".my.package.name.provider", file);

            intent.setDataAndType(uri, "application/pdf");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            try {
                startActivity(intent);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(DashBoard.this, "No Application available to view pdf", Toast.LENGTH_LONG).show();
            }
        }
    }
}



