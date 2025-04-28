const questionService = require('../../../services/questionService');

const preferred_tags = async (bot, query, value) => {
    const chatId = query.message.chat.id;

    if (!value) {
        bot.sendMessage(chatId, '❌ Не удалось определить ваш выбор. Попробуйте снова.');
        return;
    }

    console.log(`[preferred_tags.js] Выбрано значение: ${value}`);

    questionService.handleInlineAnswer(bot, query, 'preferred_tags', value);
    await bot.answerCallbackQuery(query.id)
};

module.exports = { preferred_tags };