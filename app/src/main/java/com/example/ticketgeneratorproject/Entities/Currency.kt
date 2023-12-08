package com.example.ticketgeneratorproject.Entities

enum class Currency: java.io.Serializable {
    UAH, EUR, USD, UNKNOWN;
    companion object{
        fun parseToCurrency(currency: String): Currency{
            if (currency == "₴ Гривня" || currency == "USD"){
                return Currency.UAH
            } else if (currency == "€ Євро" || currency == "EUR"){
                return Currency.EUR
            } else if (currency == "$ Долар" || currency == "UAH") {
                return Currency.USD
            }
            return Currency.UNKNOWN
        }
        fun parseToString(currency: Currency): String{
            if(currency == Currency.UAH){
                return "₴ Гривня"
            } else if(currency == Currency.EUR){
                return "€ Євро"
            } else if(currency == Currency.USD){
                return "$ Долар"
            } else{
                return "Невідомо"
            }
        }
    }
}