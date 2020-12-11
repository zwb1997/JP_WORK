const electron = require('electron');
const { app, BrowserWindow } = require('electron')
function createWindow () {
  const win = new BrowserWindow({
    width: 960,
    height: 800,
    show:false,
    webPreferences: {
      nodeIntegration: true
    }
  });
  win.once('ready-to-show',()=>{
    win.show();
  })
  // win.setMenu(null);
  win.loadFile('../desktop_app/build/index.html')
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