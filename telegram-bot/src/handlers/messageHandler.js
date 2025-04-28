const { contextService } = require("../services/contextService");
const questionService = require('../services/questionService'); // ✅ Теперь работает
const { recipeService } = require('../services/recipeService');


const messageHandler = (bot) => {
    bot.on('message', async (msg) => {
        const chatId = msg.chat.id;
        const text = msg.text;

        if (text?.startsWith('/')) return;  // if (text && text.startsWith('/')) return;

        const context = contextService.getContext(chatId);

        switch (context) {
            case 'registration':
                await questionService.handleTextAnswer(bot, msg);
                break;

            case 'search_recipe_by_id':
                try {
                    let recipe = await recipeService.getById(text);
                    bot.sendMessage(chatId, recipeService.format(recipe));
                } catch (err) {
                    console.error('Ошибка при поиске рецепта:', err.message);
                    await bot.sendMessage(chatId, '❌ Не удалось найти рецепт.');
                }
                break;

            case "idle":
            default:
                bot.sendMessage(chatId, 'Выберите действие с помощью меню.');
        }
    });
};

module.exports = { messageHandler };