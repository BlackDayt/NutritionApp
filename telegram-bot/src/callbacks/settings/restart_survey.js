const { contextService } = require('../../services/contextService');
const questionService = require('../../services/questionService');

module.exports = async function restartSurvey(bot, query) {
    const chatId = query.message.chat.id;
    const messageId = query.message.message_id;

    contextService.setContext(chatId, 'survey');
    await questionService.startSurvey(bot, chatId, messageId);

    await bot.answerCallbackQuery(query.id);
};