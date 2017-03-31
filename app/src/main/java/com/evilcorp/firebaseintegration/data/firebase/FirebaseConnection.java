package com.evilcorp.firebaseintegration.data.firebase;

import android.support.annotation.NonNull;
import android.util.Log;

import com.evilcorp.firebaseintegration.helper.Time;
import com.evilcorp.firebaseintegration.data.firebase.model.user.UserAccount;
import com.evilcorp.firebaseintegration.data.firebase.model.UserStatus;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by hristo.stoyanov on 23-Feb-17.
 */

public class FirebaseConnection {
    private static final String TAG = FirebaseConnection.class.getSimpleName();
    private static final DatabaseReference USERS_REF = FirebaseDatabase.getInstance().getReference("/users");
    public static boolean IS_CONNECTED;

    public static void init(UserAccount account){
        //final DatabaseReference lastOnlineRef = FirebaseDatabase.getInstance().getReference("/users/"+account.getId()+"/lastOnline");
        final DatabaseReference accountRef = USERS_REF.child(account.getId());

        DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
        connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                boolean connected = snapshot.getValue(Boolean.class);
                if (connected) {
                    IS_CONNECTED = true;
                    //lastOnlineRef.onDisconnect().setValue(Time.getTime());
                    Map<String,Object> new_status = new HashMap<>();
                    new_status.put("lastOnline", Time.getTime());
                    new_status.put("userStatus", UserStatus.OFFLINE);
                    accountRef.onDisconnect().updateChildren(new_status).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Log.d(TAG,"Updated user status.");
                            }
                            else {
                                Log.d(TAG,"Failed to update user status.");
                            }
                        }
                    });
                    Log.d(TAG,"Connected.");
                } else {
                    IS_CONNECTED = false;
                    Log.d(TAG,"Discnnected.");
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.d(TAG,error.getMessage());
            }
        });
    }

    public static void changeStatus(UserAccount account, final int userStatus){
        DatabaseReference accountRef = USERS_REF.child(account.getId());
        Map<String,Object> new_status = new HashMap<>();
        new_status.put("userStatus", userStatus);
        if(userStatus == UserStatus.OFFLINE || userStatus == UserStatus.INVISIBLE){
            new_status.put("lastOnline",Time.getTime());
        }
        accountRef.updateChildren(new_status).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Log.d(TAG,"Updated user status to:" + userStatus);
                }
                else {
                    Log.d(TAG,"Failed to update user status.");
                }
            }
        });
    }

    public static void goOffline(){
        FirebaseDatabase.getInstance().goOffline();
    }

    public static void goOnline(){
        FirebaseDatabase.getInstance().goOnline();
    }

}

