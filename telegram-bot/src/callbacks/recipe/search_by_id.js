const { contextService } = require('../../services/contextService');

module.exports = async function search_by_id(bot, query) {
    const chatId = query.message.chat.id;

    contextService.setContext(chatId, 'search_recipe_by_id');
    await bot.sendMessage(chatId, '🔍 Введи ID рецепта, который ты хочешь найти:');
    await bot.answerCallbackQuery(query.id)
};