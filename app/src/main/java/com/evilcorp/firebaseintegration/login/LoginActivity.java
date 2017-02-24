package com.evilcorp.firebaseintegration.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import com.evilcorp.firebaseintegration.R;
import com.evilcorp.firebaseintegration.base.BaseActivity;
import com.evilcorp.firebaseintegration.register.RegisterFragment;
import com.theartofdev.edmodo.cropper.CropImage;

public class LoginActivity extends BaseActivity {
    private static final String TAG = LoginActivity.class.getSimpleName();
    public static boolean image_selected = false;

    LoginFragment loginFragment;
    RegisterFragment registerFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loginFragment = new LoginFragment();
        registerFragment = new RegisterFragment();
        loadFragment(loginFragment,false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        loginFragment = null;
        registerFragment = null;
    }

    public void loadFragment(Fragment fragment, boolean backstackEnabled) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction()
                .replace(R.id.login_fragment_container, fragment);
        if (backstackEnabled) transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == RegisterFragment.SELECT_PICTURE || requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
            registerFragment.onActivityResult(requestCode, resultCode, data);
        else
        loginFragment.onActivityResult(requestCode, resultCode, data);
    }
}

