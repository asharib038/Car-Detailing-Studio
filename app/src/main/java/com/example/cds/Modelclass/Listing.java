package com.example.cds.Modelclass;

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
        this.l_photo = l_photo;
        this.l_basic = l_basic;
        this.l_premium = l_premium;
        this.l_basic_txt = l_basic_txt;
        this.l_premium_txt = l_premium_txt;
    }
}
