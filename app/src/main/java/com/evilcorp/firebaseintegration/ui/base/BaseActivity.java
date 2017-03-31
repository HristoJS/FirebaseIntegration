package com.evilcorp.firebaseintegration.ui.base;


import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.evilcorp.firebaseintegration.data.firebase.model.AccountType;
import com.evilcorp.firebaseintegration.ChatterinoApp;
import com.evilcorp.firebaseintegration.R;
import com.evilcorp.firebaseintegration.data.firebase.FirebaseConnection;
import com.evilcorp.firebaseintegration.view.RounderCornerImage;
import com.evilcorp.firebaseintegration.data.firebase.model.user.UserAccount;
import com.evilcorp.firebaseintegration.data.firebase.model.UserStatus;

public abstract class BaseActivity extends AppCompatActivity implements BaseContract.View {
    private ProgressDialog mProgressDialog;
    private AlertDialog mAlertDialog;
    private static boolean isActive = false;

    @Override
    protected void onPause() {
        super.onPause();
        FirebaseConnection.goOffline();
        isActive = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        FirebaseConnection.goOnline();
        isActive = true;
    }

    private void dismissDialog(DialogInterface dialog) {
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dismissProgress();
        dismissDialog(mAlertDialog);
        dismissDialog(mProgressDialog);
    }

    protected void showProgress(String msg) {
        if (mProgressDialog != null && mProgressDialog.isShowing())
            dismissProgress();

        mProgressDialog = ProgressDialog.show(this, getResources().getString(R.string.app_name), msg);
    }

    protected void dismissProgress() {
        dismissDialog(mProgressDialog);
    }

    protected void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    protected void showAlert(String msg) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        mAlertDialog = builder.setTitle(getResources().getString(R.string.app_name))
                .setMessage(msg)
                .setCancelable(false)
                .setIcon(R.drawable.ic_firebase_logo)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).create();
        mAlertDialog.show();
    }

    protected void showAlert(String msg, DialogInterface.OnClickListener onClickListener, boolean cancelable) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.app_name))
                .setMessage(msg)
                .setCancelable(false)
                .setIcon(R.drawable.ic_firebase_logo)
                .setPositiveButton("OK", onClickListener);
        if(cancelable){
            builder.setNegativeButton("GO BACK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
        }
        mAlertDialog = builder.create();
        mAlertDialog.show();
    }

    protected void Log(String message){
        Log.d(this.getLocalClassName(),message);
    }

    protected void addUserPanel(){
        final UserAccount user = ChatterinoApp.getCurrentAccount();
        if (user == null) {
            finish();
            return;
        }
        Toolbar userToolbar = (Toolbar) findViewById(R.id.userToolbar);
        userToolbar.setTitle(user.getName());
        setSupportActionBar(userToolbar);

        ImageView userAvatar = (ImageView) findViewById(R.id.userAvatar);
        Object avatar = user.getAvatar();
        if (avatar != null) {
            int color = ContextCompat.getColor(this, R.color.colorPrimaryDark);
            RounderCornerImage image = new RounderCornerImage(this, userAvatar, color);
            Glide.with(this)
                    .load(avatar)
                    .asBitmap()
                    .centerCrop()
                    .into(image);
        }

        final ImageView userStatusIcon = (ImageView) findViewById(R.id.userStatusIcon);
        Spinner userStatusSpinner = (Spinner) findViewById(R.id.userStatusSpinner);
        //userStatusSpinner.setBackgroundColor(ContextCompat.getColor(this,R.color.colorPrimary));
        if(user.getAccountType() == AccountType.GUEST){
            userStatusSpinner.setVisibility(View.INVISIBLE);
            userStatusIcon.setVisibility(View.INVISIBLE);
            return;
        }
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.user_status_item, UserStatus.getAll());
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(R.layout.user_status_dropdown);
        // Apply the adapter to the spinner
        userStatusSpinner.setAdapter(adapter);
        userStatusSpinner.setSelection(user.getUserStatus());
        userStatusSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                int[] colors = getResources().getIntArray(R.array.user_statuses_array);
                userStatusIcon.setColorFilter(colors[position]);
                user.setUserStatus(position);
                FirebaseConnection.changeStatus(user, position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    public void loadFragment(Fragment fragment, boolean backstackEnabled){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right,android.R.anim.slide_in_left,android.R.anim.slide_out_right);
        transaction.replace(R.id.fragment_placeholder, fragment);
        if(backstackEnabled)
            transaction.addToBackStack(null);
        transaction.commit();
    }


    @Override
    public void onError(String error) {
        showAlert(error);
    }

    @Override
    public boolean isActive() {
        return isActive;
    }
}
