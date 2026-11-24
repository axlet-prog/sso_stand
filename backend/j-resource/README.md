# j-resource

Пример защищённого API на Spring Boot. При получении Bearer-токена сервис
проверяет его в SSO через Nginx и восстанавливает пользователя и его права
из ответа introspection. HTTP-сессия для запросов API не создаётся.

## Docker Compose

Из корня репозитория запустить весь стенд:

```bash
docker compose up -d --build --wait
```

Если SSO уже работает, достаточно добавить API:

```bash
docker compose up -d --build --wait j-resource
```

API доступен на `http://localhost:9001`, спецификация —
[http://localhost:9001/v3/api-docs](http://localhost:9001/v3/api-docs).
В [Swagger UI](http://localhost:7778/) выберите `J resource`.

Путь проверки токена внутри Docker:

```text
j-resource -> http://j-sso-client/oauth2/token-info -> j-sso-sever:7777
```

`j-sso-client` — имя контейнерного сервиса Nginx; внутри Docker он слушает порт
`80`. Для браузера OAuth2-адреса в спецификации используют `http://localhost:8080`.
Ни браузер, ни локально запущенный Java-процесс не должны использовать
Docker-имя `j-sso-client`.

Для introspection используется отдельная аутентификация OAuth2-клиента
через HTTP Basic. В Compose по умолчанию заданы `test-client` / `test-client`,
соответствующие dev-регистрации в БД. Для другой регистрации задайте
`RESOURCE_SSO_CLIENT_ID` и `RESOURCE_SSO_CLIENT_SECRET` в окружении команды
Compose или корневом `.env`, затем выполните `docker compose up -d j-resource`.
Клиент должен поддерживать метод `client_secret_basic`.

## Проверка API

| Запрос                 | Ожидаемое поведение                                              |
|------------------------|------------------------------------------------------------------|
| `GET /actuator/health` | Без токена, здоровье самого приложения                           |
| `GET /v3/api-docs`     | Без токена, спецификация OpenAPI                                 |
| `GET /test`            | С действующим Bearer-токеном, информация о principal             |
| `GET /test-auth`       | С токеном пользователя с правом `GET_OWN_DATA`, имя пользователя |

```bash
curl -fsS http://localhost:9001/actuator/health
curl -fsS http://localhost:9001/v3/api-docs
curl -i http://localhost:9001/test
```

Последний запрос должен вернуть `401`. Для проверки авторизованного запроса
сохраните выданный SSO access token в переменную `ACCESS_TOKEN`:

```bash
curl -fsS -H "Authorization: Bearer ${ACCESS_TOKEN}" http://localhost:9001/test
curl -i -H "Authorization: Bearer ${ACCESS_TOKEN}" http://localhost:9001/test-auth
```

Недействующий токен приводит к `401`; отсутствие права `GET_OWN_DATA`
у аутентифицированного principal — к `403`. Токен Client Credentials
не содержит пользователя и этого права.

Контейнер ждёт готовности Nginx. Его healthcheck проверяет сам API;
работающий SSO и корректная регистрация клиента нужны для introspection.

## Локальный запуск

Нужен JDK 25. Из `backend/j-resource`:

```bash
./mvnw package
java -jar target/j-resource.jar
```

По умолчанию локальный процесс также обращается через Nginx:
`http://localhost:8080/oauth2/token-info`. Для локального запуска освободите
порт `9001`, если он занят контейнером.

Основные переменные приложения:

| Переменная                                                           | Значение по умолчанию вне Compose         |
|----------------------------------------------------------------------|-------------------------------------------|
| `SERVER_PORT`                                                        | `9001`                                    |
| `SPRING_SECURITY_OAUTH2_RESOURCESERVER_OPAQUETOKEN_INTROSPECTIONURI` | `http://localhost:8080/oauth2/token-info` |
| `SPRING_SECURITY_OAUTH2_RESOURCESERVER_OPAQUETOKEN_CLIENTID`         | `test-client`                             |
| `SPRING_SECURITY_OAUTH2_RESOURCESERVER_OPAQUETOKEN_CLIENTSECRET`     | `test-client`                             |
| `CORS_ALLOWED_ORIGINS`                                               | `http://localhost:7778`                   |

## Тесты и логи

Из `backend/j-resource`:

```bash
./mvnw test
```

Тесты HTTP-клиента проверяют форму запроса introspection, Basic-аутентификацию,
разбор ответа SSO с датами и правами, отклонение неактивных токенов,
пустых ответов и ошибок аутентификации клиента. Тест запуска приложения
проверяет healthcheck, CORS документации и отклонение запросов без Bearer-токена
без создания сессии. Все тесты также выполняются при сборке Docker-образа.

Из корня репозитория:

```bash
docker compose ps j-resource
docker compose logs --tail=100 j-resource
docker compose logs --tail=100 j-sso-client
```
