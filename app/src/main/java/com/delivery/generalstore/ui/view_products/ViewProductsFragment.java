package com.delivery.generalstore.ui.view_products;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.delivery.generalstore.R;

import java.util.List;

public class ViewProductsFragment extends Fragment {

    private ViewProductsViewModel viewProductsViewModel;
    String[] product_categories = { "Genral Items", "Floors and Rawa's ", "Dals, Pulses and Oils", "All Items"};

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        viewProductsViewModel =
                ViewModelProviders.of(this).get(ViewProductsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_view_products, container, false);
        Spinner category_spinner=(Spinner)root.findViewById(R.id.category_spinner);
        final TextView textView = root.findViewById(R.id.text_dashboard);
        viewProductsViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, product_categories);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        category_spinner.setAdapter(dataAdapter);
        category_spinner.setSelection(product_categories.length-1);
        viewProductsViewModel.setItem(product_categories[product_categories.length-1]);

        category_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                viewProductsViewModel.setItem(product_categories[i]);
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        return root;
    }
}