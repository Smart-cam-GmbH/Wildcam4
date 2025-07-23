export class FTPImageViewer {
  constructor() {
    this.images = []
    this.isLoading = false
    this.container = null
    
    // FTP Configuration
    this.ftpConfig = {
      host: '213.3.5.20',
      username: 'Wildcam',
      password: 'Quickcam_02',
      folder: '/'
    }
  }

  mount(selector) {
    this.container = document.querySelector(selector)
    this.render()
    this.loadImages()
  }

  render() {
    this.container.innerHTML = `
      <div class="app-container">
        <header class="header">
          <h1>Camera Images</h1>
        </header>
        
        <main class="main-content">
          <div class="status-card">
            <div class="status-icon">
              ${this.getCloudIcon()}
            </div>
            <div class="status-content">
              <div class="status-title">FTP Connection</div>
              <div class="status-subtitle" id="status-text">
                ${this.isLoading ? 'Loading images from server...' : 'Ready to load images'}
              </div>
            </div>
            <div id="loading-indicator" style="display: ${this.isLoading ? 'block' : 'none'}">
              <div class="loading-spinner"></div>
            </div>
          </div>
          
          <div id="images-container">
            ${this.renderImages()}
          </div>
        </main>
        
        <button class="fab" id="refresh-btn" title="Refresh Images">
          ${this.getRefreshIcon()}
        </button>
      </div>
    `
    
    this.attachEventListeners()
  }

  renderImages() {
    if (this.images.length === 0) {
      return `
        <div class="empty-state">
          ${this.getImageIcon()}
          <h3>No Images Found</h3>
          <p>Click the refresh button to load images from the FTP server</p>
        </div>
      `
    }

    return `
      <div class="images-grid">
        ${this.images.map((image, index) => `
          <div class="image-card" style="animation-delay: ${index * 0.1}s">
            <div class="image-container">
              <img src="${image.url}" alt="${image.name}" loading="lazy" />
              <div class="image-overlay">
                <button class="download-btn" onclick="window.ftpViewer.downloadImage('${image.url}', '${image.name}')" title="Download Image">
                  ${this.getDownloadIcon()}
                </button>
              </div>
            </div>
            <div class="image-info">
              <div class="image-title">${image.name}</div>
              <div class="image-size">${image.size}</div>
            </div>
          </div>
        `).join('')}
      </div>
    `
  }

  attachEventListeners() {
    const refreshBtn = document.getElementById('refresh-btn')
    refreshBtn.addEventListener('click', () => this.refreshImages())
    
    // Make this instance globally accessible for the download function
    window.ftpViewer = this
  }

  async loadImages() {
    this.setLoading(true, 'Connecting to FTP server...')
    
    try {
      // Simulate FTP connection and image loading
      // In a real implementation, you'd need a backend service to handle FTP
      await this.simulateFTPLoad()
      
      this.setLoading(false, `${this.images.length} images loaded successfully`)
      this.updateImagesContainer()
      
    } catch (error) {
      console.error('Error loading images:', error)
      this.setLoading(false, 'Error loading images')
      this.showToast('Failed to load images from FTP server', 'error')
    }
  }

  async simulateFTPLoad() {
    // Simulate network delay
    await new Promise(resolve => setTimeout(resolve, 2000))
    
    // Mock image data (in real app, this would come from FTP server)
    const mockImages = [
      {
        name: 'IMG_001.jpg',
        url: 'https://images.pexels.com/photos/1108099/pexels-photo-1108099.jpeg?auto=compress&cs=tinysrgb&w=400',
        size: '2.4 MB'
      },
      {
        name: 'IMG_002.jpg',
        url: 'https://images.pexels.com/photos/147411/italy-mountains-dawn-daybreak-147411.jpeg?auto=compress&cs=tinysrgb&w=400',
        size: '3.1 MB'
      },
      {
        name: 'IMG_003.jpg',
        url: 'https://images.pexels.com/photos/417074/pexels-photo-417074.jpeg?auto=compress&cs=tinysrgb&w=400',
        size: '1.8 MB'
      },
      {
        name: 'IMG_004.jpg',
        url: 'https://images.pexels.com/photos/1287145/pexels-photo-1287145.jpeg?auto=compress&cs=tinysrgb&w=400',
        size: '2.7 MB'
      },
      {
        name: 'IMG_005.jpg',
        url: 'https://images.pexels.com/photos/1323550/pexels-photo-1323550.jpeg?auto=compress&cs=tinysrgb&w=400',
        size: '3.5 MB'
      },
      {
        name: 'IMG_006.jpg',
        url: 'https://images.pexels.com/photos/1366919/pexels-photo-1366919.jpeg?auto=compress&cs=tinysrgb&w=400',
        size: '2.9 MB'
      },
      {
        name: 'IMG_007.jpg',
        url: 'https://images.pexels.com/photos/1624496/pexels-photo-1624496.jpeg?auto=compress&cs=tinysrgb&w=400',
        size: '4.2 MB'
      },
      {
        name: 'IMG_008.jpg',
        url: 'https://images.pexels.com/photos/1761279/pexels-photo-1761279.jpeg?auto=compress&cs=tinysrgb&w=400',
        size: '1.6 MB'
      }
    ]
    
    this.images = mockImages
  }

  refreshImages() {
    this.images = []
    this.updateImagesContainer()
    this.loadImages()
  }

  setLoading(loading, message) {
    this.isLoading = loading
    const statusText = document.getElementById('status-text')
    const loadingIndicator = document.getElementById('loading-indicator')
    
    if (statusText) statusText.textContent = message
    if (loadingIndicator) {
      loadingIndicator.style.display = loading ? 'block' : 'none'
    }
  }

  updateImagesContainer() {
    const container = document.getElementById('images-container')
    if (container) {
      container.innerHTML = this.renderImages()
    }
  }

  async downloadImage(url, filename) {
    try {
      this.showToast('Downloading image...', 'info')
      
      const response = await fetch(url)
      const blob = await response.blob()
      
      const downloadUrl = window.URL.createObjectURL(blob)
      const link = document.createElement('a')
      link.href = downloadUrl
      link.download = filename
      document.body.appendChild(link)
      link.click()
      document.body.removeChild(link)
      window.URL.revokeObjectURL(downloadUrl)
      
      this.showToast('Image downloaded successfully!', 'success')
    } catch (error) {
      console.error('Download error:', error)
      this.showToast('Failed to download image', 'error')
    }
  }

  showToast(message, type = 'info') {
    const toast = document.createElement('div')
    toast.className = `toast ${type}`
    toast.textContent = message
    
    document.body.appendChild(toast)
    
    setTimeout(() => {
      toast.remove()
    }, 3000)
  }

  // SVG Icons
  getCloudIcon() {
    return `
      <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
        <path d="M17.5 19H9a7 7 0 1 1 6.71-9h1.79a4.5 4.5 0 1 1 0 9Z"/>
        <path d="M12 13v8l-4-4"/>
        <path d="m12 21 4-4"/>
      </svg>
    `
  }

  getRefreshIcon() {
    return `
      <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
        <path d="M3 12a9 9 0 0 1 9-9 9.75 9.75 0 0 1 6.74 2.74L21 8"/>
        <path d="M21 3v5h-5"/>
        <path d="M21 12a9 9 0 0 1-9 9 9.75 9.75 0 0 1-6.74-2.74L3 16"/>
        <path d="M8 16H3v5"/>
      </svg>
    `
  }

  getDownloadIcon() {
    return `
      <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
        <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"/>
        <polyline points="7,10 12,15 17,10"/>
        <line x1="12" y1="15" x2="12" y2="3"/>
      </svg>
    `
  }

  getImageIcon() {
    return `
      <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
        <rect width="18" height="18" x="3" y="3" rx="2" ry="2"/>
        <circle cx="9" cy="9" r="2"/>
        <path d="m21 15-3.086-3.086a2 2 0 0 0-2.828 0L6 21"/>
      </svg>
    `
  }
}
