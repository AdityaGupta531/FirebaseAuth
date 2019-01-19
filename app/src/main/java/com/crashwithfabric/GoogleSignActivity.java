package com.crashwithfabric;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.TwitterAuthProvider;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class GoogleSignActivity extends AppCompatActivity
{
    Button bt_gmail,bt_facebook,bt_twitter;
    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 9001;

    // [START declare_auth]
    private FirebaseAuth mAuth;
    // [END declare_auth]
    private CallbackManager callbackManager;
    private TwitterAuthClient client;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_sociallogin);
        bt_gmail = (Button)findViewById(R.id.bt_gmail);
        bt_facebook = (Button)findViewById(R.id.bt_facebook);
        bt_twitter = (Button)findViewById(R.id.bt_twitter);
        FacebookSdk.setApplicationId("266584887317038");
        FacebookSdk.sdkInitialize(getApplicationContext());

        printKeyHash(GoogleSignActivity.this);
        callbackManager = CallbackManager.Factory.create();


        // Configure Twitter SDK
        TwitterAuthConfig authConfig =  new TwitterAuthConfig(
                getString(R.string.twitter_consumer_key),
                getString(R.string.twitter_consumer_secret));

        TwitterConfig twitterConfig = new TwitterConfig.Builder(this)
                .twitterAuthConfig(authConfig)
                .build();

        Twitter.initialize(twitterConfig);
        client = new TwitterAuthClient();



          try {
              // [START config_signin]
              // Configure Google Sign In
              GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                      .requestIdToken("381864264440-8e8o2hgcbsmn4cl1ejmms3hrep1q26di.apps.googleusercontent.com")
                      .requestEmail()
                      .build();
              // [END config_signin]

              mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

              // [START initialize_auth]
              // Initialize Firebase Auth
              mAuth = FirebaseAuth.getInstance();
              // [END initialize_auth]
              bt_gmail.setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View v) {
                      signIn();
                  }
              });

              bt_facebook.setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View v) {
                    //  LoginManager.getInstance().logInWithReadPermissions(GoogleSignActivity.this, Arrays.asList("email"));
                      LoginManager.getInstance().logInWithReadPermissions(GoogleSignActivity.this, Arrays.asList("email", "public_profile"));
                  }
              });

              bt_twitter.setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View v) {
                             customLoginTwitter(v);
                  }
              });
          }
          catch (Exception e)
          {
              Log.e("error",e.toString());
          }


          /*cODE FOR FACEBOOK START*/
          try
          {
              LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                  @Override
                  public void onSuccess(final LoginResult loginResult) {
                      final String fbUserID = loginResult.getAccessToken().getUserId();

                      GraphRequest request = GraphRequest.newMeRequest(
                              loginResult.getAccessToken(),

                              new GraphRequest.GraphJSONObjectCallback() {
                                  @Override
                                  public void onCompleted(JSONObject data, GraphResponse response) {
                                      try {
                                          String fbEmail = "";
                                          //    isFbLogin = true;
                                          String fbID = data.getString("id");
                                          String personName = data.getString("name");
                                          if (data.has("email"))
                                              fbEmail = data.getString("email");

                                          //   callFbLogin(fbID, personName, fbEmail, "facebook");
                                          Log.v("fbEmail", fbEmail + " ");
                                          Log.v("fbEmail", fbID + " ");
                                          Log.v("fbEmail", personName + " ");

                                          //userLogin(fbID, "", "facebook", fbID, personName);
                                          handleFacebookAccessToken(loginResult.getAccessToken());
                                          LoginManager.getInstance().logOut();

                                          //Toast.makeText(LoginActivity.this, "Login: " + fbEmail + " " + fbID + " " + personName + " ", Toast.LENGTH_SHORT).show();
                                      } catch (JSONException e) {
                                          e.printStackTrace();
                                      }
                                  }
                              });

                      Bundle parameters = new Bundle();
                      parameters.putString("fields","id,name,email,link,picture");
                      request.setParameters(parameters);
                      request.executeAsync();
                  }


                  @Override
                  public void onCancel() {
                      // Toast.makeText(LoginActivity.this, "", Toast.LENGTH_SHORT).show();
                  }

                  @Override
                  public void onError(FacebookException error) {
                      Toast.makeText(GoogleSignActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                  }

              });
          }
          catch (Exception e)
          {
              Log.e("FacebookError",e.toString());
          }

          /*CODE for FACEbook END*/


    }




    // [START on_start_check_user]
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }
    // [END on_start_check_user]

    // [START onactivityresult]
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately

                Log.e("APIException",e.getMessage());
                Log.e("APIExceptionstatus",e.getStatusCode()+"");
                // [START_EXCLUDE]
                updateUI(null);
                // [END_EXCLUDE]
            }
        }
        else
        {
            callbackManager.onActivityResult(requestCode, resultCode, data);
            /*if (client != null)
                client.onActivityResult(requestCode, resultCode, data);*/
        }
    }
    // [END onactivityresult]

    /**
     * get authenticates user session
     *
     * @return twitter session
     */
    private TwitterSession getTwitterSession() {
        TwitterSession session = TwitterCore.getInstance().getSessionManager().getActiveSession();

        //NOTE : if you want to get token and secret too use uncomment the below code
        /*TwitterAuthToken authToken = session.getAuthToken();
        String token = authToken.token;
        String secret = authToken.secret;*/

        return session;
    }

    // [START auth_with_google]
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct)
    {
        try {
            Log.d("getaccountid", "firebaseAuthWithGoogle:" + acct.getId());
            // [START_EXCLUDE silent]

            // [END_EXCLUDE]

            AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
            mAuth.signInWithCredential(credential)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d("Success", "signInWithCredential:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                updateUI(user);
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w("", "signInWithCredential:failure", task.getException());
                                updateUI(null);
                            }

                            // [START_EXCLUDE]

                            // [END_EXCLUDE]
                        }
                    });
        }
        catch (Exception e)
        {
            Log.e("facebookerror",e.toString());
        }

    }
    // [END auth_with_google]

    // [START signin]
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    // [END signin]

    private void signOut() {
        // Firebase sign out
        mAuth.signOut();

        // Google sign out
        mGoogleSignInClient.signOut().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        updateUI(null);
                    }
                });
    }

    private void revokeAccess() {
        // Firebase sign out
        mAuth.signOut();

        // Google revoke access
        mGoogleSignInClient.revokeAccess().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        updateUI(null);
                    }
                });
    }



    private void updateUI(FirebaseUser user) {

       /* if (user != null) {
            mStatusTextView.setText(getString(R.string.google_status_fmt, user.getEmail()));
            mDetailTextView.setText(getString(R.string.firebase_status_fmt, user.getUid()));

            findViewById(R.id.signInButton).setVisibility(View.GONE);
            findViewById(R.id.signOutAndDisconnect).setVisibility(View.VISIBLE);
        } else {
            mStatusTextView.setText(R.string.signed_out);
            mDetailTextView.setText(null);

            findViewById(R.id.signInButton).setVisibility(View.VISIBLE);
            findViewById(R.id.signOutAndDisconnect).setVisibility(View.GONE);
        }*/
    }


    public static String printKeyHash(Activity context) {
        PackageInfo packageInfo;
        String key = null;
        try {
            //getting application package name, as defined in manifest
            String packageName = context.getApplicationContext().getPackageName();

            //Retriving package info
            packageInfo = context.getPackageManager().getPackageInfo(packageName,
                    PackageManager.GET_SIGNATURES);

            Log.e("Package Name=", context.getApplicationContext().getPackageName());

            for (Signature signature : packageInfo.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                key = new String(Base64.encode(md.digest(), 0));

                // String key = new String(Base64.encodeBytes(md.digest()));
                Log.e("KeyHash=", key);
            }
        } catch (PackageManager.NameNotFoundException e1) {
            Log.e("Name not found", e1.toString());
        }
        catch (NoSuchAlgorithmException e) {
            Log.e("No such an algorithm", e.toString());
        } catch (Exception e) {
            Log.e("Exception", e.toString());
        }

        return key;
    }


    // [START auth_with_facebook]
    private void handleFacebookAccessToken(AccessToken token) {
        Log.d("token", "handleFacebookAccessToken:" + token);
        // [START_EXCLUDE silent]
       // showProgressDialog();
        // [END_EXCLUDE]

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("signinsuccess", "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                            mAuth.signOut();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("signinfail", "signInWithCredential:failure", task.getException());
                            Toast.makeText(GoogleSignActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        // [START_EXCLUDE]
                       //hideprogress
                        // [END_EXCLUDE]
                    }
                });
    }
    // [END auth_with_facebook]


    public void customLoginTwitter(View view) {
        //check if user is already authenticated or not
        if (getTwitterSession() == null) {

            //if user is not authenticated start authenticating
            client.authorize(this, new Callback<TwitterSession>() {
                @Override
                public void success(Result<TwitterSession> result) {

                    // Do something with result, which provides a TwitterSession for making API calls
                    TwitterSession twitterSession = result.data;


                  String ss =   twitterSession.getAuthToken().token;

                  handleTwitterSession(twitterSession);

                  Log.e("e",ss);

                    //call fetch email only when permission is granted
                    fetchTwitterEmail(twitterSession);
                }

                @Override
                public void failure(TwitterException e) {
                    // Do something on failure
                    Log.e("error@twitter",e.toString());
                    Toast.makeText(GoogleSignActivity.this, "Failed to authenticate. Please try again."+e.toString(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            //if user is already authenticated direct call fetch twitter email api
            Toast.makeText(this, "User already authenticated", Toast.LENGTH_SHORT).show();
            fetchTwitterEmail(getTwitterSession());
        }
    }


    /**
     * Before using this feature, ensure that “Request email addresses from users” is checked for your Twitter app.
     *
     * @param twitterSession user logged in twitter session
     */
    public void fetchTwitterEmail(final TwitterSession twitterSession) {
        client.requestEmail(twitterSession, new Callback<String>() {
            @Override
            public void success(Result<String> result) {
                //here it will give u only email and rest of other information u can get from TwitterSession
              //  userDetailsLabel.setText("User Id : " + twitterSession.getUserId() + "\nScreen Name : " + twitterSession.getUserName() + "\nEmail Id : " + result.data);



            }

            @Override
            public void failure(TwitterException exception) {
                Toast.makeText(GoogleSignActivity.this, "Failed to authenticate. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }



    // [START auth_with_twitter]
    private void handleTwitterSession(TwitterSession session) {
        Log.d("twitter", "handleTwitterSession:" + session);
        // [START_EXCLUDE silent]
       // showProgressDialog();
        // [END_EXCLUDE]

        AuthCredential credential = TwitterAuthProvider.getCredential(
                session.getAuthToken().token,
                session.getAuthToken().secret);

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("signintwitter", "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();


                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("signincredential", "signInWithCredential:failure", task.getException());
                            Toast.makeText(GoogleSignActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        // [START_EXCLUDE]
                       // hideProgressDialog();
                        // [END_EXCLUDE]
                    }
                });
    }
    // [END auth_with_twitter]

}
