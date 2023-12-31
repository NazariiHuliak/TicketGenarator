package com.example.ticketgeneratorproject.Entities
data class TicketModel(
    var id: Int = 0,
    val fullName: String = "",
    val tripNumber: String = "",
    val departureAddress: Address = Address("", "", "", ""),
    val destinationAddress: Address = Address("", "", "", ""),
    var departureDateTime: DateTime = DateTime("", ""),
    var destinationDateTime: DateTime = DateTime("", ""),
    val seat: Int = 0,
    var price: Double = 0.0,
    var currency: Currency = Currency.UNKNOWN,
    var purchaseDateTime: DateTime = DateTime("", "")
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