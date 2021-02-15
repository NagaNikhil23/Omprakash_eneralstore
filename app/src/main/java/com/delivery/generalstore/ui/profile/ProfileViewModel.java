package com.delivery.generalstore.ui.profile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.HashMap;
import java.util.Map;

public class ProfileViewModel extends ViewModel {

    private MutableLiveData<Map<String, Object>> mText;

    public ProfileViewModel() {
        mText = new MutableLiveData<>();
    }

    public LiveData<Map<String, Object>> getText() {
        return mText;
    }

    public void setdetails(String name, String email, String phone, Map<String,Object> addresses) {
        Map<String, Object> data=new HashMap<>();
        data.put("Name",name);
        data.put("Phone",phone);
        data.put("Email",email );
        data.put("Address", addresses);
        mText.setValue(data);

    }
}