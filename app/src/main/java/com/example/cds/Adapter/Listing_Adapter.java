package com.example.cds.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.cds.Modelclass.Listing;
import com.example.cds.R;
import com.example.cds.ui.home.DetailsActivity;
import com.google.gson.Gson;

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
        TextView basic = convertView.findViewById(R.id.listing_basic);
       // TextView provider = convertView.findViewById(R.id.listing_provider);

        title.setText(listing.l_name);
        int basicValue = listing.l_basic;
        basic.setText("Starting from: $"+String.valueOf(basicValue));
       // provider.setText("By " + listing.l_provider);

        Glide.with(context).load(ImgURLConv(listing.l_photo))
                .fitCenter().thumbnail(0.1f).transform(new RoundedCorners(24))
                .placeholder(R.drawable.placeholder).into(image);

        image.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetailsActivity.class);
            intent.putExtra("listing", new Gson().toJson(listing));
            context.startActivity(intent);
        });

        return convertView;
    }

    public String ImgURLConv(String driveUrl) {
        // Example input: https://drive.google.com/file/d/FILE_ID/view?usp=sharing
        try {
            String[] parts = driveUrl.split("/");
            String fileId = parts[5];
            String directUrl = "https://drive.google.com/uc?export=download&id=" + fileId;

            // Log the URL
            Log.d("DirectDownloadURL", "Converted URL: " + directUrl);

            return directUrl;
        } catch (Exception e) {
            Log.e("DirectDownloadURL", "Error converting URL: " + e.getMessage());
            return null;
        }
    }
}
