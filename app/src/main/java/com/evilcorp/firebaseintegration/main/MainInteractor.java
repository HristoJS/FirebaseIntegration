package com.evilcorp.firebaseintegration.main;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.evilcorp.firebaseintegration.BuildConfig;
import com.evilcorp.firebaseintegration.MyApp;
import com.evilcorp.firebaseintegration.R;
import com.evilcorp.firebaseintegration.helper.FirebaseCallback;
import com.evilcorp.firebaseintegration.model.firebase.AccountType;
import com.evilcorp.firebaseintegration.model.firebase.UserAccount;
import com.evilcorp.firebaseintegration.model.firebase.UserStatus;
import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/**
 * Created by hristo.stoyanov on 15-Feb-17.
 */

public class MainInteractor {
    private static final String WELCOME_MSG_KEY = "welcome_message";
    private static final String USERS = "users";
    private static final String TAG = MainInteractor.class.getSimpleName();

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private FirebaseRemoteConfig mFirebaseRemoteConfig;
    private FirebaseStorage mFirebaseStorage;
    private DatabaseReference mUserDbRef;

    MainInteractor() {
        initFirebase();
        updateUserStatus(UserStatus.ONLINE);
    }

    private void initFirebase() {
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(BuildConfig.DEBUG)
                .build();
        mFirebaseRemoteConfig.setConfigSettings(configSettings);
        mFirebaseRemoteConfig.setDefaults(R.xml.remote_config_defaults);
        mFirebaseStorage = FirebaseStorage.getInstance();
        mUserDbRef = FirebaseDatabase.getInstance().getReference().child(USERS);


    }

    void logout() {
        LoginManager.getInstance().logOut();
        mAuth.signOut();
    }

    void deleteUser() {
        mUser.delete();
    }

    FirebaseUser getUser() {
        return mUser;
    }

    String getUserId() {
        return mUser.getUid();
    }

    int getAccountType() {
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

    public void downloadImage(final FirebaseCallback<Uri> callback) {
        StorageReference storageRef = mFirebaseStorage.getReferenceFromUrl("gs://fir-integration-ea093.appspot.com");
        StorageReference pathReference = storageRef.child("icon_vader.jpg");
        pathReference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    callback.success(task.getResult());
                } else callback.fail(task.getException());
            }
        });
    }


    void getWelcomeMessage(final FirebaseCallback<String> callback) {
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
                    callback.fail(task.getException());
                }
            }
        });
    }

    private void updateUserStatus(int userStatus) {
        UserAccount user = MyApp.getCurrentAccount();
        user.setUserStatus(userStatus);
        mUserDbRef.child(user.getId()).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
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
