package com.sample.socialnetwork.twitter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.crashlytics.android.Crashlytics;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;

import io.fabric.sdk.android.Fabric;

/**
 * Created by Ekaterina on 23.12.2015.
 */
public class TwitterLoginManager  extends ReactContextBaseJavaModule {

    private static final String TWITTER_KEY = "W6fCp9y56QNTpy9wweJwYkJjl";
    private static final String TWITTER_SECRET = "3Kg434wobTk3qgjaiW15VARNeklmOLzfxYtWuxhse50GTat3AU";

    TwitterAuthClient mTwitterAuthClient;

    private Context mActivityContext;

    public TwitterLoginManager(ReactApplicationContext reactContext, Context activityContext) {
        super(reactContext);

        mActivityContext = activityContext;

        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(mActivityContext, new Crashlytics(), new Twitter(authConfig));

        mTwitterAuthClient = new TwitterAuthClient();
    }

    @Override
    public String getName() {
        return "TwitterLoginManager";
    }

    @ReactMethod
    public String getCurrentToken(final Callback callback) {
        TwitterSession session = Twitter.getSessionManager().getActiveSession();
        if (session != null) {
            TwitterAuthToken authToken = session.getAuthToken();
            String token = authToken.token;
            String secret = authToken.secret;
            if (callback != null) {
                callback.invoke(token);
            }
            return token;
        }
        if (callback != null) {
            callback.invoke(null);
        }
        return null;
    }

    @ReactMethod
    public void login(final Callback callback) {

        mTwitterAuthClient.authorize((Activity) mActivityContext, new com.twitter.sdk.android.core.Callback<TwitterSession>() {

            @Override
            public void success(Result<TwitterSession> twitterSessionResult) {
                //Toast.makeText(mActivityContext, twitterSessionResult.toString(), Toast.LENGTH_SHORT).show();
                callback.invoke(getCurrentToken(null));
            }

            @Override
            public void failure(TwitterException e) {
                e.printStackTrace();
                //Toast.makeText(mActivityContext, "Failed to connect to Twitter " + e.getMessage(), Toast.LENGTH_SHORT).show();
                callback.invoke(null);
            }
        });

    }

    @ReactMethod
    public void logout() {
        Twitter.getSessionManager().clearActiveSession();
    }

    public void handleActivityResult(final int requestCode, final int resultCode, final Intent data) {
        mTwitterAuthClient.onActivityResult(requestCode, resultCode, data);
    }

}
