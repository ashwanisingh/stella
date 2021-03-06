package com.ns.networking.retrofit;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.ns.networking.model.BookingRequest;
import com.ns.networking.model.*;
import com.ns.networking.model.flightsseats.FlightSeatListResponse;
import com.ns.networking.model.foods.GlobalFoodPrefResponse;
import com.ns.networking.model.guestrequest.AddGuestPrefsRequest;
import com.ns.networking.model.guestrequest.EditGuestPrefsRequest;
import com.ns.networking.model.guestrequest.GuestPrefsRequest;
import com.ns.networking.model.schedulefood.ScheduleFoodListResponse;
import com.ns.networking.model.secondaryusers.AddSecondaryUserResponse;
import com.ns.networking.model.secondaryusers.DeactivateSecondaryUserResponse;
import com.ns.networking.model.secondaryusers.SecondaryUsersListResponse;
import com.ns.networking.utils.Constants;
import okhttp3.ResponseBody;
import org.json.JSONArray;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.List;

public interface StellarApiService {

    // login Api
    @POST(Constants.LOGIN_API)
    @FormUrlEncoded
    Call<LoginResponse> doLogin(
            @Field("username") String username,
            @Field("password") String password
    );

    @POST(Constants.SWITCH_TO_PRIMARY_API)
    @FormUrlEncoded
    Call<LoginResponse> switchPrimaryUsers(
            @Field("token") String token,
            @Field("primary_user") int primaryUserId
    );

    @POST(Constants.VALIDATE_USER_API)
    @FormUrlEncoded
    Call<ValidateCustomerResponse> doValidateCustomer(
            @Field("username") String username
    );

    @GET(Constants.CUSTOMER_DATA_API)
    Call<LoginResponse> getCustomerData(
            @Query("token") String token
    );

    @POST(Constants.REFRESH_TOKEN_API)
    @FormUrlEncoded
    Call<RefreshTokenResponse> getUpdatedToken(
            @Field("refresh_token") String token
    );


    @GET(Constants.FLIGHT_SCHEDULE_API)
    Call<FlightScheduleResponse> getFlightSchedules(
            @Query("token") String token,
            @Query("from_city") String fromCity,
            @Query("to_city") String toCity,
            @Query("days") String days
    );

    @FormUrlEncoded
    @POST(Constants.FLIGHT_SEATS_API)
    Call<FlightSeatListResponse> getFlightSeats(
            @Field("token") String token,
            @Field("flight_id") int flightId,
            @Field("from_city") int fromCity,
            @Field("to_city") int toCity,
            @Field("journey_date") String journeyDate,
            @Field("journey_time") String journeyTime
    );

    @FormUrlEncoded
    @POST(Constants.FLIGHT_SEATS_CONFIRM_API)
    Call<FlightSeatsConfirmResponse> confirmFlightSeats(
            @Field("token") String token,
            @Field("flight_id") int flightId,
            @Field("from_city") int fromCity,
            @Field("to_city") int toCity,
            @Field("journey_date") String journeyDate,
            @Field("journey_time") String journeyTime,
            @Field("seats_for_unlock[]") List<Integer> mSeatsUnlock,
            @Field("seats_for_lock[]") List<Integer> mSeatsLock
    );

    @FormUrlEncoded
    @POST(Constants.BOOK_SEATS_API)
    Call<BookingConfirmResponse> confirmFlightBooking(
            @Field("token") String token,
            @Field("from_city") int fromCity,
            @Field("to_city") int toCity,
            @Field("journey_date") String journeyDate,
            @Field("journey_time") String journeyTime,
            @Field("arrival_time") String arrivalTime,
            @Field("flight_id") int flightId,
            @Field("seat_ids[]") List<Integer> mSeatsCodeList,
            @Field("guests[]") List<Integer> mGuestId,
            @Field("travelling_self") int selfTravelling,
            @Field("schedule_id") String scheduleId,
            @Field("seat_passenger_relation") JSONArray bookingUserRelation
//            @Field("guest_prefs[]")ArrayList<GuestPrefsDataRequest> guestPrefs
    );

    @POST(Constants.BOOK_SEATS_API)
    Call<BookingConfirmResponse> confirmFlightBookingSeats(
            @Body BookingRequest bookingRequest
    );

    @POST(Constants.GUEST_CONFIRM_API)
    Call<GuestConfirmResponse> bookGuestInfo(
            @Body GuestPrefsRequest guestPrefsRequest
    );

