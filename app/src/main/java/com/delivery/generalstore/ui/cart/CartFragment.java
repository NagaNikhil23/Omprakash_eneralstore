package com.delivery.generalstore.ui.cart;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.delivery.generalstore.R;
import com.delivery.generalstore.ui.my_orders.MyOrdersViewModel;
import com.delivery.generalstore.ui.view_products.ItemsLayoutAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.Map;

public class CartFragment extends Fragment {

    private CartViewModel mViewModel;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private FirebaseFirestore db ;
    ListView listView;

    public static CartFragment newInstance() {
        return new CartFragment();
    }

    @Override
    public void onCreate(@androidx.annotation.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        db= FirebaseFirestore.getInstance();

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.cart_fragment, container, false);
        mViewModel= ViewModelProviders.of(this).get(CartViewModel.class);

        listView=(ListView)view.findViewById(R.id.listview);

        mViewModel.getText().observe(getViewLifecycleOwner(), new Observer<List<Map<String, Object>>>() {
            @Override
            public void onChanged(List<Map<String, Object>> list) {
                //listItems.add(list.toString());
                CartItemsLayoutAdapter adapter=new CartItemsLayoutAdapter(getActivity(),list);
                Log.e("list",list.toString());
                listView.setAdapter(adapter);
            }
        });

        mDatabase.child("carts").child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists()){
                    Map<String, Object> products = (Map<String, Object>) snapshot.getValue();
                    mViewModel.setCartData(products);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(CartViewModel.class);
        // TODO: Use the ViewModel
    }



}