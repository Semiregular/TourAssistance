const { app, BrowserWindow, ipcMain } = require("electron/main");
const path = require("node:path");
let mainWindow = null;

app.on("ready", () => {
  mainWindow = new BrowserWindow({
    width: 1200,
    height: 800,
    titleBarStyle: "hidden",
    // titleBarOverlay: {
    //   color: 'rgb(255,255,255)',
    //   symbolColor: 'rgb(241,31,31)',
    //   height: 40,
    // },
    webPreferences: {
      preload: path.join(__dirname, "preload.js"),
    },
  });

  mainWindow.loadFile("index.html");
  mainWindow.on("closed", () => {
    mainWindow = null;
  });
});

ipcMain.on("close", (event) => {
  const focusedWindow = BrowserWindow.getFocusedWindow();
  if (focusedWindow) {
    focusedWindow.close();
  }
});

ipcMain.on("min", (event) => {
  const focusedWindow = BrowserWindow.getFocusedWindow();
  if (focusedWindow) {
    focusedWindow.minimize();
  }
});

ipcMain.on("max", (event) => {
  const focusedWindow = BrowserWindow.getFocusedWindow();
  if (focusedWindow) {
    if (focusedWindow.isMaximized()) {
      focusedWindow.unmaximize();
    } else {
      focusedWindow.maximize();
    }
  }
});
