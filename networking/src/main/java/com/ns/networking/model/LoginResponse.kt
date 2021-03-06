package com.ns.networking.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class LoginResponse(
    val `data`: Data,
    val message: String,
    val resultcode: Int
): Parcelable

@Parcelize
data class Data(
    val refresh_token: String,
    val token: String,
    val user_data: UserData
): Parcelable

@Parcelize
data class UserData(
    val addresses: List<Addresse>,
    val cities: List<City>,
    val contacts: List<Contact>,
    val primary_users: List<PrimaryUser>,
    val created_at: String,
    val customer_care_info: CustomerCareInfo,
    val customer_prefs: CustomerPrefs,
    val email: String,
    val locked_seats: List<LockedSeats>?,
    val name: String,
    val phone: String,
    val user_id: Int
):Parcelable

/*@Parcelize
data class PrimaryUser(
    val email: String?,
    val id: Int?,
    val name: String?,
    val phone: String?
):Parcelable*/

@Parcelize
data class Subscription(
    val created_at: String?,
    val expiry_date: String?,
    val no_of_seats: Int?
):Parcelable

@Parcelize
data class CustomerPrefs(
    val flight_days: List<String>,
    val food_categories: MutableList<FoodCategory>,
    var food_prefs: MutableList<FoodPref>,
    val gstin: String,
    val member_type: String,
    val member_type_text: String,
    val membership_details: MembershipDetails,
    val seats_available: Int,
    val subscription_expiry: Long,
    val subscription_expiry_datetime: String
):Parcelable

@Parcelize
data class MembershipDetails(
    val id: Int,
    val prepaid_terms: String,
    val seat_cost: String,
    val title: String
):Parcelable

@Parcelize
data class FoodPref(
    var days: MutableList<String> ,
    var food_category: String
):Parcelable {

    override fun equals(other: Any?): Boolean {
         super.equals(other)

        if(other is FoodPref) {
            var  pref: FoodPref = other as FoodPref;
            return pref.food_category.equals(food_category)
        }
        return false
    }

    override fun hashCode(): Int {
        return food_category.hashCode()
    }
}

@Parcelize
data class FoodCategory(
    val cat_key: String,
    val cat_text: String,
    val show_days: Boolean
):Parcelable  {
    override fun equals(other: Any?): Boolean {
        super.equals(other)

        if(other is FoodCategory) {
            var  pref: FoodCategory = other as FoodCategory;
            return pref.cat_key.equals(cat_key)
        }
        return false
    }

    override fun hashCode(): Int {
        return cat_key.hashCode()
    }
}

/*@Parcelize
data class CustomerPrefs(
    val flight_days: List<String>,
    val gstin: String,
    val member_type: String,
    val member_type_text: String,
    val membership_details: MembershipDetails,
    val seats_available: Int,
    val subscription_expiry: Long,
    val subscription_expiry_datetime: String
):Parcelable

@Parcelize
data class MembershipDetails(
    val id: Int,
    val prepaid_terms: String,
    val seat_cost: String,
    val title: String
):Parcelable*/

@Parcelize
data class FoodCategories(
    val continental: String
//    val non-veg: String,
//    val veg: String
):Parcelable

@Parcelize
data class SeatPreferences(
    val flight: Flight?,
    val flight_id: Int?,
    val id: Int?,
    val seat_code: String?,
    val sort_order: Int?
):Parcelable


@Parcelize
data class Food(
    val description: String?,
    val food_image: String?,
    val food_type: String?,
    val food_type_text: String?,
    val id: Int?,
    val image_path: String?,
    val img_url: String?,
    val name: String?,
    var pref: Boolean?
):Parcelable

@Parcelize
data class CustomerCareInfo(
    val email: String?,
    val phone: String?
):Parcelable

@Parcelize
data class Addresse(
    val address: String?,
    val address_tag: String?,
    val city: Int?,
    val city_info: CityInfo?,
    val created_at: String?,
    val id: Int?,
    val user: Int?
):Parcelable

@Parcelize
data class CityInfo(
    val id: Int?,
    val name: String?
):Parcelable

@Parcelize
data class City(
    val airport: String,
    val id: Int,
    val name: String,
    val short_name: String,
    val status: Int
):Parcelable

@Parcelize
data class Contact(
    val email: String?,
    val id: Int?,
    val name: String?,
    val nick_name: String?,
    val phone: String?,
    val relationship: String?,
    val user: Int?,
    val user_info: UserInfo?
):Parcelable

@Parcelize
data class UserInfo(
    val id: Int?,
    val name: String?
) : Parcelable

@Parcelize
data class LockedSeats(
    val flight: Flight?,
    val flight_id: Int?,
    val flight_seat: FlightSeat?,
    val flight_seat_id: Int?,
    val from_city: Int?,
    val id: Int?,
    val journey_date: String?,
    val journey_time: String?,
    val seat_reserved_at: String?,
    val status: Int?,
    val to_city: Int?,
    val user: Int?,
    val datetime_ms : Long? ,
    val expire_within_s : Int?,
    val direction : String?,
    val sun_rise_set : String?,
    val arrival_time : String?
):Parcelable

@Parcelize
data class Flight(
    val flight_no: String?,
    val id: Int?,
    val model: String?,
    val no_of_seats: Int?
):Parcelable

@Parcelize
data class FlightSeat(
    val id: Int?,
    val seat_code: String?
):Parcelable