/**
 * Anime Hub - Video Sources Configuration
 * 
 * Configure your licensed CDN, private streaming servers, or custom video APIs.
 * Supports HLS (m3u8), MP4, multiple bitrates, WebVTT/SRT subtitles, and authentication headers.
 */

export interface SubtitleTrackConfig {
  id: string;
  language: string; // 'ar', 'en', etc.
  label: string;
  url: string;
  isDefault?: boolean;
}

export interface VideoSourceConfig {
  id: string;
  animeId: number;
  episodeNumber: number;
  url: string;
  type: 'hls' | 'mp4';
  quality: string;
  title: string;
  subtitles?: SubtitleTrackConfig[];
  headers?: Record<string, string>;
}

export interface VideoProviderConfig {
  baseCdnUrl?: string;
  apiEndpoint?: string;
  enableLicensedSamples: boolean;
  defaultQuality: '1080p' | '720p' | '480p' | 'auto';
  autoNextEpisode: boolean;
}

export const defaultProviderConfig: VideoProviderConfig = {
  baseCdnUrl: process.env.VITE_ANIME_CDN_URL || '',
  apiEndpoint: process.env.VITE_ANIME_API_ENDPOINT || '',
  enableLicensedSamples: true,
  defaultQuality: '1080p',
  autoNextEpisode: true,
};

/**
 * Open-source and legally licensed demonstration streams for player verification.
 */
export const licensedDemoSources: VideoSourceConfig[] = [
  {
    id: 'demo-hls-1',
    animeId: 0,
    episodeNumber: 1,
    url: 'https://test-streams.mux.dev/x36xhzz/x36xhzz.m3u8',
    type: 'hls',
    quality: '1080p Multi-Bitrate',
    title: 'Licensed Demo Stream (HLS)',
    subtitles: [
      {
        id: 'sub-ar',
        language: 'ar',
        label: 'العربية',
        url: 'https://bitmovin-a.akamaihd.net/content/sintel/subtitles/subtitles_en.vtt',
        isDefault: true,
      },
      {
        id: 'sub-en',
        language: 'en',
        label: 'English',
        url: 'https://bitmovin-a.akamaihd.net/content/sintel/subtitles/subtitles_en.vtt',
      }
    ]
  },
  {
    id: 'demo-mp4-1',
    animeId: 0,
    episodeNumber: 1,
    url: 'https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4',
    type: 'mp4',
    quality: '720p HD',
    title: 'Licensed Demo Stream (MP4)'
  }
];

export function resolveVideoSource(animeId: number, episodeNumber: number): VideoSourceConfig | null {
  // If private backend is connected, resolve URL dynamically:
  if (defaultProviderConfig.baseCdnUrl) {
    return {
      id: `cdn-${animeId}-${episodeNumber}`,
      animeId,
      episodeNumber,
      url: `${defaultProviderConfig.baseCdnUrl}/${animeId}/${episodeNumber}/master.m3u8`,
      type: 'hls',
      quality: '1080p',
      title: `خادم البث الرسمي - حلقة ${episodeNumber}`
    };
  }
  return null;
}
