package com.example.ticketgeneratorproject.Data.Entities

import java.util.HashMap

data class TicketModel(
    var id: Int = 0,
    val fullName: String = "",
    val tripNumber: String = "",
    val departureAddress: Address = Address(0, "", "", "", ""),
    val destinationAddress: Address = Address(0, "", "", "", ""),
    var departureDateTime: DateTime = DateTime("", ""),
    var destinationDateTime: DateTime = DateTime("", ""),
    val seat: Int = 0,
    var price: Double = 0.0,
    var currency: Currency = Currency.UNKNOWN,
    var purchaseDateTime: DateTime = DateTime("", "")
): java.io.Serializable{

    fun setId(id: Int): TicketModel {
        this.id = id
        return this
    }
    fun setDepartureDestinationDateTime(departureDateTime: DateTime, destinationDateTime: DateTime): TicketModel {
        this.departureDateTime = departureDateTime
        this.destinationDateTime = destinationDateTime
        return this
    }
    fun setPurchaseDateTime(purchaseDateTime: DateTime): TicketModel {
        this.purchaseDateTime = purchaseDateTime
        return this
    }
    fun setPrice(price: Double): TicketModel {
        this.price = price
        return this
    }
    fun setCurrency(currency: Currency): TicketModel {
        this.currency = currency
        return this
    }

    fun getHashMap(): Map<String, Any> {
        return mapOf(
            "id" to this.id,
            "fullName" to this.fullName,
            "tripNumber" to this.tripNumber,
            "departureAddress" to this.departureAddress,
            "destinationAddress" to this.destinationAddress,
            "departureDateTime" to this.departureDateTime,
            "destinationDateTime" to this.destinationDateTime,
            "seat" to this.seat,
            "price" to this.price,
            "currency" to this.currency.name,
            "purchaseDateTime" to this.purchaseDateTime
        )
    }
}