package com.example.keyboard.engine

import com.example.keyboard.model.WordSuggestion

class SuggestionEngine {

    private val arabicWordList = listOf(
        "السلام", "عليكم", "ورحمة", "الله", "وبركاته", "صباح", "الخير", "النور",
        "مساء", "الورد", "شكراً", "شكرا", "جزيلاً", "أهلاً", "وسهلاً", "مرحباً",
        "كيف", "حالك", "أخبارك", "الحمد", "لله", "تمام", "بخير", "إن", "شاء",
        "الله", "ما", "شاء", "تبارك", "الرحمن", "مبروك", "بالتوفيق", "رمضان",
        "كريم", "عيد", "مبارك", "أنا", "أنت", "هو", "هي", "نحن", "هم",
        "نعم", "لا", "ربما", "حسناً", "تمام", "أكيد", "طبعاً", "ممكن", "لازم",
        "اليوم", "غداً", "أمس", "الآن", "قريباً", "دائماً", "أبداً", "هنا", "هناك",
        "تطبيق", "لوحة", "المفاتيح", "الذكية", "ممتاز", "رائع", "جميل", "سريع"
    )

    private val englishWordList = listOf(
        "the", "be", "to", "of", "and", "a", "in", "that", "have", "i",
        "it", "for", "not", "on", "with", "he", "as", "you", "do", "at",
        "this", "but", "his", "by", "from", "they", "we", "say", "her", "she",
        "or", "an", "will", "my", "one", "all", "would", "there", "their", "what",
        "so", "up", "out", "if", "about", "who", "get", "which", "go", "me",
        "when", "make", "can", "like", "time", "no", "just", "him", "know", "take",
        "people", "into", "year", "your", "good", "some", "could", "them", "see", "other",
        "than", "then", "now", "look", "only", "come", "its", "over", "think", "also",
        "back", "after", "use", "two", "how", "our", "work", "first", "well", "way",
        "even", "new", "want", "because", "any", "these", "give", "day", "most", "us",
        "hello", "thanks", "thank", "please", "welcome", "awesome", "keyboard", "great"
    )

    private val commonTypoCorrections = mapOf(
        "teh" to "the",
        "recieve" to "receive",
        "dont" to "don't",
        "cant" to "can't",
        "wont" to "won't",
        "im" to "I'm",
        "thx" to "thanks",
        "pls" to "please",
        "انشاء" to "إن شاء",
        "انشالله" to "إن شاء الله",
        "واللهي" to "والله",
        "مشكور" to "مشكور جداً",
        "عليك" to "عليكم",
        "سلام" to "السلام عليكم",
        "صباحو" to "صباح الخير"
    )

    private val nextWordPredictions = mapOf(
        "السلام" to listOf("عليكم", "ورحمة", "الوطني"),
        "عليكم" to listOf("ورحمة", "السلام", "جميعاً"),
        "ورحمة" to listOf("الله", "وبركاته"),
        "صباح" to listOf("الخير", "النور", "الورد"),
        "مساء" to listOf("الخير", "الورد", "النور"),
        "إن" to listOf("شاء", "لم", "كنت"),
        "شاء" to listOf("الله", "تبارك"),
        "الحمد" to listOf("لله", "والشكر"),
        "شكراً" to listOf("جزيلاً", "لك", "لكم"),
        "شكرا" to listOf("جزيلا", "لك", "لكم"),
        "how" to listOf("are", "is", "can"),
        "thank" to listOf("you", "so", "very"),
        "good" to listOf("morning", "night", "luck"),
        "see" to listOf("you", "it", "more"),
        "i" to listOf("am", "will", "have")
    )

    fun getSuggestions(currentWord: String, previousWord: String? = null): List<WordSuggestion> {
        val cleanCurrent = currentWord.trim().lowercase()

        // 1. If no current word is being typed, show next word predictions
        if (cleanCurrent.isEmpty()) {
            if (!previousWord.isNullOrBlank()) {
                val prevClean = previousWord.trim().lowercase()
                val nexts = nextWordPredictions[prevClean]
                if (nexts != null && nexts.isNotEmpty()) {
                    return nexts.take(3).map { WordSuggestion(it, isAutoCorrect = false) }
                }
            }
            return listOf(
                WordSuggestion("السلام", false),
                WordSuggestion("شكراً", false),
                WordSuggestion("Hello", false)
            )
        }

        // 2. Exact typo replacement
        val directCorrection = commonTypoCorrections[cleanCurrent]
        val results = mutableListOf<WordSuggestion>()

        if (directCorrection != null) {
            results.add(WordSuggestion(directCorrection, isAutoCorrect = true))
        }

        // 3. Prefix matching from dictionary
        val isArabic = cleanCurrent.any { it in '\u0600'..'\u06FF' }
        val dict = if (isArabic) arabicWordList else englishWordList

        val matches = dict.filter { it.lowercase().startsWith(cleanCurrent) }
        for (m in matches) {
            if (results.none { it.text.equals(m, ignoreCase = true) }) {
                results.add(WordSuggestion(m, isAutoCorrect = results.isEmpty()))
            }
            if (results.size >= 3) break
        }

        // 4. If still less than 3, keep current input
        if (results.none { it.text.equals(currentWord, ignoreCase = true) }) {
            results.add(0, WordSuggestion(currentWord, isAutoCorrect = false))
        }

        return results.take(3)
    }
}
