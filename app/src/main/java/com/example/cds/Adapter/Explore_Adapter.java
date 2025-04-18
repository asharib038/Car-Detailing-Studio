package com.example.cds.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.cds.Modelclass.Listing;
import com.example.cds.Modelclass.Seller;
import com.example.cds.R;
import com.example.cds.ui.home.DetailsActivity;
import com.google.gson.Gson;

import java.util.List;


public class Explore_Adapter extends ArrayAdapter<Seller> {
    public Explore_Adapter(@NonNull Context context, @NonNull List<Seller> sellers) {
        super(context, 0, sellers);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView,
                        @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.seller_item, parent, false);
        }

        Seller seller = getItem(position);

        ImageView img = convertView.findViewById(R.id.sellerImage);
        TextView name = convertView.findViewById(R.id.sellerName);
        TextView price = convertView.findViewById(R.id.sellerPrice);
        RatingBar ratingBar = convertView.findViewById(R.id.sellerRatingBar);

        name.setText(seller.getS_name());
        price.setText("Price: ₹" + seller.getS_price());
        try {
            float rating = (seller.getS_rating());
            ratingBar.setRating(rating);
        } catch (NumberFormatException e) {
            ratingBar.setRating(0f); // default if parsing fails
        }

        Glide.with(getContext()).load(seller.getS_photo()).into(img);

        return convertView;
    }
}
