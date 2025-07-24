// Service worker used with vite-plugin-pwa (injectManifest strategy).
// The plugin will replace `self.__WB_MANIFEST` with the list of build assets
// so that the correct hashed files are cached for offline usage.
import { precacheAndRoute } from 'workbox-precaching'

precacheAndRoute(self.__WB_MANIFEST)