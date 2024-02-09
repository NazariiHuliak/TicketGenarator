package com.example.ticketgeneratorproject.Entities

import android.util.Log
import com.example.ticketgeneratorproject.AddTicketPage2

data class Address(
    val id: Int = 0,
    val country: String = "",
    val city: String = "",
    val street: String = "",
    val number: String = ""
): java.io.Serializable{

    override fun toString(): String {
        return "$country, $city, $street, $number"
    }

    fun getUniqueId(): String{
        return AddTicketPage2.transliterateToEnglish(this.country + this.city + this.street + this.number)
    }

    companion object{
        fun parseAddress(address: String): Address{
            var formattedAddress = address.replace(Regex("[.,/:]"), "").
                replace("вул", "").trimEnd()
            while("  " in formattedAddress){
                formattedAddress = formattedAddress.replace("  ", " ")
            }
            val components = formattedAddress.split(" ")
            if (components.size == 4){
                return Address(0, components[0], components[1], components[2], components[3])
            } else if(components.size == 5){
                return Address(0, components[0], components[1], components[2] + " " + components[3], components[4])
            }
            return Address(0, "", "", "", "")
        }
    }
}
