package com.ns.stellarjet.drawer

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.ns.networking.model.secondaryusers.AddSecondaryUserResponse
import com.ns.networking.model.secondaryusers.SecondaryUserInfoList
import com.ns.networking.retrofit.RetrofitAPICaller
import com.ns.stellarjet.R
import com.ns.stellarjet.databinding.ActivityPreferenceManagerInfoBinding
import com.ns.stellarjet.utils.Progress
import com.ns.stellarjet.utils.SharedPreferencesHelper
import com.ns.stellarjet.utils.StellarJetUtils
import com.ns.stellarjet.utils.UiUtils
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class PreferenceManagerInfoActivity : AppCompatActivity() {

    private lateinit var mSecondaryUserBinding:ActivityPreferenceManagerInfoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preference_manager_info)

        mSecondaryUserBinding = DataBindingUtil.setContentView(
            this ,
            R.layout.activity_preference_manager_info
        )

        val isAddUser = intent?.extras?.getBoolean("isAddSecondaryUser")
        val mSecondaryUserInfoList = intent?.extras?.getParcelable<SecondaryUserInfoList>("SecondaryUserInfo")

        if(isAddUser!!){
            mSecondaryUserBinding.buttonManagerInfoEdit.visibility = View.GONE
            mSecondaryUserBinding.textViewManagersInfoName.visibility = View.GONE
            mSecondaryUserBinding.textViewManagersInfoMobile.visibility = View.GONE
            mSecondaryUserBinding.textViewManagersInfoEmail.visibility = View.GONE
        }else{
            mSecondaryUserBinding.buttonManagerInfoEdit.visibility = View.GONE
            mSecondaryUserBinding.textViewManagerInfoTitle.text = resources.getString(R.string.preferences_manager_info_title)
            mSecondaryUserBinding.textViewManagerInfoSubHeading.visibility = View.GONE
            mSecondaryUserBinding.buttonManagersInfoConfirm.visibility = View.GONE
            mSecondaryUserBinding.editTextManagersInfoEmail.visibility= View.GONE
            mSecondaryUserBinding.editTextManagersInfoMobile.visibility= View.GONE
            mSecondaryUserBinding.editTextManagersInfoName.visibility= View.GONE
            mSecondaryUserBinding.textViewManagersInfoName.text = mSecondaryUserInfoList?.su_name
            mSecondaryUserBinding.textViewManagersInfoMobile.text = mSecondaryUserInfoList?.su_phone
            mSecondaryUserBinding.textViewManagersInfoEmail.text = mSecondaryUserInfoList?.su_email

        }
        mSecondaryUserBinding.buttonManagerInfoBack.setOnClickListener {
            onBackPressed()
        }

        mSecondaryUserBinding.buttonManagersInfoConfirm.setOnClickListener {
            val name = mSecondaryUserBinding.editTextManagersInfoName.text.toString()
            val email = mSecondaryUserBinding.editTextManagersInfoEmail.text.toString()
            val phone = mSecondaryUserBinding.editTextManagersInfoMobile.text.toString()

            if(name.isNotEmpty() && email.isNotEmpty() && phone.isNotEmpty()){

                if(StellarJetUtils.isConnectingToInternet(applicationContext)){
                    addSecondaryUser(name , email , phone)
                }else{
                    UiUtils.showNoInternetDialog(this@PreferenceManagerInfoActivity)
                }

            }else{
                UiUtils.showSimpleDialog(
                    this@PreferenceManagerInfoActivity ,
                    "All Fields are mandatory"
                )
            }
        }


    }


    private fun addSecondaryUser(name: String , email :String , phone:String){
        val progress = Progress.getInstance()
        progress.showProgress(this)
        val addSecondaryUserCall: Call<AddSecondaryUserResponse> = RetrofitAPICaller.getInstance(this)
            .stellarJetAPIs.addSecondaryUserToken(
            SharedPreferencesHelper.getUserToken(this) ,
            name ,
            email,
            phone
        )

        addSecondaryUserCall.enqueue(object : Callback<AddSecondaryUserResponse> {
            override fun onResponse(
                call: Call<AddSecondaryUserResponse>,
                response:
                Response<AddSecondaryUserResponse>) {
                progress.hideProgress()
                if(response.body()!=null && response.body()!!.resultcode == 1){
                    UiUtils.showToast(
                        this@PreferenceManagerInfoActivity ,
                        response.body()!!.message
                    )
                    finish()
                } else if (response.code() == 400) {
                    val mJsonObject: JSONObject
                    try {
                        mJsonObject = JSONObject(response.errorBody()!!.string())
                        val errorMessage = mJsonObject.getString("message")
                        UiUtils.showSimpleDialog(
                            this@PreferenceManagerInfoActivity ,
                            errorMessage
                        )
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }

            override fun onFailure(call: Call<AddSecondaryUserResponse>, t: Throwable) {
                Log.d("Booking", "onResponse: $t")
                progress.hideProgress()
                UiUtils.showServerErrorDialog(this@PreferenceManagerInfoActivity)
            }
        })
    }
}
