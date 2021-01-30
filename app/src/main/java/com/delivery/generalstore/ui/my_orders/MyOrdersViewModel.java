package com.delivery.generalstore.ui.my_orders;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MyOrdersViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public MyOrdersViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("MyOrders Page is under construction");
    }

    public LiveData<String> getText() {
        return mText;
    }
}