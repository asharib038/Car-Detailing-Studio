package com.example.cds.ui.explore;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.cds.Modelclass.Seller;
import com.example.cds.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

public class SellerDetailsActivity extends AppCompatActivity {

    private FirebaseFirestore db;

    private ImageView detailImage;
    private TextView detailName, detailEmail, detailLocation, detailAddress,
            detailContact, detailPrice, detailRating;

    private Button btnCall, btnSMS, btnMap, btnWhatsapp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_details);

        // Init views
        detailImage = findViewById(R.id.detailImage);
        detailName = findViewById(R.id.detailName);
        detailEmail = findViewById(R.id.detailEmail);
        detailLocation = findViewById(R.id.detailLocation);
        detailAddress = findViewById(R.id.detailAddress);
        detailContact = findViewById(R.id.detailContact);
        detailPrice = findViewById(R.id.detailPrice);
        detailRating = findViewById(R.id.detailRating);

        // Buttons
        btnCall = findViewById(R.id.btnCall);
        btnWhatsapp = findViewById(R.id.btnWhatsapp);
        btnSMS = findViewById(R.id.btnSMS);
        btnMap = findViewById(R.id.btnMap);

        // Firestore init
        db = FirebaseFirestore.getInstance();

        String sellerEmail = getIntent().getStringExtra("seller_email");

        if (sellerEmail != null) {
            loadSellerData(sellerEmail);
        } else {
            Toast.makeText(this, "No seller email provided", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void loadSellerData(String email) {
        db.collection("Seller")
                .whereEqualTo("s_email", email)
                .limit(1)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot doc = queryDocumentSnapshots.getDocuments().get(0);

                        String name = doc.getString("s_name");
                        String photo = doc.getString("s_photo");
                        GeoPoint location = doc.getGeoPoint("s_location");
                        String address = doc.getString("s_address");
                        long contact = doc.getLong("s_contact");
                        long price = doc.getLong("s_price");
                        Double rating = doc.getDouble("s_rating");

                        Seller seller = new Seller(name, photo, rating != null ? rating.floatValue() : 0.0f,
                                email, address, location, contact, price);

                        double slat = location.getLatitude();
                        double slong = location.getLongitude();
                        //https://drive.google.com/uc?export=view&id=1soyw3gwRt9zJM9srXZOvqjYi3pbIx5EQ

                        Glide.with(this).load(ImgURLConv(photo)).transform(new RoundedCorners(16)).into(detailImage);
                        detailName.setText("" + name);
                        detailEmail.setText("Email: " + email);
                        detailLocation.setText("Location: " + slat + ", " + slong);
                        detailAddress.setText("Address: " + address);
                        detailContact.setText("Contact: " + contact);
                        detailPrice.setText("Starting from: PKR" + price);
                        detailRating.setText("Rating: " + rating + " ★");

                        // Button click listeners
                        btnCall.setOnClickListener(v -> {
                            Intent intent = new Intent(Intent.ACTION_DIAL);
                            intent.setData(Uri.parse("tel:" + contact));
                            startActivity(intent);
                        });

                        btnWhatsapp.setOnClickListener(v -> {
                            String phoneNumber = "+91" + contact; // Add your country code here
                            try {
                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                intent.setData(Uri.parse("https://wa.me/" + phoneNumber));
                                startActivity(intent);
                            } catch (Exception e) {
                                Toast.makeText(SellerDetailsActivity.this, "WhatsApp not installed", Toast.LENGTH_SHORT).show();
                            }
                        });

                        btnSMS.setOnClickListener(v -> {
                            Intent intent = new Intent(Intent.ACTION_SENDTO);
                            intent.setData(Uri.parse("smsto:" + contact));
                            intent.putExtra("sms_body", "Hello, I’m interested in your services.");
                            startActivity(intent);
                        });

                        btnMap.setOnClickListener(v -> {
                            String geoUri = "geo:" + slat + "," + slong + "?q=" + Uri.encode(address);
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(geoUri));
                            intent.setPackage("com.google.android.apps.maps");
                            startActivity(intent);
                        });

                    } else {
                        Toast.makeText(this, "Seller not found", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error loading seller: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    finish();
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
