package com.example.cds.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.example.cds.Modelclass.Listing;
import com.example.cds.R;

import java.util.List;

public class Listing_Adapter extends ArrayAdapter<Listing> {
    private Context context;
    private List<Listing> listingList;

    public Listing_Adapter(Context context, List<Listing> listings) {
        super(context, 0, listings);
        this.context = context;
        this.listingList = listings;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Listing listing = listingList.get(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_listing, parent, false);
        }

        ImageView image = convertView.findViewById(R.id.listing_image);
        TextView title = convertView.findViewById(R.id.listing_title);

        title.setText(listing.l_name);
        Glide.with(getContext()).load(listing.l_photo).into(image); // add Picasso dependency

        image.setOnClickListener(v -> {
          //  Intent intent = new Intent(context, DetailActivity.class);
          //  intent.putExtra("listing", new Gson().toJson(listing));
          //  context.startActivity(intent);
        });

        return convertView;
    }
}
