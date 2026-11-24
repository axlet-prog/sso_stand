# j-swagger-ui

Отдельное приложение Swagger UI для документации SSO и J resource.

## Docker Compose

Из корня репозитория:

```bash
docker compose up -d --build --wait j-swagger-ui
```

Открыть [http://localhost:7778/](http://localhost:7778/).
Образ собирается из исходников через Maven Wrapper; установленная на хосте Java
не требуется. Приложение работает под непривилегированным пользователем.

Для запуска всего стенда:

```bash
docker compose up -d --build --wait
```

Swagger UI запускается независимо от доступности API. В Compose документация
SSO выбрана по умолчанию и загружается с `http://localhost:8080/v3/api-docs`.
Документация J resource доступна с `http://localhost:9001/v3/api-docs`;
сервис включён в общий Compose. В интерфейсе выберите `J resource`.

Адреса задаются переменными `SSO_API_DOCS_URL` и `RESOURCE_API_DOCS_URL`
в окружении команды Compose или корневом `.env`. Они должны быть доступны **из браузера**, поскольку спецификации
загружает Swagger UI на клиенте.
После изменения адресов выполните `docker compose up -d j-swagger-ui`.
Сервисы API должны разрешать CORS для `http://localhost:7778`;
в конфигурации SSO этот origin уже разрешён.

Проверка и логи:

```bash
docker compose ps j-swagger-ui
curl -fsS http://localhost:7778/actuator/health
curl -fsS http://localhost:7778/v3/api-docs/swagger-config
docker compose logs --tail=100 j-swagger-ui
```

## Локальный запуск

Из `backend/j-swagger-ui`, с JDK 21 или новее:

```bash
./mvnw package
java -jar target/j-swagger-ui.jar
```

По умолчанию используются порт `7778`, документация SSO на `localhost:7777`
и J resource на `localhost:9001`. Порт меняется через `SERVER_PORT`, адреса
спецификаций — через те же переменные, что и в Compose.
