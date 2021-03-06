package com.ns.stellarjet.login;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.ns.networking.model.LoginResponse;
import com.ns.networking.model.SecondaryUserLoginResponse;
import com.ns.networking.retrofit.RetrofitAPICaller;
import com.ns.stellarjet.PassCodeActivity;
import com.ns.stellarjet.R;
import com.ns.stellarjet.home.HomeActivity;
import com.ns.stellarjet.utils.Progress;
import com.ns.stellarjet.utils.SharedPreferencesHelper;
import com.ns.stellarjet.utils.UIConstants;
import com.ns.stellarjet.utils.UiUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.Objects;

public class OTPActivity extends AppCompatActivity implements View.OnClickListener {


    private EditText mCurrentEditText;
    private String mFirstEditTextPassCode;
    private String mSecondEditTextPassCode;
    private String mThreeEditTextPassCode;
    private String mFourEditTextPassCode;
    Button mPasscodeOneButton;
    Button mPasscodeTwoButton;
    Button mPasscodeThreeButton;
    Button mPasscodeFOurButton;
    Button mPasscodeFiveButton;
    Button mPasscodeSixButton;
    Button mPasscodeSevenButton;
    Button mPasscodeEightButton;
    Button mPasscodeNineButton;
    Button mPasscodeZeroButton;
    Button mPasscodeDelButton;
    TextView mPasswordHeading;
    EditText mNumberOneEditText;
    EditText mNumberTwoEditText;
    EditText mNumberThreeEditText;
    EditText mNumberFourEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);

        String username = getIntent().getStringExtra("userEmail");

        mPasscodeOneButton = findViewById(R.id.button_passcode_one);
        mPasscodeTwoButton = findViewById(R.id.button_passcode_two);
        mPasscodeThreeButton = findViewById(R.id.button_passcode_three);
        mPasscodeFOurButton = findViewById(R.id.button_passcode_four);
        mPasscodeFiveButton = findViewById(R.id.button_passcode_five);
        mPasscodeSixButton = findViewById(R.id.button_passcode_six);
        mPasscodeSevenButton = findViewById(R.id.button_passcode_seven);
        mPasscodeEightButton = findViewById(R.id.button_passcode_eight);
        mPasscodeNineButton = findViewById(R.id.button_passcode_nine);
        mPasscodeZeroButton = findViewById(R.id.button_passcode_zero);
        mPasscodeDelButton = findViewById(R.id.button_passcode_del);

        mNumberOneEditText = findViewById(R.id.editText_passcode_number_one);
        mNumberTwoEditText = findViewById(R.id.editText_passcode_number_two);
        mNumberThreeEditText = findViewById(R.id.editText_passcode_number_three);
        mNumberFourEditText = findViewById(R.id.editText_passcode_number_four);

        mPasswordHeading = findViewById(R.id.textView_otp_heading);

        mPasscodeOneButton.setOnClickListener(this);
        mPasscodeTwoButton.setOnClickListener(this);
        mPasscodeThreeButton.setOnClickListener(this);
        mPasscodeFOurButton.setOnClickListener(this);
        mPasscodeFiveButton.setOnClickListener(this);
        mPasscodeSixButton.setOnClickListener(this);
        mPasscodeSevenButton.setOnClickListener(this);
        mPasscodeEightButton.setOnClickListener(this);
        mPasscodeNineButton.setOnClickListener(this);
        mPasscodeZeroButton.setOnClickListener(this);
        mPasscodeDelButton.setOnClickListener(this);

        mNumberOneEditText.requestFocus();
        mCurrentEditText = mNumberOneEditText;

        mNumberOneEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String inputText = s.toString();
                if(inputText.length()==1){
                    mFirstEditTextPassCode = inputText;
                    mCurrentEditText = mNumberTwoEditText;
                    mNumberTwoEditText.requestFocus();
                }
            }
        });

        mNumberTwoEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String inputText = s.toString();
                if(inputText.length()==1){
                    mSecondEditTextPassCode = inputText;
                    mCurrentEditText = mNumberThreeEditText;
                    mNumberThreeEditText.requestFocus();
                }
            }
        });

        mNumberThreeEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String inputText = s.toString();
                if(inputText.length()==1){
                    mThreeEditTextPassCode = inputText;
                    mCurrentEditText = mNumberFourEditText;
                    mNumberFourEditText.requestFocus();
                }
            }
        });

        mNumberFourEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String inputText = s.toString();
                if(inputText.length()==1){
                    mFourEditTextPassCode = inputText;
                    disableKeyPad();
                    new Handler().postDelayed(
                            () -> {
                                String passCode = mFirstEditTextPassCode + mSecondEditTextPassCode +
                                        mThreeEditTextPassCode + mFourEditTextPassCode;
                                doSecondaryUserLogin(username , passCode);
                                enableKeyPad();
                            }, 150
                    );
                }
            }
        });
    }

    private void disableKeyPad() {
        mPasscodeOneButton.setEnabled(false);
        mPasscodeTwoButton.setEnabled(false);
        mPasscodeThreeButton.setEnabled(false);
        mPasscodeFOurButton.setEnabled(false);
        mPasscodeFiveButton.setEnabled(false);
        mPasscodeSixButton.setEnabled(false);
        mPasscodeSevenButton.setEnabled(false);
        mPasscodeEightButton.setEnabled(false);
        mPasscodeNineButton.setEnabled(false);
        mPasscodeZeroButton.setEnabled(false);
        mPasscodeDelButton.setEnabled(false);
    }


    private void enableKeyPad(){
        mPasscodeOneButton.setEnabled(true);
        mPasscodeTwoButton.setEnabled(true);
        mPasscodeThreeButton.setEnabled(true);
        mPasscodeFOurButton.setEnabled(true);
        mPasscodeFiveButton.setEnabled(true);
        mPasscodeSixButton.setEnabled(true);
        mPasscodeSevenButton.setEnabled(true);
        mPasscodeEightButton.setEnabled(true);
        mPasscodeNineButton.setEnabled(true);
        mPasscodeZeroButton.setEnabled(true);
        mPasscodeDelButton.setEnabled(true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button_passcode_one:
                mCurrentEditText.setText(getString(R.string.passcode_kwypad_one));
                break;
            case R.id.button_passcode_two:
                mCurrentEditText.setText(getString(R.string.passcode_kwypad_two));
                break;
            case R.id.button_passcode_three:
                mCurrentEditText.setText(getString(R.string.passcode_kwypad_three));
                break;
            case R.id.button_passcode_four:
                mCurrentEditText.setText(getString(R.string.passcode_kwypad_four));
                break;
            case R.id.button_passcode_five:
                mCurrentEditText.setText(getString(R.string.passcode_kwypad_five));
                break;
            case R.id.button_passcode_six:
                mCurrentEditText.setText(getString(R.string.passcode_kwypad_six));
                break;
            case R.id.button_passcode_seven:
                mCurrentEditText.setText(getString(R.string.passcode_kwypad_seven));
                break;
            case R.id.button_passcode_eight:
                mCurrentEditText.setText(getString(R.string.passcode_kwypad_eight));
                break;
            case R.id.button_passcode_nine:
                mCurrentEditText.setText(getString(R.string.passcode_kwypad_nine));
                break;
            case R.id.button_passcode_zero:
                mCurrentEditText.setText(getString(R.string.passcode_kwypad_zero));
                break;
            case R.id.button_passcode_del:
                deletePassCode();
                break;
        }
    }

    private void deletePassCode(){
        if(mCurrentEditText == mNumberOneEditText){
            mCurrentEditText.setText("");
        }else if(mCurrentEditText == mNumberTwoEditText){
            mCurrentEditText.setText("");
            mCurrentEditText = mNumberOneEditText;
        }else if(mCurrentEditText == mNumberThreeEditText){
            mCurrentEditText.setText("");
            mCurrentEditText = mNumberTwoEditText;
        }else if(mCurrentEditText == mNumberFourEditText){
            mCurrentEditText.setText("");
            mCurrentEditText = mNumberThreeEditText;
        }
    }

    private void doSecondaryUserLogin(String userNameParams , String otpParams){
        final Progress progress = Progress.getInstance();
        progress.showProgress(OTPActivity.this);
        Call<SecondaryUserLoginResponse> mLoginResponseCall = RetrofitAPICaller.getInstance(OTPActivity.this)
                .getStellarJetAPIs().loginSecondaryUser(
                        userNameParams, otpParams);

        mLoginResponseCall.enqueue(new Callback<SecondaryUserLoginResponse>() {
            @Override
            public void onResponse(@NonNull Call<SecondaryUserLoginResponse> call, @NonNull Response<SecondaryUserLoginResponse> response) {
                progress.hideProgress();
                if(response.body()!=null){
                    Log.d("OTP", "onResponse: " +response.body());
                    String token = Objects.requireNonNull(response.body().getData()).getToken();
                    String refreshToken = response.body().getData().getRefresh_token();
                    SharedPreferencesHelper.saveUserToken(OTPActivity.this , token);
                    SharedPreferencesHelper.saveUserRefreshToken(OTPActivity.this , refreshToken);
//                    SharedPreferencesHelper.saveLoginStatus(OTPActivity.this , true);
                    SharedPreferencesHelper.setPrimaryUserSelectionStatus(OTPActivity.this , false);
                    selectDefaultPrimaryUser(Objects.requireNonNull(response.body().getData().getPrimary_users()).get(0).getId());
                }else {
                    UiUtils.Companion.showToast(
                            OTPActivity.this, "OTP Error"
                    );
                }
            }

            @Override
            public void onFailure(@NonNull Call<SecondaryUserLoginResponse> call, @NonNull Throwable t) {
                Log.d("OTP", "onResponse: " +t);
                progress.hideProgress();
                UiUtils.Companion.showServerErrorDialog(OTPActivity.this);
            }
        });
    }

    private void selectDefaultPrimaryUser(int userId){
        final Progress progress = Progress.getInstance();
        progress.showProgress(OTPActivity.this);
        Call<LoginResponse> mSwitchUserCall = RetrofitAPICaller.getInstance(OTPActivity.this)
                .getStellarJetAPIs().switchPrimaryUsers(
                        SharedPreferencesHelper.getUserToken(OTPActivity.this),
                        userId);

        mSwitchUserCall.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(@NonNull Call<LoginResponse> call, @NonNull Response<LoginResponse> response) {
                progress.hideProgress();
                if(response.body()!=null){
                    Log.d("PrimaryUsers", "onResponse: " + response.body());
                    HomeActivity.Companion.clearAllBookingData();
                    SharedPreferencesHelper.saveUserToken(OTPActivity.this , response.body().getData().getToken());
                    SharedPreferencesHelper.saveUserRefreshToken(OTPActivity.this , response.body().getData().getRefresh_token());
                    SharedPreferencesHelper.saveUserRefreshToken(OTPActivity.this , response.body().getData().getRefresh_token());
                    SharedPreferencesHelper.saveUserName(OTPActivity.this , response.body().getData().getUser_data().getName());
                    SharedPreferencesHelper.saveUserEmail(OTPActivity.this , response.body().getData().getUser_data().getEmail());
                    SharedPreferencesHelper.saveUserPhone(OTPActivity.this , response.body().getData().getUser_data().getPhone());
//                    SharedPreferencesHelper.saveLoginStatus(OTPActivity.this , true);
                    SharedPreferencesHelper.saveCurrentPrimaryUserId(OTPActivity.this , response.body().getData().getUser_data().getUser_id());
                    SharedPreferencesHelper.saveCurrentPrimaryUserName(OTPActivity.this , response.body().getData().getUser_data().getName());
                    Intent mPassCodeIntent = new Intent(
                            OTPActivity.this ,
                            PassCodeActivity.class
                    );
                    mPassCodeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    mPassCodeIntent.putExtra(UIConstants.BUNDLE_USER_DATA , response.body().getData().getUser_data());
                    startActivity(mPassCodeIntent);
                }else {
                    UiUtils.Companion.showToast(
                            OTPActivity.this, "Something went wrong"
                    );
                }
            }

            @Override
            public void onFailure(@NonNull Call<LoginResponse> call, @NonNull Throwable t) {
                progress.hideProgress();
                Log.d("PrimaryUsers", "onResponse: " + t);
                UiUtils.Companion.showServerErrorDialog(OTPActivity.this);
            }
        });

    }
}
