package com.evilcorp.firebaseintegration.register;


import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.evilcorp.firebaseintegration.R;
import com.evilcorp.firebaseintegration.base.BaseFragment;
import com.evilcorp.firebaseintegration.helper.Util;
import com.evilcorp.firebaseintegration.login.LoginActivity;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class RegisterFragment extends BaseFragment implements View.OnClickListener,RegisterContract.View {
    private ImageView picture;
    private static final String TAG = RegisterFragment.class.getSimpleName();

    public static final int SELECT_PICTURE = 0x214;
    public static final int CROP_PICTURE = 0x314;

    private static Uri selectedImageUri;
    private RegisterContract.Presenter presenter;
    private TextInputEditText email;
    private TextInputEditText repeat_email;
    private TextInputEditText password;
    private TextInputEditText repeat_password;
    private TextInputEditText name;
    private TextInputEditText country;
    private boolean image_selected = false;

    public RegisterFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_register, container, false);
        picture = (ImageView) root.findViewById(R.id.picture);
        email = (TextInputEditText) root.findViewById(R.id.email);
        repeat_email = (TextInputEditText) root.findViewById(R.id.repeat_email);
        password = (TextInputEditText) root.findViewById(R.id.password);
        repeat_password = (TextInputEditText) root.findViewById(R.id.repeat_password);
        name = (TextInputEditText) root.findViewById(R.id.name);
        country = (TextInputEditText) root.findViewById(R.id.country);

        Button add_picture_button = (Button) root.findViewById(R.id.add_picture_button);
        add_picture_button.setOnClickListener(this);
        Button fragment_register_button = (Button) root.findViewById(R.id.fragment_register_button);
        fragment_register_button.setOnClickListener(this);

        presenter = new RegisterPresenter(this);
        return root;
    }

    private void register(){
        if(!Util.isNetworkAvailable(getContext())){
            showAlert(getString(R.string.network_unavailable));
            return;
        }
        String email_text = email.getText().toString();
        String repeat_email_text = repeat_email.getText().toString();
        String password_text = password.getText().toString();
        String repeat_password_text = repeat_password.getText().toString();
        String name_text = name.getText().toString();
        String country_text = country.getText().toString();

        showProgress("Validating Fields");
        boolean validated = presenter.validateFields(email_text,repeat_email_text,password_text,
                repeat_password_text, name_text,country_text,selectedImageUri);
        if(!validated) dismissProgress();
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.add_picture_button:
                getPicture();
                break;
            case R.id.fragment_register_button:
                register();
                break;
            default:
                break;
        }
    }

    private void getPicture(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        //intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        getActivity().startActivityForResult(Intent.createChooser(intent,
                "Select Picture"), SELECT_PICTURE);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case SELECT_PICTURE:
                    selectedImageUri = data.getData();
                    LoginActivity.image_selected = true;
                    break;
                case CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE:
                    CropImage.ActivityResult result = CropImage.getActivityResult(data);
                    selectedImageUri = result.getUri();
                default:
                    break;
            }
        }
        else Log.e(TAG,"Activity result error code " + resultCode);
    }


    private void cropImage(Uri picUri) {
        CropImage.activity(picUri)
                .setMinCropResultSize(100,100)
                .setMaxCropResultSize(100,100)
                .setCropShape(CropImageView.CropShape.OVAL)
                .start(getActivity());
    }

    @Override
    public void onResume() {
        View view = getView();
        if(LoginActivity.image_selected) {
            LoginActivity.image_selected = false;
            cropImage(selectedImageUri);
        }
        if(view!=null && selectedImageUri!=null){
            picture.setImageURI(selectedImageUri);
        }
        super.onResume();
    }

    @Override
    public void registerSuccess() {
        dismissProgress();
        showAlert("A confirmation email has been sent.", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                getActivity().onBackPressed();
            }
        });
    }

    @Override
    public void registerFail(String reason) {
        dismissProgress();
        showAlert(reason);
    }

    @Override
    public void validationError(String error) {
        showAlert(error);
    }
}

