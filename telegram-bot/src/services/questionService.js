const axios = require('axios');
const { backendUrl } = require('../config/config');
const {keyboardService} = require("./keyboardService");
const camelcaseKeys = require('camelcase-keys').default;
const { contextService } = require("../services/contextService");

class QuestionService {
    constructor() {
        this.userAnswers = new Map(); // Хранилище ответов пользователей
        this.baseQuestions = [
            { key: 'name', text: 'Как тебя зовут?', type: 'text' },
            { key: 'gender', text: 'Укажи пол:', type: 'inline', options: [
                    { text: 'Мужской', callback: 'MALE' },
                    { text: 'Женский', callback: 'FEMALE' }
                ] },
            { key: 'age', text: 'Сколько тебе лет?', type: 'text' },
            { key: 'height', text: 'Какой у тебя рост (в см)?', type: 'text' },
            { key: 'weight', text: 'Какой у тебя вес (в кг)?', type: 'text' },
            { key: 'activity_level', text: 'Выбери активность:', type: 'inline', options: [
                    { text: 'Малоподвижный образ жизни', callback: 'SEDENTARY' },
                    { text: 'Лёгкие тренировки 1-3 раза в неделю', callback: 'LIGHT' },
                    { text: 'СредТренировки 3-5 раз в неделю', callback: 'MODERATE' },
                    { text: 'Тренировки 5-7 раз в неделю', callback: 'ACTIVE' },
                    { text: 'Интенсивные тренировки каждый день', callback: 'VERY_ACTIVE' }
                ] },
            { key: 'diet_goal', text: 'Твоя цель:', type: 'inline', options: [
                    { text: 'Поддержание веса', callback: 'MAINTAIN' },
                    { text: 'Похудение', callback: 'WEIGHT_LOSS' },
                    { text: 'Экстремальное похудение', callback: 'EXTREME_WEIGHT_LOSS' },
                    { text: 'Набор массы', callback: 'MUSCLE_GAIN' },
                    { text: 'Интенсивный набор массы', callback: 'BULK' },
                    { text: 'Сушка', callback: 'CUTTING' }
                ]},
            { key: 'meal_count', text: 'Количество приемов пищи:', type: 'inline', options: [
                    { text: '3', callback: '3' },
                    { text: '4', callback: '4' },
                    { text: '5', callback: '5' }
                ]}
        ];
    }

    /**
     * Запуск опроса для пользователя
     * @param {Object} bot - экземпляр Telegram Bot
     * @param {Number} chatId - ID чата пользователя
     * @param messageId - ID сообщения
     */
    // async startSurvey(bot, { chatId, messageId }) {
    async startSurvey(bot, chatId, messageId) {
        this.userAnswers.set(chatId, { step: 0, answers: {} });

        const questions = [...this.baseQuestions];

        // Загрузка тегов
        const tags =await this.fetchTags();
        this.tags = tags;
        questions.push({
            key: 'preferred_tags',
            text: 'Выбери теги, которые тебе подходят. Можно выбрать несколько:',
            type: 'inline',
            multiple: true,
            options: tags.map(tag => ({
                text: tag.name,
                callback: tag.id
            })).concat([{ text: '✅ Готово', callback: 'tag_done' }])
        });

        // Загрузка ингредиентов
        const ingredients =await this.fetchIngredients();
        this.ingredients = ingredients;
        questions.push({
            key: 'excluded_ingredients',
            text: 'Выбери ингредиенты, которые ты хочешь исключить. Можно выбрать несколько:',
            type: 'inline',
            multiple: true,
            options: ingredients.map(ingredient => ({
                text: ingredient.name,
                callback: ingredient.id
            })).concat([{ text: '✅ Готово', callback: 'ingredient_done' }])
        });

        // Устанавливаем анкету и ответы
        this.userAnswers.set(chatId, {
            step: 0,
            answers: {},
            questions, // <-- индивидуально для каждого пользователя
            messageId
        });

        this.sendNextQuestion(bot,  chatId);
    }

    setAnswer(chatId, key, value) {
        if (!this.userAnswers.has(chatId)) return;
        this.userAnswers.get(chatId).answers[key] = value;
    }

