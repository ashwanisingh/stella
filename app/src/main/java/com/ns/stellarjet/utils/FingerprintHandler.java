package com.ns.stellarjet.utils;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.CancellationSignal;
import android.widget.Toast;
import androidx.annotation.RequiresApi;
import androidx.legacy.app.ActivityCompat;
import com.ns.networking.model.UserData;
import com.ns.stellarjet.PassCodeActivity;
import com.ns.stellarjet.TouchIdActivity;
import com.ns.stellarjet.home.HomeActivity;


@RequiresApi(api = Build.VERSION_CODES.M)
public class FingerprintHandler extends FingerprintManager.AuthenticationCallback {

    private CancellationSignal cancellationSignal;
    private Context context;
    private UserData mUserData;


    public FingerprintHandler(Context mContext , UserData userDataParams) {
        context = mContext;
        mUserData = userDataParams;
    }

    public void startAuth(FingerprintManager manager, FingerprintManager.CryptoObject cryptoObject) {
        cancellationSignal = new CancellationSignal();
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        manager.authenticate(cryptoObject, cancellationSignal, 0, this, null);
    }

    @Override
    public void onAuthenticationError(int errMsgId,
                                      CharSequence errString) {
        Toast.makeText(context,
                "Authentication error\n" + errString,
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onAuthenticationFailed() {
        Toast.makeText(context,
                "Authentication failed",
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onAuthenticationHelp(int helpMsgId,
                                     CharSequence helpString) {
        Toast.makeText(context,
                "Authentication help\n" + helpString,
                Toast.LENGTH_LONG).show();
    }


    @Override
    public void onAuthenticationSucceeded(
            FingerprintManager.AuthenticationResult result) {

      /*  Toast.makeText(context,
                "Success!",
                Toast.LENGTH_LONG).show();*/
        launchHomeActivity();

    }


    private void launchHomeActivity(){
        Intent mHomeIntent = new Intent(context, HomeActivity.class);
        // send bundle
        mHomeIntent.putExtra(UIConstants.BUNDLE_USER_DATA , mUserData);
        context.startActivity(mHomeIntent);
        ((Activity)context).finish();
    }


    private void launchPasscodeActivity(){
        Intent mPasscodeIntent = new Intent(context , PassCodeActivity.class);
        // send bundle
        mPasscodeIntent.putExtra(UIConstants.BUNDLE_USER_DATA , mUserData);
        context.startActivity(mPasscodeIntent);
        ((Activity)context).finish();
    }


}