package com.delivery.generalstore.ui.cart;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CartViewModel extends ViewModel {

    private MutableLiveData<List<Map<String,Object>>> products;
    private List<Map<String,Object>> products_list;
    FirebaseFirestore db ;

    public CartViewModel() {
        products = new MutableLiveData<>();
        products_list=new ArrayList<>();
        products.setValue(products_list);
        db=FirebaseFirestore.getInstance();
    }

    public void setCartData(final Map<String, Object> products_from_fragment) {
        products_list=new ArrayList<>();
        for (final String product:products_from_fragment.keySet())
        db.collection("products").document(product).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(final DocumentSnapshot ds) {
                Log.e("product id",product.toString());
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
                                    product_details.put("Count",products_from_fragment.get(product));
                                    products_list.add(product_details);
                                    Log.d("In CartViewModel","All product details added");
                                    products.setValue(products_list);
                                }
                            });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("In ViewProductsModel","Unable to get product details");
            }
        });
    }

    public LiveData<List<Map<String, Object>>> getText() {
                return products;
    }
    // TODO: Implement the ViewModel
}