const questionService = require('../../services/questionService');

const preferredTags = (bot, query, value) => {
    const chatId = query.message.chat.id;

    if (!value) {
        bot.sendMessage(chatId, '❌ Не удалось определить ваш выбор. Попробуйте снова.');
        return;
    }

    console.log(`[preferredTags.js] Выбрано значение: ${value}`);

    questionService.handleInlineAnswer(bot, query, 'preferredTags', value);
};

module.exports = { preferredTags };