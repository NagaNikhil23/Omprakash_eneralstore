package com.delivery.generalstore.ui.login;

import androidx.annotation.Nullable;

/**
 * Data validation state of the login form.
 */
class LoginFormState {
    @Nullable
    private Integer phoneError;
    @Nullable
    private Integer otpError;
    @Nullable
    private Integer nameError;
    @Nullable
    private Integer emailError;
    private boolean isDataValid;

    LoginFormState(@Nullable Integer nameError,@Nullable Integer emailError,@Nullable Integer phoneError, @Nullable Integer otpError) {
        this.phoneError = phoneError;
        this.otpError = otpError;
        this.nameError=nameError;
        this.emailError=emailError;
        this.isDataValid = false;
    }

    LoginFormState(boolean isDataValid) {
        this.phoneError = null;
        this.otpError = null;
        this.nameError=null;
        this.emailError=null;
        this.isDataValid = isDataValid;
    }

    @Nullable
    Integer getPhoneError() {
        return phoneError;
    }

    @Nullable
    Integer getOtpError() {
        return otpError;
    }
    @Nullable
    Integer getNameError() {
        return nameError;
    }
    @Nullable
    Integer getEmailError() {
        return emailError;
    }


    boolean isDataValid() {
        return isDataValid;
    }
}