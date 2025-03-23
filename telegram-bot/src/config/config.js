require('dotenv').config({ path: '../.env' });


console.log('config.js -> TELEGRAM_BOT_TOKEN:', process.env.TELEGRAM_BOT_TOKEN);
console.log('config.js -> BACKEND_URL:', process.env.BACKEND_URL);

module.exports = {
    telegramBotToken: process.env.TELEGRAM_BOT_TOKEN,
    backendUrl: process.env.BACKEND_URL
};