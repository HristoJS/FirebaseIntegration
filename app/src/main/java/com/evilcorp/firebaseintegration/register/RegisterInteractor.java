package com.evilcorp.firebaseintegration.register;

import android.net.Uri;
import android.support.annotation.NonNull;

import com.evilcorp.firebaseintegration.MyApp;
import com.evilcorp.firebaseintegration.helper.BitmapHelper;
import com.evilcorp.firebaseintegration.model.firebase.UserAccount;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


class RegisterInteractor {
    private static final String USERS = "users";
    private FirebaseAuth mAuth;
    private FirebaseStorage mFirebaseStorage;
    private FirebaseDatabase mFirebaseDb;
    private UserAccount user;
    private FirebaseUser firebaseUser;
    private FirebaseCallback callback;
    private DatabaseReference mUserDbRef;
    private int failure_count;

    interface FirebaseCallback {
        void success();
        void fail(String reason);
    }

    RegisterInteractor(FirebaseCallback callback){
        this.callback = callback;
        mAuth = FirebaseAuth.getInstance();
        mFirebaseStorage = FirebaseStorage.getInstance();
        mUserDbRef = FirebaseDatabase.getInstance().getReference().child(USERS);
    }

    void createUser(UserAccount user){
        failure_count = 0;
        this.user = user;
        if(user!=null) {
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(user.getEmail(), user.getPassword())
                    .addOnSuccessListener(new SuccessListener<AuthResult>())
                    .addOnFailureListener(new FailureListener());
        }
    }

    private void verifyEmail(){
        firebaseUser.sendEmailVerification()
                .addOnSuccessListener(new SuccessListener<Void>())
                .addOnFailureListener(new FailureListener());
    }

    private void saveAvatar() {
        String avatarPath = user.getAvatar();
        StorageReference storageRef = mFirebaseStorage.getReferenceFromUrl("gs://fir-integration-ea093.appspot.com");
        StorageReference avatarsRef = storageRef.child("avatars/" + firebaseUser.getUid());
        avatarsRef.putFile(Uri.parse(avatarPath))
                .addOnSuccessListener(new SuccessListener<UploadTask.TaskSnapshot>())
                .addOnFailureListener(new FailureListener());
    }

    private void updateProfile(Uri photoUri){
        assert photoUri != null;
        firebaseUser.updateProfile(new UserProfileChangeRequest.Builder()
                .setDisplayName(user.getName())
                .setPhotoUri(photoUri)
                .build())
                .addOnFailureListener(new FailureListener());
        user.setAvatar(photoUri.toString());
        addUserToDatabase(firebaseUser);
    }

    private void addUserToDatabase(FirebaseUser firebaseUser){
        mUserDbRef.child(firebaseUser.getUid()).setValue(user)
                .addOnFailureListener(new FailureListener());
    }


    private class SuccessListener<T> implements OnSuccessListener<T>{
        @Override
        public void onSuccess(T result){
            if(result instanceof AuthResult){
                firebaseUser = ((AuthResult) result).getUser();
                if (firebaseUser != null) {
                    verifyEmail();
                    saveAvatar();
                }
            }
            else if(result instanceof UploadTask.TaskSnapshot){
                Uri uri = ((UploadTask.TaskSnapshot) result).getDownloadUrl();
                updateProfile(uri);
            }
            else if(failure_count == 0){
                callback.success();
            }
        }
    }

    private class FailureListener implements OnFailureListener{
        @Override
        public void onFailure(@NonNull Exception exception){
            failure_count++;
            exception.printStackTrace();
            callback.fail(exception.getMessage());
        }
    }
}
