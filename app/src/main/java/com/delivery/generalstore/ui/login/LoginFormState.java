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
    private boolean isDataValid;

    LoginFormState(@Nullable Integer phoneError, @Nullable Integer otpError) {
        this.phoneError = phoneError;
        this.otpError = otpError;
        this.isDataValid = false;
    }

    LoginFormState(boolean isDataValid) {
        this.phoneError = null;
        this.otpError = null;
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

    boolean isDataValid() {
        return isDataValid;
    }
}