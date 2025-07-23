const CACHE_NAME = 'camera-images-v1'
const urlsToCache = [
  '/',
  '/src/main.js',
  '/src/style.css',
  '/src/components/MobileFTPViewer.js',
  '/manifest.json'
]

self.addEventListener('install', (event) => {
  event.waitUntil(
    caches.open(CACHE_NAME)
      .then((cache) => cache.addAll(urlsToCache))
  )
})

self.addEventListener('fetch', (event) => {
  event.respondWith(
    caches.match(event.request)
      .then((response) => {
        if (response) {
          return response
        }
        return fetch(event.request)
      })
  )
})
