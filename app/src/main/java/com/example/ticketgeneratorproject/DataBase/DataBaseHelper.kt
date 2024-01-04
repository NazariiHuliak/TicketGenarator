package com.example.ticketgeneratorproject.DataBase

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DataBaseHelper(context: Context): SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {
    companion object {
        private const val DB_NAME = "TicketsDataBase.db"
        private const val DB_VERSION = 3

        const val TICKET_TABLE = "ticketsTable"
        const val TICKET_ID = "ticketID"
        const val FULL_NAME = "fullName"
        const val TRIP_NUMBER = "tripNumber"
        const val DEPARTURE_ADDRESS = "departureAddresses"
        const val DESTINATION_ADDRESS = "destinationAddresses"
        const val DEPARTURE_TIME = "departureTime"
        const val DESTINATION_TIME = "destinationTime"
        const val SEAT = "seat"
        const val PRICE = "price"
        const val CURRENCY = "currency"
        const val PURCHASE_TIME = "purchaseTime"

        const val ADDRESSES_TABLE = "frequentlyUsedAddresses"
        const val ADDRESS_ID = "addressID"
        const val ADDRESS_NAME = "addressName"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTableTickets = "CREATE TABLE $TICKET_TABLE " +
                "($TICKET_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$FULL_NAME TEXT, " +
                "$TRIP_NUMBER TEXT, " +
                "$DEPARTURE_ADDRESS TEXT, " +
                "$DESTINATION_ADDRESS TEXT, " +
                "$DEPARTURE_TIME TEXT, " +
                "$DESTINATION_TIME TEXT, " +
                "$SEAT INTEGER, " +
                "$PRICE REAL," +
                "$CURRENCY TEXT, " +
                "$PURCHASE_TIME TEXT)"

        val createTableAddress = "CREATE TABLE $ADDRESSES_TABLE " +
                "($ADDRESS_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$ADDRESS_NAME TEXT)"

        db?.execSQL(createTableTickets)
        db?.execSQL(createTableAddress)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TICKET_TABLE")
        db?.execSQL("DROP TABLE IF EXISTS $ADDRESSES_TABLE")
    }
}
