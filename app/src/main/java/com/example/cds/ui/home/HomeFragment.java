package com.example.cds.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.example.cds.Modelclass.Listing;
import com.example.cds.Adapter.Listing_Adapter;
import com.example.cds.Modelclass.Listing;
import com.example.cds.R;
import com.example.cds.databinding.FragmentHomeBinding;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private ListView listView;
    private List<Listing> listings = new ArrayList<>();
    private Listing_Adapter adapter;
    private FirebaseFirestore db;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        listView = root.findViewById(R.id.listing_listview);
        db = FirebaseFirestore.getInstance();
        adapter = new Listing_Adapter(getContext(), listings);
        listView.setAdapter(adapter);

        fetchListings();

        return root;
    }

    private void fetchListings() {
        db.collection("Listings")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    listings.clear();
                    for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                        Listing l = new Listing();
                        l.l_id = doc.getLong("l_id").intValue();
                        l.l_name = doc.getString("l_name");
                        l.l_photo = doc.getString("l_photo");
                        l.l_basic = doc.getLong("l_basic").intValue();
                        l.l_premium = doc.getLong("l_premium").intValue();
                        l.l_basic_txt = (List<String>) doc.get("l_basictxt");
                        l.l_premium_txt = (List<String>) doc.get("l_premiumtxt");
                        listings.add(l);
                    }
                    adapter.notifyDataSetChanged();
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
