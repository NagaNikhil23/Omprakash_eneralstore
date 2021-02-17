package com.delivery.generalstore.ui.my_orders;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;

import com.delivery.generalstore.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class MyOrdersFragment extends Fragment {

    private MyOrdersViewModel myOrdersViewModel;
    DatePicker datePicker;
    Button get_data;
    ListView listView;
    ImageView empty_image;
    FirebaseAuth mAuth;  //for user auth details
    FirebaseFirestore db ; //for firestore access
    ProgressBar tProgressbar;
    List<Map<String, Object>> orders_list;


    public void onCreate(@androidx.annotation.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        db= FirebaseFirestore.getInstance();
        orders_list=new ArrayList<>();
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        myOrdersViewModel =
                ViewModelProviders.of(this).get(MyOrdersViewModel.class);
        View root = inflater.inflate(R.layout.fragment_my_orders, container, false);
        //intialize all componenets
        datePicker=(DatePicker) root.findViewById(R.id.datepicker);
        get_data=(Button)root.findViewById(R.id.get_data);
        listView=(ListView)root.findViewById(R.id.listview);
        empty_image=(ImageView)root.findViewById(R.id.empty_image);
        tProgressbar=(ProgressBar)root.findViewById(R.id.progressbar);

        myOrdersViewModel.getOrderDetails().observe(getViewLifecycleOwner(), new Observer<List<Map<String, Object>>>() {
            @Override
            public void onChanged(List<Map<String, Object>> list) {
                orders_list=list;
                initiate_list();
                Log.e("Data",orders_list.toString());
            }
        });


        //when get data is clicked
        get_data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                show_progress_bar();  //show progress bar
                //String date=datePicker.getDayOfMonth()+"-"+(datePicker.getMonth()+1)+"-"+datePicker.getYear();  //get picked date

                Calendar calendar = Calendar.getInstance();
                calendar.set(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth());

                SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
                String strDate = format.format(calendar.getTime());

                //Get orders that are placed by this user on selected date
                Log.e(mAuth.getCurrentUser().getUid(),strDate);
                db.collection("Orders").whereEqualTo("UserID",mAuth.getCurrentUser().getUid()).whereEqualTo("Date",strDate.trim()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        Log.e("data",queryDocumentSnapshots.getDocuments().toString());
                        //if no orders are found, initialize listview adapter with empty orderslist
                        if(queryDocumentSnapshots.isEmpty()){
                            hide_progress_bar();
                            empty_image.setVisibility(View.VISIBLE);
                            listView.setVisibility(View.INVISIBLE);
                            return;
                        }
                        empty_image.setVisibility(View.INVISIBLE);
                        listView.setVisibility(View.VISIBLE);
                        //add values to viewmodels setOrderdetails method.
                        for(final DocumentSnapshot orders_doc:queryDocumentSnapshots){
                            myOrdersViewModel.setOrderDetails(orders_doc.get("OrderID").toString(),orders_doc.get("Amount").toString(),orders_doc.get("Items"),orders_doc.get("Status").toString(),orders_doc.get("Date").toString(),orders_doc.get("Time").toString());
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("failed","failed");
                        hide_progress_bar();
                    }
                });

            }
        });
        return root;
    }

    private void initiate_list(){
        Collections.sort(orders_list, new ListMapComparator());
        MyOrdersListAdapterClass adapter=new MyOrdersListAdapterClass(getActivity(),orders_list);
        /*Log.e("list",orders_list.toString());*/
        listView.setAdapter(adapter);
        hide_progress_bar();
    }

    public void show_progress_bar(){  //showing progress bar and disabling user interaction on screen
        tProgressbar.setVisibility(View.VISIBLE);
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    public void hide_progress_bar(){ //hiding progress bar and enabling user interaction on screen
        tProgressbar.setVisibility(View.GONE);
        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

}

class ListMapComparator implements Comparator {
    public int compare(Object obj1, Object obj2) {
        Map<String, String> test1 = (Map<String, String>) obj1;
        Map<String, String> test2 = (Map<String, String>) obj2;
        return test1.get("DateTime").compareTo(test2.get("DateTime"));
    }
}