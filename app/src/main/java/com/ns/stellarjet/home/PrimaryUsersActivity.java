package com.ns.stellarjet.home;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.ns.networking.model.LoginResponse;
import com.ns.networking.model.PrimaryUser;
import com.ns.networking.retrofit.RetrofitAPICaller;
import com.ns.stellarjet.PassCodeActivity;
import com.ns.stellarjet.R;
import com.ns.stellarjet.home.adapter.PrimaryUsersAdapter;
import com.ns.stellarjet.utils.Progress;
import com.ns.stellarjet.utils.SharedPreferencesHelper;
import com.ns.stellarjet.utils.UIConstants;
import com.ns.stellarjet.utils.UiUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PrimaryUsersActivity extends AppCompatActivity implements PrimaryUsersAdapter.onPrimaryUsersSelectClickListener {

    RecyclerView mPrimaryUsersRecyclerView;
    Button mBackButton;

    private List<PrimaryUser> mSecondaryUserData = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_primary_users);

        ButterKnife.bind(PrimaryUsersActivity.this);

        mPrimaryUsersRecyclerView = findViewById(R.id.recyclerView_booking_for);
        mBackButton = findViewById(R.id.button_primary_users_back);

        if(SharedPreferencesHelper.getCurrentPrimaryUserId(PrimaryUsersActivity.this) == 0){
                mBackButton.setVisibility(View.GONE);
        }

        mBackButton.setOnClickListener(v -> onBackPressed());

        try {
            mSecondaryUserData = Objects.requireNonNull(getIntent().getExtras()).getParcelableArrayList(UIConstants.BUNDLE_SECONDARY_USER_DATA);
        }catch (Exception e){
            Log.d("PrimaryUsers", "onCreate: " + e);
        }
        setPrimaryUsersRecyclerView();
    }

    private void setPrimaryUsersRecyclerView(){
        List<PrimaryUser> primary_users = mSecondaryUserData;

        PrimaryUsersAdapter mPrimaryUsersAdapter = new PrimaryUsersAdapter(
                this , primary_users
        );

        LinearLayoutManager layoutManager = new LinearLayoutManager(
                PrimaryUsersActivity.this ,
                RecyclerView.VERTICAL ,
                false
        );

        mPrimaryUsersRecyclerView.setAdapter(mPrimaryUsersAdapter);
        mPrimaryUsersRecyclerView.setLayoutManager(layoutManager);


    }

    @Override
    public void onPlaceSelected(String placeName, int placeId) {
        final Progress progress = Progress.getInstance();
        progress.showProgress(PrimaryUsersActivity.this);
        Call<LoginResponse> mSwitchUserCall = RetrofitAPICaller.getInstance(PrimaryUsersActivity.this)
                .getStellarJetAPIs().switchPrimaryUsers(
                        SharedPreferencesHelper.getUserToken(PrimaryUsersActivity.this),
                        placeId);

        mSwitchUserCall.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(@NonNull Call<LoginResponse> call, @NonNull Response<LoginResponse> response) {
                progress.hideProgress();
                if(response.body()!=null){
                    Log.d("PrimaryUsers", "onResponse: " + response.body());
                    HomeActivity.Companion.clearAllBookingData();
                    SharedPreferencesHelper.saveUserToken(PrimaryUsersActivity.this , response.body().getData().getToken());
                    SharedPreferencesHelper.saveUserRefreshToken(PrimaryUsersActivity.this , response.body().getData().getRefresh_token());
                    SharedPreferencesHelper.saveUserRefreshToken(PrimaryUsersActivity.this , response.body().getData().getRefresh_token());
                    SharedPreferencesHelper.saveUserName(PrimaryUsersActivity.this , response.body().getData().getUser_data().getName());
                    SharedPreferencesHelper.saveUserEmail(PrimaryUsersActivity.this , response.body().getData().getUser_data().getEmail());
                    SharedPreferencesHelper.saveUserPhone(PrimaryUsersActivity.this , response.body().getData().getUser_data().getPhone());
                    SharedPreferencesHelper.saveLoginStatus(PrimaryUsersActivity.this , true);
                    SharedPreferencesHelper.saveCurrentPrimaryUserId(PrimaryUsersActivity.this , placeId);
                    SharedPreferencesHelper.saveCurrentPrimaryUserName(PrimaryUsersActivity.this , placeName);
                    SharedPreferencesHelper.setPrimaryUserSelectionStatus(PrimaryUsersActivity.this , true);
                    SharedPreferencesHelper.saveSeatCount(PrimaryUsersActivity.this ,
                            response.body().getData().getUser_data().getCustomer_prefs().getSeats_available());
                    Intent mHomeIntent = new Intent(
                            PrimaryUsersActivity.this ,
                            HomeActivity.class
                    );
                    mHomeIntent.putExtra(UIConstants.BUNDLE_USER_DATA , response.body().getData().getUser_data());
                    mHomeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(mHomeIntent);
                }else {
                    UiUtils.Companion.showSimpleDialog(
                            PrimaryUsersActivity.this,
                            "Something went wrong"
                    );
                }
            }

            @Override
            public void onFailure(@NonNull Call<LoginResponse> call, @NonNull Throwable t) {
                progress.hideProgress();
                Log.d("PrimaryUsers", "onResponse: " + t);
                UiUtils.Companion.showSimpleDialog(
                        PrimaryUsersActivity.this,
                        getResources().getString(R.string.error_server)
                );
            }
        });

    }
}
