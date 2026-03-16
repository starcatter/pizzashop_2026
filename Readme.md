# Laboratorium 4: Logowanie, Adnotacje — 17.03.2026, 8:00–9:30

## Temat zajęć
Konfiguracja logowania z SLF4J i Logback. Tworzenie i przetwarzanie adnotacji w runtime. Zastosowanie obu technik w projekcie pizzerii.

Zaczynamy od nowego, minimalnego projektu demonstracyjnego. W drugiej połowie zajęć wracamy do projektu pizzerii.

Projekt pizzerii (branch `lab4-start`): **https://github.com/starcatter/pizzaShop_2026**

---

## Wejściówka 2 (8:00–8:15)

### 9 pytań jednokrotnego wyboru.
Czas: 15 minut.
Pytania dotyczą materiału z Lab 2 i Lab 3.

## Część 1 (8:15–8:40): Logowanie z SLF4J + Logback

### 1.1 Nowy projekt demonstracyjny

Utwórz nowy projekt Maven, który posłuży do nauki logowania i adnotacji:

1. W IntelliJ: *File → New → Project*.
2. Wybierz **Maven Archetype** po lewej stronie.
3. Ustaw:
   - **Name**: `logging-demo`
   - **Archetype**: `maven-archetype-quickstart` (wersja 1.5 lub najnowsza)
   - **GroupId**: `org.example`
4. Kliknij **Create**.

Odpowiednik w linii komend (wymaga zainstalowanego Maven):
```bash
mvn archetype:generate -DgroupId=org.example -DartifactId=logging-demo \
    -DarchetypeArtifactId=maven-archetype-quickstart -DarchetypeVersion=1.5 \
    -DinteractiveMode=false
```

#### Maven Archetypes — podstawy

**Maven Archetype** to szablon projektu, który automatycznie generuje standardową strukturę katalogów, podstawowy plik `pom.xml` oraz przykładowe pliki źródłowe. Działa to na podobnej zasadzie jak `create-react-app` czy `cargo new` w innych ekosystemach.

**Jak to działa?**
Archetype to projekt Maven zawierający szablony plików w formacie **Apache Velocity** (rozszerzenie `.vm`). Podczas generowania projektu Maven podstawia zmienne takie jak `${groupId}`, `${artifactId}` czy `${package}` do tych szablonów, tworząc gotowe do użycia pliki.

```
maven-archetype-quickstart generuje:

logging-demo/
├── pom.xml
├── src/main/java/org/example/App.java
└── src/test/java/org/example/AppTest.java
```

#### Tryb interaktywny (do eksploracji)
```bash
mvn archetype:generate
```
Uruchomienie bez parametrów uruchamia interaktywny kreator, który:
1. Pobiera katalog dostępnych archetype'ów
2. Wyświetla listę do wyboru — wpisz numer szablonu
3. Prosi o potwierdzenie parametrów projektu
4. Generuje projekt po zatwierdzeniu (`Y`)

#### Przydatne linki

- Dokumentacja Maven Archetypes: https://maven.apache.org/archetypes/
- Katalog w Maven Central: https://search.maven.org/search?q=g:org.apache.maven.archetypes
- Apache Velocity: https://velocity.apache.org/

### 1.2 Dodanie zależności

W pliku `pom.xml`, w sekcji `<dependencies>`, dodaj:

```xml
<dependency>
    <groupId>org.slf4j</groupId>
    <artifactId>slf4j-api</artifactId>
    <version>2.0.16</version>
</dependency>
<dependency>
    <groupId>ch.qos.logback</groupId>
    <artifactId>logback-classic</artifactId>
    <version>1.5.12</version>
</dependency>
```

Odśwież projekt Maven: `Ctrl+Shift+O` lub ikona odświeżania w panelu Maven.

**Uwaga dotycząca bezpieczeństwa:** Po odświeżeniu IntelliJ może wyświetlić ostrzeżenie o podatnościach (CVE) w pobranej wersji Logbacka. To normalne — starsze wersje bibliotek często zawierają znane luki bezpieczeństwa.

Kliknij na ostrzeżenie — IDE pokaże szczegóły: numer CVE, opis podatności i informację, od której wersji jest naprawiona. Zaktualizuj numer wersji w `pom.xml` do wskazanej bezpiecznej wersji i odśwież Maven ponownie.

