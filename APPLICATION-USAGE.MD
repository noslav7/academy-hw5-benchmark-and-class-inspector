# Руководство по запуску и использованию

## 1. Бенчмарки (`hw5-benchmark`)

### Сборка
```powershell
mvn package
```

### Запуск JMH
- Класс запуска: `org.openjdk.jmh.Main`
- Минимальный пример (PowerShell):
```powershell
java -cp "hw5-benchmark\target\classes;hw5-benchmark\target\dependency\*" org.openjdk.jmh.Main StudentNameBenchmark
```
- Чтобы сохранить отчёт в файл (формат text):
```powershell
java -cp "hw5-benchmark\target\classes;hw5-benchmark\target\dependency\*" org.openjdk.jmh.Main StudentNameBenchmark -rf text -rff hw5-benchmark\benchmark-results.txt
```

### Что измеряется
- 4 способа вызова `Student#name()`:
  1) directCall (прямой вызов)
  2) reflectionCall (`Method`)
  3) methodHandleCall (`MethodHandle`)
  4) lambdaMetafactoryCall (`LambdaMetafactory`)

### Файлы результатов
- `hw5-benchmark/benchmark-results.txt` — краткая таблица одного прогона (если запускали с `-rff`).
- `hw5-benchmark/benchmark-results/student-name-benchmark.txt` — отчёт с шапкой (JMH/JDK/параметры), получен отдельным запуском. Числа могут отличаться, это разные прогоны.
- Файлы не обновляются автоматически: переписываются только при запуске JMH с указанием `-rff` в тот же путь или при явном редиректе вывода.

## 2. ClassInspector (`hw5-class-inspector`)

### Возможности
- Инспекция класса: имя, суперкласс, интерфейсы, поля (доступ, тип), методы (доступ, параметры, возвращаемый тип), аннотации, дерево наследования (включая sealed permitted).
- Форматы вывода: `TEXT` (по умолчанию) и `JSON`.
- Генерация экземпляра класса с автозаполнением случайными данными (`--create`, аналог Instancio.create).
- Коды возврата CLI:
  - 0 — успех
  - 2 — некорректные параметры (класс не найден/формат)
  - 1 — прочие ошибки выполнения

### Требования к кодировке (Windows/PowerShell)
- Выполнить перед запуском, чтобы русские сообщения не “ломались”:
```powershell
chcp 65001
$env:JAVA_TOOL_OPTIONS="-Dfile.encoding=UTF-8"
```
- Либо добавить к конкретной команде `-Dfile.encoding=UTF-8`.

### Примеры запуска
- Инспекция в TEXT:
```powershell
java -cp "hw5-class-inspector\target\classes;hw5-class-inspector\target\dependency\*" academy.Application --class java.lang.String --format TEXT
```
- Инспекция в JSON:
```powershell
java -cp "hw5-class-inspector\target\classes;hw5-class-inspector\target\dependency\*" academy.Application --class java.util.ArrayList --format JSON
```
- Создание экземпляра и вывод в JSON:
```powershell
java -cp "hw5-class-inspector\target\classes;hw5-class-inspector\target\dependency\*" academy.Application --class your.Class --create
```
  Если экземпляр не может быть создан (абстрактный/интерфейс/нет доступного конструктора), будет ошибка (exit code 1).

### Опции CLI
- `--class, -c` (обязательный): полное имя класса для анализа.
- `--format, -f`: `TEXT` | `JSON` (по умолчанию `TEXT`).
- `--create`: сгенерировать экземпляр и вывести JSON вместо инспекции.

### Примечания
- При повторных запусках с `JAVA_TOOL_OPTIONS` не забудьте сбросить при необходимости: `Remove-Item Env:JAVA_TOOL_OPTIONS`.
- Иерархия учитывает sealed-классы через permitted subclasses.
- Генерация объектов ограничена глубиной (MAX_DEPTH = 3) и кешом, чтобы избежать циклов.

