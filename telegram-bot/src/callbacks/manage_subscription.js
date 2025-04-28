module.exports = async function manageSubscription(bot, query) {
    const chatId = query.message.chat.id;

    await bot.sendMessage(chatId, '📝 Управление подпиской пока не реализовано.');

    await bot.answerCallbackQuery(query.id);
};