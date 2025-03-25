const axios = require('axios');

const baseUrl = 'http://localhost:8080';

const sendRequest = {
    async get(endpoint) {
        const response = await axios.get(endpoint);
        return response.data;
    },

    async post(endpoint, data) {
        const response = await axios.post(endpoint, data);
        return response.data;
    }
};

module.exports = sendRequest;

/*/
const axios = require("axios");
const { backendUrl } = require("../config/config");

const sendRequest = async (bot, query) => {
    const chatId = query.message.chat.id;
    try {
        const response = await axios.get(`${backendUrl}/${query.data}`);
        bot.sendMessage(chatId, `Ответ от сервера: ${response.data.message}`);
    } catch (error) {
        console.error("Ошибка при запросе:", error.message);
        bot.sendMessage(chatId, "Ошибка при запросе к серверу.");
    }
};

module.exports = { sendRequest };
/*/