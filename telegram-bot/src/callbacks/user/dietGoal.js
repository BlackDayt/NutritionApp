const questionService = require('../../services/questionService');

const dietGoal = (bot, query, value) => {
    const chatId = query.message.chat.id;

    if (!value) {
        bot.sendMessage(chatId, '❌ Не удалось определить ваш выбор. Попробуйте снова.');
        return;
    }

    console.log(`[dietGoal.js] Выбрано значение: ${value}`);

    questionService.handleInlineAnswer(bot, chatId, 'dietGoal', value);
};

module.exports = { dietGoal };