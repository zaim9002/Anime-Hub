/**
 * Anime Hub - AniList GraphQL API & Jikan Fallback Service
 */

export interface AnimeMedia {
  id: number;
  title: {
    romaji: string;
    english: string;
    native: string;
  };
  coverImage: {
    extraLarge?: string;
    large: string;
    medium: string;
  };
  bannerImage?: string;
  description: string;
  averageScore?: number;
  format: string;
  status: string;
  episodes?: number;
  duration?: number;
  genres: string[];
  season?: string;
  seasonYear?: number;
  studios?: { nodes: { name: string }[] };
}

const ANILIST_URL = 'https://graphql.anilist.co';

export async function fetchTrendingAnime(page = 1, perPage = 15): Promise<AnimeMedia[]> {
  const query = `
    query ($page: Int, $perPage: Int) {
      Page(page: $page, perPage: $perPage) {
        media(type: ANIME, sort: TRENDING_DESC) {
          id
          title { romaji english native }
          coverImage { extraLarge large medium }
          bannerImage
          description(asHtml: false)
          averageScore
          format
          status
          episodes
          duration
          genres
        }
      }
    }
  `;

  try {
    const res = await fetch(ANILIST_URL, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json', 'Accept': 'application/json' },
      body: JSON.stringify({ query, variables: { page, perPage } })
    });
    const json = await res.json();
    return json.data?.Page?.media || [];
  } catch (err) {
    console.error('AniList API error, fallback to Jikan', err);
    const jikanRes = await fetch('https://api.jikan.moe/v4/top/anime?filter=bypopularity&limit=15');
    const jikanJson = await jikanRes.json();
    return (jikanJson.data || []).map((item: any) => ({
      id: item.mal_id,
      title: { romaji: item.title, english: item.title_english || item.title, native: item.title_japanese || '' },
      coverImage: { large: item.images?.jpg?.large_image_url || '' },
      description: item.synopsis || '',
      averageScore: item.score ? Math.round(item.score * 10) : undefined,
      format: item.type || 'TV',
      status: item.status || 'FINISHED',
      episodes: item.episodes,
      genres: (item.genres || []).map((g: any) => g.name)
    }));
  }
}

export async function searchAnime(query: string, genre?: string, year?: number): Promise<AnimeMedia[]> {
  const gqlQuery = `
    query ($search: String, $genre: String, $year: Int) {
      Page(page: 1, perPage: 20) {
        media(type: ANIME, search: $search, genre: $genre, seasonYear: $year, sort: POPULARITY_DESC) {
          id
          title { romaji english native }
          coverImage { large }
          description(asHtml: false)
          averageScore
          format
          status
          episodes
          genres
        }
      }
    }
  `;

  const res = await fetch(ANILIST_URL, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({
      query: gqlQuery,
      variables: {
        search: query ? query.trim() : undefined,
        genre: genre || undefined,
        year: year || undefined
      }
    })
  });
  const json = await res.json();
  return json.data?.Page?.media || [];
}
