package com.sample.socialnetwork.google;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

/**
 * Created by Ekaterina on 17.12.2015.
 */
public class GoogleSigninModule extends ReactContextBaseJavaModule
        implements GoogleApiClient.OnConnectionFailedListener {

    private Activity mActivity;
    private GoogleApiClient mApiClient;
    private static ReactApplicationContext mContext;

    public static final int RC_SIGN_IN = 9001;

    public GoogleSigninModule(ReactApplicationContext reactContext, Activity activity) {
        super(reactContext);
        mActivity = activity;
        mContext = reactContext;
    }

    @Override
    public String getName() {
        return "GoogleSignin";
    }

    @ReactMethod
    public void init() {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestEmail()
                        .build();

                mApiClient = new GoogleApiClient.Builder(mActivity.getBaseContext())
                        .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                        .build();
                mApiClient.connect();
                start();
            }
        });
    }

    private void start() {
        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mApiClient);

        if (opr.isDone()) {
            GoogleSignInResult result = opr.get();
            handleSignInResult(result);
        } else {
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(GoogleSignInResult googleSignInResult) {
                    handleSignInResult(googleSignInResult);
                }
            });
        }
    }

    @ReactMethod
    public void signIn() {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mApiClient);
                mActivity.startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });
    }


    public static void onActivityResult(int requestCode, Intent data) {

        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    private static void handleSignInResult(GoogleSignInResult result) {
        WritableMap params = Arguments.createMap();

        if (result.isSuccess()) {
            GoogleSignInAccount acct = result.getSignInAccount();
            params.putString("name", acct.getDisplayName());
            params.putString("email", acct.getEmail());
            params.putString("accessToken", acct.getIdToken());

            mContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                    .emit("googleSignIn", params);
        } else {
            params.putString("error", "signin error");

            mContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                    .emit("googleSignInError", params);
        }
    }

    @ReactMethod
    public void signOut() {
        Auth.GoogleSignInApi.signOut(mApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        WritableMap params = Arguments.createMap();
                        mContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                                .emit("googleSignOut", params);
                    }
                });
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (!connectionResult.hasResolution()) {
            GooglePlayServicesUtil.getErrorDialog(connectionResult.getErrorCode(), mActivity, 0).show();
            return;
        }

        try {
            connectionResult.startResolutionForResult(mActivity, RC_SIGN_IN);
        } catch (IntentSender.SendIntentException e) {
            e.printStackTrace();
        }
    }
}
