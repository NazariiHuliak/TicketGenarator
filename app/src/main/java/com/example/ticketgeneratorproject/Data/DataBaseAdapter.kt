package com.example.ticketgeneratorproject.Data

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.example.ticketgeneratorproject.Data.Entities.Address
import com.example.ticketgeneratorproject.Data.Entities.Currency
import com.example.ticketgeneratorproject.Data.Entities.DateTime
import com.example.ticketgeneratorproject.Data.Entities.TicketModel

class DataBaseAdapter(private val context: Context) {
    private var preLoadedListOfAddresses = listOf(
        Address(
            country = "Україна",
            city = "Львів",
            street = "Стрийський автовокзал",
            number = ""),
        Address(
            country = "Україна",
            city = "Львів",
            street = "Залізничний вокзал",
            number = ""),
        Address(
            country = "Україна",
            city = "Золочів",
            street = "Залізничний вокзал",
            number = "")
    )

    private val database: SQLiteDatabase by lazy {
        DataBaseHelper(context).writableDatabase
    }
    fun addTicket(ticket: TicketModel): Long{
        val values = ContentValues().apply {
            put(DataBaseHelper.FULL_NAME, ticket.fullName)
            put(DataBaseHelper.TRIP_NUMBER, ticket.tripNumber)
            put(DataBaseHelper.DEPARTURE_ADDRESS, ticket.departureAddress.country + ", " +
                    ticket.departureAddress.city + ", " + ticket.departureAddress.street + ", " +
                    ticket.departureAddress.number)
            put(DataBaseHelper.DESTINATION_ADDRESS, ticket.destinationAddress.country + ", " +
                    ticket.destinationAddress.city + ", " + ticket.destinationAddress.street + ", " +
                    ticket.destinationAddress.number)
            put(DataBaseHelper.DEPARTURE_TIME, ticket.departureDateTime.date + " " + ticket.departureDateTime.time)
            put(DataBaseHelper.DESTINATION_TIME, ticket.destinationDateTime.date + " " + ticket.destinationDateTime.time)
            put(DataBaseHelper.SEAT, ticket.seat)
            put(DataBaseHelper.PRICE, ticket.price)
            put(DataBaseHelper.CURRENCY, ticket.currency.name)
            put(DataBaseHelper.PURCHASE_TIME, ticket.purchaseDateTime.date + " " + ticket.purchaseDateTime.time)
        }
        return database.insert(DataBaseHelper.TICKET_TABLE, null, values)
    }

    fun addAllTickets(ticketsList: MutableList<TicketModel>) {
        for (ticket: TicketModel in ticketsList){
            addTicket(ticket)
        }
    }

    fun updateTicket(ticket: TicketModel): Int {
        val values = ContentValues().apply {
            put(DataBaseHelper.FULL_NAME, ticket.fullName)
            put(DataBaseHelper.TRIP_NUMBER, ticket.tripNumber)
            put(DataBaseHelper.DEPARTURE_ADDRESS, ticket.departureAddress.country + ", " +
                    ticket.departureAddress.city + ", " + ticket.departureAddress.street + ", " +
                    ticket.departureAddress.number)
            put(DataBaseHelper.DESTINATION_ADDRESS, ticket.destinationAddress.country + ", " +
                    ticket.destinationAddress.city + ", " + ticket.destinationAddress.street + ", " +
                    ticket.destinationAddress.number)
            put(DataBaseHelper.DEPARTURE_TIME, ticket.departureDateTime.date + " " + ticket.departureDateTime.time)
            put(DataBaseHelper.DESTINATION_TIME, ticket.destinationDateTime.date + " " + ticket.destinationDateTime.time)
            put(DataBaseHelper.SEAT, ticket.seat)
            put(DataBaseHelper.PRICE, ticket.price)
            put(DataBaseHelper.CURRENCY, ticket.currency.name)
            put(DataBaseHelper.PURCHASE_TIME, ticket.purchaseDateTime.date + " " + ticket.purchaseDateTime.time)
        }

        val whereClause = "${DataBaseHelper.TICKET_ID} = ?"
        val whereArgs = arrayOf(ticket.id.toString())

        return database.update(DataBaseHelper.TICKET_TABLE, values, whereClause, whereArgs)
    }

