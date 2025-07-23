import './style.css'
import { MobileFTPViewer } from './components/MobileFTPViewer.js'

// Register service worker for PWA
if ('serviceWorker' in navigator) {
  window.addEventListener('load', () => {
    navigator.serviceWorker.register('/sw.js')
      .then((registration) => {
        console.log('SW registered: ', registration)
      })
      .catch((registrationError) => {
        console.log('SW registration failed: ', registrationError)
      })
  })
}

document.querySelector('#app').innerHTML = `
  <div id="mobile-viewer"></div>
`

const viewer = new MobileFTPViewer()
viewer.mount('#mobile-viewer')
