const { navigationService } = require('../services/navigationService');
const axios = require('axios');
const { backendUrl } = require('../config/config');

async function isRegistered(telegramId) {
    try {
        const response = await axios.get(`${backendUrl}/api/users/telegram/${telegramId}`);
        return response.status === 200 && response.data !== null;
    } catch (e) {
        return false;
    }
}

const keyboardService = {
    getMainMenu: async (telegramId) => {
        const isUserRegistered = await isRegistered(telegramId);

        const keyboard = [];

        if (!isUserRegistered) {
            keyboard.push([{ text: '📝 Регистрация', callback_data: 'registration' }]);
        } else {
            keyboard.push([{ text: '🍽 Твой план питания', callback_data: 'meal_plan' }]);
        }

        keyboard.push([
            { text: '🎲 Случайный рецепт', callback_data: `random_recipe:${Date.now()}` },
            { text: '🔍 Поиск по ID', callback_data: 'search_by_id' }
        ]);

        keyboard.push([{ text: '⚙️ Настройки', callback_data: 'settings_menu' }]);

        keyboard.push([{ text: '⚙️ Управление подпиской', callback_data: 'manage_subscription' }]);


        return { inline_keyboard: keyboard };
    },

    getSettingsMenu: async (telegramId) => {

        const keyboard = [
            [{ text: '✏️ Изменить профиль', callback_data: 'edit_profile' }],
            [{ text: '🔄 Пройти анкету заново', callback_data: 'restart_survey' }],
            [{ text: '⬅️ Назад', callback_data: 'main_menu' }]
        ];

        return { inline_keyboard: keyboard };
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