const fs = require("fs");
const path = require("path");

const callbacks = {};

const loadCallbacks = (dir) => {
    fs.readdirSync(dir).forEach((file) => {
        const fullPath = path.join(dir, file);
        if (fs.statSync(fullPath).isDirectory()) {
            loadCallbacks(fullPath);
        } else if (file.endsWith(".js")) {
            const callbackName = file.replace(".js", "");
            const callbackModule = require(fullPath);

            if (!callbackModule[callbackName]) {
                console.warn(`⚠ Нет обработчика в файле ${file}`);
            } else {
                callbacks[callbackName] = callbackModule[callbackName];
                console.log(`✔ Загружен коллбэк: ${callbackName}`);
            }
        }
    });
};

loadCallbacks(path.join(__dirname, "../callbacks"));

module.exports = { callbacks };
