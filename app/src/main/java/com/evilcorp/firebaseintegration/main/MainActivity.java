package com.evilcorp.firebaseintegration.main;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.evilcorp.firebaseintegration.R;
import com.evilcorp.firebaseintegration.base.BaseActivity;
import com.evilcorp.firebaseintegration.findaddress.FindAddressFragment;
import com.evilcorp.firebaseintegration.friendlist.FriendListFragment;
import com.evilcorp.firebaseintegration.login.LoginActivity;
import com.evilcorp.firebaseintegration.model.firebase.AccountType;
import com.evilcorp.firebaseintegration.model.starwars.Film;
import com.evilcorp.firebaseintegration.settings.SettingsFragment;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.appinvite.AppInviteInvitation;

import java.util.List;

public class MainActivity extends BaseActivity implements MainContract.View {
    private static final String TAG = "MainActivity";
    private static final int REQUEST_INVITE = 512;
    public static final String PROFILE_EXTRA = "PROFILE";
    private static final int BILLING_RESPONSE_RESULT_OK = 0;
    private MainContract.Presenter presenter;
    private InterstitialAd mInterstitialAd;
    private static int currentFragmentId;

    //region Activity methods
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        presenter = new MainPresenter(this, new MainInteractor());
        LoadFragment(new FriendListFragment(),true);
        currentFragmentId = R.id.action_chat;
        addBottomNavigation();
        addUserPanel();
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
    private void addBottomNavigation(){
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigationView);
        if(bottomNavigationView != null) {
            bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    int itemId = item.getItemId();
                    if(itemId != currentFragmentId){
                        switch (itemId) {
                            case R.id.action_chat:
                                if (presenter.getAccountType() == AccountType.GUEST) {
                                    showAlert("This feature is only available for registered users.");
                                    return false;
                                } else {
                                    LoadFragment(new FriendListFragment(), true);
                                }
                                break;
                            case R.id.action_address:
                                LoadFragment(new FindAddressFragment(), true);
                                break;
                            case R.id.action_settings:
                                LoadFragment(new SettingsFragment(), true);
                                break;
                            default:
                                break;
                        }
                        currentFragmentId = itemId;
                    }
                    return true;
                }
            });
        }
    }

    private void LoadFragment(Fragment fragment, boolean backstackEnabled){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_placeholder, fragment);
        if(backstackEnabled)
            transaction.addToBackStack(null);
        transaction.commit();
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
        //TextView welcome_msg = (TextView) findViewById(R.id.welcome_msg);
        //welcome_msg.setText(message);
    }

    @Override
    public void setImage(Uri imageUri) {
        //ImageView image = (ImageView) findViewById(R.id.image);
        //Glide.with(this).load(imageUri).into(image);
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
