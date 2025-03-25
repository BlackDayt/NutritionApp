const axios = require('axios');
const { backendUrl } = require('../config/config');

class QuestionService {
    constructor() {
        this.userAnswers = new Map(); // Хранилище ответов пользователей
        this.questions = [
            { key: 'name', text: 'Как тебя зовут?', type: 'text' },
            { key: 'gender', text: 'Укажи пол:', type: 'inline', options: [
                    { text: 'Мужской', callback: 'MALE' },
                    { text: 'Женский', callback: 'FEMALE' }
                ] },
            { key: 'age', text: 'Сколько тебе лет?', type: 'text' },
            { key: 'height', text: 'Какой у тебя рост (в см)?', type: 'text' },
            { key: 'weight', text: 'Какой у тебя вес (в кг)?', type: 'text' },
            { key: 'activityLevel', text: 'Выбери активность:', type: 'inline', options: [
                    { text: 'Малоподвижный образ жизни', callback: 'SEDENTARY' },
                    { text: 'Лёгкие тренировки 1-3 раза в неделю', callback: 'LIGHT' },
                    { text: 'СредТренировки 3-5 раз в неделюняя', callback: 'MODERATE' },
                    { text: 'Тренировки 5-7 раз в неделю', callback: 'ACTIVE' },
                    { text: 'Интенсивные тренировки каждый день', callback: 'VERY_ACTIVE' }
                ] },
            { key: 'dietGoal', text: 'Твоя цель:', type: 'inline', options: [
                    { text: 'Поддержание веса', callback: 'MAINTAIN' },
                    { text: 'Похудение', callback: 'WEIGHT_LOSS' },
                    { text: 'Экстремальное похудение', callback: 'EXTREME_WEIGHT_LOSS' },
                    { text: 'Набор массы', callback: 'MUSCLE_GAIN' },
                    { text: 'Интенсивный набор массы', callback: 'BULK' },
                    { text: 'Сушка', callback: 'CUTTING' }
                ] }
        ];
    }

    /**
     * Запуск опроса для пользователя
     * @param {Object} bot - экземпляр Telegram Bot
     * @param {Number} chatId - ID чата пользователя
     */
    startSurvey(bot, chatId) {
        this.userAnswers.set(chatId, { step: 0, answers: {} });
        this.sendNextQuestion(bot,  chatId);
    }

    setAnswer(chatId, key, value) {
        if (!this.userAnswers.has(chatId)) return;
        this.userAnswers.get(chatId).answers[key] = value;
    }

    sendNextQuestion(bot, chatId) {
        const userState = this.userAnswers.get(chatId);
        if (!userState) return;

        const  step = userState.step;


        if (step >= this.questions.length) {
            this.finishSurvey(bot, chatId, userState.answers);
            this.userAnswers.delete(chatId);
            return;
        }

        const currentQuestion = this.questions[step];
        console.log(`[sendNextQuestion] chatId: ${chatId}, step: ${step}, question: ${currentQuestion.key}`);

        if (currentQuestion.type === 'inline') {
            bot.sendMessage(chatId, currentQuestion.text, {
                reply_markup: {
                    inline_keyboard: currentQuestion.options.map(opt => [{text: opt.text, callback_data: `${currentQuestion.key}:${opt.callback}`}])
                }
            });
        } else {
            bot.sendMessage(chatId, currentQuestion.text);
        }
    }

    /**
     * Обработчики ответов пользователя
     * @param {Object} bot - экземпляр Telegram Bot
     * @param {Object} msg - сообщение пользователя
     */
    async handleTextAnswer(bot, msg) {
        const chatId = msg.chat.id;
        if (!this.userAnswers.has(chatId)) return;

        const userState = this.userAnswers.get(chatId);
        const currentStep = userState.step;
        const currentQuestion = this.questions[currentStep];

        console.log(`[handleTextAnswer] chatId: ${chatId}, step: ${currentStep}, questionKey: ${currentQuestion?.key}`);

        if (!currentQuestion || currentQuestion.type === 'inline') return;


        this.setAnswer(chatId, currentQuestion.key, msg.text);
        userState.step++;
        this.sendNextQuestion(bot, chatId);
    }

    handleInlineAnswer(bot, chatId, key, value) {
        if (!this.userAnswers.has(chatId)) return;

        const userState = this.userAnswers.get(chatId);
        const currentQuestion = this.questions[userState.step];

        if (!currentQuestion || currentQuestion.key !== key || currentQuestion.type !== 'inline') {
            console.log(currentQuestion.type);
            return;
        }

        console.log(`[handleInlineAnswer] key: ${key}, value: ${value}, step: ${userState.step}`);

        this.setAnswer(chatId, key, value);
        userState.step++;
        this.sendNextQuestion(bot, chatId);
    }

    /**
     * Отправка данных пользователя на сервер
     * @param {Object} bot - экземпляр Telegram Bot
     * @param {Number} chatId - ID чата пользователя
     * @param {Object} userData - объект с ответами пользователя
     */
    async finishSurvey(bot, chatId, answers) {
        const userData = {
            telegramId: chatId,
            mealCount: 3,
            preferredTagIds: [],
            excludedIngredientIds: [],
            ...answers
        };

        try{
            console.log(userData);
            const response = await axios.post(`${backendUrl}/api/users`, userData);

            bot.sendMessage(chatId, `✅ Анкета заполнена! Данные отправлены.
            
Рост: ${userData.height} см
Вес: ${userData.weight} кг
Пол: ${userData.gender}`);
        } catch (error) {
            console.error('Ошибка при отправке данных:', error.message);
            bot.sendMessage(chatId, '❌ Ошибка при регистрации. Попробуйте позже.');
        }
    }

}

// Экспортируем один экземпляр класса
module.exports = new QuestionService();

