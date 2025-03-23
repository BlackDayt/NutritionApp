const axios = require("axios");
const { backendUrl } = require("../config/config");

class QuestionService {
    constructor() {
        this.userAnswers = new Map(); // Хранилище ответов пользователей
        this.questions = [
            { key: "height", text: "Какой у тебя рост (в см)?", validate: (input) => !isNaN(input) && input > 0 },
            { key: "weight", text: "Какой у тебя вес (в кг)?", validate: (input) => !isNaN(input) && input > 0 },
            { key: "goal", text: "Какая твоя цель? (похудение, поддержание, набор массы)", validate: (input) => ["похудение", "поддержание", "набор массы"].includes(input.toLowerCase()) }
        ];
    }

    /**
     * Запуск опроса для пользователя
     * @param {Object} bot - экземпляр Telegram Bot
     * @param {Number} chatId - ID чата пользователя
     */
    startSurvey(bot, chatId) {
        this.userAnswers.set(chatId, { step: 0, answers: {} });
        bot.sendMessage(chatId, this.questions[0].text);
    }

    /**
     * Обработчик ответов пользователя
     * @param {Object} bot - экземпляр Telegram Bot
     * @param {Object} msg - сообщение пользователя
     */
    async handleAnswer(bot, msg) {
        const chatId = msg.chat.id;
        if (!this.userAnswers.has(chatId)) return;

        const userState = this.userAnswers.get(chatId);
        const currentQuestion = this.questions[userState.step];

        // Проверка валидности ответа
        if (!currentQuestion.validate(msg.text)) {
            bot.sendMessage(chatId, `❌ Некорректный ответ. ${currentQuestion.text}`);
            return;
        }

        // Сохраняем ответ
        userState.answers[currentQuestion.key] = msg.text;

        if (userState.step < this.questions.length - 1) {
            userState.step++;
            bot.sendMessage(chatId, this.questions[userState.step].text);
        } else {
            await this.sendUserData(bot, chatId, userState.answers);
            this.userAnswers.delete(chatId); // Удаляем состояние
        }
    }

    /**
     * Отправка данных пользователя на сервер
     * @param {Object} bot - экземпляр Telegram Bot
     * @param {Number} chatId - ID чата пользователя
     * @param {Object} userData - объект с ответами пользователя
     */
    async sendUserData(bot, chatId, userData) {
        try {
            console.log(userData);
            const response = await axios.post(`${backendUrl}/users`, userData);
            bot.sendMessage(chatId, `✅ Ты зарегистрирован!\nРост: ${userData.height} см\nВес: ${userData.weight} кг\nЦель: ${userData.goal}`);
        } catch (error) {
            console.error("Ошибка при отправке данных:", error.message);
            bot.sendMessage(chatId, "❌ Ошибка при регистрации. Попробуй снова.");
        }
    }
}

// Экспортируем один экземпляр класса
module.exports = new QuestionService();

