package com.delivery.generalstore.ui.login;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import android.util.Patterns;

import com.delivery.generalstore.data.LoginRepository;
import com.delivery.generalstore.data.Result;
import com.delivery.generalstore.data.model.LoggedInUser;
import com.delivery.generalstore.R;

public class LoginViewModel extends ViewModel {

    private MutableLiveData<LoginFormState> loginFormState = new MutableLiveData<>();
    LiveData<LoginFormState> getLoginFormState() {
        return loginFormState;
    }

    public void loginDataChanged(String phone, String otp) {
        if (!isPhoneValid(phone)) {
            loginFormState.setValue(new LoginFormState(R.string.invalid_phone_number, null));
        } else if (!isOtpValid(otp)) {
            loginFormState.setValue(new LoginFormState(null, R.string.invalid_otp));
        } else {
            loginFormState.setValue(new LoginFormState(true));
        }
    }

    // A placeholder username validation check
    private boolean isPhoneValid(String phone) {
        if (phone == null) {
            return false;
        }
        else {
            return phone.trim().length() == 10 && Patterns.PHONE.matcher(phone).matches();
        }
    }

    // A placeholder password validation check
    private boolean isOtpValid(String otp) {
        return otp != null && otp.trim().length() > 5;
    }
}