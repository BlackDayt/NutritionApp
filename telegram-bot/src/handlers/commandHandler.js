const { keyboardService } = require('../services/keyboardService');
const { contextService } = require('../services/contextService');

const commandHandler = (bot) => {
    bot.onText(/\/start/, async (msg) => {
        const chatId = msg.chat.id;
        const text = 'Выберите действие из меню: ';
        contextService.clearContext(chatId);
        // Получаем динамическое меню с проверкой регистрации
        const keyboard = await keyboardService.getMainMenu(chatId);
        keyboardService.sendKeyboard(bot, chatId, text, keyboard);
    });
};

module.exports = { commandHandler };