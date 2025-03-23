const { navigationService } = require('../services/navigationService');

const keyboardService = {
    getMainMenu: () => ({
        inline_keyboard: [
            [{ text: 'Регистрация', callback_data: 'registration' }],
            [{ text: 'Генерация', callback_data: 'generate' }, { text: 'Поиск по ID', callback_data: 'searchById' }],
            [{ text: 'Управление подпиской', callback_data: 'menageSubscription' }]
        ]
    }),

    mainMenu: {
        inline_keyboard: [
            [{ text: 'Регистрация', callback_data: 'registration' }],
            [{ text: 'Генерация', callback_data: 'generate' }, { text: 'Поиск по ID', callback_data: 'searchById' }],
            [{ text: 'Управление подпиской', callback_data: 'menageSubscription' }]
        ]
    },

    settingsMenu: {
        inline_keyboard: [
            [{ text: 'Изменить профиль', callback_data: 'edit_profile' }],
            [{ text: 'Подписки', callback_data: 'subscriptions' }],
            [{ text: 'Назад', callback_data: 'back' }]
        ]
    },

    getCustomMenu: (options) => {
        return {
            inline_keyboard: options.map((option) => [
                { text: option.label, callback_data: option.callback }
            ])
        };
    },


    // Метод для отправки клавиатуры и сохранения в стек
    sendKeyboard: (bot, chatId, text, keyboard) => {
        navigationService.setKeyboardState(chatId, keyboard); // Сохраняем в историю для кнопки "Назад"
        bot.sendMessage(chatId, text, { reply_markup: keyboard });
    },

    // Метод для обновления уже существующего сообщения (например, при нажатии кнопок)
    updateKeyboard: (bot, chatId, messageId, keyboard) => {
        navigationService.setKeyboardState(chatId, keyboard);
        bot.editMessageReplyMarkup(keyboard, { chat_id: chatId, message_id: messageId });
    }
};

module.exports = { keyboardService };