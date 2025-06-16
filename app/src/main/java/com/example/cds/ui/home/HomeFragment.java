package com.example.cds.ui.home;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private GridView listView;
    private List<Listing> listings = new ArrayList<>();
    private Listing_Adapter adapter;
    private FirebaseFirestore db;
    private EditText searchBar;
    private Spinner sortSpinner;
    private List<Listing> fullList = new ArrayList<>();


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
            v.setPadding(0, 0, 0, 0);
            return WindowInsetsCompat.CONSUMED;
        });

        listView = root.findViewById(R.id.listing_listview);

        searchBar = root.findViewById(R.id.search_bar);
        sortSpinner = root.findViewById(R.id.sort_spinner);

        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(
                getContext(), R.array.sort_options, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sortSpinner.setAdapter(spinnerAdapter);

        searchBar.addTextChangedListener(new TextWatcher() {
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterListings(s.toString());
            }
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
        });

        sortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sortListings(position);
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });


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
                    fullList.clear();
                    for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                        Listing l = new Listing();
                        l.l_id = doc.getLong("l_id").intValue();
                        l.l_name = doc.getString("l_name");
                        l.l_photo = doc.getString("l_photo");
                        l.l_basic = doc.getLong("l_basic").intValue();
                        l.l_premium = doc.getLong("l_premium").intValue();
                        l.l_basic_txt = (List<String>) doc.get("l_basictxt");
                        l.l_premium_txt = (List<String>) doc.get("l_premiumtxt");
                        fullList.add(l);
                    }
                    listings.addAll(fullList);
                    adapter.notifyDataSetChanged();
                });
    }

    private void filterListings(String query) {
        listings.clear();
        for (Listing l : fullList) {
            if (l.l_name.toLowerCase().contains(query.toLowerCase())) {
                listings.add(l);
            }
        }
        adapter.notifyDataSetChanged();
    }

    private void sortListings(int option) {
        switch (option) {
            case 0: // Price: Low to High
                Collections.sort(listings, Comparator.comparingInt(l -> l.l_basic));
                break;
            case 1: // Price: High to Low
                Collections.sort(listings, (a, b) -> b.l_basic - a.l_basic);
                break;
            case 2: // Name: A-Z
                Collections.sort(listings, Comparator.comparing(l -> l.l_name.toLowerCase()));
                break;
            case 3: // Name: Z-A
                Collections.sort(listings, (a, b) -> b.l_name.toLowerCase().compareTo(a.l_name.toLowerCase()));
                break;
        }
        adapter.notifyDataSetChanged();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

