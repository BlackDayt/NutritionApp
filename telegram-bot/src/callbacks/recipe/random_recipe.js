const axios = require('axios');
const recipeService = require('../../services/recipeService');

module.exports = async function random_recipe(bot, query) {
    const chatId = query.message.chat.id;

    try {
        const recipe = await recipeService.getRandom(chatId);

        if (!recipe) {
            return bot.sendMessage(chatId, '❌ Рецепт не найден.');
        }

        const text = recipeService.format(recipe);

        if (recipe.imageUrl) {
            await bot.sendPhoto(chatId, recipe.imageUrl, { caption: text, parse_mode: 'Markdown' });
        } else {
            await bot.sendMessage(chatId, text, { parse_mode: 'Markdown' });
        }
        await bot.answerCallbackQuery(query.id)

    } catch (error) {
        console.error('Ошибка при получении рецепта:', error.message);
        bot.sendMessage(chatId, '❌ Не удалось получить рецепт. Попробуйте позже.');
    }
};