const { navigationService } = require('../services/navigationService');
const { keyboardService } = require('../services/keyboardService');

const goBack = (bot, query) => {
    const chatId = query.message.chat.id;
    const messageId = query.message.message_id;

    const previousKeyboard = navigationService.getPreviousKeyboard(chatId) || keyboardService.getMainMenu();

    // const previousKeyboard = navigationService.getPreviousKeyboard(chatId);
    // if (previousKeyboard) {
    //     bot.editMessageReplyMarkup(previousKeyboard, { chat_id: chatId, message_id: messageId });
    // } else {
    //     // Если истории нет, возвращаем в главное меню
    //     bot.editMessageReplyMarkup(keyboardService.mainMenu, { chat_id: chatId, message_id: messageId });
    // }

    keyboardService.updateKeyboard(bot, chatId, messageId, previousKeyboard);
    // editMessageKeyboard(bot, chatId, messageId, initialKeyboard);
};

module.exports = { goBack };