package com.evilcorp.firebaseintegration.base;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
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
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.DrawableTypeRequest;
import com.bumptech.glide.Glide;
import com.evilcorp.firebaseintegration.model.firebase.AccountType;
import com.evilcorp.firebaseintegration.view.ExpandablePanel;
import com.evilcorp.firebaseintegration.MyApp;
import com.evilcorp.firebaseintegration.R;
import com.evilcorp.firebaseintegration.helper.FirebaseConnectionHelper;
import com.evilcorp.firebaseintegration.helper.RounderCornerImage;
import com.evilcorp.firebaseintegration.model.firebase.UserAccount;
import com.evilcorp.firebaseintegration.model.firebase.UserStatus;

public abstract class BaseActivity extends AppCompatActivity {
    private ProgressDialog mProgressDialog;
    private AlertDialog mAlertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onPause() {
        super.onPause();
        FirebaseConnectionHelper.goOffline();
    }

    @Override
    protected void onResume() {
        super.onResume();
        FirebaseConnectionHelper.goOnline();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dismissProgress();
        if(mAlertDialog!=null)
            mAlertDialog.dismiss();
        if(mProgressDialog!=null)
            mProgressDialog.dismiss();
    }

    protected void showProgress(String msg) {
        if (mProgressDialog != null && mProgressDialog.isShowing())
            dismissProgress();

        mProgressDialog = ProgressDialog.show(this, getResources().getString(R.string.app_name), msg);
    }

    protected void dismissProgress() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
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
        final UserAccount user = MyApp.getCurrentAccount();
        if(user == null) {
            return;
        }
        Toolbar userToolbar = (Toolbar) findViewById(R.id.userToolbar);
        userToolbar.setTitle(user.getName());
        setSupportActionBar(userToolbar);

        ImageView userAvatar = (ImageView) findViewById(R.id.userAvatar);
        Object avatar = user.getAvatar();
        if (avatar != null) {
            Glide.with(this)
                    .load(avatar)
                    .asBitmap()
                    .centerCrop()
                    .into(new RounderCornerImage(this, userAvatar));
        }

        final ImageView userStatusIcon = (ImageView) findViewById(R.id.userStatusIcon);
        Spinner userStatusSpinner = (Spinner) findViewById(R.id.userStatusSpinner);
        userStatusSpinner.setBackgroundColor(ContextCompat.getColor(this,R.color.colorPrimary));
        if(user.getAccountType() == AccountType.GUEST){
            userStatusSpinner.setVisibility(View.INVISIBLE);
            userStatusIcon.setVisibility(View.INVISIBLE);
            return;
        }
        // Create an ArrayAdapter using the string array and a default spinner layout
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.user_status_item, UserStatus.getAll());
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(R.layout.user_status_dropdown);
        // Apply the adapter to the spinner
        userStatusSpinner.setAdapter(adapter);
        userStatusSpinner.setSelection(user.getUserStatus());
        userStatusSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
//                int color = 0;
//                switch(position){
//                    case 0:
//                        color = R.color.statusOfflineColor;
//                        break;
//                    case 1:
//                        color = R.color.statusOnlineColor;
//                        break;
//                    case 2:
//                        color = R.color.statusAwayColor;
//                        break;
//                    case 3:
//                        color = R.color.statusBusyColor;
//                        break;
//                    case 4:
//                        color = R.color.statusOfflineColor;
//                        break;
//                }

                int[] colors = getResources().getIntArray(R.array.user_statuses_array);
                userStatusIcon.setColorFilter(colors[position]);
                user.setUserStatus(position);
                FirebaseConnectionHelper.changeStatus(user, position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    protected void addUserPanel(Context context) {
        final UserAccount user = MyApp.getCurrentAccount();
        ExpandablePanel userPanel = new ExpandablePanel(this);// = (ExpandablePanel) findViewById(R.id.expandablePanel);
        if(user != null && userPanel != null) {
            userPanel.setOnExpandListener(new ExpandablePanel.OnExpandListener() {
                @Override
                public void onExpand(View handle, View content) {
                    Log("Expand");
                }

                @Override
                public void onCollapse(View handle, View content) {
                    Log("Collapse");
                }
            });
            ImageView userAvatar = (ImageView) findViewById(R.id.userAvatar);
            String avatar = user.getAvatar();
            if(avatar != null) {
                Glide.with(this)
                        .load(user.getAvatar())
                        .asBitmap()
                        .centerCrop()
                        .into(new RounderCornerImage(this, userAvatar));
            }
            TextView userName = (TextView) findViewById(R.id.userName);
            userName.setText(user.getName());

            TextView userEmail = (TextView) findViewById(R.id.userEmail);
            userEmail.setText(user.getEmail());

            final ImageView userStatusIcon = (ImageView) findViewById(R.id.userStatusIcon);

            Spinner userStatusSpinner = (Spinner) findViewById(R.id.userStatusSpinner);
            if(user.getAccountType() == AccountType.GUEST){
                userStatusSpinner.setVisibility(View.INVISIBLE);
                userStatusIcon.setVisibility(View.INVISIBLE);
                return;
            }
            // Create an ArrayAdapter using the string array and a default spinner layout
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,UserStatus.getAll());
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            // Apply the adapter to the spinner
            userStatusSpinner.setAdapter(adapter);
            userStatusSpinner.setSelection(user.getUserStatus());
            userStatusSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                    int color = 0;
                    switch(position){
                        case 0:
                            color = R.color.statusOfflineColor;
                            break;
                        case 1:
                            color = R.color.statusOnlineColor;
                            break;
                        case 2:
                            color = R.color.statusAwayColor;
                            break;
                        case 3:
                            color = R.color.statusBusyColor;
                            break;
                        case 4:
                            color = R.color.statusOfflineColor;
                            break;
                    }
                    userStatusIcon.setColorFilter(ContextCompat.getColor(BaseActivity.this,color));
                    user.setUserStatus(position);
                    FirebaseConnectionHelper.changeStatus(user, position);
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
        }
    }

    public void loadFragment(Fragment fragment, boolean backstackEnabled){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right,android.R.anim.slide_in_left,android.R.anim.slide_out_right);
        transaction.replace(R.id.fragment_placeholder, fragment);
        if(backstackEnabled)
            transaction.addToBackStack(null);
        transaction.commit();
    }
}
