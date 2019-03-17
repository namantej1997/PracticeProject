package com.example.canibuy;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class CustomAdapter extends BaseAdapter {

    Context c;
    ArrayList<Ledger> ledgerList;

    public CustomAdapter(Context c, ArrayList<Ledger> ledgerList) {
        this.c = c;
        this.ledgerList = ledgerList;
    }

    @Override
    public int getCount() {
        return ledgerList.size();
//        if(ledgerList!=null) {
//            if (ledgerList.size() > 0)
//                return ledgerList.size();
//        }
//        return 0;
    }

    @Override
    public Object getItem(int position) {
        return ledgerList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(c).inflate(R.layout.ledger_model, parent, false);
        }

        TextView debited = (TextView) convertView.findViewById(R.id.debited);
        TextView item = (TextView) convertView.findViewById(R.id.item);
        TextView ammount = (TextView) convertView.findViewById(R.id.ammount);
        TextView category = (TextView) convertView.findViewById(R.id.categoryModel);

        final Ledger ledger = (Ledger) this.getItem(position);

        debited.setText(ledger.debited ? "Debited" : "Credited");
        item.setText("Item: " + ledger.getItemName());
        ammount.setText("Rs. " + ledger.getAmmount());
        category.setText("Type: " + ledger.getCategory());

        convertView.setOnClickListener((v) -> {
            Toast.makeText(c, ledger.getItemName(), Toast.LENGTH_SHORT).show();
        });

        return convertView;
    }

}
