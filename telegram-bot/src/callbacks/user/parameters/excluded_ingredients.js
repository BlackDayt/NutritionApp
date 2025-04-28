const questionService = require('../../../services/questionService');

const excluded_ingredients = async (bot, query, value) => {
    const chatId = query.message.chat.id;

    if (!value) {
        bot.sendMessage(chatId, '❌ Не удалось определить ваш выбор. Попробуйте снова.');
        return;
    }

    console.log(`[excluded_ingredients.js] Выбрано значение: ${value}`);

    questionService.handleInlineAnswer(bot, query, 'excluded_ingredients', value);
    await bot.answerCallbackQuery(query.id)
};

module.exports = { excluded_ingredients };