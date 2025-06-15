package com.example.cds.ui.home;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.bumptech.glide.Glide;
import com.example.cds.Modelclass.Listing;
import com.example.cds.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class DetailsActivity extends AppCompatActivity {

    TextView title, basic, premium, basicTxt, premiumTxt;
    ImageView imageView;
    Button btnBook;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        title = findViewById(R.id.detail_title);
        basic = findViewById(R.id.detail_basic);
        premium = findViewById(R.id.detail_premium);
        basicTxt = findViewById(R.id.detail_basic_txt);
        premiumTxt = findViewById(R.id.detail_premium_txt);
        imageView = findViewById(R.id.detail_image);
        btnBook = findViewById(R.id.btn_book);

        Listing listing = new Gson().fromJson(getIntent().getStringExtra("listing"), Listing.class);

        title.setText(listing.l_name);
        basic.setText("Price: $" + listing.l_basic);
        premium.setText("Price: $" + listing.l_premium);

        if (listing.l_basic_txt != null) {
            String basicFeatures = "• " + android.text.TextUtils.join("\n• ", listing.l_basic_txt);
            basicTxt.setText(basicFeatures);
        }

        if (listing.l_premium_txt != null) {
            String premiumFeatures = "• " + android.text.TextUtils.join("\n• ", listing.l_premium_txt);
            premiumTxt.setText(premiumFeatures);
        }

        Glide.with(this)
                .load(ImgURLConv(listing.l_photo))
                .placeholder(R.drawable.placeholder)
                .fitCenter()
                .into(imageView);

        btnBook.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            @SuppressLint("InflateParams")
            final android.view.View dialogView = getLayoutInflater().inflate(R.layout.dialog_book, null);
            builder.setView(dialogView);
            final AlertDialog dialog = builder.create();

            RadioGroup packageGroup = dialogView.findViewById(R.id.radio_package);
            DatePicker datePicker = dialogView.findViewById(R.id.date_picker);
            TimePicker timePicker = dialogView.findViewById(R.id.time_picker);
            timePicker.setIs24HourView(false);
            EditText editLocation = dialogView.findViewById(R.id.edit_location);
            Button btnGetLocation = dialogView.findViewById(R.id.btn_get_location);
            Spinner spinnerPayment = dialogView.findViewById(R.id.spinner_payment);

            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                    this,
                    R.array.payment_methods,
                    android.R.layout.simple_spinner_item
            );
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerPayment.setAdapter(adapter);

            Button btnConfirm = dialogView.findViewById(R.id.btn_confirm);
            btnGetLocation.setOnClickListener(v1 -> {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1001);
                    return;
                }

                FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
                fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
                    if (location != null) {
                        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                        try {
                            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                            if (!addresses.isEmpty()) {
                                String address = addresses.get(0).getAddressLine(0);
                                editLocation.setText(address);
                            } else {
                                Toast.makeText(this, "Address not found", Toast.LENGTH_SHORT).show();
                            }
                        } catch (IOException e) {
                            Toast.makeText(this, "Geocoder error", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "Location not available", Toast.LENGTH_SHORT).show();
                    }
                });
            });

            btnConfirm.setOnClickListener(view -> {
                int selectedId = packageGroup.getCheckedRadioButtonId();
                String selectedPackage = (selectedId == R.id.radio_basic) ? "Basic" : "Premium";

                int day = datePicker.getDayOfMonth();
                int month = datePicker.getMonth() + 1;
                int year = datePicker.getYear();

                int hour = timePicker.getHour();
                int minute = timePicker.getMinute();

                String location = editLocation.getText().toString().trim();
                String payment = spinnerPayment.getSelectedItem().toString();

                if (location.isEmpty()) {
                    editLocation.setError("Location required");
                    return;
                }

                dialog.dismiss();

                String selectedPrice = String.valueOf(selectedPackage.equals("Basic") ? listing.l_basic : listing.l_premium);
                String bookingId = FirebaseFirestore.getInstance().collection("Bookings").document().getId();

                HashMap<String, Object> booking = new HashMap<>();
                booking.put("booking_id", bookingId);
                booking.put("listing_name", listing.l_name);
                booking.put("listing_id", listing.l_id);
                booking.put("selected_package", selectedPackage);
                booking.put("package_price", selectedPrice);
                booking.put("listing_image", ImgURLConv(listing.l_photo));
                booking.put("date", day + "/" + month + "/" + year);
                booking.put("time", String.format(Locale.getDefault(), "%02d:%02d", hour, minute));
                booking.put("location", location);
                booking.put("payment_method", payment);
                booking.put("timestamp", Timestamp.now());

                FirebaseFirestore.getInstance()
                        .collection("Bookings")
                        .document(bookingId)
                        .set(booking)
                        .addOnSuccessListener(unused -> Toast.makeText(this, "Booking Confirmed!", Toast.LENGTH_SHORT).show())
                        .addOnFailureListener(e -> Toast.makeText(this, "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            });

            dialog.show();
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
