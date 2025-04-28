const questionService = require('../../../services/questionService');

const meal_count = async (bot, query, value) => {
    const chatId = query.message.chat.id;

    if (!value) {
        bot.sendMessage(chatId, '❌ Не удалось определить ваш выбор. Попробуйте снова.');
        return;
    }

    console.log(`[meal_count.js] Выбрано значение: ${value}`);

    questionService.handleInlineAnswer(bot, query, 'meal_count', value);
    await bot.answerCallbackQuery(query.id)
};

module.exports = { meal_count };