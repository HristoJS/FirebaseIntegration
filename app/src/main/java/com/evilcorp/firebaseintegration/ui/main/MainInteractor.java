package com.evilcorp.firebaseintegration.ui.main;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.evilcorp.firebaseintegration.ChatterinoApp;
import com.evilcorp.firebaseintegration.data.firebase.FirebaseCallback;
import com.evilcorp.firebaseintegration.data.firebase.FirebaseInteractor;
import com.evilcorp.firebaseintegration.data.firebase.model.AccountType;
import com.evilcorp.firebaseintegration.data.firebase.model.user.UserAccount;
import com.evilcorp.firebaseintegration.data.firebase.model.UserStatus;
import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.StorageReference;

/**
 * Created by hristo.stoyanov on 15-Feb-17.
 */

public class MainInteractor extends FirebaseInteractor implements MainContract.Interactor {
    private static final String TAG = MainInteractor.class.getSimpleName();
    private static final String WELCOME_MSG_KEY = "welcome_message";

    private final FirebaseUser mUser;

    MainInteractor() {
        mUser = mFirebaseAuth.getCurrentUser();
        updateUserStatus(UserStatus.ONLINE);
    }

    @Override
    public void logout() {
        LoginManager.getInstance().logOut();
        mFirebaseAuth.signOut();
    }

    @Override
    public void deleteUser() {
        mUser.delete();
    }

    @Override
    public int getAccountType() {
        if (mUser.isAnonymous())
            return AccountType.GUEST;
        else switch (mUser.getProviderId()) {
            case "FacebookLogin":
                return AccountType.FACEBOOK;
            case "Twitter":
                return AccountType.TWITTER;
            case "Google":
                return AccountType.GOOGLE;
            case "Email":
                return AccountType.EMAIL;
            default:
                return AccountType.UNKNOWN;
        }
    }

    @Override
    public void downloadImage(final FirebaseCallback<Uri> callback) {
        StorageReference storageRef = mFirebaseStorage.getReferenceFromUrl("gs://fir-integration-ea093.appspot.com");
        StorageReference pathReference = storageRef.child("icon_vader.jpg");
        pathReference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    callback.success(task.getResult());
                } else {
                    callback.fail("Unable to download image");
                    Log.e(TAG, "Trying to download image", task.getException());
                }
            }
        });
    }


    @Override
    public void getWelcomeMessage(final FirebaseCallback<String> callback) {
        // cacheExpirationSeconds is set to cacheExpiration here, indicating that any previously
        // fetched and cached config would be considered expired because it would have been fetched
        // more than cacheExpiration seconds ago. Thus the next fetch would go to the server unless
        // throttling is in progress. The default expiration duration is 43200 (12 hours).
        mFirebaseRemoteConfig.fetch().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    // Once the config is successfully fetched it must be activated before newly fetched
                    // values are returned.
                    mFirebaseRemoteConfig.activateFetched();
                    String message = mFirebaseRemoteConfig.getString(WELCOME_MSG_KEY);
                    callback.success(message);
                    //mainView.setMessage(message);
                } else {
                    callback.fail("Unable to retrieve message");
                    Log.e(TAG, "Trying to retrieve a message from remote config", task.getException());
                }
            }
        });
    }

    private void updateUserStatus(int userStatus) {
        UserAccount user = ChatterinoApp.getCurrentAccount();
        if (user == null || user.getAccountType() == AccountType.GUEST) {
            return;
        }
        user.setUserStatus(userStatus);
        mUsersTable.child(user.getId()).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "Changed status to online.");
                } else {
                    Log.d(TAG, "Failed to change status");
                }
            }
        });
    }
}
