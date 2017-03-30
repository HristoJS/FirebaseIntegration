package com.evilcorp.firebaseintegration.ui.main;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.evilcorp.firebaseintegration.R;
import com.evilcorp.firebaseintegration.helper.NetworkInfoHelper;
import com.evilcorp.firebaseintegration.ui.base.BaseActivity;
import com.evilcorp.firebaseintegration.ui.friendlist.FriendListFragment;
import com.evilcorp.firebaseintegration.ui.login.LoginActivity;
import com.evilcorp.firebaseintegration.settings.SettingsFragment;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.appinvite.AppInviteInvitation;

public class MainActivity extends BaseActivity implements MainContract.View {
    private static final String TAG = "MainActivity";
    private static final int REQUEST_INVITE = 512;

    private int mCurrentFragmentId;
    private MainContract.Presenter mPresenter;
    private InterstitialAd mInterstitialAd;

    //region Activity methods
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mPresenter = new MainPresenter(this, new MainInteractor());
        loadFragment(new FriendListFragment(), false);
        mCurrentFragmentId = R.id.action_chat;
        addBottomNavigation();
        addUserPanel();
        //NetworkInfoHelper.isNetworkAvailable(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mPresenter.getWelcomeMessage();
        mPresenter.downloadImage();
        //initAds();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_INVITE) {
            if (resultCode == RESULT_OK) {
                // Get the invitation IDs of all sent messages
                String[] ids = AppInviteInvitation.getInvitationIds(resultCode, data);
                for (String id : ids) {
                    Toast.makeText(this, "Invite sent.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Log.e(TAG, "Send failed.");
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.user_toolbar_items, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout_button:
                logout();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //endregion

    //region Private methods
    private void addBottomNavigation() {
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigationView);
        if (bottomNavigationView != null) {
            bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    int itemId = item.getItemId();
                    if (itemId != mCurrentFragmentId) {
                        switch (itemId) {
                            case R.id.action_chat:
                                if (mPresenter.isGuest()) {
                                    showAlert("This feature is only available for registered users.");
                                    return false;
                                } else {
                                    loadFragment(new FriendListFragment(), true);
                                }
                                break;
                            case R.id.action_address:
                                break;
                            case R.id.action_settings:
                                loadFragment(new SettingsFragment(), true);
                                break;
                            default:
                                break;
                        }
                        mCurrentFragmentId = itemId;
                    }
                    return true;
                }
            });
        }
    }

    private void inviteFriend() {
        Intent intent = new AppInviteInvitation.IntentBuilder(getString(R.string.invitation_title))
                .setMessage(getString(R.string.invitation_message))
                //.setCustomImage(imageUri)
                .setCallToActionText(getString(R.string.invitation_cta))
                .build();
        startActivityForResult(intent, REQUEST_INVITE);
    }

    private void logout() {
        showAlert("Are you sure you want to logout?", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mPresenter.logout();
            }
        }, true);
    }
    //endregion

    //region Interface methods
    @Override
    public void logoutSuccess() {
        Intent loginIntent = new Intent(this, LoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
    }

    @Override
    public void logoutFail() {
        Log.d(TAG, "Failed to logout");
    }

    @Override
    public void setMessage(String message) {
        //TextView welcome_msg = (TextView) findViewById(R.id.welcome_msg);
        //welcome_msg.setText(message);
    }

    @Override
    public void setImage(Uri imageUri) {
        //ImageView image = (ImageView) findViewById(R.id.image);
        //Glide.with(this).load(imageUri).into(image);
    }


    //endregion

    //region Ads
    private void initAds() {
        AdView mAdView = new AdView(this);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getString(R.string.interstitial_ad_unit_id));
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                requestNewInterstitial();
            }
        });
        requestNewInterstitial();
    }

    private void loadAd() {
        if (mInterstitialAd.isLoaded())
            mInterstitialAd.show();
    }

    private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder().build();
        mInterstitialAd.loadAd(adRequest);
    }


    //endregion

}
