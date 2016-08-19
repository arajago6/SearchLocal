package edu.iit.arajago6hawk.searchlocal;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphRequestAsyncTask;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.server.converter.StringToIntConverter;

import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;

import static com.facebook.GraphRequest.TAG;
import static com.facebook.internal.CallbackManagerImpl.RequestCodeOffset.Login;

public class LoginScreen extends Activity {
    private Button loginButton;
    private CallbackManager callbackManager;
    private GoogleApiClient mGoogleApiClient;
    private AccessTokenTracker accessTokenTracker;
    static AccessToken newAccessToken;
    private Boolean exit = false;
    private ProgressDialog progressDialog;

    static String userName = "";
    static String userId = "";
    static String userEmail = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);

        TextView txt = (TextView) findViewById(R.id.sl_textView);
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/Pacifico.ttf");
        txt.setTypeface(font);

        FacebookSdk.sdkInitialize(getApplicationContext());
        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken newAccessToken) {
                updateWithToken(newAccessToken);
                LoginScreen.newAccessToken = newAccessToken;
            }
        };
        updateWithToken(AccessToken.getCurrentAccessToken());

        callbackManager = CallbackManager.Factory.create();
        loginButton = (Button) findViewById(R.id.login_button);

        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                progressDialog = new ProgressDialog(LoginScreen.this);
                progressDialog.setMessage("Getting your data...");
                progressDialog.show();
                final AccessToken accessToken = loginResult.getAccessToken();
                GraphRequestAsyncTask request = GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject user, GraphResponse response) {
                        userName = user.optString("name");
                        userId = user.optString("id");
                        userEmail = user.optString("email");
                        Intent intent = new Intent(getBaseContext(), MainActivity.class);
                        startActivity(intent);
                    }
                }).executeAsync();

            }

            @Override
            public void onCancel() {
                Toast.makeText(getApplicationContext(), "Login Cancelled", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException e) {
                Toast.makeText(getApplicationContext(), "Login Error", Toast.LENGTH_SHORT).show();
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginManager.getInstance().logInWithReadPermissions(LoginScreen.this, Arrays.asList("public_profile", "email"));
            }
        });

        final Button button2 = (Button) findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                    Intent intent = new Intent(getBaseContext(), MainActivity.class);
                    startActivity(intent);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        try {
                if (exit) {
                    Intent intent = new Intent(Intent.ACTION_MAIN);
                    intent.addCategory(Intent.CATEGORY_HOME);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//***Change Here***
                    startActivity(intent);
                } else {
                    String cbMsg = "Press Back again to Exit.";
                    String htmlString = " <font color=\"#27AD80\"><b><i>LEAVING?!</font></i></b><br/>" + cbMsg;
                    Toast.makeText(getApplicationContext(), Html.fromHtml(htmlString), Toast.LENGTH_SHORT).show();
                    exit = true;
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            exit = false;
                        }
                    }, 3 * 1000);

                }
        }
        catch(Exception e){}
    }

    private void updateWithToken(final AccessToken currentAccessToken) {

        if (currentAccessToken != null) {
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    progressDialog = new ProgressDialog(LoginScreen.this);
                    progressDialog.setMessage("Processing your data...");
                    progressDialog.show();
                    GraphRequestAsyncTask request = GraphRequest.newMeRequest(currentAccessToken, new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        public void onCompleted(JSONObject user, GraphResponse response) {
                            userName = user.optString("name");
                            userId = user.optString("id");
                            userEmail = user.optString("email");
                            Intent intent = new Intent(getBaseContext(), MainActivity.class);
                            startActivity(intent);
                        }
                    }).executeAsync();
                }
            }, 2);
        } else {
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                }
            }, 2);
        }
    }

}
