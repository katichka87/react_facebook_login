package com.sample;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.react.LifecycleState;
import com.facebook.react.ReactInstanceManager;
import com.facebook.react.ReactRootView;
import com.facebook.react.modules.core.DefaultHardwareBackBtnHandler;
import com.facebook.react.shell.MainReactPackage;
import com.sample.socialnetwork.TwitterActivity;
import com.sample.socialnetwork.facebook.FacebookLoginPackage;
import com.sample.socialnetwork.google.GoogleSigninModule;
import com.sample.socialnetwork.google.GoogleSigninPackage;
//import com.sample.socialnetwork.google.GoogleSigninPackage;

import com.sample.socialnetwork.twitter.TwitterLoginPackage;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.models.Configuration;

import io.fabric.sdk.android.Fabric;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MainActivity extends Activity implements DefaultHardwareBackBtnHandler {



    private ReactInstanceManager mReactInstanceManager;
    private ReactRootView mReactRootView;

    private FacebookLoginPackage mFacebookLoginPackage;
    private GoogleSigninPackage mGoogleSigninPackage;
    private TwitterLoginPackage mTwitterLoginPackage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //facebook
        mFacebookLoginPackage = new FacebookLoginPackage(this);

        //google
        mGoogleSigninPackage = new GoogleSigninPackage(this);

        //twitter
        mTwitterLoginPackage = new TwitterLoginPackage(this);

        mReactRootView = new ReactRootView(this);
        mReactInstanceManager = ReactInstanceManager.builder()
                .setApplication(getApplication())
                .setBundleAssetName("index.android.bundle")
                .setJSMainModuleName("index.android")
                .addPackage(new MainReactPackage())
                .setUseDeveloperSupport(BuildConfig.DEBUG)
                .setInitialLifecycleState(LifecycleState.RESUMED)
                .addPackage(mFacebookLoginPackage)
                .addPackage(mGoogleSigninPackage)
                .addPackage(mTwitterLoginPackage)
                .build();

        mReactRootView.startReactApplication(mReactInstanceManager, "sample", null);

        setContentView(mReactRootView);


        /*try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.sample", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }*/


        //startActivity(new Intent(this, TwitterActivity.class));
    }


    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mFacebookLoginPackage.handleActivityResult(requestCode, resultCode, data);
        mTwitterLoginPackage.handleActivityResult(requestCode, resultCode, data);
        if (requestCode == GoogleSigninModule.RC_SIGN_IN) {
            GoogleSigninModule.onActivityResult(requestCode, data);
        }
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        //if (keyCode == KeyEvent.KEYCODE_MENU && mReactInstanceManager != null) {
            mReactInstanceManager.showDevOptionsDialog();
            return true;
        //}
        //return super.onKeyUp(keyCode, event);
    }

    @Override
    public void onBackPressed() {
      if (mReactInstanceManager != null) {
        mReactInstanceManager.onBackPressed();
      } else {
        super.onBackPressed();
      }
    }

    @Override
    public void invokeDefaultOnBackPressed() {
      super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();

        AppEventsLogger.deactivateApp(this);

        if (mReactInstanceManager != null) {
            mReactInstanceManager.onPause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        AppEventsLogger.activateApp(this);

        if (mReactInstanceManager != null) {
            mReactInstanceManager.onResume(this, this);
        }
    }
}
