# PHONEBOOK

### Описание
Телефонный справочник работников, реализованный в виде веб-приложения.

Имеется возможности сортировки справочника по заданному полю, по приоритету, выделение значимых работников (руководители организации, руководители подразделений).

Поддерживает импорт списка работников из Microsoft Active Directory.

Поддерживает разделение ролей: 
- не авторизованный пользователь - только просмотр справочника.
- писатель - редактирование справочника, импорт из AD.
- администратор - все вышеперечисленное + управление пользователями и ролями.

По-умолчанию приложение использует шаблонизатор и в ответ на запрос возвращает готовую HTML страницу.

Приложение также поддерживает REST API.

### Средства разработки
Java 11 (Spring Boot 2, Spring Security, JPA, REST API, Thymeleaf templates, LDAP, Hateoas)\
Система сборки: Maven

### Системные требования
- ОС Linux
- JDK 11
- MySQL Server
- Maven

### Установка
Для компиляции приложения требуется JDK 11 или выше и maven версии 2 и выше.

#####Установка на примере Ubuntu 20.04
Установка JDK 11
````
sudo apt update
sudo apt install openjdk-11-jdk -y
# Проверка версии
java -version
````
Установка MySQL Server
````
sudo apt install mysql-server mysql-client -y
# Проверка версии
mysql -V
# Проверка что сервис запущен. Должен быть running
sudo systemctl status mysql
````
Настройка MySQL Server
````
# Запустите скрипт настройки безопасности
# В процессе необходимо будет задать сложность пароля, задать root пароль на доступ в MySQL
sudo mysql_secure_installation

# Подключение к СУБД
sudo mysql -u root
# Создание базы данных
CREATE DATABASE PhoneBook;
# Создание пользователя для подключения к БД
CREATE USER 'phonebook'@'localhost' IDENTIFIED BY 'Qwerty911#@!';
# Выдача прав админа для БД
GRANT ALL PRIVILEGES ON PhoneBook.* TO 'phonebook'@'localhost';
# Применение настроек привелегий
FLUSH PRIVILEGES;
# Проверка
SHOW GRANTS FOR 'phonebook'@'localhost';
quit;
````
Установка системы сборки Maven
````
sudo apt install maven -y
````
Установка системы контроля версий Git
````
sudo apt install git -y
````
#### Сборка приложения
Копирование исходных файлов из репозитория GitHub
````
# Установка в папку пользователя. При необходимости можно изменить.
cd ~
mkdir webapp
cd ./webapp
git clone https://github.com/fenix2k/PhoneBook.git
````
Сборка при помощи maven
````
cd ./PhoneBook
mvn package
````
При успешной сборе приложения готовый к запуску файл будет лежать в папке ./target/PhoneBook-1.0.jar

Далее помещаем его в отдельную папку и распаковываем JAR файл
````
mkdir www
cp ./target/PhoneBook-1.0.jar ./www
cd ./www
jar -xf PhoneBook-1.0.jar
````
Запуск приложения
````
sudo java org.springframework.boot.loader.JarLauncher
````
По-умолчанию веб-сервер будет слушать порт 8888.\
Перед запуском приложения необходимо произвести настройку конфирурационныого файла (см. ниже).

### Конфигурирование
##### Первоначальная найстрока приложения производиться в файле
````
./BOOT-INF/classes/application.properties
````
##### В нем необходимо произвести следующие настройки:

Указать порт, который будет слушать веб-сервер:
````
# Стандартный порт 80
server.port=80
````
Указать параметры подключения к СУБД. На данном случае MySQL/MariaDB Server:
````
# Ввести IP адрес или доменное имя сервера БД и имя базы данных
spring.datasource.url=jdbc:mysql://<адрес>:3306/<имя базы данных>?createDatabaseIfNotExist=true&allowPublicKeyRetrieval=true&useSSL=false&autoReconnect=true&useLegacyDatetimeCode=false&serverTimezone=UTC&useUnicode=yes&characterEncoding=UTF-8
# Ввести имя пользователя, котоый обладает правами на подключения и изменене таблиц в БД
spring.datasource.username=phonebook
# Пароль вышеуказанного пользователя БД
spring.datasource.password=Qwerty911#@!
````

#### Интеграция с Active Directory
При необходимости можно настроить интеграцию с Active Directory по протоколу Ldap:
````
# Путь с контроллерам домена (DC). Указываются через запятую
spring.ldap.urls=ldap://<IP адрес или DNS имя>:389
# Путь в базовому каталогу относительно которогу будут выполняться запросы поиска
spring.ldap.base=OU=CORP,DC=corp,DC=local
# Логин пользователя AD с правами чтения
spring.ldap.username=sysuser@corp.local
# Пароль пользователя
spring.ldap.password=1234567
````
Настройка импорта списка работников из AD:
````
# Путь в базовому каталогу относительно которогу будут выполняться запросы поиска
spring.ldap.query.relativeDn=
# LDAP фильтр поиска
# Запрос выдаст всех не заблокированных пользователей
spring.ldap.query.search-filter=(&(objectCategory=user)(objectClass=person)(!(UserAccountControl=66050)))
````
Настройка автоматического импорта с указанной периодичностью с использованием CRON планировщика:
````
# Настройка переодичности импорта из Active Directory
# Чтобы остановить автоимпорт нужно вписать "-"
# seconds - minutes - hour - day - month - day of week
# В данном случае импорт из АД запускается каждый час
phonebook.cron.job.import-from-ldap=0 0 0-23 * * *
````
#### Настройка отображения справочника
Для того чтобы скрыть ненужные столбцы используйте следуюй параметр:
````
# Скрывает из публичного справочника перечисленные столбцы
phonebook.employee.collumns.hide=externalId,created,updated,isVisible,isDeleted,displayPriority,displayClassStyle
````
Список идентификаторов столбцов указан ниже:
- fullName - ФИО
- email - email
- title - должность
- department - подразделение
- company - компания
- internalPhoneNumber - внутренний номер
- cityPhoneNumber - городсткой номер
- mobilePhoneNumber - мобильный номер
- address - местоположение
- externalId - идертификатор пользователя если он импортирован из внешней системы
- displayPriority - приоритет сортировки
- displayClassStyle - стиль выделения
- created - дата и время создания записи
- updated - дата и время изменения записи
- isVisible - показать или скрыть запись
- isDeleted - пометить запись на удаление

