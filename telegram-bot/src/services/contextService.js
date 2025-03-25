const userContexts = new Map(); // { chatId: "survey" | "search" | "idle" }

const contextService = {
    setContext(chatId, context) {
        userContexts.set(chatId, context);
    },

    getContext(chatId) {
        return userContexts.get(chatId) || "idle";
    },

    clearContext(chatId) {
        userContexts.delete(chatId);
    }
};

module.exports = { contextService };
