const questionService = require('../services/questionService'); // ✅ Теперь работает


const messageHandler = (bot) => {
    bot.on('message', async (msg) => {
        const chatId = msg.chat.id;
        const text = msg.text;


        if (text?.startsWith('/')) return;  // if (text && text.startsWith('/')) return;
        await questionService.handleAnswer(bot, msg);
        // bot.sendMessage(chatId, 'Пожалуйста, используйте кнопки для навигации.');
    });
};

module.exports = { messageHandler };