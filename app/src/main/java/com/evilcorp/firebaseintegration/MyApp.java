package com.evilcorp.firebaseintegration;

import android.app.Application;
import android.preference.PreferenceManager;

import com.evilcorp.firebaseintegration.helper.FirebaseConnectionHelper;
import com.evilcorp.firebaseintegration.helper.Time;
import com.evilcorp.firebaseintegration.model.firebase.UserAccount;
import com.evilcorp.firebaseintegration.model.firebase.UserStatus;
import com.facebook.CallbackManager;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInApi;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.FirebaseApp;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;

import io.fabric.sdk.android.Fabric;



public class MyApp extends Application {

    public static final int GOOGLE_AUTH_REQUEST_CODE = 0x2103;
    public static final int TWITTER_AUTH_REQUEST_CODE = TwitterAuthConfig.DEFAULT_AUTH_REQUEST_CODE;
    private static GoogleApiClient mGoogleApiClient = null;
    private static FirebaseAnalytics mFirebaseAnalytics = null;
    private static CallbackManager mFbCallbackManager = null;
    private static UserAccount currentAccount;

    @Override
    public void onCreate() {
        super.onCreate();
        TwitterAuthConfig authConfig = new TwitterAuthConfig(BuildConfig.TWITTER_KEY, BuildConfig.TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                //.enableAutoManage(getBaseContext() /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        MobileAds.initialize(getApplicationContext(),BuildConfig.ADS_PUBLIC_KEY);
        FirebaseApp.initializeApp(getApplicationContext());
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        PreferenceManager.setDefaultValues(getApplicationContext(), R.xml.preferences, false);
        Time.init();
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

    public static UserAccount getCurrentAccount(){
        if(currentAccount == null) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            currentAccount = new UserAccount(user);
            FirebaseConnectionHelper.init(currentAccount);
        }
        return currentAccount;
    }

    public static void logout(){
        FirebaseConnectionHelper.changeStatus(currentAccount, UserStatus.OFFLINE);
        currentAccount = null;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }
}
