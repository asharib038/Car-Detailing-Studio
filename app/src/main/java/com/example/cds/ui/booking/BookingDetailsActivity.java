package com.example.cds.ui.booking;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.cds.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class BookingDetailsActivity extends AppCompatActivity {

    ImageView imageView;
    TextView title, pack, price, date, time, location, payment;
    Button btnCancel;
    String bookingId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_details);

        imageView = findViewById(R.id.image_booking);
        title = findViewById(R.id.text_title);
        pack = findViewById(R.id.text_package);
        price = findViewById(R.id.text_price);
        date = findViewById(R.id.text_date);
        time = findViewById(R.id.text_time);
        location = findViewById(R.id.text_location);
        payment = findViewById(R.id.text_payment);
        btnCancel = findViewById(R.id.btn_cancel);

        Button btnCall = findViewById(R.id.btn_call);
        Button btnChat = findViewById(R.id.btn_chat);
        Button btnMap = findViewById(R.id.btn_map);
        Button btnWhatsApp = findViewById(R.id.btn_whatsapp);

// Replace with real seller phone & location data if available
        String sellerPhone = "+921234567890";
        String sellerMessage = "Hi, I have a query regarding my booking.";
        String sellerLat = "33.6844";
        String sellerLng = "73.0479";

        btnCall.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(android.net.Uri.parse("tel:" + sellerPhone));
            startActivity(intent);
        });

        btnChat.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(android.net.Uri.parse("smsto:" + sellerPhone));
            intent.putExtra("sms_body", sellerMessage);
            startActivity(intent);
        });

        btnMap.setOnClickListener(v -> {
            String uri = "geo:" + sellerLat + "," + sellerLng + "?q=" + android.net.Uri.encode("Seller Location");
            Intent intent = new Intent(Intent.ACTION_VIEW, android.net.Uri.parse(uri));
            startActivity(intent);
        });

        btnWhatsApp.setOnClickListener(v -> {
            String url = "https://wa.me/" + sellerPhone.replace("+", "") + "?text=" + android.net.Uri.encode(sellerMessage);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(android.net.Uri.parse(url));
            startActivity(intent);
        });


        // Get booking ID from intent
        bookingId = getIntent().getStringExtra("booking_id");
        if (bookingId == null) {
            Toast.makeText(this, "No booking ID provided", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Fetch booking from Firestore
        FirebaseFirestore.getInstance().collection("Bookings")
                .document(bookingId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        displayBookingDetails(documentSnapshot);
                    } else {
                        Toast.makeText(this, "Booking not found", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    finish();
                });

        // Cancel booking
        btnCancel.setOnClickListener(v -> {
            if (bookingId != null) {
                FirebaseFirestore.getInstance().collection("Bookings")
                        .document(bookingId)
                        .delete()
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(this, "Booking cancelled", Toast.LENGTH_SHORT).show();
                            finish();
                        })
                        .addOnFailureListener(e -> Toast.makeText(this, "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void displayBookingDetails(DocumentSnapshot doc) {
        String imageUrl = doc.getString("listing_image");
        String listingName = doc.getString("listing_name");
        String selectedPackage = doc.getString("selected_package");
        String packagePrice = doc.getString("package_price");
        String bookingDate = doc.getString("date");
        String bookingTime = doc.getString("time");
        String bookingLocation = doc.getString("location");
        String paymentMethod = doc.getString("payment_method");

        Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.placeholder)
                .into(imageView);

        title.append(listingName);
        pack.append(selectedPackage);
        price.append(packagePrice);
        date.append(bookingDate);
        time.append(bookingTime);
        location.append(bookingLocation);
        payment.append(paymentMethod);
    }
}
