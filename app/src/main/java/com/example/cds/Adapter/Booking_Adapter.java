package com.example.cds.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.cds.R;
import com.example.cds.ui.booking.BookingDetailsActivity;
import com.google.gson.Gson;

import java.util.List;
import java.util.Map;

public class Booking_Adapter extends RecyclerView.Adapter<Booking_Adapter.BookingViewHolder> {

    List<Map<String, Object>> list;
    Context context;

    public Booking_Adapter(List<Map<String, Object>> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public BookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new BookingViewHolder(LayoutInflater.from(context).inflate(R.layout.item_booking, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull BookingViewHolder holder, int position) {
        Map<String, Object> booking = list.get(position);
        holder.title.setText((String) booking.get("listing_name"));
        holder.price.setText("Price: $" + booking.get("package_price"));
        holder.date.setText("Date: " + booking.get("date"));
        holder.time.setText("Time: " + booking.get("time"));
        Glide.with(context).load((String) booking.get("listing_image")).into(holder.image);

        holder.itemView.setOnClickListener(v -> {
            String bookingId = (String) booking.get("booking_id");
            Intent intent = new Intent(context, BookingDetailsActivity.class);
            intent.putExtra("booking_id", bookingId);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class BookingViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView title;
        TextView price, date, time;


        public BookingViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.booking_image);
            title = itemView.findViewById(R.id.booking_title);
            price = itemView.findViewById(R.id.booking_price);
            date = itemView.findViewById(R.id.booking_date);
            time = itemView.findViewById(R.id.booking_time);
        }
    }
}

