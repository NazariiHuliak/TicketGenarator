package com.example.ticketgeneratorproject.Entities

enum class Currency: java.io.Serializable {
    UAH, EUR, USD, UNKNOWN;
    companion object{
        fun parseToCurrency(currency: String): Currency{
            if (currency == "₴ Гривня" || currency == "UAH"){
                return Currency.UAH
            } else if (currency == "€ Євро" || currency == "EUR"){
                return Currency.EUR
            } else if (currency == "$ Долар" || currency == "USD") {
                return Currency.USD
            }
            return Currency.UNKNOWN
        }
        fun parseToString(currency: Currency): String{
            return if(currency == Currency.UAH){
                "₴ Гривня"
            } else if(currency == Currency.EUR){
                "€ Євро"
            } else if(currency == Currency.USD){
                "$ Долар"
            } else{
                "Невідомо"
            }
        }
    }
}