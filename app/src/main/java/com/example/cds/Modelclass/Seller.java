package com.example.cds.Modelclass;

import android.util.Log;

public class Seller {



    private String s_name, s_photo, s_price;
    private float s_rating;

    public Seller() {} // Needed for Firestore

    public String getS_name() { return s_name; }
    public String getS_photo() { return ImgURLConv(s_photo); } // Convert Drive URL to Direct Download URLs_photo; }
    public String getS_price() { return s_price; }
    public float getS_rating() { return s_rating; }

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
