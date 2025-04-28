function formatMealPlan(mealPlan) {
    const lines = [
        '🍽 План питания:',
        '',
        'Прием пищи      Калории',
        '─────────────── ────────'
    ];

    const preferredOrder = ['Завтрак', 'Обед', 'Перекус', 'Ужин', 'Поздний перекус'];

    const sortedEntries = Object.entries(mealPlan).sort((a, b) => {
        return preferredOrder.indexOf(a[0]) - preferredOrder.indexOf(b[0]);
    });

    for (const [meal, calories] of sortedEntries) {
        const name = meal.padEnd(15);
        const cal = `${calories} ккал`.padStart(8);
        lines.push(`${name} ${cal}`);
    }

    return '```\n' + lines.join('\n') + '\n```';
}

module.exports = { formatMealPlan };