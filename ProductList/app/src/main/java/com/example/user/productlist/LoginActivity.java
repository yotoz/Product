package com.example.user.productlist;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.example.user.helper.ProductListManager;
import com.example.user.helper.listener.OnCheckUserListener;
import com.example.user.model.User;
import com.example.user.productlist.R;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;


public class LoginActivity extends AppCompatActivity {
    @Override
    protected void onDestroy() {
        super.onDestroy();

        signOut();
    }

    // [START declare_auth]
    private FirebaseAuth mAuth;
    // [END declare_auth]
    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 9001;

    // objects
    private ProductListManager productListManager = null;

    // ui components
    private EditText etId;
    private EditText etPwd;
    private Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Set member variables about ui.
        etId = findViewById(R.id.etId);
        etPwd = findViewById(R.id.etPwd);
        btnLogin = findViewById(R.id.btnLogin);

        // Initialize member objects
        FirebaseApp.initializeApp(this);
        productListManager = new ProductListManager();

        // [START config_signin]
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // [END config_signin]

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // [START initialize_auth]
        mAuth = FirebaseAuth.getInstance();
        try
        {
            signOut();
        } catch(Exception e) {}
        // [END initialize_auth]
    }

    // [START on_start_check_user]
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
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

            }
        }
    }
    // [END onactivityresult]

    // [START auth_with_google]
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        // [START_EXCLUDE silent]
        // [END_EXCLUDE]

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();

                            productListManager.checkUser(user.getUid(), new OnCheckUserListener() {
                                @Override
                                public void onSucceed(User user) {
                                    // check the password
                                    if (user.getAdmin().equals(mAuth.getCurrentUser().getUid())) {
                                        Intent intent = new Intent(LoginActivity.this, AdminActivity.class);
                                        intent.putExtra("user",mAuth.getCurrentUser());
                                        startActivity(intent);
                                    } else {
                                        Intent intent = new Intent(LoginActivity.this, UserActivity.class);
                                        intent.putExtra("user",mAuth.getCurrentUser());
                                        startActivity(intent);
                                    }

                                    btnLogin.setEnabled(true);
                                }

                                @Override
                                public void onFailed() {


                                    btnLogin.setEnabled(true);
                                }
                            });
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(LoginActivity.this, "Login Data Incorrect!", Toast.LENGTH_LONG).show();
                            try {
                                signOut();
                            }catch(Exception e) {}
                        }
                    }
                });
    }
    // [END auth_with_google]

    private void signOut() {
        // Firebase sign out
        mAuth.signOut();

        // Google sign out
        mGoogleSignInClient.signOut().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        revokeAccess();
                    }
                });
    }

    // [START signin]
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    // [END signin]

    private void revokeAccess() {
        // Firebase sign out
        mAuth.signOut();

        // Google revoke access
        mGoogleSignInClient.revokeAccess().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                    }
                });
    }

    public void checkLogin(View v) {
        signIn();


        /*
        final String id = etId.getText().toString();
        final String pwd = etPwd.getText().toString();

        // Check user's account
        // User can't press the login button while checking.
        // After checking, button enable.
        btnLogin.setEnabled(false);

        productListManager.checkUser(id, pwd, new OnCheckUserListener() {
            @Override
            public void onSucceed(User user) {

                Toast.makeText(LoginActivity.this,  user.getPassword(), Toast.LENGTH_LONG).show();

                // check the password
                if (user.getPassword().equals(pwd)) {
                    Intent newActivity = null;

                    // check the type
                    switch (user.getType()) {
                        case User.ADMIN_TYPE:
                            newActivity = new Intent(LoginActivity.this, AdminActivity.class);
                            break;
                        case User.USER_TYPE:
                            Toast.makeText(LoginActivity.this, "This is not supported yet.", Toast.LENGTH_LONG).show();
                            break;
                    }

                    // Show an Activity
                    if (newActivity != null) {
                        startActivity(newActivity);
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "Check your password.", Toast.LENGTH_LONG).show();
                }

                btnLogin.setEnabled(true);
            }

            @Override
            public void onFailed() {
                Toast.makeText(LoginActivity.this, "Check your account.", Toast.LENGTH_LONG).show();
                btnLogin.setEnabled(true);
            }
        });*/
    }
}
