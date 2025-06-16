package com.example.cds.ui.booking;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.cds.Adapter.Booking_Adapter;
import com.example.cds.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BookingFragment extends Fragment {

    RecyclerView recyclerView;
    List<Map<String, Object>> bookings = new ArrayList<>();
    FirebaseFirestore db;
    Booking_Adapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_booking3, container, false);
        recyclerView = view.findViewById(R.id.recycler_bookings);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        db = FirebaseFirestore.getInstance();

        adapter = new Booking_Adapter(bookings, getContext());
        recyclerView.setAdapter(adapter);

        db.collection("Bookings").get().addOnSuccessListener(querySnapshot -> {
            bookings.clear();
            for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                bookings.add(doc.getData());
            }
            adapter.notifyDataSetChanged();
        });

        return view;
    }
}
