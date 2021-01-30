package com.delivery.generalstore.ui.view_products;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ViewProductsViewModel extends ViewModel {

    private MutableLiveData<String> mText;
    private String selected_category="All Products";

    public ViewProductsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("List of "+selected_category);
    }

    public LiveData<String> getText() {
        return mText;
    }

    public void setItem(String product_category) {
        selected_category=product_category;
        mText.setValue("List of "+selected_category);
    }
}