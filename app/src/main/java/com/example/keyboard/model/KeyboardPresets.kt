package com.example.keyboard.model

object KeyboardPresets {

    val NUMBER_ROW = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "0")

    val SYMBOLS_PAGE_1 = listOf(
        listOf("@", "#", "$", "_", "&", "-", "+", "(", ")", "/"),
        listOf("*", "\"", "'", ":", ";", "!", "?", "%", "=", "\\"),
        listOf("~", "`", "|", "<", ">", "{", "}", "[", "]")
    )

    val SYMBOLS_PAGE_2 = listOf(
        listOf("€", "£", "¥", "₹", "₩", "¢", "©", "®", "™", "°"),
        listOf("^", "§", "∆", "¶", "•", "√", "π", "÷", "×", "¶"),
        listOf("≠", "≈", "∞", "≤", "≥", "«", "»", "¡", "¿")
    )

    val ARABIC_LANGUAGE = KeyboardLanguage(
        code = "ar",
        name = "العربية",
        nativeName = "العربية",
        isRtl = true,
        defaultRows = listOf(
            listOf("ض", "ص", "ث", "ق", "ف", "غ", "ع", "ه", "خ", "ح", "ج", "د"),
            listOf("ش", "س", "ي", "ب", "ل", "ا", "ت", "ن", "م", "ك", "ط"),
            listOf("ئ", "ء", "ؤ", "ر", "لا", "ى", "ة", "و", "ز", "ظ")
        ),
        shiftedRows = listOf(
            listOf("َ", "ً", "ُ", "ٌ", "ِ", "ٍ", "ْ", "ّ", "ـ", "؛", "،", "؟"),
            listOf("آ", "إ", "أ", "ء", "لإ", "لأ", "لآ", "ـ", "؛", ":", "\""),
            listOf("ئ", "ء", "ؤ", "ـ", "»", "«", "،", "؟", "!", ".")
        )
    )

    val ENGLISH_LANGUAGE = KeyboardLanguage(
        code = "en",
        name = "English",
        nativeName = "English (US)",
        isRtl = false,
        defaultRows = listOf(
            listOf("q", "w", "e", "r", "t", "y", "u", "i", "o", "p"),
            listOf("a", "s", "d", "f", "g", "h", "j", "k", "l"),
            listOf("z", "x", "c", "v", "b", "n", "m")
        )
    )

    val FRENCH_LANGUAGE = KeyboardLanguage(
        code = "fr",
        name = "Français",
        nativeName = "Français",
        isRtl = false,
        defaultRows = listOf(
            listOf("a", "z", "e", "r", "t", "y", "u", "i", "o", "p"),
            listOf("q", "s", "d", "f", "g", "h", "j", "k", "l", "m"),
            listOf("w", "x", "c", "v", "b", "n", "é", "è", "à")
        )
    )

    val SPANISH_LANGUAGE = KeyboardLanguage(
        code = "es",
        name = "Español",
        nativeName = "Español",
        isRtl = false,
        defaultRows = listOf(
            listOf("q", "w", "e", "r", "t", "y", "u", "i", "o", "p"),
            listOf("a", "s", "d", "f", "g", "h", "j", "k", "l", "ñ"),
            listOf("z", "x", "c", "v", "b", "n", "m", "¿", "¡")
        )
    )

    val GERMAN_LANGUAGE = KeyboardLanguage(
        code = "de",
        name = "Deutsch",
        nativeName = "Deutsch",
        isRtl = false,
        defaultRows = listOf(
            listOf("q", "w", "e", "r", "t", "z", "u", "i", "o", "p", "ü"),
            listOf("a", "s", "d", "f", "g", "h", "j", "k", "l", "ö", "ä"),
            listOf("y", "x", "c", "v", "b", "n", "m", "ß")
        )
    )

    val TURKISH_LANGUAGE = KeyboardLanguage(
        code = "tr",
        name = "Türkçe",
        nativeName = "Türkçe",
        isRtl = false,
        defaultRows = listOf(
            listOf("q", "w", "e", "r", "t", "y", "u", "ı", "o", "p", "ğ", "ü"),
            listOf("a", "s", "d", "f", "g", "h", "j", "k", "l", "ş", "i"),
            listOf("z", "x", "c", "v", "b", "n", "m", "ö", "ç")
        )
    )

    val RUSSIAN_LANGUAGE = KeyboardLanguage(
        code = "ru",
        name = "Русский",
        nativeName = "Русский",
        isRtl = false,
        defaultRows = listOf(
            listOf("й", "ц", "у", "к", "е", "н", "г", "ш", "щ", "з", "х", "ъ"),
            listOf("ф", "ы", "в", "а", "п", "р", "о", "л", "д", "ж", "э"),
            listOf("я", "ч", "с", "м", "и", "т", "ь", "б", "ю")
        )
    )

    val URDU_LANGUAGE = KeyboardLanguage(
        code = "ur",
        name = "اردو",
        nativeName = "اردو",
        isRtl = true,
        defaultRows = listOf(
            listOf("ٹ", "پ", "ت", "ب", "ل", "ا", "ک", "د", "و", "ر", "ن", "م"),
            listOf("ش", "س", "ی", "ف", "ق", "ع", "ہ", "ج", "چ", "ح", "خ"),
            listOf("ژ", "ز", "ڑ", "ڈ", "ذ", "ص", "ض", "ط", "ظ", "گ")
        )
    )

    val PERSIAN_LANGUAGE = KeyboardLanguage(
        code = "fa",
        name = "فارسی",
        nativeName = "فارسی",
        isRtl = true,
        defaultRows = listOf(
            listOf("ض", "ص", "ث", "ق", "ف", "غ", "ع", "ه", "خ", "ح", "ج", "چ"),
            listOf("ش", "س", "ی", "ب", "ل", "ا", "ت", "ن", "م", "ک", "گ"),
            listOf("ظ", "ط", "ز", "ژ", "ر", "ذ", "د", "پ", "و")
        )
    )

    val ALL_SUPPORTED_LANGUAGES = listOf(
        ARABIC_LANGUAGE,
        ENGLISH_LANGUAGE,
        FRENCH_LANGUAGE,
        SPANISH_LANGUAGE,
        GERMAN_LANGUAGE,
        TURKISH_LANGUAGE,
        RUSSIAN_LANGUAGE,
        URDU_LANGUAGE,
        PERSIAN_LANGUAGE
    )

    val THEMES = listOf(
        KeyboardTheme(
            id = "dark_neon",
            name = "Dark Cyber Neon",
            backgroundColor = 0xFF0B0E17,
            surfaceColor = 0xFF141926,
            keyBackgroundColor = 0xFF1E2638,
            keyTextColor = 0xFFF1F5F9,
            specialKeyColor = 0xFF26334D,
            specialKeyTextColor = 0xFF00E5FF,
            accentColor = 0xFFFF2A5F,
            suggestionBgColor = 0xFF101420,
            isDark = true
        ),
        KeyboardTheme(
            id = "dark_amoled",
            name = "AMOLED Black",
            backgroundColor = 0xFF000000,
            surfaceColor = 0xFF121212,
            keyBackgroundColor = 0xFF1E1E1E,
            keyTextColor = 0xFFFFFFFF,
            specialKeyColor = 0xFF2A2A2A,
            specialKeyTextColor = 0xFF00E5FF,
            accentColor = 0xFF3DDC84,
            suggestionBgColor = 0xFF080808,
            isDark = true
        ),
        KeyboardTheme(
            id = "royal_purple",
            name = "Royal Velvet Purple",
            backgroundColor = 0xFF12081E,
            surfaceColor = 0xFF1E0E32,
            keyBackgroundColor = 0xFF2F164D,
            keyTextColor = 0xFFFFFFFF,
            specialKeyColor = 0xFF4A2078,
            specialKeyTextColor = 0xFFE9D5FF,
            accentColor = 0xFFC084FC,
            suggestionBgColor = 0xFF180A28,
            isDark = true
        ),
        KeyboardTheme(
            id = "light_minimal",
            name = "Light Minimal",
            backgroundColor = 0xFFF1F3F5,
            surfaceColor = 0xFFFFFFFF,
            keyBackgroundColor = 0xFFFFFFFF,
            keyTextColor = 0xFF1F2937,
            specialKeyColor = 0xFFE5E7EB,
            specialKeyTextColor = 0xFF374151,
            accentColor = 0xFF2563EB,
            suggestionBgColor = 0xFFF8FAFC,
            isDark = false
        ),
        KeyboardTheme(
            id = "emerald_matrix",
            name = "Emerald Matrix",
            backgroundColor = 0xFF04120A,
            surfaceColor = 0xFF092013,
            keyBackgroundColor = 0xFF103621,
            keyTextColor = 0xFFECFDF5,
            specialKeyColor = 0xFF1B4E30,
            specialKeyTextColor = 0xFF34D399,
            accentColor = 0xFF10B981,
            suggestionBgColor = 0xFF06180E,
            isDark = true
        ),
        KeyboardTheme(
            id = "sunset_glow",
            name = "Sunset Glow",
            backgroundColor = 0xFF1A0B1A,
            surfaceColor = 0xFF281128,
            keyBackgroundColor = 0xFF3D1B3D,
            keyTextColor = 0xFFFFF1F2,
            specialKeyColor = 0xFF582358,
            specialKeyTextColor = 0xFFF472B6,
            accentColor = 0xFFFB7185,
            suggestionBgColor = 0xFF200E20,
            isDark = true
        )
    )

    val EMOJI_CATEGORIES = listOf(
        "Smileys" to listOf(
            "😀", "😃", "😄", "😁", "😆", "😅", "😂", "🤣", "🥲", "🥹", "😊", "😇",
            "🙂", "🙃", "😉", "😌", "😍", "🥰", "😘", "😗", "😙", "😚", "😋", "😛",
            "😝", "😜", "🤪", "🤨", "🧐", "🤓", "😎", "🥸", "🤩", "🥳", "😏", "😒",
            "😞", "😔", "😟", "😕", "🙁", "☹️", "😣", "😖", "😫", "😩", "🥺", "😢",
            "😭", "😤", "😠", "😡", "🤬", "🤯", "😳", "🥵", "🥶", "😱", "😨", "😰"
        ),
        "Hands & Hearts" to listOf(
            "👍", "👎", "👏", "🙌", "👐", "🤲", "🤝", "🙏", "✌️", "🤞", "🤟", "🤘",
            "👌", "🤌", "🤏", "👈", "👉", "👆", "👇", "☝️", "✋", "🤚", "🖐️", "🖖",
            "❤️", "🧡", "💛", "💚", "💙", "💜", "🖤", "🤍", "🤎", "💔", "❤️‍🔥", "💖",
            "💗", "💓", "💞", "💕", "💌", "💘", "💝", "✨", "🔥", "💯", "🎉", "⭐"
        ),
        "Animals & Nature" to listOf(
            "🐱", "🐈", "🐶", "🐕", "🦊", "🐻", "🐼", "🐨", "🐯", "🦁", "🐮", "🐷",
            "🐸", "🐵", "🙈", "🙉", "🙊", "🐒", "🐔", "🐧", "🐦", "🦅", "🦉", "🦇",
            "🐺", "🐗", "🐴", "🦄", "🐝", "🪱", "🐛", "🦋", "🐌", "🐞", "🐜", "🌸",
            "🌺", "🌹", "🌷", "🌻", "🌼", "💐", "🌴", "🌲", "🌳", "🍀", "🍁", "🍂"
        ),
        "Food & Drink" to listOf(
            "🍎", "🍐", "🍊", "🍋", "🍌", "🍉", "🍇", "🍓", "🫐", "🍈", "🍒", "🍑",
            "🥭", "🍍", "🥥", "🥝", "🍅", "🥑", "🍕", "🍔", "🍟", "🌭", "🍿", "🥓",
            "🥪", "🥙", "🧆", "🌮", "🌯", "🥗", "🥘", "🍝", "🍜", "🍲", "🍛", "🍣",
            "🍱", "🥟", "🦪", "🍤", "🍙", "🍚", "🍘", "🍦", "🍧", "🍨", "🍩", "☕"
        ),
        "Kaomoji & Emoticons" to listOf(
            "(^_^)", "(^o^)", "(◕‿◕)", "(◠‿◠)", "(¬_¬)", "(ಥ﹏ಥ)",
            "( ͡° ͜ʖ ͡°)", "¯\\_(ツ)_/¯", "(╯°□°)╯︵ ┻━┻", "┬─┬ノ( º _ ºノ)",
            "(♥_♥)", "(つ≧▽≦)つ", "(づ｡◕‿‿◕｡)づ", "(ง •̀_•́)ง",
            "ʕ•ᴥ•ʔ", "(=^･ω･^=)", "(•‿•)", "(づ￣ ³￣)づ", "(❁´◡`❁)"
        )
    )

    val SAMPLE_GIFS = listOf(
        GifItem("g1", "Thoth Happy Dance", "https://media.giphy.com/media/artj92V8o75VPL7AeQ/giphy.gif", "https://media.giphy.com/media/artj92V8o75VPL7AeQ/giphy.gif", "Reaction"),
        GifItem("g2", "Thumbs Up Cat", "https://media.giphy.com/media/BzyTuYCmvSORqs1ABM/giphy.gif", "https://media.giphy.com/media/BzyTuYCmvSORqs1ABM/giphy.gif", "Happy"),
        GifItem("g3", "Anime Wow", "https://media.giphy.com/media/11ISwbgCxEzMyY/giphy.gif", "https://media.giphy.com/media/11ISwbgCxEzMyY/giphy.gif", "Anime"),
        GifItem("g4", "Love Hearts", "https://media.giphy.com/media/26FLdm964upanco60/giphy.gif", "https://media.giphy.com/media/26FLdm964upanco60/giphy.gif", "Love"),
        GifItem("g5", "Thinking Meme", "https://media.giphy.com/media/d3mlE7uhX8KFgEmY/giphy.gif", "https://media.giphy.com/media/d3mlE7uhX8KFgEmY/giphy.gif", "Reaction"),
        GifItem("g6", "Typing Fast Keyboard", "https://media.giphy.com/media/unQ3IJU2RG7DO/giphy.gif", "https://media.giphy.com/media/unQ3IJU2RG7DO/giphy.gif", "Meme")
    )
}
