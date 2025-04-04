package com.example.cds.ui.home;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.cds.Modelclass.Listing;
import com.example.cds.R;
import com.google.gson.Gson;

public class DetailsActivity extends AppCompatActivity {

    TextView title, basic, premium, basicTxt, premiumTxt;
    ImageView imageView;
    Button btnBook;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        // Bind views
        title = findViewById(R.id.detail_title);
        basic = findViewById(R.id.detail_basic);
        premium = findViewById(R.id.detail_premium);
        basicTxt = findViewById(R.id.detail_basic_txt);
        premiumTxt = findViewById(R.id.detail_premium_txt);
        imageView = findViewById(R.id.detail_image);
        btnBook = findViewById(R.id.btn_book);

        // Get listing from intent
        Listing listing = new Gson().fromJson(getIntent().getStringExtra("listing"), Listing.class);

        // Set data
        title.setText(listing.l_name);
        basic.setText("Price: $" + listing.l_basic);
        premium.setText("Price: $" + listing.l_premium);

        // Set features with bullets
        if (listing.l_basic_txt != null) {
            String basicFeatures = "• " + android.text.TextUtils.join("\n• ", listing.l_basic_txt);
            basicTxt.setText(basicFeatures);
        }

        if (listing.l_premium_txt != null) {
            String premiumFeatures = "• " + android.text.TextUtils.join("\n• ", listing.l_premium_txt);
            premiumTxt.setText(premiumFeatures);
        }

        // Load image
        Glide.with(this)
                .load(ImgURLConv(listing.l_photo))
                .placeholder(R.drawable.placeholder)
                .fitCenter()
                .into(imageView);

        // Book button action
        btnBook.setOnClickListener(v -> {
            Toast.makeText(this, "Booking for: " + listing.l_name, Toast.LENGTH_SHORT).show();
            // Add booking functionality here
        });
    }

    public String ImgURLConv(String driveUrl) {
        try {
            String[] parts = driveUrl.split("/");
            String fileId = parts[5];
            return "https://drive.google.com/uc?export=download&id=" + fileId;
        } catch (Exception e) {
            Log.e("DirectDownloadURL", "Error converting URL: " + e.getMessage());
            return null;
        }
    }
}
