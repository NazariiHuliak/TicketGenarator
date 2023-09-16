package com.example.ticketgeneratorproject.Entities
data class TicketModel(
    val id: Int,
    val fullName: String,
    val tripNumber: String,
    val departureAddress: Address,
    val destinationAddress: Address,
    var departureDateTime: DateTime,
    var destinationDateTime: DateTime,
    val seat: Int,
    val price: Double,
    val currency: Currency,
    var purchaseTime: DateTime
): java.io.Serializable{

}
