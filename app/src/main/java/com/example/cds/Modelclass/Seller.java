package com.example.cds.Modelclass;

import android.util.Log;
import com.google.firebase.firestore.GeoPoint;

public class Seller {



    private String s_name, s_photo;
    private float s_rating;
    private String s_email, s_address;
    private GeoPoint s_location;
    private long s_contact,s_price;

    public Seller() {} // Needed for Firestore

    public Seller(String s_name, String s_photo, float s_rating, String s_email,
                  String s_address, GeoPoint s_location, long s_contact, long s_price) {
        this.s_name = s_name;
        this.s_photo = s_photo;
        this.s_rating = s_rating;
        this.s_email = s_email;
        this.s_address = s_address;
        this.s_location = s_location;
        this.s_contact = s_contact;
        this.s_price = s_price;
    }


    public String getS_name() { return s_name; }
    public String getS_photo() { return ImgURLConv(s_photo); } // Convert Drive URL to Direct Download URLs_photo; }
    public long getS_price() { return s_price; }
    public float getS_rating() { return s_rating; }
    public String getS_email() { return s_email; }
    public GeoPoint getS_location() {
        return s_location;
    }
    public void setS_location(GeoPoint s_location) {
        this.s_location = s_location;
    }
    public String getS_address() { return s_address; }
    public long getS_contact() { return s_contact; }


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
