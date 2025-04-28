const axios = require('axios');
const { backendUrl } = require('../config/config');
const { formatRecipe } = require('../utils/recipeFormatter');

const recipeService = {
    async getById(recipeId) {
        try {
            const response = await axios.get(`${backendUrl}/api/recipes/${recipeId}`);
            return response.data;
        } catch (error) {
            console.error('Ошибка при получении рецепта по ID:', error.message);
            return null;
        }
    },

    async getRandom(telegramId) {
        try {
            const response = await axios.get(`${backendUrl}/api/recipes/random`, {
                params: { telegramId }
            });

            return response.data;
        } catch (error) {
            console.error('Ошибка при получении случайного рецепта:', error.message);
            return null;
        }
    },

    async getRandomForUser(telegramId) {
        try {
            const response = await axios.get(`${backendUrl}/api/recipes/random/${telegramId}`, {
                params: { telegramId }
            });
            return response.data;
        } catch (error) {
            console.error('Ошибка при получении случайного рецепта:', error.message);
            return null;
        }
    },

    format(recipe) {
        return formatRecipe(recipe);
    }
};

module.exports = recipeService;
