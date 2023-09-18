package com.example.ticketgeneratorproject.DataBase

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.example.ticketgeneratorproject.Entities.Address
import com.example.ticketgeneratorproject.Entities.Currency
import com.example.ticketgeneratorproject.Entities.DateTime
import com.example.ticketgeneratorproject.Entities.TicketModel

class DataBaseAdapter(private val context: Context) {
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
            put(DataBaseHelper.DEPARTURE_TIME, ticket.departureDateTime.Date + " " + ticket.departureDateTime.Time)
            put(DataBaseHelper.DESTINATION_TIME, ticket.destinationDateTime.Date + " " + ticket.destinationDateTime.Time)
            put(DataBaseHelper.SEAT, ticket.seat)
            put(DataBaseHelper.PRICE, ticket.price)
            put(DataBaseHelper.CURRENCY, ticket.currency.name)
            put(DataBaseHelper.PURCHASE_TIME, ticket.purchaseDateTime.Date + " " + ticket.purchaseDateTime.Time)
        }
        return database.insert(DataBaseHelper.TABLE_NAME, null, values)
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
            put(DataBaseHelper.DEPARTURE_TIME, ticket.departureDateTime.Date + " " + ticket.departureDateTime.Time)
            put(DataBaseHelper.DESTINATION_TIME, ticket.destinationDateTime.Date + " " + ticket.destinationDateTime.Time)
            put(DataBaseHelper.SEAT, ticket.seat)
            put(DataBaseHelper.PRICE, ticket.price)
            put(DataBaseHelper.CURRENCY, ticket.currency.name)
            put(DataBaseHelper.PURCHASE_TIME, ticket.purchaseDateTime.Date + " " + ticket.purchaseDateTime.Time)
        }

        val whereClause = "${DataBaseHelper.TICKET_ID} = ?"
        val whereArgs = arrayOf(ticket.id.toString())

        return database.update(DataBaseHelper.TABLE_NAME, values, whereClause, whereArgs)
    }

    @SuppressLint("Range")
    fun getTickets(): MutableList<TicketModel>{
        var tickets: MutableList<TicketModel> = mutableListOf()
        val query = "SELECT * FROM ${DataBaseHelper.TABLE_NAME}"
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
                        Currency.parseCurrency(currency),
                        DateTime.parseDateTime(purchaseTime)
                    )
                )
            }while(cursor.moveToNext())
        }
        return tickets
    }
    fun closeDB(){
        database.close()
    }
}