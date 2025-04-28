module.exports = async function editProfile(bot, query) {
    const chatId = query.message.chat.id;

    await bot.sendMessage(chatId, '📝 Редактирование профиля пока не реализовано. Вы можете пройти анкету заново.');

    await bot.answerCallbackQuery(query.id);
};