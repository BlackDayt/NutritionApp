const questionService = require("../../services/questionService");

const registration = (bot, query) => {
    const chatId = query.message.chat.id;

    // Запускаем анкетирование
    questionService.startSurvey(bot, chatId);
};

module.exports = { registration };
