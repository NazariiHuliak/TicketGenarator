package com.example.ticketgeneratorproject.Entities

data class DateTime(
    val Date: String,
    val Time: String
) {
    companion object{
        fun parseDateTime(dateTime: String): DateTime{
            val components = dateTime.split(" ")
            return DateTime(components[0], components[1])
        }
    }
}
