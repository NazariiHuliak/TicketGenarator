package com.example.ticketgeneratorproject.Entities

enum class Currency {
    UAH, EUR, USD, UNKNOWN;
    companion object{
        fun parseCurrency(currency: String): Currency{
            if (currency == "UAH"){
                return Currency.UAH
            } else if (currency == "EUR"){
                return Currency.EUR
            } else if (currency == "USD") {
                return Currency.USD
            }
            return Currency.UNKNOWN
        }
    }
}