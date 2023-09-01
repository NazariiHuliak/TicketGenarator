package com.example.ticketgeneratorproject.Entities

enum class Currency: java.io.Serializable {
    UAH, EUR, USD, UNKNOWN;
    companion object{
        fun parseCurrency(currency: String): Currency{
            if (currency == "₴ Гривня" || currency == "USD"){
                return Currency.UAH
            } else if (currency == "€ Євро" || currency == "EUR"){
                return Currency.EUR
            } else if (currency == "$ Долар" || currency == "UAH") {
                return Currency.USD
            }
            return Currency.UNKNOWN
        }
    }
}