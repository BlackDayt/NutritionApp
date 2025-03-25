const { callbacks } = require("../core/callbacksLoader");

const callbackHandler = (bot) => {
    bot.on('callback_query', async (query) => {
        const [key, value] = query.data.split(':');

        console.log(`[callbackHandler] key: ${key}, value: ${value}`);

        const handler = callbacks[key]; // Берем обработчик по callback_data

        if (handler) {
            await handler(bot, query, value);
        } else {
            console.warn(`Нет обработчика для: ${query.data}`);
            await bot.answerCallbackQuery(query.id, {text: 'Неизвестная команда'});
        }
    });
};

module.exports = { callbackHandler };