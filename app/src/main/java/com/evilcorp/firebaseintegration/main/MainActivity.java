package com.evilcorp.firebaseintegration.main;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.evilcorp.firebaseintegration.R;
import com.evilcorp.firebaseintegration.base.BaseActivity;
import com.evilcorp.firebaseintegration.findaddress.FindAddressActivity;
import com.evilcorp.firebaseintegration.friendlist.FriendListActivity;
import com.evilcorp.firebaseintegration.login.LoginActivity;
import com.evilcorp.firebaseintegration.model.firebase.AccountType;
import com.evilcorp.firebaseintegration.model.starwars.Film;
import com.evilcorp.firebaseintegration.settings.SettingsActivity;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.appinvite.AppInviteInvitation;

import java.util.List;

public class MainActivity extends BaseActivity implements View.OnClickListener, MainContract.View {
    private static final String TAG = "MainActivity";
    private static final int REQUEST_INVITE = 512;
    public static final String PROFILE_EXTRA = "PROFILE";
    private static final int BILLING_RESPONSE_RESULT_OK = 0;
    private MainContract.Presenter presenter;
    private InterstitialAd mInterstitialAd;

    //region Activity methods
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        presenter = new MainPresenter(this, new MainInteractor());
        Button address_button = (Button) findViewById(R.id.address_button);
        address_button.setOnClickListener(this);
        Button logout_button = (Button) findViewById(R.id.logout_button);
        logout_button.setOnClickListener(this);
        Button settings_button = (Button) findViewById(R.id.settings_button);
        settings_button.setOnClickListener(this);
        Button chat_button = (Button) findViewById(R.id.chat_button);
        chat_button.setOnClickListener(this);
        Button share_button = (Button) findViewById(R.id.share_button);
        share_button.setOnClickListener(this);
        Button invite_button = (Button) findViewById(R.id.invite_button);
        invite_button.setOnClickListener(this);

        if (presenter.getAccountType() == AccountType.GUEST){
            chat_button.setEnabled(false);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        presenter.getWelcomeMessage();
        presenter.downloadImage();
        presenter.getFilms();
        //initAds();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.logout_button:
                logout();
                break;
            case R.id.address_button:
                startActivity(new Intent(this, FindAddressActivity.class));
                break;
            case R.id.settings_button:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
            case R.id.chat_button:
                loadChat();
                break;
            case R.id.share_button:
                share();
                break;
            case R.id.invite_button:
                inviteFriend();
                break;
            default:
                break;
        }
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
                Log.e(TAG,"Send failed.");
            }
        }
    }

    //endregion

    //region Private methods
    private void loadChat(){
        startActivity(new Intent(this,FriendListActivity.class));
    }


    private void share(){

    }

    private void inviteFriend(){
        Intent intent = new AppInviteInvitation.IntentBuilder(getString(R.string.invitation_title))
                .setMessage(getString(R.string.invitation_message))
                //.setCustomImage(imageUri)
                .setCallToActionText(getString(R.string.invitation_cta))
                .build();
        startActivityForResult(intent, REQUEST_INVITE);
    }

    private void logout(){
        showAlert("Are you sure you want to logout?", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                presenter.logout();
            }
        },true);
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
        TextView welcome_msg = (TextView) findViewById(R.id.welcome_msg);
        welcome_msg.setText(message);
    }

    @Override
    public void setImage(Uri imageUri) {
        ImageView image = (ImageView) findViewById(R.id.image);
        Glide.with(this).load(imageUri).into(image);
    }

    @Override
    public void showFilms(List<Film> films) {
        if(films!=null&&films.size()>0) {
            Log.d(TAG, films.get(0).toString());
        }
    }

    //endregion

    //region Ads
    private void initAds(){
        AdView mAdView = (AdView) findViewById(R.id.adView);
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
    private void loadAd(){
        if (mInterstitialAd.isLoaded())
            mInterstitialAd.show();
    }
    private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder().build();
        mInterstitialAd.loadAd(adRequest);
    }

    //endregion

}
