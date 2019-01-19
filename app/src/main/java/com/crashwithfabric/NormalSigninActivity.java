package com.crashwithfabric;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class NormalSigninActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener
{

    Button bt_gmail;
    private GoogleApiClient mGoogleApiClient;
    private static final int RC_SIGN_IN = 001;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sociallogin);
        bt_gmail = (Button)findViewById(R.id.bt_gmail);

        mAuth = FirebaseAuth.getInstance();
        bt_gmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestId()
                .requestIdToken("381864264440-8e8o2hgcbsmn4cl1ejmms3hrep1q26di.apps.googleusercontent.com")
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();



    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Signed in successfully, show authenticated UI.
                GoogleSignInAccount acct = result.getSignInAccount();
                String personName = acct.getDisplayName();
                String personEmail = acct.getEmail();
                String personId = acct.getId();
                // fbID = personId;
firebaseAuthWithGoogle(acct);

                //Toast.makeText(LoginActivity.this, "Login: " + personEmail + " " + personId + " " + personName + " ", Toast.LENGTH_SHORT).show();






                //Uri personPhoto = acct.getPhotoUrl();
                /*if (Utils.isOnline(LoginActivity.this)) {
                    isGmailLogin = true;
                    isFbLogin = true;
                    callFbLogin(personId, personName, personEmail, "gmail");
                }*/

            } else {
                //
                Toast.makeText(NormalSigninActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                // Signed out, show unauthenticated UI.
                // updateUI(false);
            }
        } else {

        }
      /*  if (requestCode == REQUEST_CODE_LOCATION) {
            if (resultCode == RESULT_OK) {
              //  updateUI();
               // isloactionperimisionallow = true;
                Log.v("checkPermissionlocation","checkPermissionlocation8888");
                isPermisionGranted = true;
                if (buttonType.equalsIgnoreCase("Signup")){
                    Intent intent1 = new Intent(LoginActivity.this, RegisterActivityStep1.class);
                    intent1.putExtra("LOGINTYPE", "other");
                    intent1.putExtra("socialid", "");
                    intent1.putExtra("name", "");
                    startActivity(intent1);
                    Log.v("checkPermissionlocation","checkPermissionlocation999");
                }


            } else {
               // isloactionperimisionallow = false;
                Log.v("checkPermissionlocation","checkPermissionlocation10");
                isPermisionGranted = false;
               *//* if (buttonType.equalsIgnoreCase("Signup")){
                    Intent intent1 = new Intent(LoginActivity.this, RegisterActivityStep1.class);
                    intent1.putExtra("LOGINTYPE", "other");
                    intent1.putExtra("socialid", "");
                    intent1.putExtra("name", "");
                    startActivity(intent1);
                    Log.v("checkPermissionlocation","checkPermissionlocation11");
                }*//*
                Log.e("LOG_TAG", "Location permission not granted:");
            }
        }*/

    }

    private FirebaseAuth mAuth;
    // [START auth_with_google]
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {

        try
        {
            Log.d("getaccountid", "firebaseAuthWithGoogle:" + acct.getId());
            // [START_EXCLUDE silent]

            // [END_EXCLUDE]


            AuthCredential credential = GoogleAuthProvider.getCredential( acct.getIdToken(), null);
            mAuth.signInWithCredential(credential)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d("Success", "signInWithCredential:success");
                                FirebaseUser user = mAuth.getCurrentUser();

                            } else {
                                // If sign in fails, display a message to the user.
                                Log.e("errorrFail",""+task.getException());
                                Toast.makeText(NormalSigninActivity.this, ""+task.getException(), Toast.LENGTH_SHORT).show();
                                Log.w("", "signInWithCredential:failure", task.getException());

                            }

                            // [START_EXCLUDE]

                            // [END_EXCLUDE]
                        }
                    });
        }
        catch (Exception e)
        {
            Log.e("firebaseerror",e.toString());
        }

    }
    // [END auth_with_google]

    // [START signin]

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();

    }
}
