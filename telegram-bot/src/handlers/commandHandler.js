const { keyboardService } = require('../services/keyboardService')
const commandHandler = (bot) => {
    bot.onText(/\/start/, (msg) => {
        const chatId = msg.chat.id;
        const text = 'Нажми на кнопку, чтобы отправить запрос на сервер: ';
        keyboardService.sendKeyboard(bot, chatId, text, keyboardService.mainMenu);
    });
};

// const sendInitialKeyboard = (bot, chatId) => {
//     bot.sendMessage(chatId, 'Нажми на кнопку, чтобы отправить запрос на сервер:', {
//         reply_markup: initialKeyboard
//     });
// };

module.exports = { commandHandler };