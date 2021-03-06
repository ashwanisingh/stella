package com.ns.stellarjet.personalize

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.ns.stellarjet.R
import com.ns.stellarjet.databinding.ActivityPersonalizeLaunchBinding
import com.ns.stellarjet.home.HomeActivity
import com.ns.stellarjet.utils.SharedPreferencesHelper
import com.ns.stellarjet.utils.UIConstants

class PersonalizeLaunchActivity : AppCompatActivity() {

    private lateinit var binding : ActivityPersonalizeLaunchBinding
    companion object {
        @JvmField var mPersonalizationSelectedFoodIds : MutableList<String> = ArrayList()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // obtain binding
        binding = DataBindingUtil.setContentView(
            this ,
            R.layout.activity_personalize_launch)

        SharedPreferencesHelper.saveCabDropPersonalize(this , "")
        SharedPreferencesHelper.saveCabPickupPersoalize(this , "")
        FoodPreferencesLaunchActivity.mCommonPreferenceFoodId.clear()
        FoodPreferencesLaunchActivity.mPersonalizationFoodId.clear()

        binding.textViewPersonalizeCabPreferences.setCompoundDrawablesWithIntrinsicBounds(
            0 ,0 ,R.drawable.ic_date_next , 0
        )

        val fromCity = intent?.extras?.getString(UIConstants.BUNDLE_FROM_CITY)!!
        val toCity = intent?.extras?.getString(UIConstants.BUNDLE_TO_CITY)!!
        binding.layoutPersonalizeCab.setOnClickListener {
            val mCabPreferencesIntent = Intent(
                this ,
                CabPreferencesActivity::class.java
            )
            mCabPreferencesIntent.putExtra("FlowFrom" , "personalize")
            mCabPreferencesIntent.putExtra(UIConstants.BUNDLE_FROM_CITY , fromCity)
            mCabPreferencesIntent.putExtra(UIConstants.BUNDLE_TO_CITY , toCity)
            startActivity(mCabPreferencesIntent)
        }

        val expiryTime = getString(R.string.personalize_update_title) + SharedPreferencesHelper.getPersonalizeTime(this)
        binding.textViewUpdatePreferences.text = expiryTime

        binding.layoutPersonalizeFood.setOnClickListener {
            val mFoodPreferencesIntent = Intent(
                this ,
                PersonalizeFoodLaunchActivity::class.java
            )
            mFoodPreferencesIntent.putExtra("FlowFrom" , "personalize")
            mFoodPreferencesIntent.putExtra("isPostBooking" , true)
            mFoodPreferencesIntent.putExtra("JourneyDate" ,
                SharedPreferencesHelper.getJourneyDate(this@PersonalizeLaunchActivity))
            mFoodPreferencesIntent.putExtra("ScheduleId" ,
                SharedPreferencesHelper.getScheduleId(this@PersonalizeLaunchActivity))
            startActivity(mFoodPreferencesIntent)
        }

        binding.buttonPersonalizeLater.setOnClickListener {
            launchHome()
        }

        binding.buttonPersonalizeHome.setOnClickListener {
            launchHome()
        }
    }

    override fun onBackPressed() {
//        super.onBackPressed()
//        launchHome()
    }

    override fun onResume() {
        super.onResume()

        Handler().postDelayed({
            if(SharedPreferencesHelper.getCabPersonalize(this) &&
                SharedPreferencesHelper.getFoodPersonalize(this)){
                val mPersonalizeSuccessIntent  =  Intent(
                    this ,
                    PersonalizeSuccessActivity::class.java
                )
                mPersonalizeSuccessIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(mPersonalizeSuccessIntent)
                finish()
                clearPersonalizedPreferences()
            }
        }, 2000)
        if(SharedPreferencesHelper.getCabPersonalize(this)){
            binding.textViewPersonalizeCabPreferences.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.ic_tick_ok ,
                0,0,0
            )
        }else{
            binding.textViewPersonalizeCabPreferences.setCompoundDrawablesWithIntrinsicBounds(
                0 ,0,0,0
            )
        }

        if(SharedPreferencesHelper.getFoodPersonalize(this)){
            binding.textViewPersonalizeFoodPreferences.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.ic_tick_ok ,
                0,0,0
            )
        }else{
            binding.textViewPersonalizeFoodPreferences.setCompoundDrawablesWithIntrinsicBounds(
                0 ,0,0,0
            )
        }
    }

    private fun launchHome(){
        val mHomeIntent = Intent(
            this ,
            HomeActivity::class.java
        )
        mHomeIntent.putExtra(UIConstants.BUNDLE_USER_DATA , HomeActivity.sUserData)
        startActivity(mHomeIntent)
        finish()
        clearPersonalizedPreferences()
        SharedPreferencesHelper.clearAllBookingData(this@PersonalizeLaunchActivity)
    }

    private fun clearPersonalizedPreferences(){
        SharedPreferencesHelper.saveCabDropPersonalize(this , "")
        SharedPreferencesHelper.saveCabPickupPersoalize(this , "")
        SharedPreferencesHelper.saveCabDropPersonalizeID(this , "")
        SharedPreferencesHelper.saveCabPickupPersoalizeID(this , "")
        SharedPreferencesHelper.saveBookingId(this , "")
        SharedPreferencesHelper.saveFoodPersonalize(this ,false)
    }

    override fun onDestroy() {
        super.onDestroy()
        mPersonalizationSelectedFoodIds.clear()
    }
}
