require('dotenv').config();
// require('dotenv').config({ path: '../.env' });


const TelegramBot = require('node-telegram-bot-api');
const { telegramBotToken } = require('./config/config');
const { commandHandler } = require('./handlers/commandHandler');
const { callbackHandler } = require('./handlers/callbackHandler');
const { messageHandler } = require('./handlers/messageHandler')

// Подключаем токен и URL бэкенда
const bot = new TelegramBot(telegramBotToken, { polling: true });



console.log('Бот запущен...');

bot.on('polling_error', (error) => {
    console.error('Ошибка поллинга:', error.code, error.message);
});

commandHandler(bot);
callbackHandler(bot);
messageHandler(bot);
