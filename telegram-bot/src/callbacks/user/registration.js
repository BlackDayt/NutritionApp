const questionService = require("../../services/questionService");
const { contextService } = require("../../services/contextService");
const registration = async (bot, query) => {
    const chatId = query.message.chat.id;
    const messageId = query.message.message_id;

    contextService.setContext(chatId, 'registration');
    // Запускаем анкетирование
    questionService.startSurvey(bot, chatId, messageId);
    await bot.answerCallbackQuery(query.id)
};

module.exports = { registration };
