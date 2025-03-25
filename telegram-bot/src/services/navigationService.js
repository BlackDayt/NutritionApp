const userState = new Map(); // Храним историю состояний клавиатуры для каждого chatId

const navigationService = {
    setKeyboardState: (chatId, keyboard) => {
        if (!userState.has(chatId)) {
            userState.set(chatId, []);
        }
        userState.get(chatId).push(keyboard);
    },

    getPreviousKeyboard: (chatId) => {
        if (!userState.has(chatId) || userState.get(chatId).length === 0) {
            return null; // Если нет истории, вернем null
        }
        return userState.get(chatId).pop(); // Удаляем последнее состояние и возвращаем его
    }
};

module.exports = { navigationService };