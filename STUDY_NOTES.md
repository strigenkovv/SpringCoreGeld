# Краткий конспект Spring Core

## Архитектура

```text
MyRunner → Service → Repository → JdbcClient → DataSource → Database
```

- `MyRunner` — получает ввод пользователя и выводит результат.
- `Service` — выполняет сценарий использования и управляет транзакцией.
- `Repository` — выполняет SQL-запросы.
- Domain class (`Mens`, `Schenking`) — представляет объект реального мира и содержит бизнес-правила.

## Repository

Репозиторий обычно выполняет один SQL-запрос в одном методе.

```java
@Repository
public class MensRepository {
    private final JdbcClient jdbcClient;
}
```

Основные операции:

```java
query(Mens.class).list();       // список записей
query(Mens.class).optional();   // одна запись или пустой Optional
update();                       // INSERT, UPDATE или DELETE
```

Для параметров SQL всегда использовать `?`:

```java
var sql = "select id, naam from mensen where id = ?";
return jdbcClient.sql(sql)
        .param(id)
        .query(Mens.class)
        .optional();
```

Не соединять SQL и ввод пользователя через `+`: это SQL-инъекция.

### Основные методы JdbcClient

```java
query(Long.class).single();       // одно scalar value
query(Mens.class).list();         // несколько объектов
query(Mens.class).optional();     // один объект или пустой Optional
params(a, b, c).update();         // несколько параметров
```

Для автоматически созданного id:

```java
var keyHolder = new GeneratedKeyHolder();
jdbcClient.sql(sql)
        .params(naam, geld)
        .update(keyHolder);
return keyHolder.getKey().longValue();
```

Читать нужно только нужные столбцы, а не `select *`.

Для нескольких id используется `in`:

```sql
where id in (?,?,?)
```

## Аннотации Spring

- `@Component` — обычный Spring bean.
- `@Service` — bean с ролью сервиса.
- `@Repository` — bean для доступа к базе и перевода `SQLException` в более конкретные Spring exceptions.
- `@Transactional` — выполнить метод в транзакции.
- `@Transactional(readOnly = true)` — транзакция только для чтения.
- `@JdbcTest` — тест JDBC с транзакцией и автоматическим rollback.
- `@Sql` — выполнить SQL-файл перед тестом.

## Spring exceptions

Исправимые ситуации:

- `DuplicateKeyException` — дублирование уникального значения;
- `DataIntegrityViolationException` — нарушение ограничения базы;
- `CannotAcquireLockException` — запись заблокирована.

Фатальные ситуации:

- `BadSqlGrammarException` — ошибка синтаксиса SQL;
- `InvalidResultSetAccessException` — обращение к несуществующему столбцу;
- `TypeMismatchDataException` — несовместимые типы Java и базы;
- `PermissionDeniedDataAccessException` — недостаточно прав.

## ACID

- **Atomic** — транзакция полностью выполняется или полностью отменяется.
- **Consistent** — база остаётся корректной после commit и rollback.
- **Isolated** — незакоммиченные изменения не видны другим транзакциям.
- **Durable** — завершённая транзакция сохраняется даже после сбоя системы.

## SQL injection

Небезопасно:

```java
"... where naam = '" + naam + "'"
```

Безопасно:

```java
"... where naam = ?"
```

```java
jdbcClient.sql(sql).param(naam).update();
```

Параметры одновременно защищают от SQL injection и позволяют базе повторно использовать execution plan.

## Logging

Для просмотра SQL и значений параметров в `application.properties`:

```properties
logging.level.org.springframework.jdbc.core=TRACE
```

Сообщения Hikari можно отключить:

```properties
logging.level.com.zaxxer.hikari=off
```

## MyRunner

`MyRunner` реализует `CommandLineRunner`. Spring вызывает `run()` после запуска приложения.

```java
@Component
public class MyRunner implements CommandLineRunner {
}
```

`MyRunner` не должен содержать SQL и бизнес-правила. Он только получает ввод пользователя и вызывает сервис.

## Большой use-case

Если операция содержит несколько действий с базой, все они выполняются через один метод сервиса:

```java
@Transactional
public void schenk(Schenking schenking) {
    // найти и заблокировать отправителя
    // найти и заблокировать получателя
    // применить бизнес-правило Mens
    // обновить обе записи
}
```

Репозиторий выполняет отдельные SQL-запросы, а сервис объединяет их в одну транзакцию.

## Lock

Чтобы прочитать и заблокировать запись до конца транзакции:

```sql
select id, naam, geld
from mensen
where id = ?
for update
```

Это предотвращает одновременное изменение или удаление записи другим пользователем.

## Service и транзакции

```java
@Service
@Transactional(readOnly = true)
public class MensService {
}
```

Если метод изменяет данные, у него должна быть обычная транзакция:

```java
@Transactional
public void schenk(Schenking schenking) {
    // несколько операций Repository
}
```

Транзакция:

```text
всё успешно → commit
ошибка      → rollback
```

`rollback` отменяет добавления, изменения и удаления, выполненные в транзакции.

Для блокировки записи используется:

```sql
select ...
from mensen
where id = ?
for update
```

## Domain class и бизнес-правила

Бизнес-правила находятся в domain class, а не в Service.

Пример `Schenking`:

- `vanMensId` — id отправителя;
- `aanMensId` — id получателя;
- `bedrag` — сумма;
- id должны быть положительными;
- нельзя дарить самому себе;
- сумма должна быть положительной.

`Mens.schenk(...)` проверяет, хватает ли денег, изменяет баланс отправителя и получателя.

Отдельный класс операции нужен, если операция имеет несколько связанных параметров и правил. Если нужен только один `id`, отдельный класс обычно не нужен.

## Optional и исключения

```java
var mens = repository.findById(id)
        .orElseThrow(() -> new MensNietGevondenException(id));
```

- найденная запись → объект `Mens`;
- запись не найдена → пустой `Optional` или собственное исключение.

## Тесты Repository

```java
@JdbcTest
@Import(MensRepository.class)
@Sql("/mensen.sql")
class MensRepositoryTest {
}
```

- `@JdbcTest` создаёт тестовый `DataSource` и запускает тест в транзакции;
- `@Sql("/mensen.sql")` выполняет SQL перед каждым тестом;
- после теста Spring делает `rollback`;
- поэтому тестовые записи не остаются в базе.

Для проверки базы используются `JdbcTestUtils`:

```java
var aantalRecords =
        JdbcTestUtils.countRowsInTable(jdbcClient, "mensen");
```

Не использовать в тестах жёстко заданное количество записей, если его можно получить из базы.

## Полезные нидерландские слова

| Nederlands | Русский |
|---|---|
| repository | репозиторий |
| service | сервис |
| database | база данных |
| record | запись |
| tabel/table | таблица |
| toevoegen | добавлять |
| wijzigen | изменять |
| verwijderen | удалять |
| zoeken/vinden | искать/находить |
| schenken | дарить |
| schenking | дарение |
| bedrag | сумма |
| geld | деньги |
| locken/vergrendelen | блокировать |
| rollback | отмена изменений |
| commit | окончательное сохранение |
| voldoende | достаточно |
| onvoldoende | недостаточно |