    @POST(Constants.GUEST_CONFIRM_API)
    Call<GuestConfirmResponse> bookExistingGuestInfo(
            @Body EditGuestPrefsRequest editGuestPrefsRequest
    );

    @POST(Constants.GUEST_CONFIRM_API)
    Call<GuestConfirmResponse> bookNewGuestInfo(
            @Body AddGuestPrefsRequest addGuestPrefsRequest
    );

    @FormUrlEncoded
    @POST(Constants.FOOD_PERSONLAIZE_API)
    Call<FoodPersonalizeResponse> personalizeFood(
            @Field("token") String token ,
            @Field("booking_id") String booking_id ,
            @Field("foods_taken[]") List<String> foods_taken
    );

    @FormUrlEncoded
    @POST(Constants.CAB_PERSONLAIZE_API)
    Call<CabPersonalizeResponse> personalizeCab(
            @Field("token") String token ,
            @Field("booking_id") String booking_id ,
            @Field("pick_address") String pickAddress ,
            @Field("drop_address") String dropAddress
    );

    @GET(Constants.BOOKING_HISTORY_API)
    Call<BookingHistoryResponse> getBookingHistoryResponse(
            @Query("token") String token ,
            @Query("offset") int offset ,
            @Query("limit") int limit ,
            @Query("show_list") String show_list
    );

    @GET(Constants.BOARDING_PASS_API)
    Call<BoardingPassResponse> getBoardingPassResponse(
            @Query("token") String token ,
            @Query("offset") int offset ,
            @Query("limit") int limit
    );


    @FormUrlEncoded
    @POST(Constants.ADD_ADDRESS_API)
    Call<AddAddressResponse> addNewAddress(
            @Field("token") String token,
            @Field("city") String cityId,
            @Field("address") String address,
            @Field("address_tag") String addressTag
    );

    @GET(Constants.ADDRESS_LIST_API)
    Call<SavedAddressResponse> getSavedAddress(
            @Query("token") String token
    );

    @FormUrlEncoded
    @POST(Constants.FORGOT_PASSWORD_API)
    Call<ForgotPasswordResponse> forgotPassword(
            @Field("email") String email
    );

    @FormUrlEncoded
    @POST(Constants.COMMON_FOOD_UPDATE_API)
    Call<CommonPersonalizeFoodResponse> updateCommonFoodPreferences(
            @Field("token") String token,
            @Field("food_prefs[]") List<String> foodsList
    );

    @FormUrlEncoded
    @POST(Constants.UPDATE_DEVICE_TOKEN_API)
    Call<UpdateDeviceToken> updateDeviceToken(
            @Field("token") String token,
            @Field("usertype") String userType,
            @Field("device_token") String deviceToken,
            @Field("device_type") String deviceType
    );

    @FormUrlEncoded
    @POST(Constants.ADD_SECONDARY_USER_API)
    Call<AddSecondaryUserResponse> addSecondaryUserToken(
            @Field("token") String token,
            @Field("name") String name,
            @Field("email") String email,
            @Field("phone") String phone
    );

    @GET(Constants.SECONDARY_USER_LIST_API)
    Call<SecondaryUsersListResponse> getSecondaryUsersList(
            @Query("token") String token ,
            @Query("status") int status

    );

    @FormUrlEncoded
    @POST(Constants.SECONDARY_USER_LOGIN_API)
    Call<SecondaryUserLoginResponse> loginSecondaryUser(
            @Field("username") String username,
            @Field("otp") String otp
    );

    @FormUrlEncoded
    @POST(Constants.DEACTIVATE_SECONDARY_USER_API)
    Call<DeactivateSecondaryUserResponse> deactivateSecondaryUser(
            @Field("token") String token ,
            @Field("secondary_user_id") int secondary_user_id,
            @Field("status") int status
    );

    @GET(Constants.FOOD_SCHEDULE_LIST_API)
    Call<ScheduleFoodListResponse> getFoodListBySchedule(
            @Query("token") String token ,
            @Query("schedule_id") int schedule_id,
            @Query("journey_date") String journey_date
    );

    @FormUrlEncoded
    @POST(Constants.BOOKING_CANCEL_API)
    Call<CancelBookingResponse> cancelBooking(
            @Field("token") String token ,
            @Field("booking_id") int booking_id,
            @Field("seat_ids[]") List<Integer> mSeatsId
    );

    @FormUrlEncoded
    @POST(Constants.ORDERID_API)
    Call<PurchaseSeatsResponse> getOrderId(
            @Field("token") String token ,
            @Field("no_of_seats") int numOfSeats
    );

