package com.example.ticketgeneratorproject.DataBase

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DataBaseHelper(context: Context): SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {
    companion object {
        private const val DB_NAME = "TicketsDataBase.db"
        private const val DB_VERSION = 1

        const val TABLE_NAME = "ticketsTable"
        const val TICKET_ID = "ticketID"
        const val FULL_NAME = "fullName"
        const val TRIP_NUMBER = "tripNumber"
        const val DEPARTURE_ADDRESS = "departureAddresses"
        const val DESTINATION_ADDRESS = "destinationAddresses"
        const val DEPARTURE_TIME = "departureTime"
        const val DESTINATION_TIME = "destinationTime"
        const val SEAT = "seat"
        const val CURRENCY = "currency"
        const val PURCHASE_TIME = "purchaseTime"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTableTickets = "CREATE TABLE $TABLE_NAME " +
                "($TICKET_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$FULL_NAME TEXT, " +
                "$TRIP_NUMBER TEXT, " +
                "$DEPARTURE_ADDRESS TEXT, " +
                "$DESTINATION_ADDRESS TEXT, " +
                "$DEPARTURE_TIME TEXT, " +
                "$DESTINATION_TIME TEXT, " +
                "$SEAT INTEGER, " +
                "$CURRENCY TEXT, " +
                "$PURCHASE_TIME TEXT)"
        db?.execSQL(createTableTickets)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
    }
}