    @SuppressLint("Range")
    fun getAllTickets(): MutableList<TicketModel> {
        var tickets: MutableList<TicketModel> = mutableListOf()
        val query = "SELECT * FROM ${DataBaseHelper.TICKET_TABLE}"
        val cursor = database.rawQuery(query, null)
        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndex(DataBaseHelper.TICKET_ID))
                val fullName = cursor.getString(cursor.getColumnIndex(DataBaseHelper.FULL_NAME))
                val tripNumber = cursor.getString(cursor.getColumnIndex(DataBaseHelper.TRIP_NUMBER))
                val departureAddresses = cursor.getString(cursor.getColumnIndex(DataBaseHelper.DEPARTURE_ADDRESS))
                val destinationAddresses = cursor.getString(cursor.getColumnIndex(DataBaseHelper.DESTINATION_ADDRESS))
                val departureTime = cursor.getString(cursor.getColumnIndex(DataBaseHelper.DEPARTURE_TIME))
                val destinationTime = cursor.getString(cursor.getColumnIndex(DataBaseHelper.DESTINATION_TIME))
                val seat = cursor.getInt(cursor.getColumnIndex(DataBaseHelper.SEAT))
                val price = cursor.getDouble(cursor.getColumnIndex(DataBaseHelper.PRICE))
                val currency = cursor.getString(cursor.getColumnIndex(DataBaseHelper.CURRENCY))
                val purchaseTime = cursor.getString(cursor.getColumnIndex(DataBaseHelper.PURCHASE_TIME))

                tickets.add(
                    TicketModel(
                        id,
                        fullName,
                        tripNumber,
                        Address.parseAddress(departureAddresses),
                        Address.parseAddress(destinationAddresses),
                        DateTime.parseDateTime(departureTime),
                        DateTime.parseDateTime(destinationTime),
                        seat,
                        price,
                        Currency.parseToCurrency(currency),
                        DateTime.parseDateTime(purchaseTime)
                    )
                )
            }while(cursor.moveToNext())
        }
        return tickets
    }

    fun deleteTicketById(id: Int): Int {
        val whereClause = "${DataBaseHelper.TICKET_ID} = ?"
        val whereArgs = arrayOf(id.toString())

        return database.delete(DataBaseHelper.TICKET_TABLE, whereClause, whereArgs)
    }

    fun deleteAllTicket() {
        database.delete(DataBaseHelper.TICKET_TABLE, null, null)
    }

    fun addAddress(address: Address): Long {
        val values = ContentValues().apply {
            put(DataBaseHelper.COUNTRY, address.country)
            put(DataBaseHelper.CITY, address.city)
            put(DataBaseHelper.STREET, address.street)
            put(DataBaseHelper.NUMBER, address.number)
        }
        return database.insert(DataBaseHelper.ADDRESSES_TABLE, null, values)
    }

    fun addAllAddresses(addressesList: List<Address>){
        for (address: Address in addressesList){
            addAddress(address)
        }
    }

    @SuppressLint("Recycle", "Range")
    fun getAllAddresses(): List<Address> {
        val addresses = mutableListOf<Address>()
        val query = "SELECT * FROM ${DataBaseHelper.ADDRESSES_TABLE}"
        val cursor = database.rawQuery(query, null)
        if (cursor.moveToFirst()){
            do {
                val id = cursor.getInt(cursor.getColumnIndex(DataBaseHelper.ADDRESS_ID))
                val country = cursor.getString(cursor.getColumnIndex(DataBaseHelper.COUNTRY))
                val city = cursor.getString(cursor.getColumnIndex(DataBaseHelper.CITY))
                val street = cursor.getString(cursor.getColumnIndex(DataBaseHelper.STREET))
                val number = cursor.getString(cursor.getColumnIndex(DataBaseHelper.NUMBER))
                addresses.add(Address(id, country, city, street, number))
            } while(cursor.moveToNext())
        }
        return addresses + preLoadedListOfAddresses
    }

    fun isUniqueAddress(address: Address): Boolean {
        val commonAddresses = getAllAddresses().map{it.toString()}.map{
            it.replace(Regex("[.,/: ]"), "")}.map{it.lowercase()}
        val addressFormatted = address.toString().replace(Regex("[.,/: ]"), "").lowercase()

        for (addressItem in commonAddresses){
            if (addressFormatted == addressItem){
                return false
            }
        }
        return true
    }

    @SuppressLint("Range", "Recycle")
    fun getAddressById(id: Int): Address {
        val query = "SELECT * FROM ${DataBaseHelper.ADDRESSES_TABLE} WHERE ${DataBaseHelper.ADDRESS_ID} = ?"
        val cursor = database.rawQuery(query, arrayOf(id.toString()))

        return if (cursor.moveToFirst()) {
            val country = cursor.getString(cursor.getColumnIndex(DataBaseHelper.COUNTRY))
            val city = cursor.getString(cursor.getColumnIndex(DataBaseHelper.CITY))
            val street = cursor.getString(cursor.getColumnIndex(DataBaseHelper.STREET))
            val number = cursor.getString(cursor.getColumnIndex(DataBaseHelper.NUMBER))
            Address(id, country, city, street, number)
        } else{
            Address(0, "", "", "", "")
        }
    }

    fun deleteAddressById(id: Int):Int{
        val whereClause = "${DataBaseHelper.ADDRESS_ID} = ?"
        val whereArgs = arrayOf(id.toString())

        return database.delete(DataBaseHelper.ADDRESSES_TABLE, whereClause, whereArgs)
    }

    fun deleteAllAddresses() {
        database.delete(DataBaseHelper.ADDRESSES_TABLE, null, null)
    }
}