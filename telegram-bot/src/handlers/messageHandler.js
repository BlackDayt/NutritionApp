const { contextService } = require("../services/contextService");
const questionService = require('../services/questionService'); // ✅ Теперь работает


const messageHandler = (bot) => {
    bot.on('message', async (msg) => {
        const chatId = msg.chat.id;
        const text = msg.text;

        if (text?.startsWith('/')) return;  // if (text && text.startsWith('/')) return;

        const context = contextService.getContext(chatId);

        switch (context) {
            case 'survey':
                await questionService.handleTextAnswer(bot, msg);
                break;

            case 'search':
                // В будущем можно подключить recipeService
                bot.sendMessage(chatId, "🔍 Введите название блюда для поиска рецепта:");
                break;

            case "idle":
            default:
                bot.sendMessage(chatId, 'Выберите действие с помощью меню.');
        }
    });
};

module.exports = { messageHandler };