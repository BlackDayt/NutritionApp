const axios = require('axios');
const { backendUrl } = require('../config/config');

async function getUserByTelegramId(telegramId) {
    const response = await axios.get(`${backendUrl}/api/users/telegram/${telegramId}`);
    return response.data;
}

module.exports = { getUserByTelegramId };