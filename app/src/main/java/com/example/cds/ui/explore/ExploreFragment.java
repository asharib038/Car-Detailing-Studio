package com.example.cds.ui.explore;

import android.annotation.SuppressLint;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ExploreFragment extends Fragment {
    private ListView listView;
    private ArrayList<Seller> sellerList;
    private Explore_Adapter adapter;
    private FirebaseFirestore db;

    private EditText searchBar;
    private Spinner sortSpinner;
    private ArrayList<Seller> fullSellerList = new ArrayList<>();


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
        searchBar = view.findViewById(R.id.search_bar_explore);
        sortSpinner = view.findViewById(R.id.sort_spinner_explore);

        ArrayAdapter<CharSequence> sortAdapter = ArrayAdapter.createFromResource(
                getContext(), R.array.sort_options, android.R.layout.simple_spinner_item);
        sortAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sortSpinner.setAdapter(sortAdapter);

        searchBar.addTextChangedListener(new TextWatcher() {
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterSellers(s.toString());
            }
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
        });

        sortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sortSellers(position);
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });


        db = FirebaseFirestore.getInstance();

        loadSellers();

        return view;
    }

    private void loadSellers() {
        db.collection("Seller")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    sellerList.clear();
                    fullSellerList.clear();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Seller seller = doc.toObject(Seller.class);
                        sellerList.add(seller);
                        fullSellerList.add(seller);
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(),
                        "Error loading sellers", Toast.LENGTH_SHORT).show());
    }

    private void filterSellers(String query) {
        sellerList.clear();
        for (Seller s : fullSellerList) {
            if (s.getS_name().toLowerCase().contains(query.toLowerCase())) {
                sellerList.add(s);
            }
        }
        adapter.notifyDataSetChanged();
    }

    private void sortSellers(int option) {
        switch (option) {
            case 2: // A-Z
                Collections.sort(sellerList, Comparator.comparing(s -> s.getS_name().toLowerCase()));
                break;
            case 3: // Z-A
                Collections.sort(sellerList, (a, b) -> b.getS_name().toLowerCase().compareTo(a.getS_name().toLowerCase()));
                break;
            case 0: // Price: Low to High
                Collections.sort(sellerList, Comparator.comparingLong(Seller::getS_price));
                break;
            case 1: // Price: High to Low
                Collections.sort(sellerList, (a, b) -> (int) (b.getS_price() - a.getS_price()));
                break;
        }
        adapter.notifyDataSetChanged();
    }

}
