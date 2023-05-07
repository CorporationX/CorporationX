# Service Template

Стандартный шаблон проекта на SpringBoot

# Использованные технологии

* [Spring Boot](https://spring.io/projects/spring-boot) – как основной фрэймворк
* [PostgreSQL](https://www.postgresql.org/) – как основная реляционная база данных
* [Redis](https://redis.io/) – как кэш и очередь сообщений через pub/sub
* [testcontainers](https://testcontainers.com/) – для изолированного тестирования с базой данных
* [Liquibase](https://www.liquibase.org/) – для ведения миграций схемы БД
* [Gradle](https://gradle.org/) – как система сборки приложения

# База данных

* База поднимается в отдельном сервисе [infra](../infra)
* Redis поднимается в единственном инстансе тоже в [infra](../infra)
* Liquibase сам накатывает нужные миграции на голый PostgreSql при старте приложения
* В тестах используется [testcontainers](https://testcontainers.com/), в котором тоже запускается отдельный инстанс
  postgres
* В коде продемонстрирована работа как с JdbcTemplate, так и с JPA (Hibernate)

# Как начать разработку начиная с шаблона?

1. Сначала нужно склонировать этот репозиторий

```shell
git clone https://github.com/FAANG-School/ServiceTemplate
```

2. Далее удаляем служебную директорию для git

```shell
# Переходим в корневую директорию проекта
cd ServiceTemplate
rm -rf .git
```

3. Далее нужно создать совершенно пустой репозиторий в github/gitlab

4. Создаём новый репозиторий локально и коммитим изменения

```shell
git init
git remote add origin <link_to_repo>
git add .
git commit -m "<msg>"
```

Готово, можно начинать работу!

# Как запустить локально?

Сначала нужно развернуть базу данных из директории [infra](../infra)

Далее собрать gradle проект

```shell
# Нужно запустить из корневой директории, где лежит build.gradle.kts
gradle build
```

Запустить jar'ник

```shell
java -jar build/libs/ServiceTemplate-1.0.jar
```

Но легче всё это делать через IDE

# Код

RESTful приложения калькулятор с единственным endpoint'ом, который принимает 2 числа и выдает результаты их сложения,
вычитаяни, умножения и деления

* Обычная трёхслойная
  архитектура – [Controller](src/main/java/faang/school/postservice/controller), [Service](src/main/java/faang/school/postservice/service), [Repository](src/main/java/faang/school/postservice/repository)
* Слой Repository реализован и на jdbcTemplate, и на JPA (Hibernate)
* Написан [GlobalExceptionHandler](src/main/java/faang/school/postservice/controller/GlobalExceptionHandler.java)
  который умеет возвращать ошибки в формате `{"code":"CODE", "message": "message"}`
* Используется TTL кэширование вычислений
  в [CalculationTtlCacheService](src/main/java/faang/school/postservice/service/cache/CalculationTtlCacheService.java)
* Реализован простой Messaging через [Redis pub/sub](https://redis.io/docs/manual/pubsub/)
  * [Конфигурация](src/main/java/faang/school/postservice/config/RedisConfig.java) –
    сетапится [RedisTemplate](https://docs.spring.io/spring-data/redis/docs/current/api/org/springframework/data/redis/core/RedisTemplate.html) –
    класс, для удобной работы с Redis силами Spring
  * [Отправитель](src/main/java/faang/school/postservice/service/messaging/RedisCalculationPublisher.java) – генерит
    рандомные запросы и отправляет в очередь
  * [Получатель](src/main/java/faang/school/postservice/service/messaging/RedisCalculationSubscriber.java) –
    получает запросы и отправляет задачи асинхронно выполняться
    в [воркер](src/main/java/faang/school/postservice/service/worker/CalculationWorker.java)

# Тесты

Написаны только для единственного REST endpoint'а
* SpringBootTest
* MockMvc
* Testcontainers
* AssertJ
* JUnit5
* Parameterized tests

# TODO

* Dockerfile, который подключается к сети запущенной postgres в docker-compose
* Redis connectivity
* ...
