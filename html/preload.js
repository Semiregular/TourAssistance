const { ipcRenderer } = require('electron/renderer')

window.addEventListener("DOMContentLoaded", () => {
  const elClose = document.getElementById("close");
  elClose.addEventListener("click", () => {
    ipcRenderer.send("close", true);
  });

  const elMinimize = document.getElementById("min");
  elMinimize.addEventListener("click", () => {
    ipcRenderer.send("min", true);
  });

  const elMaximize = document.getElementById("max");
  elMaximize.addEventListener("click", () => {
    ipcRenderer.send("max", true);
  });
});