Aby potwierdzić, że używasz najnowszej wersji, wejdź na stronę projektu Logback na GitHubie: [github.com/qos-ch/logback](https://github.com/qos-ch/logback). Otwórz plik `pom.xml` w głównym katalogu repozytorium — znajdziesz tam aktualną wersję developerską. Na moment pisania tych instrukcji najnowszy release to `1.5.32`. Zaktualizuj zależność:

```xml
<version>1.5.32</version>
```

Wykonanie tego ćwiczenia — identyfikacja CVE, znalezienie aktualnej wersji, aktualizacja — to rutynowa czynność w utrzymaniu każdego projektu. Narzędzia CI/CD (np. GitHub Dependabot, Snyk) robią to automatycznie, ale warto wiedzieć, jak to zrobić ręcznie.

**SLF4J** to interfejs logowania — definiuje API (`Logger`, `LoggerFactory`), ale nie implementację. **Logback** to implementacja — przetwarza komunikaty i kieruje je do konsoli, pliku itp. Dzięki rozdzieleniu interfejsu od implementacji można podmienić backend logowania bez zmiany kodu.

Dokumentacja: [SLF4J Manual](https://www.slf4j.org/manual.html) | [Logback Manual](https://logback.qos.ch/manual/)

### 1.3 Podstawy logowania

Zamień wygenerowaną klasę `App.java` na:

```java
package org.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class App {
    private static final Logger log = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) {
        log.trace("This is TRACE — very detailed");
        log.debug("This is DEBUG — diagnostic info");
        log.info("This is INFO — normal operation");
        log.warn("This is WARN — something unusual");
        log.error("This is ERROR — something went wrong");

        String name = "Margherita";
        int count = 42;
        log.info("Prepared {} pizzas of type {}", count, name);
    }
}
```

Konwencje:
- Logger jest `private static final`, zwyczajowo nazywa się `log`.
- `LoggerFactory.getLogger(App.class)` — nazwa klasy identyfikuje źródło komunikatu w logach.
- `log.info("tekst {} i {}", arg1, arg2)` — **logowanie parametryzowane**. Znaczniki `{}` są zastępowane wartościami. String jest konstruowany tylko wtedy, gdy dany poziom jest włączony — oszczędność w produkcji.

Uruchom program. Bez pliku konfiguracyjnego Logback używa trybu domyślnego — `DEBUG` i wyższe na konsolę. Powinieneś zobaczyć 4 komunikaty (TRACE jest poniżej domyślnego progu).

**Wskazówka IDE:** Jeśli w projekcie jest zależność logowania, IntelliJ automatycznie oferuje zamianę `System.out.println()` na wywołania loggera. Najedź kursorem na `System.out.println` i naciśnij `Alt+Enter` — zobaczysz opcję *"Replace with logger call"*. IDE doda też pole loggera do klasy, jeśli go jeszcze nie ma.

Analogicznie, jeśli napiszesz `log.info("Value: " + value)` z konkatenacją stringów, IntelliJ podpowie zamianę na `log.info("Value: {}", value)`. Często szybciej jest napisać konkatenację i pozwolić IDE ją zamienić, niż od razu pamiętać o `{}`.

### 1.4 Poziomy logowania

SLF4J definiuje 5 poziomów, od najdrobniejszych szczegółów do najpoważniejszych problemów:

| Poziom | Zastosowanie | Przykład |
|---|---|---|
| `TRACE` | Najdrobniejsze szczegóły, krok po kroku | "Tick 42: checking oven 1" |
| `DEBUG` | Informacje diagnostyczne | "Cook picked up order: Margherita L" |
| `INFO` | Zdarzenia biznesowe, normalne działanie | "Pizza Margherita boxed" |
| `WARN` | Sytuacja nietypowa, ale obsłużona | "No free oven, returning pizza" |
| `ERROR` | Błąd wymagający uwagi | "Pizza burned in oven!" |

Ustawienie poziomu na `INFO` oznacza: pokaż `INFO`, `WARN`, `ERROR`; ukryj `DEBUG`, `TRACE`. Każdy wyższy poziom zawiera w sobie niższe.

### 1.5 Klasy demonstracyjne

Dodaj dwie klasy. Możesz skopiować poniższy kod i wkleić go bezpośrednio do pakietu `org.example` w drzewie plików po lewej — IntelliJ automatycznie utworzy plik o odpowiedniej nazwie.

**HelloDebug.java:**

```java
package org.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HelloDebug {
    private static final Logger log = LoggerFactory.getLogger(HelloDebug.class);

    public void run() {
        log.trace("HelloDebug: trace message");
        log.debug("HelloDebug: debug message");
        log.info("HelloDebug: info message");
        log.warn("HelloDebug: warn message");
        log.error("HelloDebug: error message");
    }
}
```

**HelloWarnings.java:**

```java
package org.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HelloWarnings {
    private static final Logger log = LoggerFactory.getLogger(HelloWarnings.class);

    public void run() {
        log.trace("HelloWarnings: trace message");
        log.debug("HelloWarnings: debug message");
        log.info("HelloWarnings: info message");
        log.warn("HelloWarnings: warn message");
        log.error("HelloWarnings: error message");
    }
}
```

Zaktualizuj `App.main()`:

```java
public static void main(String[] args) {
    log.info("Application starting");

    new HelloDebug().run();
    new HelloWarnings().run();

    log.info("Application finished");
}
```

Uruchom program. Wszystkie komunikaty `DEBUG+` powinny pojawić się na konsoli (domyślna konfiguracja Logbacka). Teraz będziemy krok po kroku budować konfigurację, żeby skierować logi z różnych klas do różnych miejsc.

### 1.6 Konfiguracja krok po kroku

Utwórz plik `src/main/resources/logback.xml`. Logback szuka go automatycznie w classpath.

**Krok 1 — podstawowa konfiguracja z konsolą.**

Na początek minimalna konfiguracja — konsola z formatowaniem:

```xml
<configuration>
    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%d{HH:mm:ss.SSS} %-5level [%logger{20}] - %msg%n</pattern>
        </encoder>
    </appender>

    <root level="DEBUG">
        <appender-ref ref="CONSOLE" />
    </root>
</configuration>
```

Elementy wzorca (`pattern`):
- `%d{HH:mm:ss.SSS}` — czas (godzina:minuta:sekunda.milisekundy)
- `%-5level` — poziom logowania, wyrównany do 5 znaków
- `%logger{20}` — nazwa loggera (skrócona do 20 znaków)
- `%msg` — treść komunikatu
- `%n` — nowa linia

Uruchom program. Wynik powinien wyglądać tak samo jak bez konfiguracji, ale z ładniejszym formatowaniem.

**Krok 2 — loggery per klasa z poziomami.**

Dodaj loggery dla poszczególnych klas, żeby kontrolować, które klasy logują na jakim poziomie:

```xml
<configuration>
    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%d{HH:mm:ss.SSS} %-5level [%logger{20}] - %msg%n</pattern>
        </encoder>
    </appender>

    <logger name="org.example.HelloDebug" level="DEBUG" />
    <logger name="org.example.HelloWarnings" level="WARN" />

    <root level="INFO">
        <appender-ref ref="CONSOLE" />
    </root>
</configuration>
```

Uruchom program. Zwróć uwagę:
- `HelloDebug` — wypisuje komunikaty `DEBUG` i wyższe (bo `level="DEBUG"`).
- `HelloWarnings` — wypisuje tylko `WARN` i `ERROR` (bo `level="WARN"`).
- `App` — wypisuje `INFO` i wyższe (bo dziedziczy poziom z roota).

Logger dziedziczy appender z roota (domyślna `additivity="true"`), więc wszystkie komunikaty trafiają na konsolę — ale każdy logger filtruje swoje komunikaty niezależnie.

**Krok 3 — dodanie appendera plikowego.**

Dodajmy plik, do którego trafiają **wszystkie** komunikaty:

```xml
<configuration>
    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%d{HH:mm:ss.SSS} %-5level [%logger{20}] - %msg%n</pattern>
        </encoder>
    </appender>

    <appender name="ALL_FILE" class="ch.qos.logback.core.FileAppender">
        <file>target/log.txt</file>
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} %-5level [%logger{36}] - %msg%n</pattern>
        </encoder>
    </appender>

    <logger name="org.example.HelloDebug" level="DEBUG" />
    <logger name="org.example.HelloWarnings" level="WARN" />

    <root level="INFO">
        <appender-ref ref="CONSOLE" />
        <appender-ref ref="ALL_FILE" />
    </root>
</configuration>
```

Uruchom program. Konsola — bez zmian. Sprawdź plik `target/log.txt` — zawiera te same komunikaty co konsola, ale z pełniejszym formatowaniem (data + pełna nazwa loggera).

Plik w katalogu `target/` nie jest commitowany do Gita (jest w `.gitignore`).

**Krok 4 — appendery per klasa.**

Nasz docelowy układ to:
- `HelloDebug` → osobny plik `target/hello-debug.log`
- `HelloWarnings` → osobny plik `target/hello-warnings.log`
- `App` → konsola
- Root → plik `target/log.txt`

Dodajmy dedykowane appendery:

```xml
<configuration>
    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%d{HH:mm:ss.SSS} %-5level [%logger{20}] - %msg%n</pattern>
        </encoder>
    </appender>

    <appender name="ALL_FILE" class="ch.qos.logback.core.FileAppender">
        <file>target/log.txt</file>
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} %-5level [%logger{36}] - %msg%n</pattern>
        </encoder>
    </appender>

    <appender name="DEBUG_FILE" class="ch.qos.logback.core.FileAppender">
        <file>target/hello-debug.log</file>
        <encoder>
            <pattern>%d{HH:mm:ss.SSS} %-5level - %msg%n</pattern>
        </encoder>
    </appender>

    <appender name="WARN_FILE" class="ch.qos.logback.core.FileAppender">
        <file>target/hello-warnings.log</file>
        <encoder>
            <pattern>%d{HH:mm:ss.SSS} %-5level - %msg%n</pattern>
        </encoder>
    </appender>

    <logger name="org.example.HelloDebug" level="DEBUG">
        <appender-ref ref="DEBUG_FILE" />
    </logger>

    <logger name="org.example.HelloWarnings" level="WARN">
        <appender-ref ref="WARN_FILE" />
    </logger>

    <logger name="org.example.App" level="INFO">
        <appender-ref ref="CONSOLE" />
    </logger>

    <root level="DEBUG">
        <appender-ref ref="CONSOLE" />
        <appender-ref ref="ALL_FILE" />
    </root>
</configuration>
```

Uruchom program i sprawdź pliki. Zauważysz problem: komunikaty z `HelloDebug` i `App` pojawiają się na konsoli **podwójnie** — raz z ich własnego appendera `CONSOLE`, raz z roota (propagacja). To dlatego, że `additivity` jest domyślnie `true`.

**Krok 5 — wyciszenie roota na konsoli.**

Chcemy, żeby root logger logował **tylko** do pliku, a konsola była obsługiwana wyłącznie przez loggery per klasa. Usuń `CONSOLE` z roota:

```xml
    <root level="DEBUG">
        <appender-ref ref="ALL_FILE" />
    </root>
```

Uruchom program. Teraz:
- Konsola: tylko komunikaty z `App` (od `INFO` w górę).
- `target/hello-debug.log`: komunikaty z `HelloDebug` (od `DEBUG` w górę).
- `target/hello-warnings.log`: komunikaty z `HelloWarnings` (od `WARN` w górę).
- `target/log.txt`: **wszystkie** komunikaty ze wszystkich klas (od `DEBUG` w górę, przez propagację do roota).

Ale czekaj — `hello-warnings.log` zawiera komunikaty `WARN` i `ERROR`, co jest poprawne, bo logger ma `level="WARN"`. Ale co jeśli chcemy, żeby logger *produkował* komunikaty na poziomie `DEBUG` (żeby trafiały do `log.txt` przez root), ale żeby jego dedykowany plik zawierał tylko `WARN+`?

**Krok 6 — filtry na appenderach.**

Zmień poziom loggera `HelloWarnings` na `DEBUG` (żeby produkował wszystkie komunikaty), ale dodaj filtr na appenderze `WARN_FILE`:

```xml
    <appender name="WARN_FILE" class="ch.qos.logback.core.FileAppender">
        <file>target/hello-warnings.log</file>
        <filter class="ch.qos.logback.classic.filter.ThresholdFilter">
            <level>WARN</level>
        </filter>
        <encoder>
            <pattern>%d{HH:mm:ss.SSS} %-5level - %msg%n</pattern>
        </encoder>
    </appender>

    <logger name="org.example.HelloWarnings" level="DEBUG">
        <appender-ref ref="WARN_FILE" />
    </logger>
```

`ThresholdFilter` na appenderze przepuszcza tylko komunikaty od danego poziomu w górę. Logger produkuje komunikaty `DEBUG+`, ale:
- Do `WARN_FILE` trafiają tylko `WARN` i `ERROR` (filtr na appenderze).
- Do `ALL_FILE` trafiają wszystkie (przez propagację do roota, bez filtra).

Analogicznie dodajmy filtr `INFO` na konsoli `App`, ustawiając logger na `DEBUG`:

```xml
    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <filter class="ch.qos.logback.classic.filter.ThresholdFilter">
            <level>INFO</level>
        </filter>
        <encoder>
            <pattern>%d{HH:mm:ss.SSS} %-5level [%logger{20}] - %msg%n</pattern>
        </encoder>
    </appender>

    <logger name="org.example.App" level="DEBUG">
        <appender-ref ref="CONSOLE" />
    </logger>
```

**Finalna konfiguracja:**

```xml
<configuration>
    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <filter class="ch.qos.logback.classic.filter.ThresholdFilter">
            <level>INFO</level>
        </filter>
        <encoder>
            <pattern>%d{HH:mm:ss.SSS} %-5level [%logger{20}] - %msg%n</pattern>
        </encoder>
    </appender>

    <appender name="ALL_FILE" class="ch.qos.logback.core.FileAppender">
        <file>target/log.txt</file>
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} %-5level [%logger{36}] - %msg%n</pattern>
        </encoder>
    </appender>

    <appender name="DEBUG_FILE" class="ch.qos.logback.core.FileAppender">
        <file>target/hello-debug.log</file>
        <encoder>
            <pattern>%d{HH:mm:ss.SSS} %-5level - %msg%n</pattern>
        </encoder>
    </appender>

    <appender name="WARN_FILE" class="ch.qos.logback.core.FileAppender">
        <file>target/hello-warnings.log</file>
        <filter class="ch.qos.logback.classic.filter.ThresholdFilter">
            <level>WARN</level>
        </filter>
        <encoder>
            <pattern>%d{HH:mm:ss.SSS} %-5level - %msg%n</pattern>
        </encoder>
    </appender>

    <!-- Loggers: produce events from DEBUG, appenders filter what they want -->
    <logger name="org.example.App" level="DEBUG">
        <appender-ref ref="CONSOLE" />
    </logger>

    <logger name="org.example.HelloDebug" level="DEBUG">
        <appender-ref ref="DEBUG_FILE" />
    </logger>

    <logger name="org.example.HelloWarnings" level="DEBUG">
        <appender-ref ref="WARN_FILE" />
    </logger>

    <!-- Root: all DEBUG+ to log.txt, no console -->
    <root level="DEBUG">
        <appender-ref ref="ALL_FILE" />
    </root>
</configuration>
```

Uruchom program i zweryfikuj wynik:

| Plik / Wyjście | Klasy | Poziomy |
|---|---|---|
| Konsola | `App` | INFO, WARN, ERROR |
| `target/hello-debug.log` | `HelloDebug` | DEBUG, INFO, WARN, ERROR |
| `target/hello-warnings.log` | `HelloWarnings` | WARN, ERROR |
| `target/log.txt` | Wszystkie | DEBUG, INFO, WARN, ERROR |

**Ćwiczenie:** Zmień poziom roota na `TRACE` i uruchom ponownie. Co się zmieniło w `log.txt`? Jak zmienić konfigurację, żeby `TRACE` trafiał do `log.txt`, ale nie do pozostałych plików?

### 1.7 Logowanie wyjątków

Wyjątek przekazujemy jako ostatni argument — SLF4J automatycznie wypisze stack trace:

```java
try {
    riskyOperation();
} catch (Exception e) {
    log.error("Operation failed", e);
}
```

To nie używa `{}` — wyjątek jest rozpoznawany automatycznie jako ostatni argument.

---

## Część 2 (8:40–9:05): Adnotacje

Kontynuujemy w tym samym projekcie demonstracyjnym.

### 2.1 Czym są adnotacje?

Adnotacje to metadane dołączone do kodu — nie zmieniają jego działania bezpośrednio, ale dostarczają informacji, które mogą być odczytane przez kompilator, narzędzia lub sam program w runtime.

Już znacie: `@Test`, `@Override`, `@BeforeEach`, `@FunctionalInterface`. Do końca semestru poznacie adnotacje JPA do baz danych, Lomboka do eliminowania boilerplate'u. To wszystko jest częścią tego samego mechanizmu — kontrakt naszego API wykracza poza sygnaturę metody.

Dokumentacja: [Oracle — Annotations](https://docs.oracle.com/javase/tutorial/java/annotations/)

### 2.2 Tworzenie adnotacji i meta-adnotacje

Adnotację definiuje się słowem kluczowym `@interface`. **Meta-adnotacje** określają jej zachowanie:

**`@Retention`** — jak długo adnotacja jest zachowana:

| Wartość | Opis |
|---|---|
| `SOURCE` | Tylko w kodzie źródłowym — kompilator ją usuwa |
| `CLASS` | W pliku `.class`, ale niedostępna w runtime |
| `RUNTIME` | Dostępna w runtime przez refleksję |

**`@Target`** — co można nią oznaczyć:

| Wartość | Opis |
|---|---|
| `TYPE` | Klasa, interfejs, enum, record |
| `METHOD` | Metoda |
| `FIELD` | Pole (w tym stałe enum) |
| `PARAMETER` | Parametr metody |

### 2.3 Pierwsza adnotacja: @Loggable

Zacznijmy od najprostszego przypadku — adnotacja bez parametrów, umieszczana na klasie:

```java
package org.example;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@interface Loggable {
}
```

Utwórz prostą klasę z tą adnotacją:

```java
package org.example;

@Loggable
class User {
    String name;
    String email;

    User(String name, String email) {
        this.name = name;
        this.email = email;
    }

    @Override
    public String toString() { return "User[" + name + "]"; }
}
```

Teraz sprawdźmy, jak odczytać adnotację w runtime. Dodaj do `App.main()`:

```java
log.info("=== Annotation check ===");

Object user = new User("Anna", "anna@example.com");
Object plain = new Object();

log.info("User has @Loggable: {}",
        user.getClass().isAnnotationPresent(Loggable.class));
log.info("Object has @Loggable: {}",
        plain.getClass().isAnnotationPresent(Loggable.class));
log.info("HelloDebug has @Loggable: {}",
        HelloDebug.class.isAnnotationPresent(Loggable.class));
```

Uruchom program. Wynik:
```
User has @Loggable: true
Object has @Loggable: false
HelloDebug has @Loggable: false
```

To jest cała "magia" adnotacji — sprawdzamy w runtime, czy klasa ma daną adnotację, i podejmujemy decyzje na tej podstawie. `java.lang.Object` i `HelloDebug` nie mają `@Loggable`, więc `isAnnotationPresent` zwraca `false`.

### 2.4 Adnotacja na polach z parametrem: @Detail

Adnotacje mogą mieć **parametry** — wartości podawane przy użyciu adnotacji. Stwórzmy adnotację do oznaczania pól, które zawierają istotne szczegóły:

```java
package org.example;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@interface Detail {
    String value() default "";
}
```

- `@Target(FIELD)` — adnotację umieszczamy na polach.
- `String value() default ""` — parametr opcjonalny (ma wartość domyślną).

Dodaj adnotacje do klasy `User`:

```java
@Loggable
class User {
    @Detail("given name") String name;
    @Detail String email;
    int loginCount;

    User(String name, String email, int loginCount) {
        this.name = name;
        this.email = email;
        this.loginCount = loginCount;
    }

    @Override
    public String toString() { return "User[" + name + "]"; }
}
```

Zwróć uwagę:
- `@Detail("given name")` — podaje etykietę jako parametr `value`.
- `@Detail` — używa wartości domyślnej (pusty string).
- `loginCount` — nie ma adnotacji, nie jest "detalem".

Sprawdźmy, jak odczytać adnotacje z pól. Dodaj do `App.main()`:

```java
log.info("=== Field annotations ===");

for (java.lang.reflect.Field field : User.class.getDeclaredFields()) {
    if (field.isAnnotationPresent(Detail.class)) {
        Detail detail = field.getAnnotation(Detail.class);
        String label = detail.value().isEmpty() ? field.getName() : detail.value();
        log.info("  Detail field: {} (label: {})", field.getName(), label);
    } else {
        log.info("  Regular field: {}", field.getName());
    }
}
```

Wynik:
```
  Detail field: name (label: given name)
  Detail field: email (label: email)
  Regular field: loginCount
```

`field.getAnnotation(Detail.class)` zwraca instancję adnotacji, z której możemy odczytać parametr `.value()`. Jeśli parametr jest pusty, używamy nazwy pola — to typowy wzorzec "convention over configuration".

### 2.5 Przykładowe klasy z adnotacjami

Mając `@Loggable` i `@Detail`, stwórzmy kilka klas o różnej konfiguracji adnotacji. Skopiuj poniższy kod i wklej do pakietu `org.example`. Plik zawiera kilka klas — możesz zostawić je w jednym pliku albo szybko wyodrębnić do osobnych za pomocą `F6` lub przeciągając z drzewa plików.

**InventoryItems.java:**

```java
package org.example;

class Computer {
    @Detail String hostname;
    @Detail("IP address") String ip;

    Computer(String hostname, String ip) {
        this.hostname = hostname;
        this.ip = ip;
    }

    @Override
    public String toString() { return "Computer[" + hostname + "]"; }
}

@Loggable
class SystemEvent {
    @Detail String type;
    @Detail("event message") String message;

    SystemEvent(String type, String message) {
        this.type = type;
        this.message = message;
    }

    @Override
    public String toString() { return "SystemEvent[" + type + "]"; }
}

class LocalFile {
    String path;
    long size;

    LocalFile(String path, long size) {
        this.path = path;
        this.size = size;
    }

    @Override
    public String toString() { return "LocalFile[" + path + "]"; }
}

@Loggable
class Notification {
    String text;

    Notification(String text) {
        this.text = text;
    }

    @Override
    public String toString() { return "Notification[" + text + "]"; }
}
```

Razem z `User` mamy pięć klas o zróżnicowanej konfiguracji:

| Klasa | `@Loggable`? | Pola `@Detail`? |
|---|---|---|
| `User` | tak | tak (2 pola) |
| `Computer` | nie | tak (2 pola) |
| `SystemEvent` | tak | tak (2 pola) |
| `LocalFile` | nie | nie |
| `Notification` | tak | nie |

### 2.6 Procesor adnotacji z logowaniem

Teraz połączmy adnotacje z logowaniem. Klasa `InventoryLogger` przegląda listę obiektów i loguje je na poziomie odpowiednim do adnotacji:

- `TRACE` — wspomina o każdym obiekcie (widoczne tylko przy najniższym poziomie).
- `DEBUG` — obiekty bez `@Loggable` (lub `@Loggable` bez `@Detail` — używa `toString()`).
- `INFO` — obiekty `@Loggable` z polami `@Detail` (wypisuje detale).

**InventoryLogger.java:**

```java
package org.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class InventoryLogger {
    private static final Logger log = LoggerFactory.getLogger(InventoryLogger.class);

    public static void logInventory(List<Object> inventory) {
        for (Object item : inventory) {
            Class<?> clazz = item.getClass();
            boolean isLoggable = clazz.isAnnotationPresent(Loggable.class);

            log.trace("Processing item: {} (loggable={})", item, isLoggable);

            if (!isLoggable) {
                log.debug("[{}] {}", clazz.getSimpleName(), item);
                continue;
            }

            // Collect @Detail fields
            List<String> details = new ArrayList<>();
            for (Field field : clazz.getDeclaredFields()) {
                if (field.isAnnotationPresent(Detail.class)) {
                    Detail detail = field.getAnnotation(Detail.class);
                    String label = detail.value().isEmpty()
                            ? field.getName()
                            : detail.value();
                    field.setAccessible(true);
                    try {
                        Object value = field.get(item);
                        details.add(label + "=" + value);
                    } catch (IllegalAccessException e) {
                        details.add(label + "=<inaccessible>");
                    }
                }
            }

            if (details.isEmpty()) {
                log.debug("[{}] {}", clazz.getSimpleName(), item);
            } else {
                log.info("[{}] {}", clazz.getSimpleName(), String.join(", ", details));
            }
        }
    }
}
```

Kluczowe elementy refleksji:
- `clazz.isAnnotationPresent(Loggable.class)` — sprawdza adnotację na klasie.
- `clazz.getDeclaredFields()` — zwraca wszystkie pola klasy (w tym prywatne).
- `field.setAccessible(true)` — pozwala odczytać wartość pola prywatnego.
- `field.get(item)` — odczytuje wartość pola na konkretnym obiekcie.

Dokumentacja: [Oracle — Reflection API](https://docs.oracle.com/javase/tutorial/reflect/)

### 2.7 Uruchomienie

Zaktualizuj `App.main()` — zastąp wcześniejsze testy adnotacji pełnym przykładem:

```java
public static void main(String[] args) {
    log.info("=== Logging demo ===");

    new HelloDebug().run();
    new HelloWarnings().run();

    log.info("=== Annotation demo ===");

    List<Object> inventory = List.of(
            new User("Anna", "anna@example.com", 42),
            new Computer("srv-01", "192.168.1.100"),
            new SystemEvent("LOGIN", "User Anna logged in"),
            new LocalFile("/tmp/data.csv", 1024),
            new Notification("System update available")
    );

    InventoryLogger.logInventory(inventory);

    log.info("=== Done ===");
}
```

Uruchom i sprawdź wynik na konsoli. Powinny być widoczne:
- `INFO` — `User` i `SystemEvent` z detalami pól.
- Na konsoli nie widać `DEBUG` (filtr `INFO`), ale w `target/log.txt` powinny być komunikaty `DEBUG` z `Computer`, `LocalFile` i `Notification`.

Zmień poziom roota na `TRACE` i sprawdź `log.txt` — zobaczysz komunikaty `TRACE` o przetwarzaniu każdego obiektu.

> **Git:** Commit, np. *"Logging and annotation demo project"*.

## Część 3 (9:05–9:30): Zastosowanie w pizzerii

Wróć do projektu pizzerii (branch `lab4-start` lub twój kod z Lab 3).

### 3.1 Dodanie logowania — szybki refactoring

**Krok 1 — dodaj zależności SLF4J i Logback** do `pom.xml` projektu pizzerii (tak samo jak w projekcie demo).

**Krok 2 — utwórz `src/main/resources/logback.xml`:**

```xml
<configuration>
    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%d{HH:mm:ss.SSS} %-5level [%logger{20}] - %msg%n</pattern>
        </encoder>
    </appender>

    <logger name="org.example" level="DEBUG" />

    <root level="INFO">
        <appender-ref ref="CONSOLE" />
    </root>
</configuration>
```

**Krok 3 — utwórz `src/test/resources/logback-test.xml`:**

Logback automatycznie używa `logback-test.xml` zamiast `logback.xml` podczas testów (gdy oba są w classpath):

```xml
<configuration>
    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <filter class="ch.qos.logback.classic.filter.ThresholdFilter">
            <level>WARN</level>
        </filter>
        <encoder>
            <pattern>%-5level [%logger{20}] - %msg%n</pattern>
        </encoder>
    </appender>

    <appender name="FILE" class="ch.qos.logback.core.FileAppender">
        <file>target/test-trace.log</file>
        <encoder>
            <pattern>%d{HH:mm:ss.SSS} %-5level [%thread] %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>

    <root level="WARN">
        <appender-ref ref="CONSOLE" />
    </root>

    <logger name="org.example" level="TRACE" additivity="true">
        <appender-ref ref="FILE" />
    </logger>
</configuration>
```

Dzięki temu:
- Konsola testów: tylko `WARN` i `ERROR` (czytelne wyjście).
- Plik `target/test-trace.log`: pełne `TRACE` (do diagnostyki).

**Krok 4 — zamień `System.out.println` na logowanie.**

To powinno pójść szybko:
1. Użyj `Ctrl+Shift+F`, wyszukaj `System.out.println` w całym projekcie.
2. Kliknij *"Open in Find tool window"* — zobaczysz listę wszystkich wystąpień.
3. Przejdź po kolei przez wyniki. Na każdym `System.out.println`:
   - Naciśnij `Alt+Enter` — IntelliJ powinien zaproponować *"Replace with logger call"*.
   - Jeśli klasa nie ma jeszcze pola loggera, IDE je doda automatycznie.
   - Jeśli IDE zaproponuje konkatenację stringów w logu, naciśnij `Alt+Enter` ponownie — powinno zaproponować zamianę na logowanie parametryzowane.
4. Dobierz odpowiedni poziom logowania do każdego komunikatu.

**Krok 5 — dodaj logowanie do kluczowych miejsc.**

Sugerowane logi (jeśli nie ma ich jeszcze po zamianie `println`):

| Klasa | Zdarzenie | Poziom |
|---|---|---|
| `PizzaShop` | Start/koniec symulacji | `INFO` |
| `PizzaShop` | Każdy tick | `TRACE` |
| `PizzaShop` | Spalona pizza (catch) | `ERROR` (z wyjątkiem) |
| `PizzaShopCook` | Podjęcie zamówienia | `DEBUG` |
| `PizzaShopCook` | Dodanie składnika | `TRACE` |
| `PizzaShopCook` | Brak wolnego pieca | `WARN` |
| `PizzaShopHelper` | Wyjęcie pizzy z pieca | `DEBUG` |
| `PizzaShopHelper` | Zapakowanie pizzy | `INFO` |
| `PizzaShopHelper` | Niekompletna pizza | `WARN` |
| `PizzaOven` | Pieczenie — ticki | `TRACE` |
| `PizzaOven` | Pizza upieczona | `DEBUG` |
| `PizzaOven` | Pizza czeka — burnTimer | `WARN` |
| `PizzaOven` | Pizza spalona | `ERROR` |

Uruchom `PizzaApp.main()` i sprawdź logi. Uruchom testy — konsola powinna być czysta, `target/test-trace.log` powinien zawierać szczegóły.

> **Git:** Commit, np. *"Add SLF4J + Logback to pizza project, replace all println"*.

### 3.2 Adnotacja @Vegetarian — podejście TDD

Dodamy adnotację oznaczającą wegetariańskie składniki i kod sprawdzający, czy przepis jest wegetariański. Zaczynamy od testów.

**Krok 1 — napisz testy.**

```java
@Test
public void testVegetarianIngredients() {
    assertTrue(RecipeUtils.isVegetarian(Ingredient.Cheese));
    assertFalse(RecipeUtils.isVegetarian(Ingredient.Peperoni));
    assertFalse(RecipeUtils.isVegetarian(Ingredient.Ham));
    assertTrue(RecipeUtils.isVegetarian(Ingredient.Mushrooms));
    assertTrue(RecipeUtils.isVegetarian(Ingredient.Pineapple));
}

@Test
public void testVegetarianRecipe() {
    // Margherita: Cheese only — vegetarian
    assertTrue(RecipeUtils.isVegetarianRecipe(shop.getMenu().getFirst()));
    // Peperoni: Cheese + Peperoni — not vegetarian
    assertFalse(RecipeUtils.isVegetarianRecipe(shop.getMenu().get(1)));
}
```

**Krok 2 — uruchom testy.** Nie kompilują się — `RecipeUtils`, `isVegetarian`, `@Vegetarian` nie istnieją.

**Krok 3 — utwórz adnotację i implementację.**

**Vegetarian.java:**
```java
package org.example;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@interface Vegetarian {
}
```

`@Target(FIELD)` — adnotację umieszczamy na polach. Stałe enum w Javie to pola — możemy oznaczać poszczególne wartości.

Zmodyfikuj `Ingredient`:

```java
enum Ingredient {
    @Vegetarian Cheese,
    Peperoni,
    Ham,
    @Vegetarian Mushrooms,
    @Vegetarian Pineapple
}
```

**RecipeUtils.java:**
```java
package org.example;

import java.util.List;

class RecipeUtils {

    public static boolean isVegetarian(Ingredient ingredient) {
        try {
            return Ingredient.class.getField(ingredient.name())
                    .isAnnotationPresent(Vegetarian.class);
        } catch (NoSuchFieldException e) {
            return false;
        }
    }

    public static boolean isVegetarianRecipe(PizzaRecipe recipe) {
        return recipe.ingredients().stream()
                .allMatch(RecipeUtils::isVegetarian);
    }

    public static List<PizzaRecipe> getVegetarianRecipes(List<PizzaRecipe> menu) {
        return menu.stream()
                .filter(RecipeUtils::isVegetarianRecipe)
                .toList();
    }
}
```

**Krok 4 — uruchom testy.** Powinny przechodzić.

> **Git:** Commit, np. *"TDD: add @Vegetarian annotation with RecipeUtils"*.

### 3.3 Adnotacja @DefaultSauce — podejście TDD

W kodzie `PizzaShopCook` sos jest hardkodowany jako `"Tomato"`. Użyjemy adnotacji na klasie kucharza, żeby zdefiniować domyślny sos.

**Krok 1 — napisz test.**

```java
@Test
public void testDefaultSauceAnnotation() {
    DefaultSauce annotation = PizzaShopCook.class.getAnnotation(DefaultSauce.class);
    assertNotNull(annotation, "PizzaShopCook should have @DefaultSauce");
    assertEquals("Tomato", annotation.value());
}

@Test
public void testSauceAppliedFromAnnotation() {
    Pizza p = shop.placeOrder(shop.getMenu().getFirst(), PizzaSize.L);
    assertNull(p.getSauce());

    boolean success = shop.simulate(List.of(p), 50);
    assertTrue(success);
    assertEquals("Tomato", p.getSauce());
}
```

**Krok 2 — uruchom testy.** Nie kompilują się — `DefaultSauce` nie istnieje.

**Krok 3 — utwórz adnotację.**

**DefaultSauce.java:**
```java
package org.example;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@interface DefaultSauce {
    String value();
}
```

Oznacz klasę kucharza:

```java
@DefaultSauce("Tomato")
class PizzaShopCook extends BasePizzaWorker {
    // ...
}
```

**Krok 4 — zmodyfikuj kod kucharza.**

W `preparePizza()`, zamień case `Dough`:

```java
case Dough -> {
    DefaultSauce sauceAnnotation = getClass().getAnnotation(DefaultSauce.class);
    if (sauceAnnotation != null) {
        workedPizza.setSauce(sauceAnnotation.value());
    }
    workedPizza.setState(PizzaState.Pie);
}
```

`getClass()` zwraca klasę runtime — nawet jeśli kucharz jest instancją podklasy (np. `BBQCook`), adnotacja będzie odczytana z właściwej klasy.

**Krok 5 — uruchom testy.** Powinny przechodzić.

Bonus — podklasa z innym sosem (nie wymaga zmiany logiki):
```java
@DefaultSauce("BBQ")
class BBQCook extends PizzaShopCook {
    public BBQCook(PizzaShop shop) { super(shop); }
}
```

> **Git:** Commit, np. *"TDD: add @DefaultSauce annotation, remove hardcoded sauce"*.

### 3.4 Zadania domowe (opcjonalne)

**Konkretne ćwiczenia:**

1. **Logowanie w HungryHelper.** Dodaj logowanie: `DEBUG` gdy normalnie obsługuje pizzę, `WARN` gdy nadgryza składnik (z informacją o zjedzonym składniku i przepisie). Uruchom testy i sprawdź `target/test-trace.log`.

2. **Adnotacja @Spicy.** Utwórz adnotację `@Spicy` i oznacz nią `Ingredient.Peperoni`. Dodaj do `RecipeUtils` metodę `isSpicyRecipe()` — zwraca `true` jeśli **jakikolwiek** składnik jest pikantny. Napisz test. Wskazówka: użyj `anyMatch()` zamiast `allMatch()`.

3. **Logowanie statystyk per tick.** Dodaj do `PizzaShop.update()` logowanie na poziomie `TRACE` z podsumowaniem stanów pizz w kolejce. Użyj `Collectors.groupingBy()`:
   ```java
   Map<PizzaState, Long> states = pizzas.stream()
           .collect(Collectors.groupingBy(Pizza::getState, Collectors.counting()));
   log.trace("Queue state: {}", states);
   ```

4. **Adnotacja @WorkerRole.** Utwórz adnotację `@WorkerRole(String value)` i oznacz nią klasy pracowników (`"cook"`, `"helper"`, `"hungry_helper"`). Napisz metodę generującą raport z rolami pracowników przez refleksję.

5. **Rolling file appender.** Zmień appender plikowy na `RollingFileAppender` z rotacją po 1 MB. Dokumentacja: [Logback — RollingFileAppender](https://logback.qos.ch/manual/appenders.html#RollingFileAppender).

**Pomysły na dalszą eksplorację:**
- Przeczytaj o Lombok (`@Getter`, `@Setter`, `@Slf4j`) — w jaki sposób przetwarza adnotacje w czasie kompilacji? Czym się to różni od naszego przetwarzania w runtime?
- Sprawdź, jak Spring Framework używa adnotacji (`@Component`, `@Autowired`, `@RestController`). Czy te adnotacje są przetwarzane w runtime, czy w czasie kompilacji?
- Zastanów się, kiedy konfiguracja powinna być w kodzie (adnotacje), a kiedy w plikach zewnętrznych (`logback.xml`, `application.properties`). Jakie są zalety i wady każdego podejścia?

---

## Rozwiązania i podpowiedzi

> Poniższe rozwiązania są przeznaczone jako pomoc, gdy utkniesz. Spróbuj najpierw rozwiązać zadanie samodzielnie.

---

### Rozwiązanie 3.1: PizzaShop z logowaniem

```java
package org.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

class PizzaShop {
    private static final Logger log = LoggerFactory.getLogger(PizzaShop.class);

    // ... fields unchanged ...

    public PizzaShop(List<PizzaRecipe> menu, int cookCount, int helperCount, int ovenCount) {
        this.menu = List.copyOf(menu);
        for (int i = 0; i < cookCount; i++) workers.add(new PizzaShopCook(this));
        for (int i = 0; i < helperCount; i++) workers.add(new PizzaShopHelper(this));
        for (int i = 0; i < ovenCount; i++) ovens.add(new PizzaOven());
        log.info("PizzaShop created: {} recipes, {} cooks, {} helpers, {} ovens",
                menu.size(), cookCount, helperCount, ovenCount);
    }

    public Pizza placeOrder(PizzaRecipe recipe, PizzaSize size) {
        Pizza p = new Pizza(size, recipe);
        pizzas.add(p);
        log.debug("Order placed: {} {}", recipe.name(), size);
        return p;
    }

    public void update() {
        ticksPassed++;
        log.trace("=== Tick {} ===", ticksPassed);

        for (PizzaShopWorker worker : workers) {
            worker.update();
        }

        for (PizzaOven oven : ovens) {
            try {
                oven.update();
            } catch (PizzaBurnedException e) {
                pizzasBurned++;
                log.error("Pizza burned! Recipe: {}", e.burnedPizza.getRecipe().name(), e);
            }
        }
    }

    public boolean simulate(List<Pizza> orderedPizzas, int maxTicks) {
        log.info("Starting simulation: {} orders, max {} ticks", orderedPizzas.size(), maxTicks);

        for (int tick = 0; tick < maxTicks; tick++) {
            update();
            boolean allDone = orderedPizzas.stream()
                    .allMatch(p -> p.getState() == PizzaState.Boxed);
            if (allDone) {
                log.info("Simulation complete in {} ticks. Prepared: {}, Burned: {}",
                        ticksPassed, pizzasPrepared, pizzasBurned);
                return true;
            }
        }
        log.warn("Simulation did not complete in {} ticks!", maxTicks);
        return false;
    }

    // ... remaining methods unchanged, see Lab 3 solution ...
}
```

---

### Rozwiązanie 3.1: PizzaShopCook z logowaniem i @DefaultSauce

```java
package org.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

@DefaultSauce("Tomato")
class PizzaShopCook extends BasePizzaWorker {
    private static final Logger log = LoggerFactory.getLogger(PizzaShopCook.class);

    public PizzaShopCook(PizzaShop shop) {
        super(shop);
    }

    @Override
    public void update() {
        if (workedPizza != null) {
            preparePizza();
        } else {
            pickUpOrder();
        }
    }

    private void pickUpOrder() {
        shop.getNextOrder().ifPresent(pizza -> {
            pickUp(pizza);
            log.debug("Picked up order: {} {}", pizza.getRecipe().name(), pizza.getSize());
        });
    }

    private void preparePizza() {
        switch (workedPizza.getState()) {
            case Ordered -> {
                workedPizza.setState(PizzaState.Dough);
            }
            case Dough -> {
                DefaultSauce sauceAnnotation = getClass().getAnnotation(DefaultSauce.class);
                if (sauceAnnotation != null) {
                    workedPizza.setSauce(sauceAnnotation.value());
                    log.trace("Applied sauce: {}", sauceAnnotation.value());
                }
                workedPizza.setState(PizzaState.Pie);
            }
            case Pie -> {
                int idx = workedPizza.getIngredientsOnPizza().size();
                if (idx < workedPizza.getRecipe().ingredients().size()) {
                    Ingredient ingredient = workedPizza.getRecipe().ingredients().get(idx);
                    workedPizza.addIngredient(ingredient);
                    log.trace("Added ingredient: {}", ingredient);
                } else {
                    workedPizza.setState(PizzaState.Filled);
                    log.debug("Pizza filled: {}", workedPizza.getRecipe().name());
                }
            }
            case Filled -> {
                Optional<PizzaOven> freeOven = shop.findFreeOven();
                if (freeOven.isPresent()) {
                    log.debug("Loaded pizza into oven: {}", workedPizza.getRecipe().name());
                    freeOven.get().loadPizza(workedPizza);
                    workedPizza = null;
                } else {
                    log.warn("No free oven, returning pizza: {}", workedPizza.getRecipe().name());
                    putBack(workedPizza);
                }
            }
            default -> {
                putBack(workedPizza);
            }
        }
    }
}
```

---

### Rozwiązanie 3.1: PizzaShopHelper z logowaniem

```java
package org.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

class PizzaShopHelper extends BasePizzaWorker {
    private static final Logger log = LoggerFactory.getLogger(PizzaShopHelper.class);

    public PizzaShopHelper(PizzaShop shop) {
        super(shop);
    }

    @Override
    public void update() {
        if (workedPizza != null) {
            handlePizza();
        } else if (!checkOven()) {
            pickUpFilledPizza();
        }
    }

    private boolean checkOven() {
        Optional<PizzaOven> ovenOpt = shop.findOvenWithBakedPizza();
        if (ovenOpt.isPresent()) {
            workedPizza = ovenOpt.get().ejectPizza();
            log.debug("Retrieved baked pizza: {}", workedPizza.getRecipe().name());
            return true;
        }
        return false;
    }

    protected void pickUpFilledPizza() {
        for (Pizza incomplete : shop.getIncompleteFilledPizzas()) {
            log.warn("Incomplete pizza detected, resetting: {}", incomplete.getRecipe().name());
            incomplete.clearIngredients();
            incomplete.setState(PizzaState.Ordered);
        }

        shop.getNextFilledPizza().ifPresent(pizza -> {
            pickUp(pizza);
            log.debug("Picked up filled pizza: {}", pizza.getRecipe().name());
        });
    }

    private void handlePizza() {
        switch (workedPizza.getState()) {
            case Filled -> {
                Optional<PizzaOven> freeOven = shop.findFreeOven();
                if (freeOven.isPresent()) {
                    log.debug("Loading filled pizza into oven: {}", workedPizza.getRecipe().name());
                    freeOven.get().loadPizza(workedPizza);
                    workedPizza = null;
                } else {
                    log.warn("No free oven, returning filled pizza: {}", workedPizza.getRecipe().name());
                    putBack(workedPizza);
                }
            }
            case Baking -> {
                throw new RuntimeException("Hot pizza!");
            }
            case Baked -> {
                log.info("Boxing pizza: {} {}", workedPizza.getRecipe().name(), workedPizza.getSize());
                complete(workedPizza);
            }
            default -> {
                putBack(workedPizza);
            }
        }
    }
}
```

---

### Rozwiązanie 3.1: PizzaOven z logowaniem

```java
package org.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

class PizzaOven {
    private static final Logger log = LoggerFactory.getLogger(PizzaOven.class);

    static final int BAKE_TIME = 5;
    static final int TIME_TO_BURN = 10;

    private Pizza contents = null;
    private int timeLeft = 0;
    private int burnTimer = 0;

    public boolean isEmpty() { return contents == null; }

    public Optional<Pizza> getBakedPizza() {
        if (contents != null && contents.getState() == PizzaState.Baked) {
            return Optional.of(contents);
        }
        return Optional.empty();
    }

    public Pizza ejectPizza() {
        Pizza pizza = contents;
        log.debug("Pizza ejected from oven: {}", pizza.getRecipe().name());
        contents = null;
        timeLeft = 0;
        burnTimer = 0;
        return pizza;
    }

    public void loadPizza(Pizza pizza) {
        contents = pizza;
        contents.setState(PizzaState.Baking);
        timeLeft = BAKE_TIME;
        burnTimer = 0;
        log.debug("Pizza loaded: {}, bake time: {}", pizza.getRecipe().name(), BAKE_TIME);
    }

    void update() throws PizzaBurnedException {
        if (contents == null) {
            return;
        }
        if (contents.getState() == PizzaState.Baking) {
            timeLeft--;
            log.trace("Baking: {} ticks left for {}", timeLeft, contents.getRecipe().name());
            if (timeLeft <= 0) {
                contents.setState(PizzaState.Baked);
                burnTimer = TIME_TO_BURN;
                log.debug("Pizza baked: {}", contents.getRecipe().name());
            }
        } else if (contents.getState() == PizzaState.Baked) {
            burnTimer--;
            log.warn("Baked pizza waiting! {} ticks to burn: {}", burnTimer, contents.getRecipe().name());
            if (burnTimer <= 0) {
                contents.setState(PizzaState.Burned);
                log.error("Pizza burned: {}", contents.getRecipe().name());
                throw new PizzaBurnedException(contents);
            }
        }
    }
}
```

---

### Rozwiązanie 3.2–3.3: Nowe adnotacje i testy

**Vegetarian.java, RecipeUtils.java, DefaultSauce.java** — patrz kod w sekcjach 3.2 i 3.3 instrukcji.

**Zaktualizowany Ingredient.java:**
```java
package org.example;

enum Ingredient {
    @Vegetarian Cheese,
    Peperoni,
    Ham,
    @Vegetarian Mushrooms,
    @Vegetarian Pineapple
}
```

**Nowe testy w AppTest.java** (dodaj do istniejących testów):

```java
@Test
public void testVegetarianIngredients() {
    assertTrue(RecipeUtils.isVegetarian(Ingredient.Cheese));
    assertFalse(RecipeUtils.isVegetarian(Ingredient.Peperoni));
    assertFalse(RecipeUtils.isVegetarian(Ingredient.Ham));
    assertTrue(RecipeUtils.isVegetarian(Ingredient.Mushrooms));
    assertTrue(RecipeUtils.isVegetarian(Ingredient.Pineapple));
}

@Test
public void testVegetarianRecipe() {
    assertTrue(RecipeUtils.isVegetarianRecipe(shop.getMenu().getFirst()));
    assertFalse(RecipeUtils.isVegetarianRecipe(shop.getMenu().get(1)));
}

@Test
public void testDefaultSauceAnnotation() {
    DefaultSauce annotation = PizzaShopCook.class.getAnnotation(DefaultSauce.class);
    assertNotNull(annotation);
    assertEquals("Tomato", annotation.value());
}

@Test
public void testSauceAppliedFromAnnotation() {
    Pizza p = shop.placeOrder(shop.getMenu().getFirst(), PizzaSize.L);
    assertNull(p.getSauce());
    boolean success = shop.simulate(List.of(p), 50);
    assertTrue(success);
    assertEquals("Tomato", p.getSauce());
}
```
