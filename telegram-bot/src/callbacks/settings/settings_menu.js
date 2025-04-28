const { keyboardService } = require('../../services/keyboardService');

module.exports = async function settingsMenu(bot, query) {
    const chatId = query.message.chat.id;
    const messageId = query.message.message_id;

    await bot.editMessageText('⚙️ Настройки профиля:', {
        chat_id: chatId,
        message_id: messageId,
        reply_markup: await keyboardService.getSettingsMenu(chatId)
    });

    await bot.answerCallbackQuery(query.id);
};