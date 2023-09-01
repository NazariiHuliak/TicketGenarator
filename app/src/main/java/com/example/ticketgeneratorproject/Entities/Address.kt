package com.example.ticketgeneratorproject.Entities

data class Address(
    val country: String,
    val city: String,
    val street: String,
    val number: String
): java.io.Serializable{
    companion object{
        fun parseAddress(address: String): Address{
            val components = address.split(", ")
            if (components.size == 4) {
                return Address(components[0], components[1], components[2], components[3])
            }
            if (components.size == 3) {
                return Address("Україна", components[0], components[1], components[2])
            }
            return Address("", "", "", "")
        }
    }
}
