const { getUserByTelegramId } = require('../../services/userService');
const { formatMealPlan } = require('../../utils/mealPlanFormatter');

module.exports = async function mealPlanCallback(bot, query) {
    const chatId = query.message.chat.id;

    try {
        const user = await getUserByTelegramId(chatId);
        if (!user?.mealPlan) {
            return bot.sendMessage(chatId, '❌ План питания не найден.');
        }

        const formattedPlan = formatMealPlan(user.mealPlan);
        await bot.sendMessage(chatId, formattedPlan, { parse_mode: 'Markdown' });
        await bot.answerCallbackQuery(query.id)
    } catch (err) {
        console.error('Ошибка при получении плана питания:', err.message);
        bot.sendMessage(chatId, '❌ Не удалось получить план питания.');
    }
};
