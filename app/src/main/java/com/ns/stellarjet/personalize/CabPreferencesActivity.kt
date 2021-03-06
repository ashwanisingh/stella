package com.ns.stellarjet.personalize

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.ns.networking.model.CabPersonalizeResponse
import com.ns.networking.retrofit.RetrofitAPICaller
import com.ns.stellarjet.R
import com.ns.stellarjet.databinding.ActivityCabPreferncesBinding
import com.ns.stellarjet.utils.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CabPreferencesActivity : AppCompatActivity() {

    private lateinit var binding : ActivityCabPreferncesBinding
    private lateinit var flow: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // obtain binding

        binding = DataBindingUtil.setContentView(
            this ,
            R.layout.activity_cab_prefernces
        )

        flow = intent?.extras?.getString("FlowFrom")!!
        val fromCity = intent?.extras?.getString(UIConstants.BUNDLE_FROM_CITY)
        val toCity = intent?.extras?.getString(UIConstants.BUNDLE_TO_CITY)

        val fromCityPickUp = intent?.extras?.getString("fromCityPickUp")
        val toCityPickUp = intent?.extras?.getString("toCityPickUp")

        var displayFromCity: String? = ""
        var displayToCity: String? = ""

        if(fromCityPickUp != null) {
            displayFromCity = resources.getString(R.string.cab_personalize_pickup_location) + " (" + fromCityPickUp + ")"
        } else {
            displayFromCity = resources.getString(R.string.cab_personalize_pickup_location) + " (" + fromCity + ")"
        }

        if(toCityPickUp != null) {
            displayToCity = resources.getString(R.string.cab_personalize_drop_location) + " ("+toCityPickUp+")"
        } else {
            displayToCity = resources.getString(R.string.cab_personalize_drop_location) + " ("+toCity+")"
        }

        binding.textViewCabPickupLocation.text =displayFromCity
        binding.textViewCabPickupLocation.alpha=1.0f
        binding.textViewCabDropLocation.text =displayToCity
        binding.textViewCabDropLocation.alpha =1.0f

        binding.editTextPickLocation.setOnClickListener {
            val pickUpIntent = Intent(
                this ,
                SavedAddressListActivity::class.java
            )
            pickUpIntent.putExtra(UIConstants.BUNDLE_CAB_TYPE , UIConstants.BUNDLE_CAB_TYPE_PICK)
            pickUpIntent.putExtra(UIConstants.BUNDLE_FROM_CITY , fromCity)
            startActivity(pickUpIntent)
        }

        binding.editTextDropLocation.setOnClickListener {
            val dropIntent = Intent(
                this ,
                SavedAddressListActivity::class.java
            )
            dropIntent.putExtra(UIConstants.BUNDLE_CAB_TYPE , UIConstants.BUNDLE_CAB_TYPE_DROP)
            dropIntent.putExtra(UIConstants.BUNDLE_TO_CITY , toCity)
            startActivity(dropIntent)
        }

        binding.buttonCabPreferencesBack.setOnClickListener {
            onBackPressed()
        }

        binding.buttonCabPreferencesUpdate.setOnClickListener {
            if(StellarJetUtils.isConnectingToInternet(applicationContext)){
                val pickUpId = SharedPreferencesHelper.getCabPickupPersonlalizeID(this)
                val dropId = SharedPreferencesHelper.getCabDropPersonalizeID(this)
                if(pickUpId.isEmpty() && dropId.isEmpty()){
                    UiUtils.showSimpleDialog(
                        this@CabPreferencesActivity,
                        "Please select either Pickup or Drop off location")
                }else{
                    updateCabPreferences(pickUpId , dropId)
                }
            }else{
                UiUtils.showNoInternetDialog(this@CabPreferencesActivity)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        setPersonalizedAddress()
    }

    private fun updateCabPreferences(pickUpId: String, dropId: String) {
        val progress = Progress.getInstance()
        progress.showProgress(this)
        val personalizeCab : Call<CabPersonalizeResponse> = RetrofitAPICaller.getInstance(this)
            .stellarJetAPIs.personalizeCab(
            SharedPreferencesHelper.getUserToken(this) ,
            SharedPreferencesHelper.getBookingId(this) ,
            pickUpId,
            dropId
        )

        personalizeCab.enqueue(object : Callback<CabPersonalizeResponse> {
            override fun onResponse(
                call: Call<CabPersonalizeResponse>,
                response: Response<CabPersonalizeResponse>){
                progress.hideProgress()
                if (response.body() != null && response.body()!!.resultcode == 1) {
                    Log.d("Booking", "onResponse: " + response.body())
                    SharedPreferencesHelper.saveCabPersonalize(
                        this@CabPreferencesActivity ,
                        true)
                    UiUtils.showToast(
                        this@CabPreferencesActivity ,
                        response.body()!!.message
                    )
                    if(flow.equals("drawer" , true)){
                        finish()
                    }else{
                        if(SharedPreferencesHelper.getFoodPersonalize(this@CabPreferencesActivity)){
                            val mPersonalizeSuccessIntent  =  Intent(
                                this@CabPreferencesActivity ,
                                PersonalizeSuccessActivity::class.java
                            )
                            mPersonalizeSuccessIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(mPersonalizeSuccessIntent)
                            finish()
                            clearPersonalizedPreferences()
                        }else{
                            finish()
                        }
                    }

                }
            }

            override fun onFailure(call: Call<CabPersonalizeResponse>, t: Throwable) {
                Log.d("Booking", "onResponse: $t")
                progress.hideProgress()
                UiUtils.showServerErrorDialog(
                    this@CabPreferencesActivity
                )
            }
        })
    }

    private fun setPersonalizedAddress(){
        val pickupCabPersonalize = SharedPreferencesHelper.getCabPickupPersonlalize(this)
        val dropCabPersonalize = SharedPreferencesHelper.getCabDropPersonalize(this)
        if(!pickupCabPersonalize.isEmpty()){
            binding.editTextPickLocation.text = pickupCabPersonalize
            binding.editTextPickLocation.alpha = 1.0f
        }
        if(!dropCabPersonalize.isEmpty()){
            binding.editTextDropLocation.text = dropCabPersonalize
            binding.editTextDropLocation.alpha = 1.0f
        }
    }

    private fun clearPersonalizedPreferences(){
        SharedPreferencesHelper.saveCabDropPersonalize(this , "")
        SharedPreferencesHelper.saveCabDropPersonalizeID(this , "")
        SharedPreferencesHelper.saveCabPickupPersoalizeID(this , "")
        SharedPreferencesHelper.saveCabPickupPersoalize(this , "")
        SharedPreferencesHelper.saveBookingId(this , "")
        SharedPreferencesHelper.saveFoodPersonalize(this ,false)
        SharedPreferencesHelper.saveCabPersonalize(this ,false)
    }
}
