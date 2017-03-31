package com.evilcorp.firebaseintegration.ui.base;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by hristo.stoyanov on 3/29/2017.
 */

public abstract class BasePresenter implements BaseContract.Presenter {
    private static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
    private static final Pattern VALID_PASSWORD_REGEX =
            Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*()_+=])(?=\\S+$).{8,20}$");

    private BaseContract.View mView;
    private BaseContract.Interactor mInteractor;

    public BasePresenter(BaseContract.View view, BaseContract.Interactor interactor) {
        mView = view;
        mInteractor = interactor;
    }

    protected boolean isEmpty(String string) {
        return "".equals(string);
    }

    protected boolean isValidEmail(String email) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(email);
        return matcher.find();
    }

    protected boolean isValidPassword(String password) {
        Matcher matcher = VALID_PASSWORD_REGEX.matcher(password);
        return matcher.find();
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDestroy() {
        mInteractor.destroyAllListeners();
    }
}
