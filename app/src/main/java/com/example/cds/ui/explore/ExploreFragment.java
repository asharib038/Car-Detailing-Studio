package com.example.cds.ui.explore;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.example.cds.Modelclass.Listing;
import com.example.cds.Adapter.Explore_Adapter;
import com.example.cds.Modelclass.Seller;
import com.example.cds.R;
import com.example.cds.databinding.FragmentHomeBinding;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ExploreFragment extends Fragment {
    private ListView listView;
    private ArrayList<Seller> sellerList;
    private Explore_Adapter adapter;
    private FirebaseFirestore db;

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_explore, container, false);

        listView = view.findViewById(R.id.sellerListView);
        sellerList = new ArrayList<>();
        adapter = new Explore_Adapter(getContext(), sellerList);
        listView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();

        loadSellers();

        return view;
    }

    private void loadSellers() {
        db.collection("Seller")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    sellerList.clear();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Seller seller = doc.toObject(Seller.class);
                        sellerList.add(seller);
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(),
                        "Error loading sellers", Toast.LENGTH_SHORT).show());
    }
}
