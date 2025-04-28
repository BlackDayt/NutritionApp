const { navigationService } = require('../services/navigationService');
const { keyboardService } = require('../services/keyboardService');

const main_menu = async (bot, query) => {
    const chatId = query.message.chat.id;
    const messageId = query.message.message_id;

    await bot.editMessageText('🏠 Главное меню:', {
        chat_id: chatId,
        message_id: messageId,
        reply_markup: await keyboardService.getMainMenu(chatId)
    });

    await bot.answerCallbackQuery(query.id);
};

module.exports = { main_menu };