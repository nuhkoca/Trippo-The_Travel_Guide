package com.nuhkoca.trippo.ui;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.nuhkoca.trippo.R;
import com.nuhkoca.trippo.databinding.ActivityAuthBinding;
import com.nuhkoca.trippo.helper.Constants;
import com.nuhkoca.trippo.module.GlideApp;

import java.util.Objects;

import timber.log.Timber;

public class AuthActivity extends AppCompatActivity implements View.OnClickListener {

    private ActivityAuthBinding mActivityAuthBinding;

    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;

    private int mReqCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityAuthBinding = DataBindingUtil.setContentView(this, R.layout.activity_auth);
        setTitle(getString(R.string.auth_name));

        mReqCode = getIntent().getIntExtra(Constants.PARENT_ACTIVITY_REQ_KEY, 0);

        if (mReqCode == Constants.PARENT_ACTIVITY_REQ_CODE) {
            ActionBar actionBar = getSupportActionBar();

            if (actionBar != null) {
                actionBar.setDisplayShowHomeEnabled(true);
                actionBar.setDisplayHomeAsUpEnabled(true);
            }
        }

        mActivityAuthBinding.tvSkipNow.setOnClickListener(this);
        mActivityAuthBinding.btnSignIn.setOnClickListener(this);
        mActivityAuthBinding.tvLogout.setOnClickListener(this);

        mActivityAuthBinding.btnSignIn.setSize(SignInButton.SIZE_WIDE);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestScopes(new Scope(Scopes.EMAIL))
                .requestIdToken(getString(R.string.oauth_token))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        updateUI(currentUser);
    }

    @Override
    public void onClick(View v) {
        int itemThatWasClicked = v.getId();

        switch (itemThatWasClicked) {
            case R.id.tvSkipNow:
                if (mReqCode > 0) {
                    finish();
                } else {

                    Intent mMainIntent = new Intent(AuthActivity.this, MainActivity.class);
                    mMainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    mMainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(mMainIntent);
                }

                break;

            case R.id.btnSignIn:
                doSignIn();
                break;

            case R.id.tvLogout:
                doSignOut();
                break;

            default:
                break;
        }
    }

    private FirebaseAuth.AuthStateListener authStateListener = new FirebaseAuth.AuthStateListener() {
        @Override
        public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
            FirebaseUser currentUser = firebaseAuth.getCurrentUser();

            if (currentUser != null) {
                mActivityAuthBinding.pbAuth.setVisibility(View.INVISIBLE);
            } else {
                mActivityAuthBinding.pbAuth.setVisibility(View.INVISIBLE);
            }
        }
    };

    private void doSignIn() {
        mActivityAuthBinding.pbAuth.setVisibility(View.VISIBLE);

        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, Constants.RC_SIGN_IN);
    }

    private void doSignOut() {
        mActivityAuthBinding.pbAuth.setVisibility(View.VISIBLE);

        FirebaseAuth.getInstance().signOut();

        mActivityAuthBinding.llSignedOut.setVisibility(View.VISIBLE);
        mActivityAuthBinding.tvSkipNow.setVisibility(View.VISIBLE);

        mActivityAuthBinding.llSignedIn.setVisibility(View.GONE);
        mActivityAuthBinding.tvLogout.setVisibility(View.GONE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == Constants.RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // Signed in successfully, show authenticated UI.
            firebaseAuthWithGoogle(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Timber.d(e);
            updateUI(null);
        }
    }

    private void updateUI(FirebaseUser currentUser) {
        if (currentUser != null) {
            mActivityAuthBinding.llSignedOut.setVisibility(View.GONE);
            mActivityAuthBinding.tvSkipNow.setVisibility(View.GONE);

            mActivityAuthBinding.llSignedIn.setVisibility(View.VISIBLE);
            mActivityAuthBinding.tvCurrentUser.setText(currentUser.getEmail());
            mActivityAuthBinding.tvLogout.setVisibility(View.VISIBLE);

            loadAvatar(currentUser);

            Timber.d("account name: %s", Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail());

            if (mReqCode != Constants.PARENT_ACTIVITY_REQ_CODE) {
                startActivity(new Intent(AuthActivity.this, MainActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            }
        }
    }

    private void loadAvatar(FirebaseUser firebaseUser) {
        GlideApp.with(this)
                .load(firebaseUser.getPhotoUrl())
                .into(mActivityAuthBinding.civProfile);
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Timber.d("firebaseAuthWithGoogle: %s", acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Timber.d("signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Timber.w(task.getException());
                            Snackbar.make(mActivityAuthBinding.clAuth, "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemThatWasClicked = item.getItemId();

        switch (itemThatWasClicked) {
            case android.R.id.home:
                supportFinishAfterTransition();

                if (mReqCode == Constants.PARENT_ACTIVITY_REQ_CODE) {
                    NavUtils.navigateUpFromSameTask(this);
                } else {
                    super.onBackPressed();
                }

                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        supportFinishAfterTransition();
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();

        FirebaseAuth.getInstance().addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        if (authStateListener != null) {
            FirebaseAuth.getInstance().removeAuthStateListener(authStateListener);
        }
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        if (authStateListener != null) {
            FirebaseAuth.getInstance().removeAuthStateListener(authStateListener);
        }
        super.onDestroy();
    }
}