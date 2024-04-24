package com.example.ticketgeneratorproject.Data.Entities

data class DateTime(
    val date: String = "",
    val time: String = "",
    val seconds: String = ""
): java.io.Serializable {
    companion object{
        fun parseDateTime(dateTime: String): DateTime {
            val components = dateTime.split(" ")
            return DateTime(components[0], components[1])
        }
    }
}