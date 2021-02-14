package com.delivery.generalstore.ui.view_products;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



public class ViewProductsViewModel extends ViewModel {

    private MutableLiveData<List<Map<String,Object>>> mText;
    private List<Map<String,Object>> products_list;
    private String selected_category="All Products";
    FirebaseFirestore db ;

    public ViewProductsViewModel() {
        mText = new MutableLiveData<>();
        products_list=new ArrayList<>();
        mText.setValue(products_list);
        db=FirebaseFirestore.getInstance();
    }

    public LiveData<List<Map<String,Object>>> getText() {
        return mText;
    }

    public void setItem(String product_category) {
        selected_category=product_category;
        products_list=new ArrayList<>();
        mText.setValue(products_list);
        if(product_category=="All Items"){
            GetProductdetails_from_firestore();
        }
        else {
            GetProductdetails_from_firestore(product_category);
        }
    }

    private void GetProductdetails_from_firestore(String category_name) {
        db.collection("products").whereEqualTo("Category",category_name).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (final DocumentSnapshot ds : queryDocumentSnapshots) {
                    String url = ds.get("Image").toString();
                    StorageReference strgref = FirebaseStorage.getInstance().getReference().child(url);
                    final long ONE_MEGABYTE = 1024 * 1024 * 5;
                    strgref.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                        @Override
                        public void onSuccess(byte[] bytes) {
                            Bitmap image_from_storage = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                            Map<String, Object> product_details = new HashMap<>();
                            product_details.put("Name", ds.get("Name"));
                            product_details.put("Weight", ds.get("Weight"));
                            product_details.put("Price", ds.get("Price"));
                            product_details.put("Image", image_from_storage);
                            product_details.put("Id", ds.getId());
                            products_list.add(product_details);
                            Log.d("In ViewProductsModel","All product details added");
                            mText.setValue(products_list);
                        }
                    });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            Log.e("In ViewProductsModel","Unable to get product details");
            }
        });
    }
    private void GetProductdetails_from_firestore() {
        db.collection("products").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (final DocumentSnapshot ds : queryDocumentSnapshots) {
                    Log.d("In ViewProductsModel","Each for "+queryDocumentSnapshots.size());
                    String url = ds.get("Image").toString();
                    StorageReference strgref = FirebaseStorage.getInstance().getReference().child(url);
                    final long ONE_MEGABYTE = 1024 * 1024 * 5;
                    strgref.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                        @Override
                        public void onSuccess(byte[] bytes) {
                            Log.d("In ViewProductsModel","product details added");
                            Bitmap image_from_storage = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                            Map<String, Object> product_details = new HashMap<>();
                            product_details.put("Name", ds.get("Name"));
                            product_details.put("Weight", ds.get("Weight"));
                            product_details.put("Price", ds.get("Price"));
                            product_details.put("Image", image_from_storage);
                            product_details.put("Id", ds.getId());
                            products_list.add(product_details);
                            mText.setValue(products_list);
                        }
                    });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("In ViewProductsModel","Unable to get product details");
            }
        });

    }

}