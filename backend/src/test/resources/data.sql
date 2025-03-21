-- Очистка таблиц для чистого старта
-- TRUNCATE TABLE user_excluded_ingredients CASCADE;
-- TRUNCATE TABLE recipe_ingredients CASCADE;
-- TRUNCATE TABLE recipe_tags CASCADE;
-- TRUNCATE TABLE recipes CASCADE;
-- TRUNCATE TABLE tags CASCADE;
-- TRUNCATE TABLE ingredients CASCADE;
-- TRUNCATE TABLE users CASCADE;

-- Добавляем тестовые теги
INSERT INTO tags (id, name) VALUES (1, 'Веганская');
INSERT INTO tags (id, name) VALUES (2, 'Безглютеновая');

-- Добавляем тестовые ингредиенты
INSERT INTO ingredients (id, name) VALUES (1, 'Тофу');
INSERT INTO ingredients (id, name) VALUES (2, 'Глютен');

-- Добавляем тестовые рецепты
INSERT INTO recipes (id, name, description, calories, proteins, fats, carbohydrates, cook_time, servings, instructions)
VALUES (1, 'Веганский суп', 'Полезный суп из овощей', 200, 10, 5, 25, 30, 2, 'Вскипятите воду, добавьте овощи, варите 30 минут.');

INSERT INTO recipes (id, name, description, calories, proteins, fats, carbohydrates, cook_time, servings, instructions)
VALUES (2, 'Мясной стейк', 'Сочный стейк из говядины', 600, 40, 30, 10, 20, 1, 'Обжарьте стейк на сковороде по 5 минут с каждой стороны.');

-- Добавляем связи рецептов с тегами
INSERT INTO recipe_tags (recipe_id, tag_id, priority) VALUES (1, 1, 1);
INSERT INTO recipe_tags (recipe_id, tag_id, priority) VALUES (1, 2, 2);

-- Добавляем связи рецептов с ингредиентами
INSERT INTO recipe_ingredients (recipe_id, ingredient_id, quantity, unit, quantity_text) VALUES (1, 1, 200, 'г', '200г Тофу');
INSERT INTO recipe_ingredients (recipe_id, ingredient_id, quantity, unit, quantity_text) VALUES (1, 2, 50, 'г', '50г Глютена');

-- Добавляем тестового пользователя
INSERT INTO users (id, name, email, password, created_at) VALUES (1, 'Тестовый Пользователь', 'test@example.com', 'hashed-password', CURRENT_TIMESTAMP);

-- Добавляем исключённые ингредиенты для пользователя
INSERT INTO user_excluded_ingredients (user_id, ingredient_id) VALUES (1, 2);
