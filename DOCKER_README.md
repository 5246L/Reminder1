
## Быстрый старт

### 1. Создай `.env` файл

```bash
cp .env.example .env
```

**Заполни реальными данными:**

```env
MAIL_USERNAME=your-email@gmail.com
MAIL_PASSWORD=your-app-password
TELEGRAM_BOT_TOKEN=123456789:ABCxyz...
GOOGLE_CLIENT_ID=your-client-id.apps.googleusercontent.com
GOOGLE_CLIENT_SECRET=your-secret
```

---

### 2. Запусти Docker Compose

```bash
# Собрать и запустить
docker-compose up --build

# Или в фоновом режиме
docker-compose up --build -d
```

---

### 3. Проверь что работает

```bash
# Посмотреть запущенные контейнеры
docker-compose ps

# Логи приложения
docker-compose logs -f app

# Логи БД
docker-compose logs -f postgres
```

**Открой в браузере:**
```
http://localhost:8080/api/v1/user/profile
```

---

## Полезные команды

### Управление контейнерами

```bash
# Остановить
docker-compose down

# Остановить + удалить volumes (БД очистится!)
docker-compose down -v

# Перезапустить только приложение
docker-compose restart app

# Пересобрать без кеша
docker-compose build --no-cache app
```

---

### Логи

```bash
# Все логи
docker-compose logs -f

# Последние 100 строк
docker-compose logs --tail=100 app

# Следить за новыми логами
docker-compose logs -f --tail=50 app
```

---

### Доступ к контейнерам

```bash
# Зайти в контейнер приложения
docker exec -it reminder_app sh

# Зайти в PostgreSQL
docker exec -it reminder_postgres psql -U postgres -d reminder_db

# Посмотреть переменные окружения
docker exec reminder_app env
```

---

### База данных

```bash
# Подключиться к БД
docker exec -it reminder_postgres psql -U postgres -d reminder_db

# SQL команды:
\dt                          # Список таблиц
\d users                     # Структура таблицы
SELECT * FROM users;         # Данные
\q                           # Выйти
```

---

## Архитектура

```
┌─────────────────┐
│   reminder_app  │ :8080
│  (Spring Boot)  │
└────────┬────────┘
         │
         │ jdbc:postgresql://postgres:5432
         │
         ▼
┌─────────────────┐
│ reminder_postgres│ :5432
│  (PostgreSQL)   │
└─────────────────┘
```

---

## Порты

- **8080** — Spring Boot API
- **5432** — PostgreSQL

---

## Volumes

- `postgres_data` — данные PostgreSQL (сохраняются между перезапусками)

---

## Что делать если что-то не работает

### Приложение не запускается

```bash
# Посмотри логи
docker-compose logs app

# Проверь что БД запустилась
docker-compose ps postgres
```

### БД не подключается

```bash
# Проверь healthcheck
docker inspect reminder_postgres | grep Health

# Перезапусти БД
docker-compose restart postgres
```

### Liquibase ошибки

```bash
# Пересоздай БД
docker-compose down -v
docker-compose up --build
```

---

## Production deployment

**Для production НЕ используй встроенные креды!**

Используй Docker Secrets или внешний secrets manager (AWS Secrets Manager, HashiCorp Vault, etc.)

---

## Troubleshooting

### Порт 8080 занят

```bash
# Найди процесс
lsof -i :8080

# Убей процесс
kill -9 <PID>

# Или измени порт в docker-compose.yml
ports:
  - "8081:8080"  # Внешний порт 8081
```

### Порт 5432 занят

```bash
# Останови локальный PostgreSQL
brew services stop postgresql

# Или измени порт
ports:
  - "5433:5432"  # Внешний порт 5433
```

---

**Готово!** 🚀
