package com.atreid.expresslanesimages.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.atreid.expresslanesimages.ExpressLaneImage;
import com.atreid.expresslanesimages.R;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

public class ExpressLaneImageAdapter extends ArrayAdapter<ExpressLaneImage> {

    Context context;
    int layoutResourceId;
    ExpressLaneImage data[] = null;

    public ExpressLaneImageAdapter(Context context, int layoutResourceId, ExpressLaneImage[] data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ExpressLaneImageHolder holder = null;

        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new ExpressLaneImageHolder();
            holder.imgIcon = (ImageView)row.findViewById(R.id.imgIcon);
            holder.txtTitle = (TextView)row.findViewById(R.id.txtTitle);

            row.setTag(holder);
        }
        else
        {
            holder = (ExpressLaneImageHolder)row.getTag();
        }

        ExpressLaneImage expressLaneImage = data[position];
        holder.txtTitle.setText(expressLaneImage.title);
        Picasso.with(context)
                .load(expressLaneImage.icon) // + "&nocache=" + this.date.getTime())
                //.memoryPolicy(MemoryPolicy.NO_CACHE)
                .resize(203 * 3, 210 * 3)
                .centerInside()
                .into(holder.imgIcon);
        return row;
    }

    static class ExpressLaneImageHolder
    {
        ImageView imgIcon;
        TextView txtTitle;
        String route;
        String direction;
    }
}