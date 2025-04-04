package com.example.cds.Modelclass;

import android.util.Log;

import java.util.List;

public class Listing {
    public int l_id;
    public String l_name;
    public String l_photo;
    public int l_basic;
    public int l_premium;
    public List<String> l_basic_txt;
    public List<String> l_premium_txt;

    public Listing() {} // Needed for Firebase

    public Listing(int l_id, String l_name, String l_photo, int l_basic, int l_premium, List<String> l_basic_txt, List<String> l_premium_txt) {
        this.l_id = l_id;
        this.l_name = l_name;
        this.l_photo = (l_photo);
        this.l_basic = l_basic;
        this.l_premium = l_premium;
        this.l_basic_txt = l_basic_txt;
        this.l_premium_txt = l_premium_txt;
    }

    // Getters
    public int getL_id() {
        return l_id;
    }

    public String getL_name() {
        return l_name;
    }

    public String getL_photo() {
        return l_photo;
    }

    public int getL_basic() {
        return l_basic;
    }

    public int getL_premium() {
        return l_premium;
    }

    public List<String> getL_basic_txt() {
        return l_basic_txt;
    }

    public List<String> getL_premium_txt() {
        return l_premium_txt;
    }

    // Image URL converter
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
