package com.evilcorp.firebaseintegration.login;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

import com.evilcorp.firebaseintegration.MyApp;
import com.evilcorp.firebaseintegration.helper.FirebaseCallback;
import com.evilcorp.firebaseintegration.helper.FirebaseConnectionHelper;
import com.evilcorp.firebaseintegration.model.firebase.AccountType;
import com.evilcorp.firebaseintegration.model.firebase.Event;
import com.evilcorp.firebaseintegration.model.firebase.UserAccount;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.TwitterAuthProvider;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;


public class LoginInteractor implements OnCompleteListener<AuthResult> {
    private static final String TAG = LoginInteractor.class.getName();
    private static final String USERS = "users";

    private FirebaseCallback<Void> mFirebaseCallback;
    private FacebookLoginHandler mFacebookLoginHandler;
    private TwitterLoginHandler mTwitterLoginHandler;
    private DatabaseReference mUserDbRef;

    LoginInteractor() {
        mFacebookLoginHandler = new FacebookLoginHandler();
        mTwitterLoginHandler = new TwitterLoginHandler();
        mUserDbRef = FirebaseDatabase.getInstance().getReference().child(USERS);
        Log.d(TAG, "is Connected: " + FirebaseConnectionHelper.IS_CONNECTED);

    }

    //region Public Methods
    void destroy() {
        mFacebookLoginHandler = null;
        mTwitterLoginHandler = null;
    }

    void setCallback(FirebaseCallback<Void> callback) {
        this.mFirebaseCallback = callback;
    }

    void loginWithEmail(String email, String password) {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password).addOnCompleteListener(this);
    }

    void loginAsGuest() {
        FirebaseAuth.getInstance().signInAnonymously().addOnCompleteListener(this);
    }

    void loginWithGoogle(GoogleSignInResult result) {
        GoogleSignInAccount account = result.getSignInAccount();
        if (account != null) {
            AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
            loginWithCredential(credential);
        }
    }

    boolean isUserLoggedIn() {
        UserAccount account = MyApp.getCurrentAccount();
        return (account != null && account.getAccountType() != AccountType.GUEST);
    }

    //endregion

    //region Private Methods

    private boolean isEmailVerified(FirebaseUser user) {
        return user.isEmailVerified();
    }

    private void loginWithCredential(AuthCredential credential) {
        FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener(this);
    }

    private void logEvent(int event) {
        switch (event) {
            case Event.LOGIN:
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    Bundle bundle = new Bundle();
                    bundle.putString("USERNAME", user.getDisplayName());
                    bundle.putString("EMAIL", user.getEmail());
                    bundle.putString("PROVIDER", user.getProviderId());
                    MyApp.getFirebaseAnalytics().logEvent(FirebaseAnalytics.Event.LOGIN, bundle);
                }
                break;
            default:
                break;
        }
    }

    private void saveUserToDB(final FirebaseUser firebaseUser) {
        final UserAccount user = new UserAccount(firebaseUser);
        Log.d(TAG, user.toString());
        final String userId = firebaseUser.getUid();

        mUserDbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChild(userId) && !firebaseUser.isAnonymous()) {
                    mUserDbRef.child(userId).setValue(user)
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    e.printStackTrace();
                                }
                            });
                    Log.d(TAG,"User Added to DB.");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, databaseError.getMessage());
            }
        });
//        mUserDbRef.child(userId).setValue(user)
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        e.printStackTrace();
//                    }
//                });
    }


    public void onComplete(@NonNull Task<AuthResult> task) {
        if (task.isSuccessful()) {
            FirebaseUser user = task.getResult().getUser();
            Log.d(TAG, user.toString());
            // TODO: Maybe later enable saving anonymous to db?
            if (!user.isAnonymous()) {
                saveUserToDB(user);
            }
            if (user.getProviderData().size() > 1) {
                for (UserInfo userInfo : user.getProviderData()) {
                    String id = userInfo.getProviderId();
                    Log.d(TAG, id);
                    if (id.equals("password") && !isEmailVerified(user)) {
                        mFirebaseCallback.fail(new Exception("Email is not verified"));
                        return;
                    }
                }
            }
            mFirebaseCallback.success(null);
            logEvent(Event.LOGIN);
        } else mFirebaseCallback.fail(task.getException());
    }
    //endregion

    //region Facebook
    private void loginWithFacebook(String token) {
        AuthCredential credential = FacebookAuthProvider.getCredential(token);
        loginWithCredential(credential);
    }

    FacebookLoginHandler getFacebookLoginHandler() {
        return mFacebookLoginHandler;
    }

    class FacebookLoginHandler implements FacebookCallback<LoginResult> {

        public void onSuccess(LoginResult loginResult) {
            loginWithFacebook(loginResult.getAccessToken().getToken());
        }

        public void onCancel() {
            Log.d(TAG, "Canceled");
        }

        public void onError(FacebookException error) {
            mFirebaseCallback.fail(new Exception("Unable to login with facebook"));
            error.printStackTrace();
        }
    }
    //endregion

    //region Twitter
    private void loginWithTwitter(String token, String secret) {
        AuthCredential credential = TwitterAuthProvider.getCredential(token, secret);
        loginWithCredential(credential);
    }

    TwitterLoginHandler getTwitterLoginHandler() {
        return mTwitterLoginHandler;
    }

    class TwitterLoginHandler extends com.twitter.sdk.android.core.Callback<TwitterSession> {
        public void success(Result<TwitterSession> twitterSessionResult) {
            TwitterAuthToken authToken = twitterSessionResult.data.getAuthToken();
            loginWithTwitter(authToken.token, authToken.secret);
        }

        public void failure(TwitterException e) {
            mFirebaseCallback.fail(new Exception("Unable to login with twitter"));
            e.printStackTrace();
        }
    }
    //endregion
}
