const questionService = require('../../services/questionService');

const activityLevel = (bot, query, value) => {
    const chatId = query.message.chat.id;

    if (!value) {
        bot.sendMessage(chatId, '❌ Не удалось определить ваш выбор. Попробуйте снова.');
        return;
    }

    console.log(`[activityLevel.js] Выбрано значение: ${value}`);

    questionService.handleInlineAnswer(bot, chatId, 'activityLevel', value);
};

module.exports = { activityLevel };