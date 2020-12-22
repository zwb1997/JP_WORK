const { app, BrowserWindow } = require('electron')

function createWindow () {
  const win = new BrowserWindow({
    width: 800,
    height: 600,
    titleBarStyle: 'customButtonsOnHover',
    show:false,// Avoid flicker
    webPreferences: {
      nodeIntegration: true
    }
  })
  win.setMenu(null);
  win.setTitle("app");
  win.loadFile('./build/index.html')
  win.once('ready-to-show', () => {
    win.show()
  })
}

app.whenReady().then(createWindow)

app.on('window-all-closed', () => {
  if (process.platform !== 'darwin') {
    app.quit()
  }
})

app.on('activate', () => {
  if (BrowserWindow.getAllWindows().length === 0) {
    createWindow()
  }
})