package com.example.ticketgeneratorproject.Data.Entities

enum class Currency: java.io.Serializable {
    UAH, EUR, USD, UNKNOWN;
    companion object{
        fun parseToCurrency(currency: String): Currency {
            return when (currency) {
                "₴ Гривня", "UAH" -> {
                    UAH
                }

                "€ Євро", "EUR" -> {
                    EUR
                }

                "$ Долар", "USD" -> {
                    USD
                }

                else -> UNKNOWN
            }
        }
        fun parseToString(currency: Currency): String{
            return when (currency) {
                UAH -> {
                    "₴ Гривня"
                }
                EUR -> {
                    "€ Євро"
                }
                USD -> {
                    "$ Долар"
                }
                else -> {
                    "Невідомо"
                }
            }
        }
    }
}