Параметры сортировки можно указать следующим образом:
````
# Поле сортировки справочника
# Можно вручную задать приоритет. Для этого для локальных записей нужно заполнить поле displayPriority.
# В AD для задания приоритета служит поле postalCode. При импорте значение этого поля присваивается displayPriority.
phonebook.employee.sort-by=fullName
# Направление сортировки ASC|DESC. По умолчанию ASC
phonebook.employee.sort-direction=DESC
````
При необходимости можно выделить определенного работника (например, жирным шрифтом).\
Для этого необходимо в поле displayClassStyle указать соответствующий CSS класс (без точки в начале).\
Сам класс нужно описать в файле
````
./BOOT-INF/classes/static/css/main.css
````
Можно использовать уже имеющиеся классы:
````
.boss {
    font-weight: bold;
}
.bigboss {
    font-weight: bold;
    text-decoration: underline;
}
````

### Начало работы
##### Запуск приложения и вход
После завершения все настроек запустите приложение командой
````
sudo java org.springframework.boot.loader.JarLauncher
````
Далее в браузере необходимо пройти по следующему адресу:
````
http://<IP адресс или DNS имя>/
````
Откроется главная страница, где в дальнейше будет отображаться список работников.\
Для редактирования справочника необходимо пройти авторизацию. Нажмите кнопку "Войти" или введите в адресную строку
````
http://<IP адресс или DNS имя>/login
````
Для входа используйте следующие данные:
````
Login: admin
Password: 123
````

##### Добавление локальной записи
Далее для добавления новых локальных работников в верхнем меню выберите "Сотрудники - Локальные" или введите в адресную строку
````
http://<IP адресс или DNS имя>/employees?type=local
````
Далее выберите "Добавить запись"

##### Импорт из AD
Для ручного импорта работников из AD в верхнем меню выберите "Сотрудники - Импорт из AD" или введите в адресную строку
````
http://<IP адресс или DNS имя>/employees?type=ldap
````
Далее нажмите кнопку "Импорт из AD".\
При успешном импорте отобразиться кол-во импортированных записей.\
Если в БД уже имеется запись с таким же уникальным ИД (userPrincipalName), то такая все поля этой записи будут перезаписаны.\
Если после импорта окажется что ранее импортированная записть не присутствует в выгрузке, то она будет помечена на удаление и скрыта.
##### Внимание!!! Изменять импортироваанные записи нет смысла, т.к. при последующем обновлении записей все изменения будут утеряны.

Из AD для каждой записи импортируются следующие поля:
````
userPrincipalName   - используется как уникальный идентификатор (параметр externalId)
sAMAccountName      - не используется
mail                - электронная почта (параметр email)
displayName         - ФИО (параметр fullName)
title               - должность (параметр fullName)
department          - подразделение/отдел (параметр fullName)
company             - компания (параметр fullName)
physicalDeliveryOfficeName  - расположение/адрес (параметр address)
telephoneNumber     - внутренний телефон (параметр internalPhoneNumber)
homeNumber          - городсткой номер (параметр cityPhoneNumber)
mobile              - мобильный номер(параметр mobilePhoneNumber)
description         - не используется
manager             - руководитель (параметр manager)
postalCode          - приоритет отображения (параметр displayPriority)
postOfficeBox       - css класс отображения (параметр displayClassStyle)
````

##### Сортировка справочника
Сортировка осуществляется по полям справочника. Поле и направление можно задать в файле конфигурации (см. выше).
Для более тонкой настройки сортировки нужно использовать поле displayPriority. Данное поле является числовым. 
По умолчанию чем больше его значение тем выше приоритет.\
Задать значение этого поля для локальных записей можно по средствам редактирования записи в самом приложении.
Для импортированных записей необходимо задавать значания этого поля в системе из которой производился импорт. Для AD это поле postalCode.

##### Применене CSS стилей в произвольным записям
Аналогично сортировке (см. выше).
Для импортированных записей из AD это поле postOfficeBox.

### Изменение HTML, CSS, JS
Файлы представления HTML расположены
````
./BOOT-INF/classes/templates
````
CSS
````
./BOOT-INF/classes/static/css
````
JS
````
./BOOT-INF/classes/static/js
````
Их можно изменять по своему усмотрению