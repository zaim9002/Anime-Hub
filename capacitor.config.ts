import type { CapacitorConfig } from '@capacitor/cli';

const config: CapacitorConfig = {
  appId: 'com.animehub.app',
  appName: 'Anime Hub',
  webDir: 'dist',
  bundledWebRuntime: false,
  android: {
    allowMixedContent: true,
    backgroundColor: '#0B0E17'
  },
  server: {
    androidScheme: 'https'
  }
};

export default config;
