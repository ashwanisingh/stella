package com.ns.networking.model

import android.os.Parcel
import android.os.Parcelable

data class MainPassenger(
    val food_items: List<FoodItems>?,
    val name: String?,
    val phone: String?,
    var status: String?,
    val last_modified_by: String?,
    val modified_user_type: String?,
    val seats_info: SeatsInfo?,
    var boarding_pass_url: String?,
    var pick_address: PickAddressBean?,
    var drop_address: DropAddressBean?

) : Parcelable {
    constructor(source: Parcel) : this(
        source.createTypedArrayList(FoodItems.CREATOR),
        source.readString(),
        source.readString(),
        source.readString(),
        source.readString(),
        source.readString(),
        source.readParcelable<SeatsInfo>(SeatsInfo::class.java.classLoader),
        source.readString(),
        source.readParcelable<PickAddressBean>(SeatsInfo::class.java.classLoader),
        source.readParcelable<DropAddressBean>(SeatsInfo::class.java.classLoader)

    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeTypedList(food_items)
        writeString(name)
        writeString(phone)
        writeString(status)
        writeString(last_modified_by)
        writeString(modified_user_type)
        writeParcelable(seats_info, 0)
        writeString(boarding_pass_url)
        writeParcelable(pick_address, 0)
        writeParcelable(drop_address, 0)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<MainPassenger> = object : Parcelable.Creator<MainPassenger> {
            override fun createFromParcel(source: Parcel): MainPassenger = MainPassenger(source)
            override fun newArray(size: Int): Array<MainPassenger?> = arrayOfNulls(size)
        }
    }
}