package com.example.animehub.data.firestore

import com.example.animehub.data.admin.FirestoreAnime

object CuratedInitialAnime {
    fun getInitialAnimeList(): List<FirestoreAnime> = listOf(
        FirestoreAnime(
            id = 101,
            titleArabic = "قاتل الشياطين: قلعة اللانهاية",
            titleEnglish = "Demon Slayer: Kimetsu no Yaiba",
            titleRomaji = "Kimetsu no Yaiba",
            description = "تدور القصة حول تانجيرو كامادو الذي يشرع في رحلة محفوفة بالمخاطر لإنقاذ أخته نيزوكو بعد تحولها إلى شيطانة وللانتقام لعائلته التي أبادها ملك الشياطين موزان كيبوتسوجي في معارك ملحمية داخل قلعة اللانهاية.",
            coverImage = "https://images.unsplash.com/photo-1578632767115-351597cf2477?w=600&auto=format&fit=crop&q=80",
            bannerImage = "https://images.unsplash.com/photo-1607604276583-eef5d076aa5f?w=1200&auto=format&fit=crop&q=80",
            screenshots = listOf(
                "https://images.unsplash.com/photo-1578632767115-351597cf2477?w=800&auto=format&fit=crop&q=80",
                "https://images.unsplash.com/photo-1607604276583-eef5d076aa5f?w=800&auto=format&fit=crop&q=80"
            ),
            genres = listOf("أكشن", "شياطين", "خيالي", "تاريخي", "شونين"),
            format = "TV",
            status = "مستمر",
            seasonYear = 2026,
            seasonName = "خريف 2026",
            studios = listOf("Ufotable"),
            averageScore = 91,
            viewsCount = 48200L,
            isPublished = true,
            isFeatured = true,
            isNew = true,
            seasonsCount = 4,
            episodesCount = 26
        ),
        FirestoreAnime(
            id = 102,
            titleArabic = "سولو ليفلينج: ارتقاء الظلال",
            titleEnglish = "Solo Leveling: Arise from the Shadow",
            titleRomaji = "Ore dake Level Up na Ken",
            description = "في عالم ظهرت فيه بوابات تربط بين البشر والوحوش، يعيش سونغ جين وو كأضعف صياد في البشرية حتى يدخل زنزانة مزدوجة غامضة ويحصل على نظام ترقية لا نهائي يجعله أقوى كائن على الإطلاق.",
            coverImage = "https://images.unsplash.com/photo-1563089145-599997674d42?w=600&auto=format&fit=crop&q=80",
            bannerImage = "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=1200&auto=format&fit=crop&q=80",
            screenshots = listOf(
                "https://images.unsplash.com/photo-1563089145-599997674d42?w=800&auto=format&fit=crop&q=80"
            ),
            genres = listOf("أكشن", "مغامرات", "فانتازيا", "قوى خارقة"),
            format = "TV",
            status = "مستمر",
            seasonYear = 2026,
            seasonName = "شتاء 2026",
            studios = listOf("A-1 Pictures"),
            averageScore = 89,
            viewsCount = 35900L,
            isPublished = true,
            isFeatured = true,
            isNew = true,
            seasonsCount = 2,
            episodesCount = 12
        ),
        FirestoreAnime(
            id = 103,
            titleArabic = "جوجوتسو كايسن: صراع الشيبويا",
            titleEnglish = "Jujutsu Kaisen Season 3",
            titleRomaji = "Jujutsu Kaisen",
            description = "يواجه يوجي إيتادوري وزملاؤه في ثانوية الجوجوتسو طوفاناً من اللعنات والكيانات الشريرة بقيادة غيتو في معركة حاسمة لتحديد مصير عالم السحرة وإنقاذ المعلم غوجو ساتورو.",
            coverImage = "https://images.unsplash.com/photo-1534447677768-be436bb09401?w=600&auto=format&fit=crop&q=80",
            bannerImage = "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=1200&auto=format&fit=crop&q=80",
            screenshots = listOf(
                "https://images.unsplash.com/photo-1534447677768-be436bb09401?w=800&auto=format&fit=crop&q=80"
            ),
            genres = listOf("أكشن", "سحر", "رعب", "شونين"),
            format = "TV",
            status = "مستمر",
            seasonYear = 2026,
            seasonName = "صيف 2026",
            studios = listOf("MAPPA"),
            averageScore = 90,
            viewsCount = 52100L,
            isPublished = true,
            isFeatured = true,
            isNew = false,
            seasonsCount = 3,
            episodesCount = 24
        ),
        FirestoreAnime(
            id = 104,
            titleArabic = "ون بيس: معركة إيغ هيد والجزيرة المستقبلية",
            titleEnglish = "One Piece: Egghead Island Arc",
            titleRomaji = "One Piece",
            description = "يصل طاقم قبعة القش بقيادة لوفي إلى جزيرة المستقبل إيغ هيد ليلتقوا بالعالم العبقري فيغابانك ويكتشفوا أسرار القرن الغائب وصراعات حكومة العالم في واحدة من أكثر الفصول إثارة في تاريخ الأنمي.",
            coverImage = "https://images.unsplash.com/photo-1514565131-fce0801e5785?w=600&auto=format&fit=crop&q=80",
            bannerImage = "https://images.unsplash.com/photo-1509198397868-475647b2a1e5?w=1200&auto=format&fit=crop&q=80",
            screenshots = listOf(
                "https://images.unsplash.com/photo-1514565131-fce0801e5785?w=800&auto=format&fit=crop&q=80"
            ),
            genres = listOf("مغامرات", "كوميديا", "دراما", "شونين", "قوى خارقة"),
            format = "TV",
            status = "مستمر",
            seasonYear = 2026,
            seasonName = "مستمر",
            studios = listOf("Toei Animation"),
            averageScore = 93,
            viewsCount = 94300L,
            isPublished = true,
            isFeatured = true,
            isNew = true,
            seasonsCount = 1,
            episodesCount = 1100
        )
    )
}
