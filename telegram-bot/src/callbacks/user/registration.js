const questionService = require("../../services/questionService");
const { contextService } = require("../../services/contextService");
const registration = (bot, query) => {
    const chatId = query.message.chat.id;

    contextService.setContext(chatId, 'survey');
    // Запускаем анкетирование
    questionService.startSurvey(bot, chatId);
};

module.exports = { registration };
