/**
 * Форматирует рецепт для отправки в Telegram
 * @param {Object} recipe - объект рецепта
 * @returns {string} - отформатированная строка
 */
function formatRecipe(recipe) {
    if (!recipe) return '❌ Рецепт не найден.';

    const {
        name,
        description,
        calories,
        proteins,
        fats,
        carbohydrates,
        ingredients = []
    } = recipe;

    const ingredientList = ingredients.length
        ? '\n🧾 Ингредиенты:\n' + ingredients.map(i => `- ${i}`).join('\n')
        : '';

    return `
🍽 *${name}*

📝 ${description || 'Нет описания'}
🔥 Калории: ${calories} ккал
🍗 Б: ${proteins ?? '–'}г | Ж: ${fats ?? '–'}г | У: ${carbohydrates ?? '–'}г${ingredientList}
    `.trim();
}

module.exports = { formatRecipe };