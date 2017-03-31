package com.evilcorp.firebaseintegration.ui.register;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.evilcorp.firebaseintegration.data.firebase.FirebaseCallback;
import com.evilcorp.firebaseintegration.data.firebase.FirebaseInteractor;
import com.evilcorp.firebaseintegration.data.firebase.model.user.UserAccount;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

@SuppressWarnings("unchecked")
class RegisterInteractor extends FirebaseInteractor implements RegisterContract.Interactor {
    private static final String TAG = RegisterInteractor.class.getSimpleName();
    private UserAccount mUserAccount;
    private FirebaseUser mFirebaseUser;
    private FirebaseCallback<Void> mFirebaseCallback;

    private final OnFailureListener mFailureListener;
    private OnSuccessListener mSuccessListener;


    RegisterInteractor() {
        mFailureListener = new FailureListener();
        mSuccessListener = new CreateUserListener();
    }

    @Override
    public void createUser(UserAccount user, FirebaseCallback<Void> callback) {
        mFirebaseCallback = callback;
        mUserAccount = user;
        if (user != null) {
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(user.getEmail(), user.getPassword())
                    .addOnSuccessListener(mSuccessListener)
                    .addOnFailureListener(mFailureListener);
        }
    }

    private void verifyEmail() {
        mFirebaseUser.sendEmailVerification().addOnFailureListener(mFailureListener);
    }

    private void saveAvatar() {
        String avatarPath = mUserAccount.getAvatar();
        StorageReference storageRef = mFirebaseStorage.getReferenceFromUrl("gs://fir-integration-ea093.appspot.com");
        StorageReference avatarsRef = storageRef.child("avatars/" + mFirebaseUser.getUid());
        mSuccessListener = new SaveAvatarListener();
        avatarsRef.putFile(Uri.parse(avatarPath))
                .addOnSuccessListener(mSuccessListener)
                .addOnFailureListener(mFailureListener);
    }

    private void updateProfile(Uri photoUri) {
        assert photoUri != null;
        mSuccessListener = new RegisterSuccessListener();
        mFirebaseUser.updateProfile(new UserProfileChangeRequest.Builder()
                .setDisplayName(mUserAccount.getName())
                .setPhotoUri(photoUri)
                .build())
                .addOnSuccessListener(mSuccessListener)
                .addOnFailureListener(mFailureListener);
        mUserAccount.setAvatar(photoUri.toString());
    }

    private final class CreateUserListener implements OnSuccessListener<AuthResult> {
        @Override
        public void onSuccess(AuthResult authResult) {
            mFirebaseUser = authResult.getUser();
            if (mFirebaseUser != null) {
                verifyEmail();
                saveAvatar();
            }
        }
    }

    private final class SaveAvatarListener implements OnSuccessListener<UploadTask.TaskSnapshot> {
        @Override
        @SuppressWarnings("VisibleForTests")
        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
            Uri uri = taskSnapshot.getDownloadUrl();
            updateProfile(uri);
        }
    }

    private final class RegisterSuccessListener implements OnSuccessListener<Void> {
        @Override
        public void onSuccess(Void aVoid) {
            mFirebaseCallback.success(null);
        }
    }

    private final class FailureListener implements OnFailureListener {
        @Override
        public void onFailure(@NonNull Exception exception) {
            mFirebaseCallback.fail("Unable to register");
            Log.e(TAG, "Trying to register", exception);
        }
    }
}
