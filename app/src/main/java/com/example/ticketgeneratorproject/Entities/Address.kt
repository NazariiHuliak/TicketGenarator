package com.example.ticketgeneratorproject.Entities

import android.util.Log

data class Address(
    val country: String,
    val city: String,
    val street: String,
    val number: String
): java.io.Serializable{
    companion object{
        fun parseAddress(address: String): Address{
            var formattedAddress = address.replace(",", "").
                                    replace(".", "").
                                    replace("вул", "")
            while("  " in formattedAddress){
                formattedAddress = formattedAddress.replace("  ", " ")
            }
            val components = formattedAddress.split(" ")
            if (components.size == 4){
                return Address(components[0], components[1], components[2], components[3])
            } else if(components.size == 5){
                return Address(components[0], components[1], components[2] + " " + components[3], components[4])
            }
            return Address("", "", "", "")
        }
    }
}
