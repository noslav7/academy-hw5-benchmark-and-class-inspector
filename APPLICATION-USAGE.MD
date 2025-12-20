# Руководство по запуску

## Сборка
```powershell
mvn package
```

## 1. Бенчмарки (`hw5-benchmark`)

### Запуск
```powershell
java -cp "hw5-benchmark\target\classes;hw5-benchmark\target\dependency\*" org.openjdk.jmh.Main StudentNameBenchmark
```

### Сохранение результатов в файл
```powershell
java -cp "hw5-benchmark\target\classes;hw5-benchmark\target\dependency\*" org.openjdk.jmh.Main StudentNameBenchmark -rf text -rff hw5-benchmark\benchmark-results.txt
```

### Что измеряется
- 4 способа вызова `Student#name()`: прямой вызов, `Method`, `MethodHandle`, `LambdaMetafactory`

## 2. ClassInspector (`hw5-class-inspector`)

### Запуск
```powershell
java -cp "hw5-class-inspector\target\classes;hw5-class-inspector\target\dependency\*" academy.Application --class java.lang.String --format TEXT
java -cp "hw5-class-inspector\target\classes;hw5-class-inspector\target\dependency\*" academy.Application --class java.util.ArrayList --format JSON
java -cp "hw5-class-inspector\target\classes;hw5-class-inspector\target\dependency\*" academy.Application --class your.Class --create
```

### Опции CLI
- `--class, -c` (обязательный): полное имя класса
- `--format, -f`: `TEXT` | `JSON` (по умолчанию `TEXT`)
- `--create`: создать экземпляр класса
