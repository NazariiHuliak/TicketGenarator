package com.example.ticketgeneratorproject.Business.Controllers

object TextController {
    fun capitalizeWords(input: String): String {
        val words = input.split(" ")
        val capitalizedWords = words.map { it.capitalize() }
        return capitalizedWords.joinToString(" ")
    }

    fun transliterateToEnglish(input: String): String {
        val ukrainianCharacters = arrayOf(
            "а", "б", "в", "г", "д", "е", "є", "ж", "з", "и", "і", "ї", "й", "к",
            "л", "м", "н", "о", "п", "р", "с", "т", "у", "ф", "х", "ц", "ч", "ш",
            "щ", "ь", "ю", "я",
            "А", "Б", "В", "Г", "Д", "Е", "Є", "Ж", "З", "И", "І", "Ї", "Й", "К",
            "Л", "М", "Н", "О", "П", "Р", "С", "Т", "У", "Ф", "Х", "Ц", "Ч", "Ш",
            "Щ", "Ь", "Ю", "Я"
        )

        val latinTransliteration = arrayOf(
            "a", "b", "v", "h", "d", "e", "ie", "zh", "z", "y", "i", "i", "i", "k",
            "l", "m", "n", "o", "p", "r", "s", "t", "u", "f", "kh", "ts", "ch", "sh",
            "shch", "", "iu", "ia",
            "A", "B", "V", "H", "D", "E", "Ye", "Zh", "Z", "Y", "I", "I", "I", "K",
            "L", "M", "N", "O", "P", "R", "S", "T", "U", "F", "Kh", "Ts", "Ch", "Sh",
            "Shch", "", "Yu", "Ya"
        )

        val stringBuilder = StringBuilder()
        val inputChars = input.toCharArray()

        for (char in inputChars) {
            val index = ukrainianCharacters.indexOf(char.toString())
            if (index != -1) {
                stringBuilder.append(latinTransliteration[index])
            } else {
                stringBuilder.append(char)
            }
        }
        return stringBuilder.toString()
    }
}