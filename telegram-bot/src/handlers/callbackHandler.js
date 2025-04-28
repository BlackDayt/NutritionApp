const { callbacks } = require("../core/callbacksLoader");
const { contextService } = require("../services/contextService");

/**
 * Обработчик всех inline-кнопок (callback_query)
 * @param {TelegramBot} bot - экземпляр бота
 */
const callbackHandler = (bot) => {
    bot.on('callback_query', async (query) => {
        try {
            const chatId = query.message.chat.id;
            const messageId = query.message.message_id;
            const rawData = query.data;

            // Разбиваем callback_data на key и value (если есть)
            let key = rawData;
            let value;

            if (rawData.includes(":")) {
                const parts = rawData.split(":");
                key = parts[0];
                value = parts.slice(1).join(":"); // поддержка value с ":" внутри
            }

            console.log(`[callbackHandler] key: ${key}, value: ${value}`);

            // === ✅ Контроль контекста ===
            const currentContext = contextService.getContext(chatId);

            // Указываем, какие key разрешены во время анкетирования
            const allowedDuringRegistration = [
                'activity_level', 'diet_goal', 'excluded_ingredients', 'gender', 'ingredient_done',
                'meal_count', 'preferred_tags', 'tag_done'
            ];

            if (currentContext === 'registration' && !allowedDuringRegistration.includes(key)) {
                await bot.answerCallbackQuery(query.id, {
                    text: 'Завершите анкету прежде чем продолжить.',
                    show_alert: true
                });
                return;
            }

            // === 🔍 Поиск и вызов обработчика ===
            const handler = callbacks[key];

            if (typeof handler === "function") {
                await handler(bot, query, value);
            } else {
                console.warn(`⚠ Нет обработчика для: ${key}`);
                await bot.answerCallbackQuery(query.id, { text: 'Неизвестная команда.' });
            }
        } catch (error) {
            console.error("❌ Ошибка в callbackHandler:", error.message);
        }
    });
};

module.exports = { callbackHandler };