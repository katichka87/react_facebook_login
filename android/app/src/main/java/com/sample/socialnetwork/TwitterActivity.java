package com.sample.socialnetwork;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.sample.R;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;

import io.fabric.sdk.android.Fabric;

public class TwitterActivity extends AppCompatActivity {
    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "W6fCp9y56QNTpy9wweJwYkJjl";
    private static final String TWITTER_SECRET = "3Kg434wobTk3qgjaiW15VARNeklmOLzfxYtWuxhse50GTat3AU";

    TwitterAuthClient mTwitterAuthClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_twitter);

        //twitter
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Crashlytics(), new Twitter(authConfig));

        Button btn = (Button) findViewById(R.id.twitter_btn);
        if (checkSession()) {
            btn.setText("Logout from twitter");
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    logoutFromTwitter();
                }
            });
        } else {
            btn.setText("Login to twitter");
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    loginToTwitter();
                }
            });
        }

        mTwitterAuthClient = new TwitterAuthClient();

    }

    private boolean checkSession() {
        TwitterSession session = Twitter.getSessionManager().getActiveSession();
        if (session != null) {
            TwitterAuthToken authToken = session.getAuthToken();
            String token = authToken.token;
            String secret = authToken.secret;
            Toast.makeText(TwitterActivity.this, "session active authToken = " + authToken, Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }

    private void loginToTwitter() {

        mTwitterAuthClient.authorize(this, new Callback<TwitterSession>() {

            @Override
            public void success(Result<TwitterSession> twitterSessionResult) {
                Toast.makeText(TwitterActivity.this, twitterSessionResult.toString(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void failure(TwitterException e) {
                e.printStackTrace();
                Toast.makeText(TwitterActivity.this, "Failed to connect to Twitter " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void logoutFromTwitter() {
        Twitter.getSessionManager().clearActiveSession();
        Button btn = (Button) findViewById(R.id.twitter_btn);
        btn.setText("Login to twitter");
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginToTwitter();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int responseCode, Intent intent) {
        mTwitterAuthClient.onActivityResult(requestCode, responseCode, intent);
    }
}
