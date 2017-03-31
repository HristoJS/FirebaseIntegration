package com.evilcorp.firebaseintegration.data.firebase;

import android.os.Bundle;

import com.evilcorp.firebaseintegration.BuildConfig;
import com.evilcorp.firebaseintegration.ChatterinoApp;
import com.evilcorp.firebaseintegration.R;
import com.evilcorp.firebaseintegration.data.firebase.model.Event;
import com.evilcorp.firebaseintegration.ui.base.BaseContract;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.google.firebase.storage.FirebaseStorage;

/**
 * Created by hristo.stoyanov on 3/29/2017.
 */

public abstract class FirebaseInteractor implements BaseContract.Interactor {
    private static final String USERS = "users";
    private static final String MESSAGES = "messages";
    private static final String CHATS = "chats";

    private final DatabaseReference mDatabase;

    protected DatabaseReference mChatsTable;
    protected final DatabaseReference mMessagesTable;
    protected DatabaseReference mUsersTable;

    protected final FirebaseRemoteConfig mFirebaseRemoteConfig;
    protected final FirebaseStorage mFirebaseStorage;
    protected final FirebaseAuth mFirebaseAuth;

    protected FirebaseInteractor() {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mChatsTable = getDBTable(CHATS);
        mUsersTable = getDBTable(USERS);
        mMessagesTable = getDBTable(MESSAGES);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(BuildConfig.DEBUG)
                .build();
        mFirebaseRemoteConfig.setConfigSettings(configSettings);
        mFirebaseRemoteConfig.setDefaults(R.xml.remote_config_defaults);
        mFirebaseStorage = FirebaseStorage.getInstance();
    }

    private DatabaseReference getDBTable(String tableId) {
        return mDatabase.child(tableId);
    }

    protected void destroyListener(ChildEventListener listener) {
        if (listener != null) {
            mUsersTable.removeEventListener(listener);
        }
    }

    protected void destroyListener(ValueEventListener listener) {
        if (listener != null) {
            mUsersTable.removeEventListener(listener);
        }
    }

    @Override
    public void destroyAllListeners() {

    }

    protected void logEvent(int event) {
        switch (event) {
            case Event.LOGIN:
                FirebaseUser user = mFirebaseAuth.getCurrentUser();
                if (user != null) {
                    Bundle bundle = new Bundle();
                    bundle.putString("USERNAME", user.getDisplayName());
                    bundle.putString("EMAIL", user.getEmail());
                    bundle.putString("PROVIDER", user.getProviderId());
                    ChatterinoApp.getFirebaseAnalytics().logEvent(FirebaseAnalytics.Event.LOGIN, bundle);
                }
                break;
            default:
                break;
        }
    }
}
