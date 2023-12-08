package com.example.ticketgeneratorproject.Entities
data class TicketModel(
    var id: Int,
    val fullName: String,
    val tripNumber: String,
    val departureAddress: Address,
    val destinationAddress: Address,
    var departureDateTime: DateTime,
    var destinationDateTime: DateTime,
    val seat: Int,
    var price: Double,
    var currency: Currency,
    var purchaseDateTime: DateTime
): java.io.Serializable{
    fun setId(id: Int): TicketModel{
        this.id = id
        return this
    }
    fun setDepartureDestinationDateTime(departureDateTime: DateTime, destinationDateTime: DateTime):TicketModel{
        this.departureDateTime = departureDateTime
        this.destinationDateTime = destinationDateTime
        return this
    }
    fun setPurchaseDateTime(purchaseDateTime: DateTime): TicketModel{
        this.purchaseDateTime = purchaseDateTime
        return this
    }
    fun setPrice(price: Double): TicketModel{
        this.price = price
        return this
    }
    fun setCurrency(currency: Currency): TicketModel{
        this.currency = currency
        return this
    }
}
