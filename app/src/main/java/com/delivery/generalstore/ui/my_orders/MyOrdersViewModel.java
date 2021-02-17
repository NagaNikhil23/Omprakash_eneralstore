package com.delivery.generalstore.ui.my_orders;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyOrdersViewModel extends ViewModel {

    MutableLiveData<List<Map<String,Object>>> mOrders;
    public  List<Map<String,Object>> mOrders_List;
    private FirebaseFirestore db;
    String item_details_text;
    int total_items_count;


    public MyOrdersViewModel() {
        mOrders = new MutableLiveData<>();
        mOrders_List=new ArrayList<>();
        mOrders.setValue(mOrders_List);
        db = FirebaseFirestore.getInstance();
    }

    public void setOrderDetails(final String orderID, final String amount, final Object items, final String status, final String date,final String time) {
        //Log.e(orderID,rName+""+amount+""+items.toString()+""+status+""+date);

        final Map<String,Object> order_details= new HashMap<>();// to store each item details as
        Log.e("In Map Data","Set Order Detials");
        final Map<String,Object> items_from_db=(Map<String, Object>)items;
        final Map<String,Object> items_to_display=new HashMap<>();
        item_details_text="";
        total_items_count=0;
        for(final String item:items_from_db.keySet()){
            db.collection("products").document(item).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    items_to_display.put(documentSnapshot.get("Name").toString(),items_from_db.get(item));
                    //total_items_count=total_items_count+Integer.parseInt(items_from_db.get(item).toString());
                    //item_details_text=item_details_text+documentSnapshot.get("Name").toString()+":"+items_from_db.get(item)+"\n";
                    Log.e("In Map Data - Items",items_from_db.toString()+":"+items_to_display+",,"+item_details_text);


                    if(items_from_db.size()==items_to_display.size()){
                        order_details.put("OrderID",orderID);
                        order_details.put("Amount",amount);
                        order_details.put("Status",status);
                        order_details.put("DateTime",date+","+time);
                        order_details.put("Items",items_to_display);
                        mOrders_List.add(order_details);
                        mOrders.setValue(mOrders_List);
                        Log.e("In Map Data",mOrders_List.toString());
                    }
                }
            });

        }

    }

    public LiveData<List<Map<String,Object>>> getOrderDetails() {
        return mOrders;
    }
}