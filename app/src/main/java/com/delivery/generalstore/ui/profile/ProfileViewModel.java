package com.delivery.generalstore.ui.profile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ProfileViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public ProfileViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Getting profile details");
    }

    public LiveData<String> getText() {
        return mText;
    }

    public void setdetails(String name, String email, String phone) {
        mText.setValue("Name: "+name + "\n Email: "+email+ "\n Phone: "+phone);
    }
}