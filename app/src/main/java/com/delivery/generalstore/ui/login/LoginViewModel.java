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

    public void loginDataChanged(String name, String email,String phone, String otp) {
        if (!isNameValid(name)) {
            loginFormState.setValue(new LoginFormState(R.string.invalid_username,null,null,null));
        } else if (!isEmailValid(email)) {
            loginFormState.setValue(new LoginFormState(null, R.string.invalid_email, null, null));
        }else  if (!isPhoneValid(phone)) {
            loginFormState.setValue(new LoginFormState(null,null,R.string.invalid_phone_number,null));
        } else if (!isOtpValid(otp)) {
            loginFormState.setValue(new LoginFormState(null,null,null, R.string.invalid_otp));
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

    // A placeholder Email validation check
    private boolean isEmailValid(String email) {
        if (email.contains("@")) {
            return Patterns.EMAIL_ADDRESS.matcher(email).matches();
        } else {
            return false;
        }
    }

    // A placeholder username validation check
    private boolean isNameValid(String name) {
        return name.trim().length() > 5 && name.matches("^[a-zA-Z ]*$");
    }

}