    sendNextQuestion(bot, chatId) {
        const userState = this.userAnswers.get(chatId);
        if (!userState) return;

        const  { step, questions, messageId } = userState;


        if (step >= questions.length) {
            this.finishSurvey(bot, chatId, userState.answers, messageId);
            this.userAnswers.delete(chatId);
            return;
        }

        const currentQuestion = questions[step];
        console.log(`[sendNextQuestion] chatId: ${chatId}, step: ${step}, question: ${currentQuestion.key}`);
        let keyboard;

        if (currentQuestion.type === 'inline') {

            const horizontalKeys = ['gender', 'meal_count'];

            // Кастомное горизонтальное расположение только для выбора пола
            if (horizontalKeys.includes(currentQuestion.key)) {
                keyboard = [
                    currentQuestion.options.map(opt => ({
                        text: opt.text,
                        callback_data: `${currentQuestion.key}:${opt.callback}`
                    }))
                ];
            } else {
                // По умолчанию — вертикально (каждая кнопка в своей строке)
                keyboard = currentQuestion.options.map(opt => [{
                    text: opt.text,
                    callback_data: `${currentQuestion.key}:${opt.callback}`
                }]);
            }

            // bot.editMessageText(currentQuestion.text, {
            //     chat_id: chatId,
            //     message_id: messageId,
            //     reply_markup: { inline_keyboard: keyboard }
            // });
            bot.sendMessage(chatId, currentQuestion.text, {
                reply_markup: { inline_keyboard: keyboard }
            });
        } else {
            // bot.editMessageText(currentQuestion.text, {
            //     chat_id: chatId,
            //     message_id: messageId
            // });
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
        const { questions } = userState;
        const currentQuestion = questions[currentStep];

        console.log(`[handleTextAnswer] chatId: ${chatId}, step: ${currentStep}, questionKey: ${currentQuestion?.key}`);

        if (!currentQuestion || currentQuestion.type === 'inline') return;


        this.setAnswer(chatId, currentQuestion.key, msg.text);
        userState.step++;
        this.sendNextQuestion(bot, chatId);
    }

    handleInlineAnswer(bot, query, key, value) {
        const chatId = query.message.chat.id;
        const messageId = query.message.message_id;

        if (!this.userAnswers.has(chatId)) return;

        const userState = this.userAnswers.get(chatId);
        const currentStep = userState.step;
        const { questions } = userState;
        const currentQuestion = questions[currentStep];


        if (!currentQuestion || currentQuestion.key !== key || currentQuestion.type !== 'inline') return;

        const isMultiSelect = currentQuestion.multiple;
        const doneValue = key === 'preferred_tags' ? 'tag_done' :
            key === 'excluded_ingredients' ? 'ingredient_done' : 'done';

        // Если это множественный выбор тегов
        if (isMultiSelect) {
            if (value === doneValue) {
                userState.step++;
                this.sendNextQuestion(bot, chatId);
                return;
            }
            // Получаем текущие выбранные теги
            let selected = userState.answers[key] || [];

            if (selected.includes(value)) {
                selected = selected.filter(v => v !== value);
            } else {
                selected.push(value)
            }
            userState.answers[key] = selected;

            const selectedNames = selected.map(id => {
                if (key === 'preferred_tags') {
                    return this.tags.find(tag => tag.id === id)?.name || id;
                } else if (key === 'excluded_ingredients') {
                    return this.ingredients.find(ing => ing.id === id)?.name || id;
                }
                return id;
            });

            const selectedText = selected.length
                ? `\n\n✅ Выбрано: ${selectedNames.join(', ')}`
                : '\n\nПока ничего не выбрано.';

            const updatedKeyboard = currentQuestion.options.map(opt => [{
                text: (selected.includes(opt.callback) ? '✅ ' : '') + opt.text,
                callback_data: `${currentQuestion.key}:${opt.callback}`
            }]);


            bot.editMessageText(currentQuestion.text + selectedText, {
                chat_id: chatId,
                message_id: messageId,
                reply_markup: {
                    inline_keyboard: updatedKeyboard
                }
            });

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
     * @param chatId
     * @param answers - ответы
     * @param messageId
     */
    async finishSurvey(bot, chatId, answers, messageId) {
        const {preferredTags, excludedIngredients, ...rest} = answers;
        const snakeUserData = {
            telegramId: chatId,
            preferredTagIds: preferredTags ?? [],
            excludedIngredientIds: excludedIngredients ?? [],
            ...rest
        };
        const userData = camelcaseKeys(snakeUserData, {deep: true});
        console.log(userData);

        try{
            console.log(userData);
            const response = await axios.post(`${backendUrl}/api/users`, userData);

            // await bot.editMessageText(`✅ Анкета заполнена! Данные отправлены.
            //     Рост: ${userData.height} см
            //     Вес: ${userData.weight} кг
            //     Пол: ${userData.gender}`, {
            //         chat_id: chatId,
            //         message_id: messageId
            //     }
            // );
            await bot.sendMessage(chatId, `✅ Анкета заполнена! Данные отправлены.
                Рост: ${userData.height} см
                Вес: ${userData.weight} кг
                Пол: ${userData.gender}`
            );

            contextService.clearContext(chatId);

            await bot.sendMessage(chatId, '🏠 Главное меню:', {
                reply_markup: keyboardService.getMainMenu(chatId)
            });
        } catch (error) {
            contextService.clearContext(chatId);
            console.error('Ошибка при отправке данных:', error.message);
            bot.sendMessage(chatId, '❌ Ошибка при регистрации. Попробуйте позже.');
        }
    }


    async fetchTags() {
        try {
            const response = await axios.get(`${backendUrl}/api/tags`);
            return response.data; // предполагается, что это массив с { id, name }
        } catch (error) {
            console.error('Ошибка при получении тегов:', error.message);
            return [];
        }
    }

    async fetchIngredients() {
        try {
            const response = await axios.get(`${backendUrl}/api/ingredients`);
            return response.data; // предполагается, что это массив с { id, name }
        } catch (error) {
            console.error('Ошибка при получении ингредиентов:', error.message);
            return [];
        }
    }

}

// Экспортируем один экземпляр класса
module.exports = new QuestionService();

