const questionService = require('../../../services/questionService');

const diet_goal = async (bot, query, value) => {
    const chatId = query.message.chat.id;

    if (!value) {
        bot.sendMessage(chatId, '❌ Не удалось определить ваш выбор. Попробуйте снова.');
        return;
    }

    console.log(`[diet_goal.js] Выбрано значение: ${value}`);

    questionService.handleInlineAnswer(bot, query, 'diet_goal', value);
    await bot.answerCallbackQuery(query.id);
};

module.exports = { diet_goal };