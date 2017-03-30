package com.evilcorp.firebaseintegration.ui.login;

import android.content.Intent;
import android.os.Bundle;

import com.evilcorp.firebaseintegration.R;
import com.evilcorp.firebaseintegration.ui.base.BaseActivity;
import com.evilcorp.firebaseintegration.ui.register.RegisterFragment;
import com.theartofdev.edmodo.cropper.CropImage;

public class LoginActivity extends BaseActivity {
    private static final String TAG = LoginActivity.class.getSimpleName();
    public static boolean IMAGE_SELECTED = false;

    private LoginFragment mLoginFragment;
    private RegisterFragment mRegisterFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mLoginFragment = new LoginFragment();
        mRegisterFragment = new RegisterFragment();
        loadFragment(mLoginFragment, false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLoginFragment = null;
        mRegisterFragment = null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RegisterFragment.SELECT_PICTURE || requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
            mRegisterFragment.onActivityResult(requestCode, resultCode, data);
        else
            mLoginFragment.onActivityResult(requestCode, resultCode, data);
    }
}

