package com.delivery.generalstore.ui.view_products;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.delivery.generalstore.R;

import java.util.List;
import java.util.Map;

public class ViewProductsFragment extends Fragment {

    private ViewProductsViewModel viewProductsViewModel;
    private ProgressBar progressBar;

    String[] product_categories = { "Genral Items", "Floors and Rawa's", "Dals, Pulses and Oils", "All Items"};

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        viewProductsViewModel =
                ViewModelProviders.of(this).get(ViewProductsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_view_products, container, false);
        Spinner category_spinner=(Spinner)root.findViewById(R.id.category_spinner);
        final ListView listView = root.findViewById(R.id.listview);
       progressBar =root.findViewById(R.id.progressbar);
        viewProductsViewModel.getText().observe(getViewLifecycleOwner(), new Observer<List<Map<String,Object>>>() {
            @Override
            public void onChanged(@Nullable List<Map<String,Object>> s) {
                ItemsLayoutAdapter adapter=new ItemsLayoutAdapter(getActivity(),s);
                Log.e("list",s.toString());
                listView.setAdapter(adapter);
                hide_progress_bar();
            }
        });

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, product_categories);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        category_spinner.setAdapter(dataAdapter);
        category_spinner.setSelection(product_categories.length-1);

        category_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                viewProductsViewModel.setItem(product_categories[i]);
                show_progress_bar();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        return root;
    }
    public void show_progress_bar(){  //showing progress bar and disabling user interaction on screen
        progressBar.setVisibility(View.VISIBLE);
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    public void hide_progress_bar(){ //hiding progress bar and enabling user interaction on screen
        progressBar.setVisibility(View.GONE);
        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

}