    @FormUrlEncoded
    @POST(Constants.ORDERID_GUESTS_API)
    Call<PurchaseSeatsResponse> getGuestOrderId(
            @Field("token") String token ,
            @Field("no_of_seats") int numOfSeats
    );

    @FormUrlEncoded
    @POST(Constants.VERIFY_PURCHASE_API)
    Call<VerifyPurchaseResponse> verifyPurchase(
            @Field("token") String token ,
            @Field("payment_id") String paymentId ,
            @Field("purchase_id") String purchaseId
    );

    @GET(Constants.BOOKING_DETAILS_API)
    Call<BookingDetailsResponse> getBookingDetails(
            @Query("token") String token ,
            @Query("booking_id") String paymentId
    );

    @FormUrlEncoded
    @POST(Constants.COMMON_FOOD_UPDATE_API)
    Call<GlobalFoodPrefResponse> updateGlobalFoodPreferences(
            @Field("token") String token,
            @Field("food_prefs") JSONArray foodArray
    );

    @GET("content/getStatic")
    Call<TCModel> getTC();

    @FormUrlEncoded
    @POST("booking/list")
    Call<JsonElement> downloadBoardingPass(
            @Field("token") String token
    );

    @GET
    Call<ResponseBody> fetchCaptcha(@Url String url);

    @POST("feedback/update")
    Call<JsonElement> feedback(@Header("Content-Type") String content_type, @Body JsonObject body);

//    @FormUrlEncoded
    /*@POST("feedback/update")
    Call<JsonElement> feedback(@Query("token") String token,
                               @Query("booking_id") String booking_id, @Query("passenger_id") String passenger_id, @Query("passenger_type") String passenger_type,
                               @Query("feedback[0][ID]") String flightExpId, @Query("feedback[0][RATING]") String flightExpRating, @Query("feedback[0][DESCRIPTION]") String flightExpDesc,
                               @Query("feedback[1][ID]") String foodExpId, @Query("feedback[1][RATING]") String foodExpRating, @Query("feedback[1][DESCRIPTION]") String foodExpDesc,
                               @Query("feedback[2][ID]") String limousineExpId, @Query("feedback[2][RATING]") String limousineExpRating, @Query("feedback[2][DESCRIPTION]") String limousineExpDesc
                               );*/


    /*@POST("feedback/update")
    Call<JsonElement> feedback(@Query(value = "token", encoded = false) String token,
                               @Query(value = "booking_id", encoded = false) String booking_id,
                               @Query(value = "passenger_id", encoded = false) String passenger_id,
                               @Query(value = "passenger_type", encoded = false) String passenger_type,
                               @Query(value = "feedback[0][ID]", encoded = false) String flightExpId,
                               @Query(value = "feedback[0][RATING]", encoded = false) String flightExpRating,
                               @Query(value = "feedback[0][DESCRIPTION]", encoded = false) String flightExpDesc,
                               @Query(value = "feedback[1][ID]", encoded = false) String foodExpId,
                               @Query(value = "feedback[1][RATING]", encoded = false) String foodExpRating,
                               @Query(value = "feedback[1][DESCRIPTION]", encoded = false) String foodExpDesc,
                               @Query(value = "feedback[2][ID]", encoded = false) String limousineExpId,
                               @Query(value = "feedback[2][RATING]", encoded = false) String limousineExpRating,
                               @Query(value = "feedback[2][DESCRIPTION]", encoded = false) String limousineExpDesc
    );*/



//    @Query(value = "token", encoded = false) String token
/*
@GET(Constants.CITY_LIST_API)
    Call<CityListResponse> getCityList(
            @Query("token") String token
    );

    @FormUrlEncoded
    @POST(Constants.ADD_GUEST_API)
    Call<AddGuestResponse> addNewGuest(
            @Field("token") String token,
            @Field("user") String userId,
            @Field("name") String name,
            @Field("phone") String phone,
            @Field("nick_name") String nickName,
            @Field("relationship") String relationship,
            @Field("email") String email
    );

    @FormUrlEncoded
    @POST(Constants.UPDATE_PREFERENCES_API)
    Call<UpdatePreferencesResponse> updatePreferences(
            @Field("token") String token,
            @Field("user") String userId,
            @Field("food_prefs[]") List<String> foodPrefs,
            @Field("kit_prefs[]") List<String> kitPrefs
    );*/
}
