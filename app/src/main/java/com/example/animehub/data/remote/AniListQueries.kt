package com.example.animehub.data.remote

object AniListQueries {

    val MEDIA_FIELDS = """
        id
        title {
            romaji
            english
            native
        }
        coverImage {
            extraLarge
            large
            medium
        }
        bannerImage
        description(asHtml: false)
        averageScore
        format
        status
        episodes
        duration
        genres
        season
        seasonYear
        studios(isMain: true) {
            nodes {
                name
            }
        }
        nextAiringEpisode {
            episode
            airingAt
        }
    """.trimIndent()

    val HOME_FEED_QUERY = """
        query (${'$'}page: Int, ${'$'}perPage: Int) {
            trending: Page(page: ${'$'}page, perPage: ${'$'}perPage) {
                media(type: ANIME, sort: TRENDING_DESC) {
                    $MEDIA_FIELDS
                }
            }
            popular: Page(page: ${'$'}page, perPage: ${'$'}perPage) {
                media(type: ANIME, sort: POPULARITY_DESC) {
                    $MEDIA_FIELDS
                }
            }
            airing: Page(page: ${'$'}page, perPage: ${'$'}perPage) {
                media(type: ANIME, status: RELEASING, sort: POPULARITY_DESC) {
                    $MEDIA_FIELDS
                }
            }
            movies: Page(page: ${'$'}page, perPage: ${'$'}perPage) {
                media(type: ANIME, format: MOVIE, sort: POPULARITY_DESC) {
                    $MEDIA_FIELDS
                }
            }
        }
    """.trimIndent()

    val SEARCH_QUERY = """
        query (
            ${'$'}search: String,
            ${'$'}genre: String,
            ${'$'}year: Int,
            ${'$'}season: MediaSeason,
            ${'$'}status: MediaStatus,
            ${'$'}format: MediaFormat,
            ${'$'}sort: [MediaSort],
            ${'$'}page: Int,
            ${'$'}perPage: Int
        ) {
            Page(page: ${'$'}page, perPage: ${'$'}perPage) {
                pageInfo {
                    hasNextPage
                    currentPage
                }
                media(
                    type: ANIME,
                    search: ${'$'}search,
                    genre: ${'$'}genre,
                    seasonYear: ${'$'}year,
                    season: ${'$'}season,
                    status: ${'$'}status,
                    format: ${'$'}format,
                    sort: ${'$'}sort
                ) {
                    $MEDIA_FIELDS
                }
            }
        }
    """.trimIndent()

    val DETAILS_QUERY = """
        query (${'$'}id: Int) {
            Media(id: ${'$'}id, type: ANIME) {
                $MEDIA_FIELDS
                characters(sort: ROLE, perPage: 12) {
                    nodes {
                        id
                        name {
                            full
                            native
                        }
                        image {
                            large
                            medium
                        }
                    }
                    edges {
                        role
                    }
                }
                relations {
                    nodes {
                        $MEDIA_FIELDS
                    }
                }
                recommendations(perPage: 10, sort: RATING_DESC) {
                    nodes {
                        mediaRecommendation {
                            $MEDIA_FIELDS
                        }
                    }
                }
            }
        }
    """.trimIndent()

    val GENRES = listOf(
        "Action", "Adventure", "Comedy", "Drama", "Fantasy",
        "Horror", "Mystery", "Romance", "Sci-Fi", "Sports",
        "Supernatural", "Slice of Life", "Psychological", "Music",
        "Thriller", "Mecha", "Mahou Shoujo"
    )
}
