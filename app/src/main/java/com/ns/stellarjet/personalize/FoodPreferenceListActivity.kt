package com.ns.stellarjet.personalize

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ns.networking.model.CommonPersonalizeFoodResponse
import com.ns.networking.model.Food
import com.ns.networking.model.FoodPersonalizeResponse
import com.ns.networking.retrofit.RetrofitAPICaller
import com.ns.stellarjet.R
import com.ns.stellarjet.databinding.ActivityFoodPreferenceListBinding
import com.ns.stellarjet.home.HomeActivity
import com.ns.stellarjet.personalize.adapter.FoodListAdapter
import com.ns.stellarjet.utils.Progress
import com.ns.stellarjet.utils.SharedPreferencesHelper
import com.ns.stellarjet.utils.StellarJetUtils
import com.ns.stellarjet.utils.UIConstants
import kotlinx.android.synthetic.main.activity_food_preference_list.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FoodPreferenceListActivity : AppCompatActivity(), (String) -> Unit {

    //    private var mSelectedFoodIds = ""
    private val mSelectedFoodIds : MutableList<Int> = ArrayList()
    private lateinit var flow: String
    private var isPersonalizeDrawer: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val activityBinding: ActivityFoodPreferenceListBinding =  DataBindingUtil.setContentView(
            this ,
            R.layout.activity_food_preference_list
        )

        val foodType : String = intent.extras?.getString(UIConstants.BUNDLE_FOOD_TYPE)!!
        flow = intent?.extras?.getString("FlowFrom")!!
        isPersonalizeDrawer = intent?.extras?.getBoolean("personalizeDrawer")!!
        activityBinding.textViewFoodListName.text = foodType


        val foodListAdapter = FoodListAdapter(
            makeFoodListByCategory(foodType) ,
            this
        )

        val layoutManager = LinearLayoutManager(
            this ,
            RecyclerView.VERTICAL ,
            false
        )
        activityBinding.recyclerViewFoodList.adapter = foodListAdapter
        activityBinding.recyclerViewFoodList.layoutManager= layoutManager

        button_food_list_back.setOnClickListener {
            onBackPressed()
        }

        button_food_list_confirm.setOnClickListener {

            Log.d("Booking", "onResponse:")
            if(StellarJetUtils.isConnectingToInternet(applicationContext)){
                if(flow.equals("drawer" , true)){
                    updateCommonPersonnalizeFood()
                }else{
                    personalizeFood()
                }
            }else{
                Toast.makeText(applicationContext, "Not Connected to Internet", Toast.LENGTH_SHORT).show()
            }

        }
    }

    private fun updateCommonPersonnalizeFood(){
        val personalizeFood : Call<CommonPersonalizeFoodResponse> = RetrofitAPICaller.getInstance(this)
            .stellarJetAPIs.updateCommonFoodPreferences(
            SharedPreferencesHelper.getUserToken(this) ,
            SharedPreferencesHelper.getUserId(this) ,
            mSelectedFoodIds
        )

        personalizeFood.enqueue(object : Callback<CommonPersonalizeFoodResponse> {
            override fun onResponse(
                call: Call<CommonPersonalizeFoodResponse>,
                response: Response<CommonPersonalizeFoodResponse>){
                Log.d("Booking", "onResponse: $response")
                finish()
            }

            override fun onFailure(call: Call<CommonPersonalizeFoodResponse>, t: Throwable) {
                Log.d("Booking", "onResponse: $t")
                Toast.makeText(this@FoodPreferenceListActivity , "Server Error" , Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun personalizeFood(){
        val progress = Progress.getInstance()
        progress.showProgress(this)
        val personalizeFood : Call<FoodPersonalizeResponse> = RetrofitAPICaller.getInstance(this)
            .stellarJetAPIs.personalizeFood(
            SharedPreferencesHelper.getUserToken(this) ,
            SharedPreferencesHelper.getBookingId(this) ,
            mSelectedFoodIds
        )

        personalizeFood.enqueue(object : Callback<FoodPersonalizeResponse> {
            override fun onResponse(
                call: Call<FoodPersonalizeResponse>,
                response: Response<FoodPersonalizeResponse>){
                progress.hideProgress()
                Log.d("Booking", "onResponse: $response")
                SharedPreferencesHelper.saveFoodPersonalize(
                    this@FoodPreferenceListActivity ,
                    true
                )
                if(isPersonalizeDrawer){
                    finish()
                }else{
                    if(SharedPreferencesHelper.getCabPersonalize(this@FoodPreferenceListActivity)){
                        val mPersonalizeSuccessIntent  =  Intent(
                            this@FoodPreferenceListActivity ,
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

            override fun onFailure(call: Call<FoodPersonalizeResponse>, t: Throwable) {
                progress.hideProgress()
                Log.d("Booking", "onResponse: $t")
                Toast.makeText(this@FoodPreferenceListActivity , "Server Error" , Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun makeFoodListByCategory(foodType : String) :  MutableList<Food>{
        val mFoodsList =  HomeActivity.sUserData.customer_prefs.foods

        val mFoodsDisplayList : MutableList<Food> = ArrayList()

        mFoodsList.forEach {
            if(it.food_type_text.equals(foodType , false)){
                mFoodsDisplayList.add(it)
            }
        }

        return mFoodsDisplayList
    }

    override fun invoke(selectedID: String) {
        mSelectedFoodIds.add(selectedID.toInt())
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



