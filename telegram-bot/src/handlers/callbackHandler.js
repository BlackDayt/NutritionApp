const fs = require('fs');
const path = require('path');

console.log('🔍 callbackHandler.js загружен'); // Отладка

const handlers = {};

// Функция для рекурсивного поиска всех JS-файлов в папке handlers
const loadHandlers = (dir) => {
    fs.readdirSync(dir).forEach((file) => {
        const fullPath = path.join(dir, file);
        if (fs.statSync(fullPath).isDirectory()) {
            // Если это папка — рекурсивно загружаем её содержимое
            loadHandlers(fullPath);
        } else if (file.endsWith('.js')) {
            const handlerName = file.replace('.js', ''); // Убираем .js
            const handlerModule = require(fullPath);

            // ✅ Пропускаем сам `callbackHandler.js`, чтобы не загружать его
            if (handlerName === 'callbackHandler') {
                console.log('⏭ Пропускаем callbackHandler.js...');
                return;
            }

            if (!handlerModule || !handlerModule[handlerName]) {
                console.warn(`⚠ Ошибка загрузки обработчика ${handlerName}:`, handlerModule);
            } else {
                console.log(`✔ Загружен обработчик: ${handlerName} ->`, handlerModule[handlerName]);
                handlers[handlerName] = handlerModule[handlerName];
            }
        }
    });
};

// Загружаем все хэндлеры из папки /handlers (и подпапок)
loadHandlers(__dirname); // + "/handlers"

const callbackHandler = (bot) => {
    bot.on('callback_query', async (query) => {
        const handler = handlers[query.data]; // Берем обработчик по callback_data

        if (handler) {
            await handler(bot, query);
        } else {
            console.warn('Нет обработчика для:', query.data);
            await bot.answerCallbackQuery(query.id, {text: 'Неизвестная команда'});
        }
    });
};

module.exports = { callbackHandler };