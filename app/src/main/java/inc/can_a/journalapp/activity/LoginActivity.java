package inc.can_a.journalapp.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.plus.Plus;

import inc.can_a.journalapp.R;

public class LoginActivity extends FragmentActivity implements
        ConnectionCallbacks, OnConnectionFailedListener,
        View.OnClickListener {

    private static final int SIGNED_IN = 0;
    private static final int STATE_SIGNING_IN = 1;
    private static final int STATE_IN_PROGRESS = 2;
    private static final int RC_SIGN_IN = 0;

    private GoogleApiClient mGoogleApiClient;
    private int mSignInProgress;
    private PendingIntent mSignInIntent;

    private SignInButton mSignInButton;
    private Button mSignOutButton;
    private Button mRevokeButton;
    private TextView mStatus;

    private static final int EXTERNAL_ACCOUNT_PERMISSION_CONSTANT = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Get references to all of the UI views
        mSignInButton = (SignInButton) findViewById(R.id.sign_in_button);
        mSignOutButton = (Button) findViewById(R.id.sign_out_button);
        mRevokeButton = (Button) findViewById(R.id.revoke_access_button);
        mStatus = (TextView) findViewById(R.id.statuslabel);


        // Add click listeners for the buttons
        mSignInButton.setOnClickListener(this);
        mSignOutButton.setOnClickListener(this);
        mRevokeButton.setOnClickListener(this);

        // Build a GoogleApiClient
        mGoogleApiClient = buildGoogleApiClient();
    }

    private GoogleApiClient buildGoogleApiClient() {
        return new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API, Plus.PlusOptions.builder().build())
                .addScope(new Scope("email"))
                .build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onConnectionSuspended(int cause) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        mSignInButton.setEnabled(false);
        mSignOutButton.setEnabled(true);
        mRevokeButton.setEnabled(true);

        // Indicate that the sign in process is complete.
        mSignInProgress = SIGNED_IN;

        try {
            String emailAddress = Plus.AccountApi.getAccountName(mGoogleApiClient);
            Intent in = new Intent(LoginActivity.this,ScrollingActivity.class);
            startActivity(in);
            finish();
        }
        catch(Exception ex){
            String exception = ex.getLocalizedMessage();
            String exceptionString = ex.toString();
            Toast.makeText(getApplicationContext(),"Exception"+exception,Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        if (mSignInProgress != STATE_IN_PROGRESS) {
            mSignInIntent = result.getResolution();
            if (mSignInProgress == STATE_SIGNING_IN) {
                resolveSignInError();
            }
        }
        // Will implement shortly
        onSignedOut();
    }

    private void resolveSignInError() {
        if (mSignInIntent != null) {
            try {
                mSignInProgress = STATE_IN_PROGRESS;
                startIntentSenderForResult(mSignInIntent.getIntentSender(), RC_SIGN_IN, null, 0, 0, 0);
            } catch (IntentSender.SendIntentException e) {
                mSignInProgress = STATE_SIGNING_IN;
                mGoogleApiClient.connect();
            }
        } else {
            // You have a play services error -- inform the user
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case RC_SIGN_IN:
                if (resultCode == RESULT_OK) {
                    mSignInProgress = STATE_SIGNING_IN;
                } else {
                    mSignInProgress = SIGNED_IN;
                }

                if (!mGoogleApiClient.isConnecting()) {
                    mGoogleApiClient.connect();
                }
                break;
        }
    }

    private void onSignedOut() {
        // Update the UI to reflect that the user is signed out.
        mSignInButton.setEnabled(true);
        mSignOutButton.setEnabled(false);
        mRevokeButton.setEnabled(false);

        mStatus.setText("Signed out");
    }

    @Override
    public void onClick(View v) {
        if (!mGoogleApiClient.isConnecting()) {
            switch (v.getId()) {
                case R.id.sign_in_button:
                    signIn();
                    break;
                case R.id.sign_out_button:
                    Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
                    mGoogleApiClient.disconnect();
                    mGoogleApiClient.connect();
                    break;
                case R.id.revoke_access_button:
                    Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
                    Plus.AccountApi.revokeAccessAndDisconnect(mGoogleApiClient);
                    mGoogleApiClient = buildGoogleApiClient();
                    mGoogleApiClient.connect();
                    break;
            }
        }
    }

    private void signIn() {
        checkPermisions();
        mStatus.setText("Signing In");
    }

    private void checkPermisions(){
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(LoginActivity.this,
                    Manifest.permission.GET_ACCOUNTS)) {
                //Show Information about why you need the permission
                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                builder.setTitle("Need Permissions");
                builder.setMessage("This app needs account permission.");
                builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        ActivityCompat.requestPermissions(LoginActivity.this,
                                new String[]{Manifest.permission.GET_ACCOUNTS},
                                EXTERNAL_ACCOUNT_PERMISSION_CONSTANT);
                    }
                });
                builder.setNegativeButton("Deny", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            } else {
                //just request the permission
                ActivityCompat.requestPermissions(LoginActivity.this,
                        new String[]{Manifest.permission.GET_ACCOUNTS}, EXTERNAL_ACCOUNT_PERMISSION_CONSTANT);
            }

        }else {
            resolveSignInError();

        }
    }
}
