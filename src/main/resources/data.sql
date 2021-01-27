INSERT INTO `users` (`id`, `login`, `email`, `external_id`, `enabled`, `encryted_password`) VALUES
(1, 'admin', 'admin@phonebook.local', NULL, b'1', '$2a$10$ZdG6EhDBpcHvIuj2VbU9fu00w5WokYZhQNts1cy0QlMIKTGMji5Hu'),
(2, 'writer', 'writer@phonebook.local', NULL, b'1', '$2a$10$ZdG6EhDBpcHvIuj2VbU9fu00w5WokYZhQNts1cy0QlMIKTGMji5Hu');

INSERT INTO `roles` (`id`, `name`, `description`) VALUES
(1, 'ROLE_ADMIN', 'Администратор (управление пользователями, редактирование телефонного справочника, настройка системы, настройка импорта.'),
(2, 'ROLE_WRITER', 'Редактирование телефонного справочника'),
(3, 'ROLE_USER', 'Пока не используется');

INSERT INTO `user_roles` (`user_id`, `privilege_id`) VALUES
(1, 1),
(1, 2),
(2, 2);