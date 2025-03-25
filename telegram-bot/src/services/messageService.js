const messageService = {
    sendMessage: (bot, chatId, text) => {
        bot.sendMessage(chatId, text);
    },

    editMessage: (bot, chatId, messageId, newText) => {
        bot.editMessageText(newText, { chat_id: chatId, message_id: messageId });
    },
};

module.exports = { messageService };
