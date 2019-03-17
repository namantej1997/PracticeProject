package com.example.canibuy;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SliderAdapter extends PagerAdapter {
    Context context;
    LayoutInflater inflater;

    //list of images
    public int[] lst_images = {
            R.drawable.none,
            R.drawable.help,
            R.drawable.game,
            R.drawable.report_analysis
    };

    //list of titles
    public String[] lst_titles = {
            "Simple and free",
            "Learning Platform",
            "Hands on Real Time Investments",
            "Report Analysis"
    };

    //list of description
    public String[] lst_description = {
            "Provides Learning and Investing in Real-time stocks and Bonds for free.",
            "An interactive platform from learn through documents and Videos.",
            "provides you with a hands on experience for getting better idea of how the market works.",
            "provides you with the analysis of your portfolio report"
    };

    public SliderAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        //It returns the number of pages slide is having
        return lst_titles.length;
    }


    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return (view == (LinearLayout) o);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.slide, container, false);

        ImageView imgslide = (ImageView) view.findViewById(R.id.slideimg);
        TextView txttitle = (TextView) view.findViewById(R.id.txttitle);
        TextView txtdesc = (TextView) view.findViewById(R.id.txtdescription);
        imgslide.setImageResource(lst_images[position]);
        txttitle.setText(lst_titles[position]);
        txtdesc.setText(lst_description[position]);
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((LinearLayout) object);
    }
}