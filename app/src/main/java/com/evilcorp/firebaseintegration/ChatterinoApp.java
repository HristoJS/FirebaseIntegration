package com.evilcorp.firebaseintegration;

import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.multidex.MultiDexApplication;

import com.evilcorp.firebaseintegration.data.firebase.FirebaseConnection;
import com.evilcorp.firebaseintegration.helper.Time;
import com.evilcorp.firebaseintegration.data.firebase.model.user.GuestAccount;
import com.evilcorp.firebaseintegration.data.firebase.model.user.UserAccount;
import com.evilcorp.firebaseintegration.data.firebase.model.UserStatus;
import com.facebook.CallbackManager;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInApi;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;

import io.fabric.sdk.android.Fabric;


public class ChatterinoApp extends MultiDexApplication {

    public static final int GOOGLE_AUTH_REQUEST_CODE = 0x2103;
    public static final int TWITTER_AUTH_REQUEST_CODE = TwitterAuthConfig.DEFAULT_AUTH_REQUEST_CODE;
    private static GoogleApiClient mGoogleApiClient = null;
    private static FirebaseAnalytics mFirebaseAnalytics = null;
    private static CallbackManager mFbCallbackManager = null;
    private static UserAccount currentAccount;

    @Override
    public void onCreate() {
        //enableStrictMode();
        super.onCreate();
        initFabric();
        initGoogleApi();
        //FirebaseApp.initializeApp(getApplicationContext());
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        setDefaultSettings();
        Time.init(this);
    }

    private void enableStrictMode() {
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectDiskReads()
                .detectDiskWrites()
                .detectNetwork()   // or .detectAll() for all detectable problems
                .penaltyLog()
                .build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .detectLeakedSqlLiteObjects()
                .detectLeakedClosableObjects()
                .penaltyLog()
                .penaltyDeath()
                .build());
    }

    private void initFabric(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                TwitterAuthConfig authConfig = new TwitterAuthConfig(BuildConfig.TWITTER_KEY, BuildConfig.TWITTER_SECRET);
                Fabric.with(ChatterinoApp.this, new Twitter(authConfig));
                MobileAds.initialize(getApplicationContext(),BuildConfig.ADS_PUBLIC_KEY);
            }
        }).start();
    }

    private void setDefaultSettings(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                PreferenceManager.setDefaultValues(getApplicationContext(), R.xml.preferences, false);
            }
        }).start();
    }

    private void initGoogleApi() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                //.enableAutoManage(getBaseContext() /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    public static FirebaseAnalytics getFirebaseAnalytics() {
        return mFirebaseAnalytics;
    }
    public static GoogleApiClient getGoogleApiClient() {
        return mGoogleApiClient;
    }
    public static GoogleSignInApi getGoogleSignInApi() {
        return Auth.GoogleSignInApi;
    }

    @Nullable
    public static UserAccount getCurrentAccount(){
        if(currentAccount == null) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if(user == null){
                return null;
            }
            if(user.isAnonymous()){
                return new GuestAccount();
            }
            currentAccount = new UserAccount(user);
            FirebaseConnection.init(currentAccount);
        }
        return currentAccount;
    }

    public static void logout(){
        FirebaseConnection.changeStatus(currentAccount, UserStatus.OFFLINE);
        currentAccount = null;
    }

}
