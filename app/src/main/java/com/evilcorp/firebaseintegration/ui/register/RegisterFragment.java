package com.evilcorp.firebaseintegration.ui.register;


import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatImageButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.evilcorp.firebaseintegration.R;
import com.evilcorp.firebaseintegration.ui.base.BaseFragment;
import com.evilcorp.firebaseintegration.helper.NetworkInfoHelper;
import com.evilcorp.firebaseintegration.ui.login.LoginActivity;
import com.evilcorp.firebaseintegration.view.RounderCornerImage;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class RegisterFragment extends BaseFragment implements View.OnClickListener, RegisterContract.View {
    private static final String TAG = RegisterFragment.class.getSimpleName();
    public static final int SELECT_PICTURE = 0x214;
    public static final int CROP_PICTURE = 0x314;
    private static final int PICTURE_CROP_WIDTH = 100;
    private static final int PICTURE_CROP_HEIGHT = 100;

    private static Uri SELECTED_IMAGE_URI;

    private ImageView mWelcomePictureImageView;
    private RegisterContract.Presenter mPresenter;
    private TextInputEditText mEmailEditText;
    private TextInputEditText mRepeatEmailEditText;
    private TextInputEditText mPasswordEditText;
    private TextInputEditText mRepeatPasswordEditText;
    private TextInputEditText mNameEditText;
    private TextInputEditText mCountryEditText;
    private AppCompatImageButton mAddImageButton;

    private boolean mImageSelected = false;

    public RegisterFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_register, container, false);
        //mWelcomePictureImageView = (ImageView) root.findViewById(R.id.welcome_picture);
        mEmailEditText = (TextInputEditText) root.findViewById(R.id.email_edittext);
        mRepeatEmailEditText = (TextInputEditText) root.findViewById(R.id.repeat_email_edittext);
        mPasswordEditText = (TextInputEditText) root.findViewById(R.id.password_edittext);
        mRepeatPasswordEditText = (TextInputEditText) root.findViewById(R.id.repeat_password_edittext);
        mNameEditText = (TextInputEditText) root.findViewById(R.id.name_edittext);
        mCountryEditText = (TextInputEditText) root.findViewById(R.id.country_edittext);

        mAddImageButton = (AppCompatImageButton) root.findViewById(R.id.add_picture_button);
        mAddImageButton.setOnClickListener(this);
        Button goBackButton = (AppCompatButton) root.findViewById(R.id.go_back_button);
        goBackButton.setOnClickListener(this);
        Button fragmentRegisterButton = (Button) root.findViewById(R.id.fragment_register_button);
        fragmentRegisterButton.setOnClickListener(this);

        mPresenter = new RegisterPresenter(this, new RegisterInteractor());
        return root;
    }

    private void register() {
        if (!NetworkInfoHelper.isNetworkAvailable(getContext())) {
            showAlert(getString(R.string.network_unavailable));
            return;
        }
        String email_text = mEmailEditText.getText().toString();
        String repeat_email_text = mRepeatEmailEditText.getText().toString();
        String password_text = mPasswordEditText.getText().toString();
        String repeat_password_text = mRepeatPasswordEditText.getText().toString();
        String name_text = mNameEditText.getText().toString();
        String country_text = mCountryEditText.getText().toString();

        showProgress("Validating Fields");
        mPresenter.register(email_text, repeat_email_text, password_text,
                repeat_password_text, name_text, country_text, SELECTED_IMAGE_URI);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.add_picture_button:
                getPicture();
                break;
            case R.id.fragment_register_button:
                register();
                break;
            case R.id.go_back_button:
                getActivity().onBackPressed();
            default:
                break;
        }
    }

    private void getPicture() {
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
                    SELECTED_IMAGE_URI = data.getData();
                    LoginActivity.IMAGE_SELECTED = true;
                    break;
                case CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE:
                    CropImage.ActivityResult result = CropImage.getActivityResult(data);
                    SELECTED_IMAGE_URI = result.getUri();
                default:
                    break;
            }
        } else Log.e(TAG, "Activity result error code " + resultCode);
    }


    private void cropImage(Uri picUri) {
        CropImage.activity(picUri)
                .setMinCropResultSize(PICTURE_CROP_WIDTH, PICTURE_CROP_HEIGHT)
                .setMaxCropResultSize(PICTURE_CROP_WIDTH, PICTURE_CROP_HEIGHT)
                .setCropShape(CropImageView.CropShape.OVAL)
                .start(getActivity());
    }

    @Override
    public void onResume() {
        View view = getView();
        if (LoginActivity.IMAGE_SELECTED) {
            LoginActivity.IMAGE_SELECTED = false;
            cropImage(SELECTED_IMAGE_URI);
        }
        if (view != null && SELECTED_IMAGE_URI != null) {
            int color = ContextCompat.getColor(getContext(), R.color.colorPrimaryDark);
            RounderCornerImage image = new RounderCornerImage(getContext(), mAddImageButton, color);
            Glide.with(this)
                    .load(SELECTED_IMAGE_URI)
                    .asBitmap()
                    .centerCrop()
                    .into(image);
            //mAddImageButton.setImageURI(SELECTED_IMAGE_URI);
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
    public void onError(String error) {
        dismissProgress();
        showAlert(error);
    }
}

