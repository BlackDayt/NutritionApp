const { editMessageKeyboard } = require("../../services/messageService");

const settings = (bot, query) => {
    bot.sendMessage(query.message.chat.id, "Настройки пока не реализованы.");
};

module.exports = { settings };