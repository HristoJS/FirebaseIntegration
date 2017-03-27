package com.evilcorp.firebaseintegration.register;

import android.net.Uri;
import android.support.annotation.NonNull;

import com.evilcorp.firebaseintegration.model.firebase.UserAccount;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
    private UserAccount mUserAccount;
    private FirebaseUser mFirebaseUser;
    private FirebaseCallback mFirebaseCallback;
    private DatabaseReference mUserDbRef;
    private int mFailureCount;

    interface FirebaseCallback {
        void success();

        void fail(String reason);
    }

    RegisterInteractor(FirebaseCallback callback) {
        this.mFirebaseCallback = callback;
        mAuth = FirebaseAuth.getInstance();
        mFirebaseStorage = FirebaseStorage.getInstance();
        mUserDbRef = FirebaseDatabase.getInstance().getReference().child(USERS);
    }

    void createUser(UserAccount user) {
        mFailureCount = 0;
        this.mUserAccount = user;
        if (user != null) {
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(user.getEmail(), user.getPassword())
                    .addOnSuccessListener(new SuccessListener<AuthResult>())
                    .addOnFailureListener(new FailureListener());
        }
    }

    private void verifyEmail() {
        mFirebaseUser.sendEmailVerification()
                .addOnSuccessListener(new SuccessListener<Void>())
                .addOnFailureListener(new FailureListener());
    }

    private void saveAvatar() {
        String avatarPath = mUserAccount.getAvatar();
        StorageReference storageRef = mFirebaseStorage.getReferenceFromUrl("gs://fir-integration-ea093.appspot.com");
        StorageReference avatarsRef = storageRef.child("avatars/" + mFirebaseUser.getUid());
        avatarsRef.putFile(Uri.parse(avatarPath))
                .addOnSuccessListener(new SuccessListener<UploadTask.TaskSnapshot>())
                .addOnFailureListener(new FailureListener());
    }

    private void updateProfile(Uri photoUri) {
        assert photoUri != null;
        mFirebaseUser.updateProfile(new UserProfileChangeRequest.Builder()
                .setDisplayName(mUserAccount.getName())
                .setPhotoUri(photoUri)
                .build())
                .addOnFailureListener(new FailureListener());
        mUserAccount.setAvatar(photoUri.toString());
        addUserToDatabase(mFirebaseUser);
    }

    private void addUserToDatabase(FirebaseUser firebaseUser) {
        mUserDbRef.child(firebaseUser.getUid()).setValue(mUserAccount)
                .addOnFailureListener(new FailureListener());
    }


    private class SuccessListener<T> implements OnSuccessListener<T> {
        @Override
        public void onSuccess(T result) {
            if (result instanceof AuthResult) {
                mFirebaseUser = ((AuthResult) result).getUser();
                if (mFirebaseUser != null) {
                    verifyEmail();
                    saveAvatar();
                }
            } else if (result instanceof UploadTask.TaskSnapshot) {
                Uri uri = ((UploadTask.TaskSnapshot) result).getDownloadUrl();
                updateProfile(uri);
            } else if (mFailureCount == 0) {
                mFirebaseCallback.success();
            }
        }
    }

    private class FailureListener implements OnFailureListener {
        @Override
        public void onFailure(@NonNull Exception exception) {
            mFailureCount++;
            exception.printStackTrace();
            mFirebaseCallback.fail(exception.getMessage());
        }
    }
}
