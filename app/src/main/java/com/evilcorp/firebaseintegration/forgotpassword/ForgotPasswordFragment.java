package com.evilcorp.firebaseintegration.forgotpassword;


import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.evilcorp.firebaseintegration.R;
import com.evilcorp.firebaseintegration.base.BaseFragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class ForgotPasswordFragment extends BaseFragment implements View.OnClickListener, ForgotPasswordContract.View {

    private TextInputEditText email;
    private TextInputEditText passcode;
    ForgotPasswordContract.Presenter presenter;
    public ForgotPasswordFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_forgot_password, container, false);
        email = (TextInputEditText) root.findViewById(R.id.email);
        passcode = (TextInputEditText) root.findViewById(R.id.passcode);
        Button send_email_button = (Button) root.findViewById(R.id.send_email_button);
        send_email_button.setOnClickListener(this);
        Button validate_passcode_button = (Button) root.findViewById(R.id.validate_passcode_button);
        validate_passcode_button.setOnClickListener(this);
        presenter = new ForgotPasswordPresenter(this);
        return root;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.send_email_button:
                presenter.sendResetEmail(email.getText().toString());
                break;
            case R.id.validate_passcode_button:
                presenter.validatePasscode(passcode.getText().toString());
                break;
            default:
                break;
        }
    }

    @Override
    public void sendEmailSuccess() {
        showAlert("Email sent.");
    }

    @Override
    public void sendEmailFail() {
        showAlert("Wrong email.");
    }

    @Override
    public void validatePasscodeSuccess() {
        showAlert("Password reset.");
        getActivity().onBackPressed();
    }

    @Override
    public void validatePasscodeFail() {
        showAlert("Wrong code.");
    }
}

