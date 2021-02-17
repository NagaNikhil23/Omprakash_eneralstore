package com.delivery.generalstore.ui.my_orders;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.delivery.generalstore.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyOrdersListAdapterClass extends ArrayAdapter<String> {

    private final Activity context;

    private List<Map<String,Object>> orders;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private String products="";
    private String logged_in_user_id;


    public MyOrdersListAdapterClass(Activity context, List list) {
        super(context, R.layout.my_orders_layout, list);
        // TODO Auto-generated constructor stub
        this.context = context;
        orders = list;
        db = FirebaseFirestore.getInstance();
        //Getting user ID
        mAuth = FirebaseAuth.getInstance();
        logged_in_user_id=mAuth.getCurrentUser().getUid();

    }
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.my_orders_layout, null,true);

        TextView tvstatus = (TextView) rowView.findViewById(R.id.status);
        TextView tvamount=(TextView)rowView.findViewById(R.id.total_amount);
        TextView viewProducts=(TextView)rowView.findViewById(R.id.view_products);
        TextView tvorder_detail_text=(TextView)rowView.findViewById(R.id.order_detail_text);

        final Map<String, Object> map = orders.get(position);
        tvamount.setText(map.get("Amount").toString());
        tvstatus.setText(map.get("Status").toString());

        tvorder_detail_text.setText("Order ID:" +map.get("OrderID").toString()+"\n"+"Placed on :"+map.get("DateTime").toString());


        //Extracting Product with specific position in list and adding it to the row View

        viewProducts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                get_item_details((Map<String, Object>) map.get("Items"),map.get("OrderID").toString());
            }
        });

        return rowView;
    };

    public void get_item_details(Map<String,Object> item_details, final String Orderid){
       int i=0;
       String item_details_text="";
       for (String item_name:item_details.keySet()){
           i=i+1;
           item_details_text=item_details_text+item_name+" : "+item_details.get(item_name)+"\n";

           if(i == item_details.size()){
               AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
               alert.setMessage(item_details_text);
               alert.setTitle("Order ID: "+Orderid);
               alert.setCancelable(true);
               alert.setNegativeButton("Close", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialogInterface, int i) {
                       dialogInterface.dismiss();
                   }
               });
               alert.show();
           }
       }

    }

}