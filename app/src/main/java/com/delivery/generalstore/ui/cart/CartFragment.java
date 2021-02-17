package com.delivery.generalstore.ui.cart;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.delivery.generalstore.R;
import com.delivery.generalstore.ui.my_orders.MyOrdersViewModel;
import com.delivery.generalstore.ui.view_products.ItemsLayoutAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CartFragment extends Fragment {

    private CartViewModel mViewModel;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private FirebaseFirestore db ;
    ListView listView;
    TextView total_cart_price;
    ProgressBar tprogressbar;

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
        total_cart_price=(TextView)view.findViewById(R.id.price_value);
        listView=(ListView)view.findViewById(R.id.listview);
        tprogressbar=(ProgressBar)view.findViewById(R.id.progressbar);

        Button place_order=(Button)view.findViewById(R.id.place_order);

        mViewModel.getText().observe(getViewLifecycleOwner(), new Observer<List<Map<String, Object>>>() {
            @Override
            public void onChanged(List<Map<String, Object>> list) {
                //listItems.add(list.toString());
                CartItemsLayoutAdapter adapter=new CartItemsLayoutAdapter(getActivity(),list);
                Log.e("list",list.toString());
                listView.setAdapter(adapter);
            }
        });

        mViewModel.getTotalCartPrice().observe(getViewLifecycleOwner(), new Observer<Long>() {
            @Override
            public void onChanged(Long aLong) {
                Log.e("Total_cart_price",aLong.toString());
                total_cart_price.setText(aLong.toString());
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

        place_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                Log.e("in", "Place order");
                db.collection("users").document(mAuth.getCurrentUser().getPhoneNumber()+"@generalstore").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Map<String, Object> data= document.getData();
                                //setting details to profile view model.

                                final Map<String,String> address_map=(Map<String,String>)data.get("address");
                                if(address_map!=null){
                                List<String> address_list=new ArrayList<>(address_map.keySet());
                                final List<String> address_details_list= new ArrayList<String>(address_map.values());


                                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                final ArrayAdapter<String> adp = new ArrayAdapter<String>(getContext(),
                                        android.R.layout.simple_spinner_dropdown_item, address_list);

                                LinearLayout layout = new LinearLayout(getContext());
                                layout.setOrientation(LinearLayout.VERTICAL);

                                final Spinner sp = new Spinner(getContext());
                                sp.setAdapter(adp);
                                sp.setPadding(1,10,1,5);
                                layout.addView(sp);

                                final TextView taddress_from_dialog = new TextView(getContext());
                                taddress_from_dialog.setPadding(10,10,10,5);
                                taddress_from_dialog.setTextSize(18);
                                taddress_from_dialog.setTextColor(Color.parseColor("#008040"));
                                layout.addView(taddress_from_dialog);

                                sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                        taddress_from_dialog.setText(address_details_list.get(i));
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> adapterView) {

                                    }
                                });

                                builder.setView(layout);
                                builder.setTitle("Confirm your address");

                                builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        show_progress_bar();
                                        Calendar c = Calendar.getInstance();
                                        SimpleDateFormat datetimeformat = new SimpleDateFormat("yyMMddhhmmss");
                                        SimpleDateFormat dateformat = new SimpleDateFormat("dd-MM-yyyy");
                                        SimpleDateFormat timeformat = new SimpleDateFormat("hh:mm");
                                        String date = dateformat.format(c.getTime());
                                        String time = timeformat.format(c.getTime());
                                        String datetime = datetimeformat.format(c.getTime());
                                        String OID = mAuth.getCurrentUser().getPhoneNumber()+ datetime;
                                        Map<String, Object> items_details=new HashMap<>();
                                        for (Map<String, Object> product : mViewModel.getText().getValue()) {   //get each item details that are present in cart along with the restaurant email..
                                            items_details.put(product.get("Id").toString(),Integer.parseInt(product.get("Count").toString()));
                                        }
                                        create_order(mAuth.getUid(),OID,items_details,mViewModel.getTotalCartPrice().getValue().intValue(),date,time,taddress_from_dialog.getText().toString());
                                        Log.e("Details",mAuth.getUid()+OID+items_details+mViewModel.getTotalCartPrice().getValue().intValue()+date+time+taddress_from_dialog.getText().toString());
                                    }
                                });
                                builder.create().show();
                                }
                                else{
                                    Toast.makeText(getActivity(),"Please add an address in profile section",Toast.LENGTH_LONG).show();
                                }

                            } else {
                                //Will get this executed if no document was found.
                                Log.d("Get Data", "No such document");
                            }
                        } else {
                            //Will get this executed if task gets failed due to any reason.
                            Log.d("Get Data", "get failed with ", task.getException());
                        }
                    }

                });
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

    //remove all items from cart
    private void empty_cart(){
        mDatabase.child("carts").child(mAuth.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.e("empty","cart");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("Non empty","cart");
            }
        });
    }

    public void show_progress_bar(){  //showing progress bar and disabling user interaction on screen
        tprogressbar.setVisibility(View.VISIBLE);
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    public void hide_progress_bar(){ //hiding progress bar and enabling user interaction on screen
        tprogressbar.setVisibility(View.GONE);
        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }


    private void create_order(final String UID, String OID, Map<String,Object> itemdetails,int amount,String date,String time,String address){
        empty_cart();
        Map<String, Object> data=new HashMap<>();
        data.put("OrderID", OID);
        data.put("UserID", UID);
        data.put("Items", itemdetails);
        data.put("Status","Placed");
        data.put("Date",date);
        data.put("Time",time);
        data.put("Address",address);
        data.put("Amount", amount);
        if(db==null){
            Log.e("In create order", "db is getting null");
        }
        else {
            Log.e("In create Order", "entered method and created data");
            db.collection("Orders").document(OID).set(data)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d("in order creation", "DocumentSnapshot successfully written!");
                            Toast.makeText(getContext(),"Order Placed",Toast.LENGTH_LONG).show();

                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                            LinearLayout layout = new LinearLayout(getContext());
                            layout.setOrientation(LinearLayout.VERTICAL);

                            ImageView iv=new ImageView(getContext());
                            iv.setImageResource(R.drawable.order_place);
                            iv.setPadding(10,10,10,10);
                            iv.setMaxHeight(10);
                            iv.setMaxWidth(10);
                            layout.addView(iv);

                            final TextView order_placed_text = new TextView(getContext());
                            order_placed_text.setText("Order Placed Successfully");
                            order_placed_text.setTextSize(20);
                            order_placed_text.setGravity(Gravity.CENTER);
                            order_placed_text.setTextColor(Color.parseColor("#008040"));
                            layout.addView(order_placed_text);

                            builder.setView(layout);
                            builder.create().show();
                            hide_progress_bar();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getContext(),"Failed to Place order" + e.toString(),Toast.LENGTH_LONG).show();
                        }
                    });
        }
    }



}