const questionService = require('../../../services/questionService');

const activity_level = async (bot, query, value) => {
    const chatId = query.message.chat.id;

    if (!value) {
        bot.sendMessage(chatId, '❌ Не удалось определить ваш выбор. Попробуйте снова.');
        return;
    }

    console.log(`[activity_level.js] Выбрано значение: ${value}`);

    questionService.handleInlineAnswer(bot, query, 'activity_level', value);
    await bot.answerCallbackQuery(query.id)
};

module.exports = { activity_level };