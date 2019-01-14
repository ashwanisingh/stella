package com.ns.stellarjet.booking;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import com.ns.networking.model.BookingConfirmResponse;
import com.ns.networking.model.Contact;
import com.ns.networking.model.GuestConfirmResponse;
import com.ns.networking.model.LoginResponse;
import com.ns.networking.model.guestrequest.*;
import com.ns.networking.retrofit.RetrofitAPICaller;
import com.ns.stellarjet.R;
import com.ns.stellarjet.databinding.ActivityPassengerListBinding;
import com.ns.stellarjet.home.HomeActivity;
import com.ns.stellarjet.utils.Progress;
import com.ns.stellarjet.utils.SharedPreferencesHelper;
import com.ns.stellarjet.utils.StellarJetUtils;
import com.ns.stellarjet.utils.UIConstants;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PassengerListActivity extends AppCompatActivity {

    private ActivityPassengerListBinding activityPassengerBinding;
    private List<String> mGuestNamesList = new ArrayList<>();
    private List<Integer> mGuestList = new ArrayList<>();
    private List<AddGuestRequestData> mGuestRequestDataList  = new ArrayList<>();
    private GuestPrefsRequest guestPrefsRequest = new GuestPrefsRequest();
    private EditGuestPrefsRequest editGuestPrefsRequest = new EditGuestPrefsRequest();
    private AddGuestPrefsRequest addGuestPrefsRequest = new AddGuestPrefsRequest();
    private boolean isGuestEdited = false;
    private boolean isOnlySelfTravelling = true;
    private boolean isOnlyGuestsSelected = false;
    private boolean isOnlyNewGuestsAdded = false;
    private int numOfGuests;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityPassengerBinding = DataBindingUtil.setContentView(
                this,
                R.layout.activity_passenger_list);

        activityPassengerBinding.buttonPassengerBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // get the number of guests
        numOfGuests = Objects.requireNonNull(getIntent().getExtras()).getInt("numOfGuests");

        // change the self and guests name
        if(numOfGuests == 1){
            activityPassengerBinding.textViewPassengerSelf.setText(getResources().getString(R.string.info_passenger_self));
            activityPassengerBinding.textViewPassengerGuests.setText(getResources().getString(R.string.info_passenger_guest));
        }else if(numOfGuests > 1){
            activityPassengerBinding.textViewPassengerSelf.setText(getResources().getString(R.string.info_passenger_self_guests));
            activityPassengerBinding.textViewPassengerGuests.setText(getResources().getString(R.string.info_passenger_guests));
        }

        makeGuestList(numOfGuests);
        getGuestNames();
        makeEmptyListUI(numOfGuests);
        setTag();

        activityPassengerBinding.buttonConfirmBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processGuestDetails();
            }
        });

        activityPassengerBinding.textViewPassengerSelf.setOnClickListener(v -> {
            isOnlySelfTravelling  =true;
            setMeAndGuestInfo();
            activityPassengerBinding.textViewPassengerSelf.setBackground(getDrawable(R.drawable.drawable_passenger_select_bg));
            activityPassengerBinding.textViewPassengerSelf.setTextColor(
                    ContextCompat.getColor(PassengerListActivity.this , android.R.color.black)
            );
            activityPassengerBinding.textViewPassengerGuests.setBackground(getDrawable(R.drawable.drawable_passenger_select));
            activityPassengerBinding.textViewPassengerGuests.setTextColor(
                    ContextCompat.getColor(PassengerListActivity.this , R.color.colorPassengerText)
            );
        });

        activityPassengerBinding.textViewPassengerGuests.setOnClickListener(v -> {
            if(isOnlySelfTravelling){
                isOnlySelfTravelling  =false;
                setMeAndGuestInfo();
                activityPassengerBinding.textViewPassengerSelf.setBackground(getDrawable(R.drawable.drawable_passenger_select));
                activityPassengerBinding.textViewPassengerSelf.setTextColor(
                        ContextCompat.getColor(PassengerListActivity.this , R.color.colorPassengerText)
                );
                activityPassengerBinding.textViewPassengerGuests.setBackground(getDrawable(R.drawable.drawable_passenger_select_bg));
                activityPassengerBinding.textViewPassengerGuests.setTextColor(ContextCompat.getColor(PassengerListActivity.this ,
                        android.R.color.black));
            }
        });
    }

    private void processGuestDetails() {
        if(isAllDataEntered()){
            validateGuests();
        }else {
            Toast.makeText(this, "Passenger details needs to be filled", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isAllDataEntered(){
        boolean isAllDataEntered = false;
        for (int i = 0; i < mGuestRequestDataList.size(); i++) {
            if(mGuestRequestDataList.get(i).getGuestMobileNUmber().equalsIgnoreCase("")
                    ||
                    mGuestRequestDataList.get(i).getGuestName().equalsIgnoreCase("")){
                isAllDataEntered = false;
                break;
            }else {
                isAllDataEntered = true;
            }
        }
        return isAllDataEntered;
    }

    private void makeGuestAddList(List<AddGuestRequestData> mGuestRequestDataList){
        List<GuestPrefsDataRequest> editGuestPrefList = new ArrayList<>();
        List<AddGuestPrefsDataRequest> addGuestPrefList = new ArrayList<>();
        mGuestList.clear();
        for (int i = 0; i < mGuestRequestDataList.size(); i++) {
            if(isOnlySelfTravelling && i==0){
                continue;
            }else {
                if(mGuestRequestDataList.get(i).getGuestStatus().equalsIgnoreCase("edit")){
                    String id = mGuestRequestDataList.get(i).getGuestId();
                    String name = mGuestRequestDataList.get(i).getGuestName();
                    String mobileNumber = mGuestRequestDataList.get(i).getGuestMobileNUmber();
                    List<Integer> mFoodsList = new ArrayList<>();
                    mFoodsList.add(mGuestRequestDataList.get(i).getGuestFoodPreferences());
                    GuestPrefsDataRequest editGuestPrefsData = new GuestPrefsDataRequest();
                    editGuestPrefsData.setGuestId(Integer.valueOf(id));
                    editGuestPrefsData.setName(name);
                    editGuestPrefsData.setPhone(mobileNumber);
                    editGuestPrefsData.setmFoodPrefsList(mFoodsList);
                    editGuestPrefList.add(editGuestPrefsData);
                    mGuestList.add(Integer.valueOf(id));
                }else if(mGuestRequestDataList.get(i).getGuestStatus().equalsIgnoreCase("add")){
                    String name = mGuestRequestDataList.get(i).getGuestName();
                    String mobileNumber = mGuestRequestDataList.get(i).getGuestMobileNUmber();
                    List<Integer> mFoodsList = new ArrayList<>();
                    mFoodsList.add(mGuestRequestDataList.get(i).getGuestFoodPreferences());
                    AddGuestPrefsDataRequest addGuestPrefsData = new AddGuestPrefsDataRequest();
                    addGuestPrefsData.setName(name);
                    addGuestPrefsData.setPhone(mobileNumber);
                    addGuestPrefsData.setmFoodPrefsList(mFoodsList);
                    addGuestPrefList.add(addGuestPrefsData);
//                mGuestList.add(Integer.valueOf(id));
                }else if(mGuestRequestDataList.get(i).getGuestStatus().equalsIgnoreCase("")){
                    String id = mGuestRequestDataList.get(i).getGuestId();
                    if(!id.equalsIgnoreCase("")){
                        mGuestList.add(Integer.valueOf(id));
                    }
                }
            }

        }
        isGuestEdited = false;
        isOnlyNewGuestsAdded = false;
        isOnlyGuestsSelected = false;
        if(editGuestPrefList.size() >0 && addGuestPrefList.size()==0){
            isGuestEdited = true;
            editGuestPrefsRequest = new EditGuestPrefsRequest();
            editGuestPrefsRequest.setEditGuestPrefsRequestList(editGuestPrefList);
        }
        if(addGuestPrefList.size()>0 && editGuestPrefList.size() == 0){
            isOnlyNewGuestsAdded = true;
            addGuestPrefsRequest = new AddGuestPrefsRequest();
            addGuestPrefsRequest.setAddGuestPrefsRequestList(addGuestPrefList);
        }
        if(editGuestPrefList.size() == 0 && addGuestPrefList.size() ==0){
            isOnlyGuestsSelected = true;
        }
        guestPrefsRequest = new GuestPrefsRequest();
        guestPrefsRequest.setEditGuestPrefsRequestList(editGuestPrefList);
        guestPrefsRequest.setAddGuestPrefsRequestList(addGuestPrefList);

        Log.d("guest", "makeGuestAddList: " + guestPrefsRequest);

        if(StellarJetUtils.isConnectingToInternet(getApplicationContext())){
            if(isGuestEdited){
                confirmOnlyExistingGuests();
//                Toast.makeText(this, "confirmOnlyExistingGuests()", Toast.LENGTH_SHORT).show();
            }else if(isOnlyNewGuestsAdded){
                confirmGuests();
//                Toast.makeText(this, "confirmGuests()", Toast.LENGTH_SHORT).show();
            }else if(isOnlyGuestsSelected){
                bookFlight();
//                Toast.makeText(this, "bookFlight()", Toast.LENGTH_SHORT).show();
            }else if(isOnlySelfTravelling){
                bookFlight();
//                Toast.makeText(this, "bookFlight()", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(getApplicationContext(), "Not Connected to Internet", Toast.LENGTH_SHORT).show();
        }
    }

    private void validateGuests(){
        for (int i = 0; i < mGuestRequestDataList.size(); i++) {
            String name = mGuestRequestDataList.get(i).getGuestName();
            String mobileNumber = mGuestRequestDataList.get(i).getGuestMobileNUmber();
            if(!isMobileNumberPresent(mobileNumber)){
                mGuestRequestDataList.get(i).setGuestName(name);
                mGuestRequestDataList.get(i).setGuestMobileNUmber(mobileNumber);
                mGuestRequestDataList.get(i).setGuestStatus("add");
            }else if(isNamePresent(name) && isMobileNumberPresent(mobileNumber)){
                mGuestRequestDataList.get(i).setGuestName(name);
                mGuestRequestDataList.get(i).setGuestMobileNUmber(mobileNumber);
                mGuestRequestDataList.get(i).setGuestStatus("");
            }else if(!isNamePresent(name) && isMobileNumberPresent(mobileNumber)){
                mGuestRequestDataList.get(i).setGuestName(name);
                mGuestRequestDataList.get(i).setGuestMobileNUmber(mobileNumber);
                mGuestRequestDataList.get(i).setGuestStatus("edit");
            }
        }
        makeGuestAddList(mGuestRequestDataList);
    }

    private boolean isNamePresent(String name){
        List<Contact> items = HomeActivity.sUserData.getContacts();
        boolean result = false;
        for (int i = 0; i < items.size(); i++) {
            if(items.get(i).getName().equalsIgnoreCase(name)){
                result = true;
            }
        }
        return result;
    }

    private boolean isMobileNumberPresent(String mobileNumber){
        List<Contact> items = HomeActivity.sUserData.getContacts();
        boolean result = false;
        for (int i = 0; i < items.size(); i++) {
            if(items.get(i).getPhone().equalsIgnoreCase(mobileNumber)){
                result = true;
            }
        }
        return result;
    }



    private void makeGuestList(int numOfGuests) {
        for (int i = 0; i < numOfGuests; i++) {
            AddGuestRequestData mAddGuestRequestData = new AddGuestRequestData();
            mAddGuestRequestData.setGuestName("");
            mAddGuestRequestData.setGuestMobileNUmber("");
            mAddGuestRequestData.setGuestId("");
            mAddGuestRequestData.setGuestStatus("");
            mAddGuestRequestData.setGuestFoodPreferences(0);
            mGuestRequestDataList.add(mAddGuestRequestData);
        }
    }

    private void makeEmptyListUI(int numOfGuests){
        LayoutInflater linf;

        linf = (LayoutInflater) getApplicationContext().getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        linf = LayoutInflater.from(this);


        for (int i = 0; i < numOfGuests; i++) {
            final View v = linf.inflate(R.layout.layout_row_passengers_list, null);
            v.setTag("Passenger "+ i);
            activityPassengerBinding.layoutPassengerScroll.addView(v);
        }
    }

    private void setMeAndGuestInfo(){
        int childCount = activityPassengerBinding.layoutPassengerScroll.getChildCount();
        if(childCount>0) {
            EditText mMobileNumberEditText =
                    activityPassengerBinding.layoutPassengerScroll
                            .getChildAt(0)
                            .findViewById(R.id.editText_passenger_self_mobile_number);
            AutoCompleteTextView mNamesAutoCompleteTextView =
                    activityPassengerBinding.layoutPassengerScroll
                            .getChildAt(0)
                            .findViewById(R.id.autoComplete_passenger_self_name);
            if (isOnlySelfTravelling) {
                TextView passengerName = activityPassengerBinding.layoutPassengerScroll.getChildAt(0).findViewById(R.id.textView_passenger_self);
                passengerName.setText("Me");

                mNamesAutoCompleteTextView.setText(HomeActivity.sUserData.getName());
                mMobileNumberEditText.setText(HomeActivity.sUserData.getPhone());
                mNamesAutoCompleteTextView.setEnabled(false);
                mNamesAutoCompleteTextView.setAlpha(0.4f);
                mMobileNumberEditText.setEnabled(false);
                mMobileNumberEditText.setAlpha(0.4f);
            }else {
                TextView passengerName = activityPassengerBinding.layoutPassengerScroll.getChildAt(0).findViewById(R.id.textView_passenger_self);
                passengerName.setText("Passenger Name (1) ");
                mNamesAutoCompleteTextView.getText().clear();
                mMobileNumberEditText.getText().clear();
                mNamesAutoCompleteTextView.setEnabled(true);
                mNamesAutoCompleteTextView.setAlpha(1.0f);
                mMobileNumberEditText.setEnabled(true);
                mMobileNumberEditText.setAlpha(1.0f);
                hideNameErrorField(mNamesAutoCompleteTextView);
                hideErrorField(mMobileNumberEditText);
            }
        }
    }

    private void setTag(){
        Context mContext = PassengerListActivity.this;

        ArrayAdapter<String> mPassengerAdapter = new ArrayAdapter<String>(
                mContext,
                android.R.layout.select_dialog_item ,
                mGuestNamesList
        );

        int childCount = activityPassengerBinding.layoutPassengerScroll.getChildCount();
        for (int i = 0; i < childCount; i++) {
            TextView passengerName = activityPassengerBinding.layoutPassengerScroll.getChildAt(i).findViewById(R.id.textView_passenger_self);
            passengerName.setText("Passenger Name ("+(i+1)+")");
            EditText mMobileNumberEditText =
                    activityPassengerBinding.layoutPassengerScroll
                            .getChildAt(i)
                            .findViewById(R.id.editText_passenger_self_mobile_number);
            AutoCompleteTextView mNamesAutoCompleteTextView =
                    activityPassengerBinding.layoutPassengerScroll
                            .getChildAt(i)
                            .findViewById(R.id.autoComplete_passenger_self_name);
            mNamesAutoCompleteTextView.setThreshold(1);
            mNamesAutoCompleteTextView.setAdapter(mPassengerAdapter);
            int finalI = i;
            mNamesAutoCompleteTextView.setOnItemClickListener((parent, view, namePosition, id) -> {
                String selectedName = mNamesAutoCompleteTextView.getAdapter().getItem(namePosition).toString();
                String mobileNumber = makeFilledData(selectedName , finalI);
                if(mobileNumber.isEmpty()){
                    mNamesAutoCompleteTextView.setText("");
                    mMobileNumberEditText.setText("");
                }else {
                    mMobileNumberEditText.setText(mobileNumber);
                }
            });
            mNamesAutoCompleteTextView.setImeOptions(EditorInfo.IME_ACTION_NEXT);
            if(finalI == (numOfGuests-1)){
                mMobileNumberEditText.setImeOptions(EditorInfo.IME_ACTION_DONE);
            }else {
                mMobileNumberEditText.setImeOptions(EditorInfo.IME_ACTION_NEXT);
            }

            mNamesAutoCompleteTextView.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if(s.length()>=3){
                        hideNameErrorField(mNamesAutoCompleteTextView);
                        mGuestRequestDataList.get(finalI).setGuestName(mNamesAutoCompleteTextView.getText().toString());
                    }else {
                        showNameErrorField(mNamesAutoCompleteTextView);
                        mGuestRequestDataList.get(finalI).setGuestName("");
                    }
                }
            });
            mMobileNumberEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if(s.length() == 10){
                        hideErrorField(mMobileNumberEditText);
                        if(!isPhoneNumberPresent(s.toString())){
                            mGuestRequestDataList.get(finalI).setGuestName(mNamesAutoCompleteTextView.getText().toString());
                            mGuestRequestDataList.get(finalI).setGuestMobileNUmber(mMobileNumberEditText.getText().toString());

                        }else {
                            Toast.makeText(mContext, "Passenger already selected", Toast.LENGTH_SHORT).show();
                            mNamesAutoCompleteTextView.getText().clear();
                            mMobileNumberEditText.getText().clear();
                        }
                    }else{
                        mGuestRequestDataList.get(finalI).setGuestName("");
                        mGuestRequestDataList.get(finalI).setGuestMobileNUmber("");
                        showErrorField(mMobileNumberEditText);
                    }
                }
            });
        }
        if(childCount>0) {
            EditText mMobileNumberEditText =
                    activityPassengerBinding.layoutPassengerScroll
                            .getChildAt(0)
                            .findViewById(R.id.editText_passenger_self_mobile_number);
            AutoCompleteTextView mNamesAutoCompleteTextView =
                    activityPassengerBinding.layoutPassengerScroll
                            .getChildAt(0)
                            .findViewById(R.id.autoComplete_passenger_self_name);
            if (isOnlySelfTravelling) {
                TextView passengerName = activityPassengerBinding.layoutPassengerScroll.getChildAt(0).findViewById(R.id.textView_passenger_self);
                passengerName.setText("Me");

                mNamesAutoCompleteTextView.setText(HomeActivity.sUserData.getName());
                mMobileNumberEditText.setText(HomeActivity.sUserData.getPhone());
                mNamesAutoCompleteTextView.setEnabled(false);
                mNamesAutoCompleteTextView.setAlpha(0.4f);
                mMobileNumberEditText.setEnabled(false);
                mMobileNumberEditText.setAlpha(0.4f);
            }else{
                TextView passengerName = activityPassengerBinding.layoutPassengerScroll.getChildAt(0).findViewById(R.id.textView_passenger_self);
                passengerName.setText("Passenger Name");
            }
        }
    }
    private void showErrorField(EditText mSampleEditText){
        mSampleEditText.setTextColor(getResources().getColor(android.R.color.white));
        mSampleEditText.setBackground(getDrawable(R.drawable.drawable_edittext_error_background));
    }

    private void showNameErrorField(AutoCompleteTextView mAutoCompleteTextView){
        mAutoCompleteTextView.setTextColor(getResources().getColor(android.R.color.white));
        mAutoCompleteTextView.setBackground(getDrawable(R.drawable.drawable_edittext_error_background));
    }

    private void hideErrorField(EditText mSampleEditText){
        mSampleEditText.setTextColor(getResources().getColor(android.R.color.white));
        mSampleEditText.setBackground(getDrawable(R.drawable.drawable_edittext_background));
    }

    private void hideNameErrorField(AutoCompleteTextView mAutoCompleteTextView){
        mAutoCompleteTextView.setTextColor(getResources().getColor(android.R.color.white));
        mAutoCompleteTextView.setBackground(getDrawable(R.drawable.drawable_edittext_background));
    }

    private boolean isPhoneNumberPresent(String number){
        boolean isPresent = false;
        for (int i = 0; i < mGuestRequestDataList.size(); i++) {
            if(number.equalsIgnoreCase(mGuestRequestDataList.get(i).getGuestMobileNUmber())){
                isPresent = true;
            }
        }
        return isPresent;
    }

    private void getGuestNames(){
        List<Contact> items = HomeActivity.sUserData.getContacts();
        mGuestNamesList = new ArrayList<>();
        for (int i = 0; i < items.size(); i++) {
            mGuestNamesList.add(items.get(i).getName());
        }
    }

    private String makeFilledData(String name , int passengerPosition){
        List<Contact> items = HomeActivity.sUserData.getContacts();
        String mobileNumber = "";
        for (int i = 0; i < items.size(); i++) {
            if(items.get(i).getName().equalsIgnoreCase(name)){
                mobileNumber = items.get(i).getPhone();
                mGuestRequestDataList.get(passengerPosition).setGuestId(String.valueOf(items.get(i).getId()));
            }
        }
        return mobileNumber;
    }

    private void confirmGuests(){

        addGuestPrefsRequest.setUserId(SharedPreferencesHelper.getUserId(PassengerListActivity.this));
        addGuestPrefsRequest.setToken(SharedPreferencesHelper.getUserToken(PassengerListActivity.this));

        final Progress progress = Progress.getInstance();
        progress.showProgress(PassengerListActivity.this);

        Call<GuestConfirmResponse> guestConfirmResponseCall =
                RetrofitAPICaller.getInstance(PassengerListActivity.this)
                        .getStellarJetAPIs().bookNewGuestInfo(
                        addGuestPrefsRequest
                );

        guestConfirmResponseCall.enqueue(new Callback<GuestConfirmResponse>() {
            @Override
            public void onResponse(Call<GuestConfirmResponse> call, Response<GuestConfirmResponse> response) {
                progress.hideProgress();
                Log.d("Booking", "onResponse: " + response);
                if(response.body().getResultcode() ==1){
                    List<Integer> mGuestResponseIds = new ArrayList<>();
                    for (int i = 0; i < response.body().getData().getNew_contacts().size(); i++) {
                        mGuestResponseIds.add(response.body().getData().getNew_contacts().get(i).getGuest_id());
                    }
                    mGuestList.addAll(mGuestResponseIds);
                    bookFlight();

                }
            }

            @Override
            public void onFailure(Call<GuestConfirmResponse> call, Throwable t) {
                progress.hideProgress();
                Log.d("Booking", "onResponse: " + t);
                Toast.makeText(PassengerListActivity.this, "Server Error Occurred", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void confirmOnlyExistingGuests(){

        editGuestPrefsRequest.setToken(SharedPreferencesHelper.getUserToken(PassengerListActivity.this));
        editGuestPrefsRequest.setUserId(SharedPreferencesHelper.getUserId(PassengerListActivity.this));
        final Progress progress = Progress.getInstance();
        progress.showProgress(PassengerListActivity.this);
        Call<GuestConfirmResponse> guestConfirmResponseCall =
                RetrofitAPICaller.getInstance(PassengerListActivity.this)
                        .getStellarJetAPIs().bookExistingGuestInfo(
                        editGuestPrefsRequest
                );

        guestConfirmResponseCall.enqueue(new Callback<GuestConfirmResponse>() {
            @Override
            public void onResponse(Call<GuestConfirmResponse> call, Response<GuestConfirmResponse> response) {
                progress.hideProgress();
                Log.d("Booking", "onResponse: " + response);
                if (response.body() != null && response.body().getResultcode() == 1) {
                    List<Integer> mGuestResponseIds = new ArrayList<>();
                    for (int i = 0; i < response.body().getData().getNew_contacts().size(); i++) {
                        mGuestResponseIds.add(response.body().getData().getNew_contacts().get(i).getGuest_id());
                    }
                    mGuestList.addAll(mGuestResponseIds);
                    bookFlight();
                }
            }

            @Override
            public void onFailure(Call<GuestConfirmResponse> call, Throwable t) {
                progress.hideProgress();
                Log.d("Booking", "onResponse: " + t);
                Toast.makeText(PassengerListActivity.this, "Server Error Occurred", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void bookFlight(){
        int selfTravelling;
        if(isOnlySelfTravelling){
            selfTravelling = 1;
        }else {
            selfTravelling = 0;
        }
        final Progress progress = Progress.getInstance();
        progress.showProgress(PassengerListActivity.this);
        Call<BookingConfirmResponse> mBookingConfirmResponseCall =  RetrofitAPICaller.getInstance(PassengerListActivity.this).getStellarJetAPIs()
                .confirmFlightBooking(
                        SharedPreferencesHelper.getUserToken(PassengerListActivity.this) ,
                        SharedPreferencesHelper.getUserId(PassengerListActivity.this) ,
                        SharedPreferencesHelper.getFromCityId(PassengerListActivity.this) ,
                        SharedPreferencesHelper.getToCityId(PassengerListActivity.this) ,
                        SharedPreferencesHelper.getJourneyDate(PassengerListActivity.this) ,
                        SharedPreferencesHelper.getJourneyTime(PassengerListActivity.this) ,
                        SharedPreferencesHelper.getArrivalTime(PassengerListActivity.this) ,
                        SharedPreferencesHelper.getFlightId(PassengerListActivity.this) ,
                        HomeActivity.mSeatNamesId ,
                        mGuestList ,
                        selfTravelling
                );

        mBookingConfirmResponseCall.enqueue(new Callback<BookingConfirmResponse>() {
            @Override
            public void onResponse(@NotNull Call<BookingConfirmResponse> call, @NotNull Response<BookingConfirmResponse> response) {
                progress.hideProgress();
                if (response.body() != null && response.body().getResultcode() == 1) {
                    Log.d("Booking", "onResponse: " + response.body());
                    SharedPreferencesHelper.saveBookingId(
                            PassengerListActivity.this ,
                            String.valueOf(response.body().getData().getBooking_id()));
                    getUserData();
                    Intent mIntent = new Intent(
                            PassengerListActivity.this ,
                            BookingConfirmedActivity.class
                    );
                    mIntent.putExtra(UIConstants.BUNDLE_FROM_CITY , SharedPreferencesHelper.getFromCity(PassengerListActivity.this));
                    mIntent.putExtra(UIConstants.BUNDLE_TO_CITY , SharedPreferencesHelper.getToCity(PassengerListActivity.this));
                    SharedPreferencesHelper.savePersonalizeTime(
                            PassengerListActivity.this,
                            StellarJetUtils.getPersonalizationHours(
                                    SharedPreferencesHelper.getJourneyTimeImMillis(PassengerListActivity.this)));
                    mIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(mIntent);
                    clearAllBooinngData();
                }
            }

            @Override
            public void onFailure(@NotNull Call<BookingConfirmResponse> call, @NotNull Throwable t) {
                progress.hideProgress();
                Log.d("Booking", "onResponse: " + t);
                Toast.makeText(PassengerListActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getUserData(){
        Call<LoginResponse> mCustomerDataResponseCall = RetrofitAPICaller.getInstance(PassengerListActivity.this)
                .getStellarJetAPIs().getCustomerData(
                        SharedPreferencesHelper.getUserToken(PassengerListActivity.this),
                        SharedPreferencesHelper.getUserId(PassengerListActivity.this)
                );

        mCustomerDataResponseCall.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if(response.body()!=null){
                    HomeActivity.sUserData = response.body().getData().getUser_data();
                }else {
                    try {
                        JSONObject mJsonObject  = new JSONObject(response.errorBody().string());
                        String errorMessage = mJsonObject.getString("message");
                        if(errorMessage.equalsIgnoreCase(UIConstants.USER_TOKEN_EXPIRY)){
//                            getNewToken();
                            Log.d("Splash", "onResponse: Expiry");
                        }else {
                            Toast.makeText(PassengerListActivity.this , errorMessage , Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                    }
                }

            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Log.d("splash", "onFailure: " + t);
            }
        });
    }

    private void clearAllBooinngData(){
        SharedPreferencesHelper.saveFlightId(PassengerListActivity.this , 0);
        SharedPreferencesHelper.saveArrivalTime(PassengerListActivity.this , "");
        SharedPreferencesHelper.saveFromCityId(PassengerListActivity.this , 0);
        SharedPreferencesHelper.saveToCityId(PassengerListActivity.this , 0);
        SharedPreferencesHelper.saveToCity(PassengerListActivity.this , "");
        SharedPreferencesHelper.saveFromCity(PassengerListActivity.this , "");
        SharedPreferencesHelper.saveJourneyTimeImMillis(PassengerListActivity.this , 0);
        SharedPreferencesHelper.saveJourneyTime(PassengerListActivity.this , "");
        SharedPreferencesHelper.saveJourneyDate(PassengerListActivity.this , "");
        SharedPreferencesHelper.saveArrivalTime(PassengerListActivity.this , "");
    }
}
