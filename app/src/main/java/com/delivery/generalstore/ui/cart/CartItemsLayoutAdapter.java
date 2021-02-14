package com.delivery.generalstore.ui.cart;

import android.app.Activity;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.delivery.generalstore.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CartItemsLayoutAdapter extends ArrayAdapter<String> {

    private final Activity context;
    private List<Map<String,Object>> products;
    private FirebaseAuth mAuth;
    private String logged_in_user_id;
    private DatabaseReference mDatabase;


    public CartItemsLayoutAdapter(Activity context, List list) {
        super(context, R.layout.items_layout, list);
        // TODO Auto-generated constructor stub
        this.context = context;
        products = list;
        mAuth = FirebaseAuth.getInstance();
        mDatabase=FirebaseDatabase.getInstance().getReference();
        logged_in_user_id=mAuth.getCurrentUser().getUid();
    }
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.items_layout, null, true);

        TextView titleText = (TextView) rowView.findViewById(R.id.name);
        TextView subtitleText = (TextView) rowView.findViewById(R.id.description);
        TextView price = (TextView) rowView.findViewById(R.id.price);
        Button add_to_cart = (Button) rowView.findViewById(R.id.add_to_cart_button);
        final Button remove_from_cart = (Button) rowView.findViewById(R.id.remove_from_cart);
        final TextView quantities_added = (TextView) rowView.findViewById(R.id.quantities_added);
        final ImageView image = (ImageView) rowView.findViewById(R.id.icon);

        //Extracting Product with specific position in list and adding it to the row View
        Map<String, Object> map = products.get(position);
        titleText.setText(map.get("Name").toString());
        subtitleText.setText(map.get("Weight").toString());
        price.setText(map.get("Price").toString());
        image.setImageBitmap((Bitmap) map.get("Image"));
        quantities_added.setText(map.get("Count").toString());
        final String PID=map.get("Id").toString();

        Log.e("Cart Page",map+"");


        add_to_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int quantites=Integer.parseInt(quantities_added.getText().toString())+1;
                quantities_added.setText(String.valueOf(quantites));
               Log.e("quantities",quantites+"");
                Map<String, Object> updates = new HashMap<>();
                updates.put("/carts/"+logged_in_user_id+"/"+PID+"/",quantities_added.getText().toString());
                mDatabase.updateChildren(updates).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.e("Updated","cart");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Failed",e+"");
                    }
                });
            }
        });

        remove_from_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(quantities_added.getText().toString().trim().equals("0")) {  // to ignore remove button click when the products count in cart is 0.
                    Log.e("Value in if",quantities_added.getText().toString());
                    Toast.makeText(getContext(),"Selected Item Not in Cart", Toast.LENGTH_LONG).show();
                    return;
                }
                //Get latest updated count selected from user
                int quantites=Integer.parseInt(quantities_added.getText().toString())-1;
                quantities_added.setText(String.valueOf(quantites));

                if(quantites<=1) { // to delete productid from cart when the products count is less than 1,i.e 0.
                    Log.e("Items in cart",quantities_added.getText().toString());
                    mDatabase.child("carts").child(logged_in_user_id).child(PID).removeValue();
                    return;
                }
                //Creating a MAP variable to add latest values of this Product to the database
                Map<String, Object> updates = new HashMap<>();
                updates.put("/carts/"+logged_in_user_id+"/"+PID+"/",quantities_added.getText().toString());
                mDatabase.updateChildren(updates).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.e("Updated","cart");
                    }
                })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e("Failed",e+"");
                            }
                        });
            }
        });
        //Get the products count in cart for specific Product, with the PID
        return rowView;

    }

}
