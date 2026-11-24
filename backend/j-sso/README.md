# j-sso

SSO-сервис на Spring Boot и Spring Security: вход по логину/паролю и через
GitHub, Google, Яндекс; OAuth2 Authorization Code, Refresh Token и Client Credentials.
Выдаёт opaque-токены с проверкой через introspection.

PostgreSQL хранит пользователей и OAuth-клиентов, Redis — сессии и авторизации.
Liquibase применяет миграции при старте. Интерфейс — React в каталоге [client](client).

**Команды ниже выполняются из `backend/j-sso`.** Для Docker нужен Compose v2;
для локальной сборки — JDK 25. Maven Wrapper сам загрузит Maven, а профиль
сборки клиента — Node.js и npm.

## Подготовка

Если `.env` ещё нет, создайте его из [.env.example](.env.example):

```bash
cp .env.example .env
```

Заполните `GITHUB_CLIENT_ID`, `GITHUB_CLIENT_SECRET`, `GOOGLE_CLIENT_ID`,
`GOOGLE_CLIENT_SECRET`, `YANDEX_CLIENT_ID`, `YANDEX_CLIENT_SECRET`.
Все шесть значений обязательны; непустые заполнители подходят для проверки запуска,
настоящие credentials нужны для входа через провайдеров.

Для совместимости со Spring используйте формат `KEY=value` без `export` и кавычек.
При локальном запуске `.env` читается из рабочего каталога процесса;
Compose передаёт его значения через `env_file`.

## Docker Compose

Весь стенд запускается одним Compose: frontend/Nginx, backend, PostgreSQL и Redis.

```bash
docker compose -f ../../docker-compose.yml up -d --build --wait
docker compose -f ../../docker-compose.yml ps
```

- Интерфейс: [http://localhost:8080/login](http://localhost:8080/login).
- Backend и Actuator: `http://localhost:7777`.
- Nginx проксирует POST формы входа, OAuth и API в backend.
- Backend-образ не содержит React-статику; GET `/login` на порту `7777`
  перенаправляет на frontend.

```bash
docker compose -f ../../docker-compose.yml logs --tail=100 j-sso-client j-sso-sever
docker compose -f ../../docker-compose.yml down
```

PostgreSQL 18 сохраняет данные в Docker volume `postgres-data`, подключённом
к `/var/lib/postgresql`. `docker compose down` сохраняет том, `down -v` удаляет его.
Постоянный том Redis не настроен: после пересоздания контейнера сессии могут пропасть.

## Локальный запуск: backend и frontend в одном JAR

Запустите только зависимости, затем соберите приложение вместе с актуальным React:

```bash
docker compose -f ../../docker-compose.yml up -d --wait sso-postgres sso-redis
./mvnw -Pdev,client-build-and-copy clean package

APP_FRONTEND_URL= \
SPRING_PROFILES_ACTIVE=development \
SPRING_SECURITY_OAUTH2_AUTHORIZATIONSERVER_AUTHENTICATIONSUCCESSURL=http://localhost:7777/ui-test \
java -jar target/j-sso.jar
```

Интерфейс: [http://localhost:7777/login](http://localhost:7777/login).
Пустой `APP_FRONTEND_URL` включает встроенную страницу. Порт `7777` должен быть
свободен от Docker-backend.

Для IDE: main-класс `com.example.jsso.JSsoApplication`, рабочий каталог
`backend/j-sso`, Spring-профиль `development`. Перед запуском выполните сборку выше:
она подготовит frontend и `build-info.properties`.

`client-only-copy` включает в JAR уже готовый `client/dist`. Maven-профили `dev`/`prod`
выбирают режим сборки клиента; Spring-профиль задаётся отдельно при запуске.

## Основные настройки

Конфигурация: [application.yml](src/main/resources/application.yml).
Переменные используют формат `${ИМЯ:локальный дефолт}` и могут задаваться
в окружении или `.env`.

| Переменная | Назначение / локальный дефолт |
|---|---|
| `SERVER_PORT` | Порт backend: `7777` |
| `SPRING_DATASOURCE_URL` | `jdbc:postgresql://localhost:5431/j-sso` |
| `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD` | PostgreSQL: `user` / `user` |
| `SPRING_DATA_REDIS_HOST`, `SPRING_DATA_REDIS_PORT` | Redis: `localhost:6379` |
| `SPRING_DATA_REDIS_PASSWORD` | `qwerty12345678` |
| `APP_FRONTEND_URL` | Пусто — встроенный UI; в Compose — `http://localhost:8080` |
| `CORS_ALLOWED_ORIGINS` | Разрешённые origins через запятую |

Адрес backend для Nginx задаёт **`SSO_BACKEND_URL`**: дефолт
`http://j-sso-sever:7777`. Переопределяйте его в окружении команды Compose или
корневом `.env`; после изменения достаточно пересоздать frontend без пересборки.

Callback у каждого внешнего OAuth-провайдера:
`http://localhost:<порт>/login/oauth2/code/<provider>`, где `<provider>` —
`github`, `google` или `yandex`; порт `8080` для Compose и `7777` для локального JAR.

Профиль `development` включает dev-данные Liquibase. Для входа по паролю
демонстрационным пользователям нужно задать BCrypt-хэш известного пароля:
готовая рабочая пара логин/пароль в миграциях не предусмотрена.

## Проверки

```bash
./mvnw test
curl -fsS http://localhost:7777/actuator/health
curl -fsS http://localhost:7777/v3/api-docs
```

`/actuator/health` учитывает PostgreSQL и Redis. `/healthz` на порту `8080`
проверяет только Nginx. Также доступны GET `/actuator/info` и `/actuator/metrics`.

