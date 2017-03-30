package com.evilcorp.firebaseintegration.data.firebase;

import com.evilcorp.firebaseintegration.BuildConfig;
import com.evilcorp.firebaseintegration.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
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

public abstract class FirebaseInteractor {
    private static final String USERS = "users";
    private static final String MESSAGES = "messages";
    private static final String CHATS = "chats";

    private DatabaseReference mDatabase;

    protected DatabaseReference mChatsTable;
    protected DatabaseReference mMessagesTable;
    protected DatabaseReference mUsersTable;

    protected FirebaseRemoteConfig mFirebaseRemoteConfig;
    protected FirebaseStorage mFirebaseStorage;
    protected FirebaseAuth mFirebaseAuth;

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
}
