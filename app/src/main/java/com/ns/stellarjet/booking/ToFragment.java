package com.ns.stellarjet.booking;


import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.ns.networking.model.City;
import com.ns.networking.model.FlightScheduleData;
import com.ns.networking.model.FlightScheduleResponse;
import com.ns.networking.retrofit.RetrofitAPICaller;
import com.ns.stellarjet.R;
import com.ns.stellarjet.booking.adapter.PlaceSelectAdapter;
import com.ns.stellarjet.databinding.FragmentToBinding;
import com.ns.stellarjet.home.HomeActivity;
import com.ns.stellarjet.utils.Progress;
import com.ns.stellarjet.utils.SharedPreferencesHelper;
import com.ns.stellarjet.utils.StellarJetUtils;
import com.ns.stellarjet.utils.UiUtils;
import org.jetbrains.annotations.NotNull;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */
public class ToFragment extends Fragment implements PlaceSelectAdapter.onPlaceSelectClickListener {

    private List<ObjectAnimator> mEnterObjectAnimatorList = new ArrayList<>();
    private List<ObjectAnimator> mReverseEnterObjectAnimatorList = new ArrayList<>();
    private List<ObjectAnimator> mExitObjectAnimatorList = new ArrayList<>();

    public ToFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        FragmentToBinding dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_to, container, false);
        View mRootView = dataBinding.getRoot();

        List<City> mCitiesList = HomeActivity.sUserData.getCities();

        List<City> mDisplayCitiesList = new ArrayList<>();

        for (int i = 0; i < mCitiesList.size(); i++) {
            if(mCitiesList.get(i).getId() != SharedPreferencesHelper.getFromCityId(getActivity())){
                mDisplayCitiesList.add(mCitiesList.get(i));
            }
        }

        PlaceSelectAdapter mPlaceSelectAdapter = new PlaceSelectAdapter(
                this ,
                mDisplayCitiesList
        );

        LinearLayoutManager layoutManager = new LinearLayoutManager(
                getActivity() ,
                RecyclerView.VERTICAL,
                false
        );

        dataBinding.recyclerViewTo.setAdapter(mPlaceSelectAdapter);
        dataBinding.recyclerViewTo.setLayoutManager(layoutManager);

        runLayoutAnimation(dataBinding.recyclerViewTo);

        /*int childCount = dataBinding.layoutToBase.getChildCount();
        int minimumEnterAnimationTime = childCount * 150;
        int maximumExitAnimationTime = childCount * 150;
        for (int i = 0; i < childCount; i++) {
            Log.d("DrawerActivity", "onCreate: child objects " + dataBinding.layoutToBase.getChildAt(i));
            if (i != 0){
                minimumEnterAnimationTime = minimumEnterAnimationTime + 150 ;
                maximumExitAnimationTime = maximumExitAnimationTime - 150;
            }
            mEnterObjectAnimatorList.add(createEnterObjectAnimators(
                    StellarJetUtils.getScreenWidth(getActivity()) ,
                    dataBinding.layoutToBase.getChildAt(i) , minimumEnterAnimationTime) );

            mReverseEnterObjectAnimatorList.add(createReverseEnterObjectAnimators(
                    dataBinding.layoutToBase.getChildAt(i) , minimumEnterAnimationTime) );

            mExitObjectAnimatorList.add(createExitObjectAnimators(
                    dataBinding.layoutToBase.getChildAt(i) , maximumExitAnimationTime) );
        }

        if(!PlaceSelectionActivity.isToFragmentVisible){
            startEntryAnimation();
        }else {
            startReverseEntryAnimation();
        }*/

        return mRootView;
    }

    private void runLayoutAnimation(RecyclerView mRecyclerView) {
        final Context context = mRecyclerView.getContext();

        int animRes = R.anim.layout_animation_from_right;

        final LayoutAnimationController controller =
                AnimationUtils.loadLayoutAnimation(context, animRes);

        mRecyclerView.setLayoutAnimation(controller);
        mRecyclerView.getAdapter().notifyDataSetChanged();
        mRecyclerView.scheduleLayoutAnimation();
    }

    @Override
    public void onResume() {
        super.onResume();
        PlaceSelectionActivity.isFromFragmentVisible = false;
        PlaceSelectionActivity.isToFragmentVisible= true;
    }

    @Override
    public void onPlaceSelected(String placeName, int placeId) {
//        Toast.makeText(getActivity(), placeId + "=="+ placeName, Toast.LENGTH_SHORT).show();
        SharedPreferencesHelper.saveToCityId(getActivity() , placeId);
        SharedPreferencesHelper.saveToCity(getActivity() , placeName);
        if(StellarJetUtils.isConnectingToInternet(getActivity())){
            getFlightSchedules();
        }else{
            UiUtils.Companion.showNoInternetDialog(getActivity());
        }
    }

    private void getFlightSchedules(){
        final Progress progress = Progress.getInstance();
        progress.showProgress(getActivity());
        Call<FlightScheduleResponse> mFlightScheduleResponseCall = RetrofitAPICaller.getInstance(getActivity())
                .getStellarJetAPIs().getFlightSchedules(
                        SharedPreferencesHelper.getUserToken(getActivity()) ,
                        String.valueOf(SharedPreferencesHelper.getFromCityId(getActivity())) ,
                        String.valueOf(SharedPreferencesHelper.getToCityId(getActivity())) ,
                        "90"
                );

        mFlightScheduleResponseCall.enqueue(new Callback<FlightScheduleResponse>() {
            @Override
            public void onResponse(@NonNull Call<FlightScheduleResponse> call, @NonNull Response<FlightScheduleResponse> response) {
                progress.hideProgress();
                if(response.code()==200){
                    Log.d("Calendar", "onResponse: " + response.body());
                    List<FlightScheduleData> mFlightScheduleDataList = response.body().getData();
                    Intent mCalendarIntent = new Intent(
                            getActivity() ,
                            DateSelectionActivity.class
                    );
                    mCalendarIntent.putExtra("dates" , (ArrayList<? extends Parcelable>)  mFlightScheduleDataList);
                    Objects.requireNonNull(getActivity()).startActivity(mCalendarIntent);
                }else if(response.code() == 500){
                    UiUtils.Companion.showServerErrorDialog(Objects.requireNonNull(getActivity()));
                }
            }

            @Override
            public void onFailure(@NonNull Call<FlightScheduleResponse> call, @NonNull Throwable t) {
                progress.hideProgress();
                UiUtils.Companion.showServerErrorDialog(Objects.requireNonNull(getActivity()));
                Log.d("Calendar", "onFailure: " + t);
            }
        });
    }

    private ObjectAnimator createEnterObjectAnimators(int width , View mChildView , int timeMillis){
        ObjectAnimator mViewObjectAnimator = ObjectAnimator.ofFloat(
                mChildView,
                "translationX",
                width, 0.0f);
        mViewObjectAnimator.setDuration(timeMillis);

        return mViewObjectAnimator;
    }

    private ObjectAnimator createExitObjectAnimators(View mChildView , int timeMillis){
        int screenwidth = StellarJetUtils.getScreenWidth(getActivity())+100;
        ObjectAnimator mViewObjectAnimator = ObjectAnimator.ofFloat(
                mChildView,
                "translationX",
                0.0f, -screenwidth);
        mViewObjectAnimator.setDuration(timeMillis);

        return mViewObjectAnimator;
    }

    private ObjectAnimator createReverseEnterObjectAnimators( View mChildView , int timeMillis){
        int screenwidth = StellarJetUtils.getScreenWidth(getActivity())+100;
        ObjectAnimator mViewObjectAnimator = ObjectAnimator.ofFloat(
                mChildView,
                "translationX",
                -screenwidth , 0.0f);
        mViewObjectAnimator.setDuration(timeMillis);

        return mViewObjectAnimator;
    }

    private void startEntryAnimation(){
        for (int i = 0; i < mEnterObjectAnimatorList.size(); i++) {
            mEnterObjectAnimatorList.get(i).start();
        }
    }

    private void startReverseEntryAnimation(){
        for (int i = 0; i < mReverseEnterObjectAnimatorList.size(); i++) {
            mReverseEnterObjectAnimatorList.get(i).start();
        }
    }

    private void startExitAnimation(){
        for (int i = 0; i < mExitObjectAnimatorList.size(); i++) {
            mExitObjectAnimatorList.get(i).start();
        }
    }

}
