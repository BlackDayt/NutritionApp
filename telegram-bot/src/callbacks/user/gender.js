const questionService = require('../../services/questionService');

const gender = (bot, query, value) => {
    const chatId = query.message.chat.id;

    if (!value) {
        bot.sendMessage(chatId, '❌ Не удалось определить ваш выбор. Попробуйте снова.');
        return;
    }

    console.log(`[gender.js] Выбрано значение: ${value}`);

    questionService.handleInlineAnswer(bot, chatId, 'gender', value);
};

module.exports = { gender };
