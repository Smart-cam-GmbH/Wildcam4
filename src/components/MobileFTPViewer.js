export class MobileFTPViewer {
  constructor() {
    this.images = []
    this.isLoading = false
    this.container = null
    this.currentView = 'gallery'
    this.totalSize = 0
    this.downloadCount = 0
    
    // FTP Configuration
    this.ftpConfig = {
      host: '213.3.5.20',
      username: 'Wildcam',
      password: 'Quickcam_02',
      folder: '/'
    }

    // Touch handling for pull-to-refresh
    this.touchStartY = 0
    this.touchCurrentY = 0
    this.isPulling = false
  }

  mount(selector) {
    this.container = document.querySelector(selector)
    this.render()
    this.setupTouchHandlers()
    this.loadImages()
    
    // Make globally accessible
    window.mobileViewer = this
  }

  render() {
    this.container.innerHTML = `
      <div class="mobile-app">
        <header class="mobile-header">
          <div class="header-content">
            <h1 class="app-title">
              ${this.getCameraIcon()}
              Camera Images
            </h1>
            <div class="connection-status">
              <div class="status-dot ${this.getStatusClass()}"></div>
              <span>FTP Server</span>
            </div>
          </div>
          
          <div class="stats-bar">
            <div class="stat-item">
              <span class="stat-number">${this.images.length}</span>
              <span class="stat-label">Images</span>
            </div>
            <div class="stat-item">
              <span class="stat-number">${this.formatTotalSize()}</span>
              <span class="stat-label">Total Size</span>
            </div>
            <div class="stat-item">
              <span class="stat-number">${this.downloadCount}</span>
              <span class="stat-label">Downloaded</span>
            </div>
          </div>
        </header>
        
        <main class="mobile-content pull-to-refresh" id="main-content">
          <div class="pull-indicator" id="pull-indicator">
            ${this.getRefreshIcon()}
          </div>
          
          <div id="content-area">
            ${this.renderContent()}
          </div>
        </main>
        
        <nav class="bottom-nav">
          <button class="nav-btn ${this.currentView === 'gallery' ? 'active' : ''}" onclick="window.mobileViewer.switchView('gallery')">
            ${this.getGridIcon()}
            <span>Gallery</span>
          </button>
          <button class="nav-btn" onclick="window.mobileViewer.refreshImages()">
            ${this.getRefreshIcon()}
            <span>Refresh</span>
          </button>
          <button class="nav-btn ${this.currentView === 'settings' ? 'active' : ''}" onclick="window.mobileViewer.switchView('settings')">
            ${this.getSettingsIcon()}
            <span>Settings</span>
          </button>
        </nav>
      </div>
    `
  }

  renderContent() {
    if (this.currentView === 'settings') {
      return this.renderSettings()
    }

    if (this.isLoading) {
      return this.renderLoading()
    }

    if (this.images.length === 0) {
      return this.renderEmpty()
    }

    return this.renderGallery()
  }

  renderGallery() {
    return `
      <div class="mobile-grid">
        ${this.images.map((image, index) => `
          <div class="mobile-image-card" style="animation-delay: ${index * 0.05}s" onclick="window.mobileViewer.viewImage(${index})">
            <div class="mobile-image-container">
              <img src="${image.url}" alt="${image.name}" loading="lazy" />
              <div class="mobile-image-overlay">
                <div class="image-actions">
                  <button class="mobile-download-btn" onclick="event.stopPropagation(); window.mobileViewer.downloadImage('${image.url}', '${image.name}')" title="Download">
                    ${this.getDownloadIcon()}
                  </button>
                </div>
                <div class="mobile-image-info">
                  <div class="mobile-image-title">${image.name}</div>
                  <div class="mobile-image-size">${image.size}</div>
                </div>
              </div>
            </div>
          </div>
        `).join('')}
      </div>
    `
  }

  renderLoading() {
    return `
      <div class="mobile-loading">
        <div class="mobile-loading-spinner"></div>
        <div class="loading-text">Loading Images</div>
        <div class="loading-subtext">Connecting to FTP server...</div>
      </div>
      
      <div class="mobile-grid">
        ${Array(6).fill(0).map(() => `
          <div class="skeleton-card">
            <div class="skeleton skeleton-image"></div>
            <div class="skeleton skeleton-text"></div>
            <div class="skeleton skeleton-text short"></div>
          </div>
        `).join('')}
      </div>
    `
  }

  renderEmpty() {
    return `
      <div class="mobile-empty">
        ${this.getImageIcon()}
        <div class="empty-title">No Images Found</div>
        <div class="empty-subtitle">Pull down to refresh or check your FTP connection</div>
      </div>
    `
  }

  renderSettings() {
    return `
      <div style="padding: var(--spacing-md);">
        <div style="background: var(--surface); border-radius: var(--border-radius); padding: var(--spacing-lg); box-shadow: var(--shadow);">
          <h3 style="margin-bottom: var(--spacing-md); color: var(--text-primary);">FTP Settings</h3>
          
          <div style="margin-bottom: var(--spacing-md);">
            <label style="display: block; margin-bottom: var(--spacing-xs); color: var(--text-secondary); font-size: 0.9rem;">Server</label>
            <div style="padding: var(--spacing-sm); background: var(--background); border-radius: var(--border-radius-sm); color: var(--text-primary);">${this.ftpConfig.host}</div>
          </div>
          
          <div style="margin-bottom: var(--spacing-md);">
            <label style="display: block; margin-bottom: var(--spacing-xs); color: var(--text-secondary); font-size: 0.9rem;">Username</label>
            <div style="padding: var(--spacing-sm); background: var(--background); border-radius: var(--border-radius-sm); color: var(--text-primary);">${this.ftpConfig.username}</div>
          </div>
          
          <div style="margin-bottom: var(--spacing-md);">
            <label style="display: block; margin-bottom: var(--spacing-xs); color: var(--text-secondary); font-size: 0.9rem;">Folder</label>
            <div style="padding: var(--spacing-sm); background: var(--background); border-radius: var(--border-radius-sm); color: var(--text-primary);">${this.ftpConfig.folder}</div>
          </div>
          
          <button onclick="window.mobileViewer.testConnection()" style="width: 100%; padding: var(--spacing-md); background: var(--primary); color: white; border: none; border-radius: var(--border-radius-sm); font-weight: 600; cursor: pointer;">
            Test Connection
          </button>
        </div>
        
        <div style="background: var(--surface); border-radius: var(--border-radius); padding: var(--spacing-lg); box-shadow: var(--shadow); margin-top: var(--spacing-md);">
          <h3 style="margin-bottom: var(--spacing-md); color: var(--text-primary);">App Info</h3>
          <p style="color: var(--text-secondary); line-height: 1.6; margin-bottom: var(--spacing-sm);">Version 1.0.0</p>
          <p style="color: var(--text-secondary); line-height: 1.6; margin-bottom: var(--spacing-sm);">Mobile FTP Image Viewer</p>
          <p style="color: var(--text-secondary); line-height: 1.6;">Built for seamless image browsing and downloading from FTP servers.</p>
        </div>
      </div>
    `
  }

  setupTouchHandlers() {
    const mainContent = document.getElementById('main-content')
    if (!mainContent) return

    mainContent.addEventListener('touchstart', (e) => {
      if (mainContent.scrollTop === 0) {
        this.touchStartY = e.touches[0].clientY
        this.isPulling = true
      }
    }, { passive: true })

    mainContent.addEventListener('touchmove', (e) => {
      if (!this.isPulling) return

      this.touchCurrentY = e.touches[0].clientY
      const pullDistance = this.touchCurrentY - this.touchStartY

      if (pullDistance > 0 && pullDistance < 100) {
        const pullIndicator = document.getElementById('pull-indicator')
        if (pullIndicator) {
          pullIndicator.style.transform = `translateX(-50%) translateY(${pullDistance}px)`
          pullIndicator.style.opacity = Math.min(pullDistance / 60, 1)
        }
      }
    }, { passive: true })

    mainContent.addEventListener('touchend', (e) => {
      if (!this.isPulling) return

      const pullDistance = this.touchCurrentY - this.touchStartY
      const pullIndicator = document.getElementById('pull-indicator')

      if (pullDistance > 60) {
        this.refreshImages()
      }

      if (pullIndicator) {
        pullIndicator.style.transform = 'translateX(-50%) translateY(0)'
        pullIndicator.style.opacity = '0'
      }

      this.isPulling = false
      this.touchStartY = 0
      this.touchCurrentY = 0
    }, { passive: true })
  }

  async loadImages() {
    this.setLoading(true)
    
    try {
      await this.simulateFTPLoad()
      this.calculateTotalSize()
      this.setLoading(false)
      this.updateContent()
      this.showToast('Images loaded successfully!', 'success')
    } catch (error) {
      console.error('Error loading images:', error)
      this.setLoading(false)
      this.updateContent()
      this.showToast('Failed to load images', 'error')
    }
  }

  async simulateFTPLoad() {
    await new Promise(resolve => setTimeout(resolve, 2000))
    
    const mockImages = [
      { name: 'CAM_001.jpg', url: 'https://images.pexels.com/photos/1108099/pexels-photo-1108099.jpeg?auto=compress&cs=tinysrgb&w=600', size: '2.4 MB', bytes: 2516582 },
      { name: 'CAM_002.jpg', url: 'https://images.pexels.com/photos/147411/italy-mountains-dawn-daybreak-147411.jpeg?auto=compress&cs=tinysrgb&w=600', size: '3.1 MB', bytes: 3251200 },
      { name: 'CAM_003.jpg', url: 'https://images.pexels.com/photos/417074/pexels-photo-417074.jpeg?auto=compress&cs=tinysrgb&w=600', size: '1.8 MB', bytes: 1887436 },
      { name: 'CAM_004.jpg', url: 'https://images.pexels.com/photos/1287145/pexels-photo-1287145.jpeg?auto=compress&cs=tinysrgb&w=600', size: '2.7 MB', bytes: 2831155 },
      { name: 'CAM_005.jpg', url: 'https://images.pexels.com/photos/1323550/pexels-photo-1323550.jpeg?auto=compress&cs=tinysrgb&w=600', size: '3.5 MB', bytes: 3670016 },
      { name: 'CAM_006.jpg', url: 'https://images.pexels.com/photos/1366919/pexels-photo-1366919.jpeg?auto=compress&cs=tinysrgb&w=600', size: '2.9 MB', bytes: 3041280 },
      { name: 'CAM_007.jpg', url: 'https://images.pexels.com/photos/1624496/pexels-photo-1624496.jpeg?auto=compress&cs=tinysrgb&w=600', size: '4.2 MB', bytes: 4404019 },
      { name: 'CAM_008.jpg', url: 'https://images.pexels.com/photos/1761279/pexels-photo-1761279.jpeg?auto=compress&cs=tinysrgb&w=600', size: '1.6 MB', bytes: 1677721 },
      { name: 'CAM_009.jpg', url: 'https://images.pexels.com/photos/2662116/pexels-photo-2662116.jpeg?auto=compress&cs=tinysrgb&w=600', size: '2.1 MB', bytes: 2202009 },
      { name: 'CAM_010.jpg', url: 'https://images.pexels.com/photos/1591373/pexels-photo-1591373.jpeg?auto=compress&cs=tinysrgb&w=600', size: '3.8 MB', bytes: 3984588 }
    ]
    
    this.images = mockImages
  }

  calculateTotalSize() {
    this.totalSize = this.images.reduce((total, img) => total + img.bytes, 0)
  }

  formatTotalSize() {
    if (this.totalSize === 0) return '0 MB'
    const mb = this.totalSize / (1024 * 1024)
    return `${mb.toFixed(1)} MB`
  }

  refreshImages() {
    this.images = []
    this.totalSize = 0
    this.updateContent()
    this.loadImages()
  }

  switchView(view) {
    this.currentView = view
    this.render()
    if (view === 'gallery') {
      this.setupTouchHandlers()
    }
  }

  viewImage(index) {
    const image = this.images[index]
    this.showToast(`Viewing ${image.name}`, 'info')
    // In a real app, you might open a full-screen image viewer here
  }

  async downloadImage(url, filename) {
    try {
      this.showToast('Downloading...', 'info')
      
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
      
      this.downloadCount++
      this.updateStats()
      this.showToast('Downloaded successfully!', 'success')
    } catch (error) {
      console.error('Download error:', error)
      this.showToast('Download failed', 'error')
    }
  }

  testConnection() {
    this.showToast('Testing FTP connection...', 'info')
    setTimeout(() => {
      this.showToast('Connection successful!', 'success')
    }, 1500)
  }

  setLoading(loading) {
    this.isLoading = loading
  }

  updateContent() {
    const contentArea = document.getElementById('content-area')
    if (contentArea) {
      contentArea.innerHTML = this.renderContent()
    }
    this.updateStats()
  }

  updateStats() {
    const statNumbers = document.querySelectorAll('.stat-number')
    if (statNumbers.length >= 3) {
      statNumbers[0].textContent = this.images.length
      statNumbers[1].textContent = this.formatTotalSize()
      statNumbers[2].textContent = this.downloadCount
    }
  }

  getStatusClass() {
    if (this.isLoading) return 'loading'
    if (this.images.length === 0) return 'error'
    return ''
  }

  showToast(message, type = 'info') {
    const toast = document.createElement('div')
    toast.className = `mobile-toast ${type}`
    toast.innerHTML = `
      ${this.getToastIcon(type)}
      <span>${message}</span>
    `
    
    document.body.appendChild(toast)
    
    setTimeout(() => {
      toast.remove()
    }, 3000)
  }

  getToastIcon(type) {
    switch (type) {
      case 'success': return this.getCheckIcon()
      case 'error': return this.getXIcon()
      case 'warning': return this.getAlertIcon()
      default: return this.getInfoIcon()
    }
  }

  // SVG Icons
  getCameraIcon() {
    return `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M14.5 4h-5L7 7H4a2 2 0 0 0-2 2v9a2 2 0 0 0 2 2h16a2 2 0 0 0 2-2V9a2 2 0 0 0-2-2h-3l-2.5-3z"/><circle cx="12" cy="13" r="3"/></svg>`
  }

  getGridIcon() {
    return `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect width="7" height="7" x="3" y="3" rx="1"/><rect width="7" height="7" x="14" y="3" rx="1"/><rect width="7" height="7" x="3" y="14" rx="1"/><rect width="7" height="7" x="14" y="14" rx="1"/></svg>`
  }

  getRefreshIcon() {
    return `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M3 12a9 9 0 0 1 9-9 9.75 9.75 0 0 1 6.74 2.74L21 8"/><path d="M21 3v5h-5"/><path d="M21 12a9 9 0 0 1-9 9 9.75 9.75 0 0 1-6.74-2.74L3 16"/><path d="M8 16H3v5"/></svg>`
  }

  getSettingsIcon() {
    return `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M12.22 2h-.44a2 2 0 0 0-2 2v.18a2 2 0 0 1-1 1.73l-.43.25a2 2 0 0 1-2 0l-.15-.08a2 2 0 0 0-2.73.73l-.22.38a2 2 0 0 0 .73 2.73l.15.1a2 2 0 0 1 1 1.72v.51a2 2 0 0 1-1 1.74l-.15.09a2 2 0 0 0-.73 2.73l.22.38a2 2 0 0 0 2.73.73l.15-.08a2 2 0 0 1 2 0l.43.25a2 2 0 0 1 1 1.73V20a2 2 0 0 0 2 2h.44a2 2 0 0 0 2-2v-.18a2 2 0 0 1 1-1.73l.43-.25a2 2 0 0 1 2 0l.15.08a2 2 0 0 0 2.73-.73l.22-.39a2 2 0 0 0-.73-2.73l-.15-.08a2 2 0 0 1-1-1.74v-.5a2 2 0 0 1 1-1.74l.15-.09a2 2 0 0 0 .73-2.73l-.22-.38a2 2 0 0 0-2.73-.73l-.15.08a2 2 0 0 1-2 0l-.43-.25a2 2 0 0 1-1-1.73V4a2 2 0 0 0-2-2z"/><circle cx="12" cy="12" r="3"/></svg>`
  }

  getDownloadIcon() {
    return `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"/><polyline points="7,10 12,15 17,10"/><line x1="12" y1="15" x2="12" y2="3"/></svg>`
  }

  getImageIcon() {
    return `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect width="18" height="18" x="3" y="3" rx="2" ry="2"/><circle cx="9" cy="9" r="2"/><path d="m21 15-3.086-3.086a2 2 0 0 0-2.828 0L6 21"/></svg>`
  }

  getCheckIcon() {
    return `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M20 6 9 17l-5-5"/></svg>`
  }

  getXIcon() {
    return `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="m18 6-12 12"/><path d="m6 6 12 12"/></svg>`
  }

  getAlertIcon() {
    return `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="m21.73 18-8-14a2 2 0 0 0-3.48 0l-8 14A2 2 0 0 0 4 21h16a2 2 0 0 0 1.73-3Z"/><path d="M12 9v4"/><path d="m12 17.02.01 0"/></svg>`
  }

  getInfoIcon() {
    return `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"/><path d="m12 16-4-4 4-4"/><path d="M16 12H8"/></svg>`
  }
}
