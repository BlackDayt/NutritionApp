const questionService = require('../../../services/questionService');

const gender = async (bot, query, value) => {
    const chatId = query.message.chat.id;

    if (!value) {
        bot.sendMessage(chatId, '❌ Не удалось определить ваш выбор. Попробуйте снова.');
        return;
    }

    console.log(`[gender.js] Выбрано значение: ${value}`);

    questionService.handleInlineAnswer(bot, query, 'gender', value);
    await bot.answerCallbackQuery(query.id)
};

module.exports = { gender };
