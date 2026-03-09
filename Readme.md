# Laboratorium 3: Strumienie, wyrażenia lambda, Optional — 10.03.2026, 8:00–9:30

## Temat zajęć
Refactoring projektu pizzerii z użyciem strumieni, wyrażeń lambda i typu `Optional`. Enkapsulacja klas, separacja odpowiedzialności, czytelność kodu.

Kontynuujemy pracę z projektem pizzerii z Lab 2. Jeśli nie masz aktualnej wersji projektu, pobierz zaktualizowaną wersję z repozytorium:

> **https://github.com/starcatter/pizzaShop_2026**
>
> Branch: `lab3-start`

Ta wersja zawiera poprawki z Lab 2: naprawiony błąd ze składnikami, naprawioną blokadę pomocnika, dodany sos na pizzy, głodnego pomocnika oraz zmienioną nazwę `PizzaFurnace` na `PizzaOven`.

Jeśli pracujesz na swoim kodzie z Lab 2, upewnij się, że masz te poprawki. W razie wątpliwości — zacznij od brancha `lab3-start`.

---

## Wejściówka 1 (8:00–8:10)

3 pytania jednokrotnego wyboru. Czas: 10-15 minut. Pytania dotyczą materiału z Lab 1 i Lab 2.

## Część 1 (8:10–8:30): Wprowadzenie — lambdy, strumienie, Optional

Ta sekcja to materiał referencyjny — przeczytaj go, żeby zrozumieć pojęcia, których będziemy używać w dalszej części zajęć. Przykłady kodu ilustrują mechanizmy, które za chwilę zastosujemy w projekcie pizzerii.

### 1.1 Wyrażenia lambda

Wyrażenie lambda to skrócony zapis anonimowej implementacji interfejsu z jedną metodą abstrakcyjną (tzw. **interfejs funkcyjny**).

Dokumentacja: [Oracle — Lambda Expressions](https://docs.oracle.com/javase/tutorial/java/javaOO/lambdaexpressions.html)

**Przykład — sortowanie listy.**

Bez lambdy — tworzymy anonimową klasę implementującą `Comparator`:
```java
List<String> names = new ArrayList<>(List.of("Karol", "Anna", "Jan"));
names.sort(new Comparator<String>() {
    @Override
    public int compare(String a, String b) {
        return a.compareTo(b);
    }
});
```

Z lambdą — kompilator wie, że `sort()` oczekuje `Comparator`, więc wystarczy podać ciało metody `compare`:
```java
names.sort((a, b) -> a.compareTo(b));
```

Z referencją do metody — jeszcze krócej, gdy lambda po prostu wywołuje istniejącą metodę:
```java
names.sort(String::compareTo);
```

Wszystkie trzy zapisy robią to samo. Lambda i referencja do metody to skrócone formy — kompilator sam rozpoznaje, że `Comparator<String>` ma jedną metodę `compare`, i dopasowuje lambdę.

**Składnia wyrażeń lambda:**

```java
// Pełna forma — typy parametrów, nawiasy klamrowe, return
(String s) -> { return s.length(); }

// Skrócona — typ parametru wynika z kontekstu
(s) -> { return s.length(); }

// Jeszcze krótsza — jeden parametr, jedno wyrażenie (bez return, bez klamer)
s -> s.length()
```

Reguły skracania:
- Typy parametrów można pominąć, jeśli kompilator potrafi je wydedukować (prawie zawsze).
- Nawiasy wokół jednego parametru można pominąć.
- Jeśli ciało lambdy to jedno wyrażenie, można pominąć `{ }` i `return`.

**Referencje do metod** to jeszcze krótsza forma, gdy lambda po prostu przekazuje argument do istniejącej metody:

| Lambda | Referencja do metody | Typ |
|---|---|---|
| `s -> s.toUpperCase()` | `String::toUpperCase` | Metoda instancji (na argumencie) |
| `s -> System.out.println(s)` | `System.out::println` | Metoda instancji (na obiekcie) |
| `s -> Integer.parseInt(s)` | `Integer::parseInt` | Metoda statyczna |
| `() -> new ArrayList<>()` | `ArrayList::new` | Konstruktor |

**Interfejsy funkcyjne z biblioteki standardowej** (`java.util.function`):

| Interfejs | Metoda | Opis | Przykład |
|---|---|---|---|
| `Predicate<T>` | `boolean test(T t)` | Warunek — zwraca true/false | `s -> s.length() > 5` |
| `Function<T, R>` | `R apply(T t)` | Przekształcenie T → R | `String::length` |
| `Consumer<T>` | `void accept(T t)` | Akcja — nic nie zwraca | `System.out::println` |
| `Supplier<T>` | `T get()` | Dostawca — nic nie przyjmuje | `() -> new Pizza(...)` |

Te interfejsy pojawiają się jako typy parametrów w API strumieni i `Optional`. Gdy widzisz metodę przyjmującą `Predicate<Pizza>`, wiesz, że możesz przekazać lambdę `pizza -> pizza.getState() == PizzaState.Ordered`.

**Przechwytywanie zmiennych.** Lambda może odwoływać się do zmiennych z otaczającego zakresu, ale muszą one być *effectively final* — nie mogą być zmieniane po zadeklarowaniu:

```java
String prefix = "Name: ";    // effectively final — nigdy nie zmieniana
Consumer<String> printer = s -> System.out.println(prefix + s);  // OK

int counter = 0;
Consumer<String> bad = s -> counter++;  // BŁĄD — counter jest zmieniany
```

### 1.2 Strumienie (Stream API)

Strumień to sekwencja operacji na kolekcji danych. Zamiast pisać pętlę z warunkami i akumulatorem, opisujemy *co* chcemy uzyskać, a nie *jak*.

Dokumentacja: [Oracle — Stream API](https://docs.oracle.com/javase/tutorial/collections/streams/)

**Przykład — filtrowanie i transformacja:**

Bez strumieni:
```java
List<String> names = List.of("Anna", "Jan", "Karolina", "Bo");
List<String> longNames = new ArrayList<>();
for (String name : names) {
    if (name.length() > 3) {
        longNames.add(name.toUpperCase());
    }
}
// longNames = ["ANNA", "KAROLINA"]
```

Ze strumieniem:
```java
List<String> longNames = names.stream()
        .filter(name -> name.length() > 3)
        .map(String::toUpperCase)
        .toList();
```

Strumień jest **leniwy** — operacje pośrednie nie wykonują się natychmiast. Dopiero operacja terminalna (tu: `toList()`) uruchamia przetwarzanie. Strumień jest **jednorazowy** — po konsumpcji nie można go użyć ponownie.

**Anatomia potoku strumieniowego:**

```
źródło          operacje pośrednie          operacja terminalna
  ↓                    ↓                           ↓
collection.stream().filter(...).map(...).sorted().toList()
```

**Operacje pośrednie** (zwracają nowy strumień, można je łańcuchować):

| Operacja | Opis | Przykład |
|---|---|---|
| `filter(Predicate)` | Zachowaj elementy spełniające warunek | `.filter(p -> p.getState() == PizzaState.Ordered)` |
| `map(Function)` | Przekształć każdy element | `.map(String::toUpperCase)` |
| `sorted()` | Posortuj (naturalny porządek) | `.sorted()` |
| `sorted(Comparator)` | Posortuj według komparatora | `.sorted(Comparator.comparing(Pizza::getSize))` |
| `distinct()` | Usuń duplikaty | `.distinct()` |
| `limit(n)` | Zachowaj tylko pierwszych n elementów | `.limit(3)` |
| `peek(Consumer)` | Wykonaj akcję na każdym elemencie (debugowanie) | `.peek(System.out::println)` |

**Operacje terminalne** (kończą strumień, zwracają wynik):

| Operacja | Zwracany typ | Opis |
|---|---|---|
| `toList()` | `List<T>` | Zbierz do nowej listy |
| `collect(Collector)` | zależny od kolektora | Zbierz do dowolnej struktury |
| `forEach(Consumer)` | `void` | Wykonaj akcję na każdym elemencie |
| `count()` | `long` | Policz elementy |
| `findFirst()` | `Optional<T>` | Zwróć pierwszy element |
| `anyMatch(Predicate)` | `boolean` | Czy jakikolwiek element spełnia warunek |
| `allMatch(Predicate)` | `boolean` | Czy wszystkie elementy spełniają warunek |
| `noneMatch(Predicate)` | `boolean` | Czy żaden element nie spełnia warunku |
| `reduce(identity, BinaryOperator)` | `T` | Zredukuj do jednej wartości |

**Kilka przykładów:**

```java
List<Integer> numbers = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

// Suma liczb parzystych
int sum = numbers.stream()
        .filter(n -> n % 2 == 0)
        .reduce(0, Integer::sum);
// sum = 30

// Ile liczb jest większych od 5?
long count = numbers.stream().filter(n -> n > 5).count();
// count = 5

// Pierwsze 3 nieparzyste, jako stringi
List<String> result = numbers.stream()
        .filter(n -> n % 2 != 0)
        .limit(3)
        .map(n -> "Number: " + n)
        .toList();
// ["Number: 1", "Number: 3", "Number: 5"]
```

**`findFirst()` zwraca `Optional`** — nie `null`, nie element, lecz kontener, który może być pusty. To naturalny most między strumieniami a typem `Optional`.

### 1.3 Optional

`Optional<T>` to kontener, który może zawierać wartość typu `T` **albo być pusty**. Służy jako bezpieczna alternatywa dla `null` — wymusza na programiście jawną obsługę przypadku braku wartości.

Dokumentacja: [Oracle — Optional](https://docs.oracle.com/javase/8/docs/api/java/util/Optional.html)

**Tworzenie:**

```java
Optional<String> present = Optional.of("hello");        // zawiera "hello"
Optional<String> empty   = Optional.empty();             // pusty
Optional<String> maybe   = Optional.ofNullable(null);    // pusty (bezpieczne)
Optional<String> maybe2  = Optional.ofNullable("hello"); // zawiera "hello"
// Uwaga: Optional.of(null) rzuca NullPointerException!
```

`Optional.of()` gwarantuje, że wartość nie jest `null` — jeśli jest, błąd pojawia się natychmiast, zamiast objawiać się daleko od źródła. `Optional.ofNullable()` akceptuje `null` i zwraca pusty `Optional`.

**Odczyt:**

| Metoda | Gdy present | Gdy empty |
|---|---|---|
| `get()` | Zwraca wartość | Rzuca `NoSuchElementException` |
| `orElse(default)` | Zwraca wartość | Zwraca `default` |
| `orElseThrow()` | Zwraca wartość | Rzuca `NoSuchElementException` |
| `orElseThrow(Supplier)` | Zwraca wartość | Rzuca wskazany wyjątek |
| `isPresent()` | `true` | `false` |
| `isEmpty()` | `false` | `true` |

Unikaj `get()` bez wcześniejszego sprawdzenia `isPresent()` — to neguje cały sens `Optional`. Preferuj `orElse()`, `orElseThrow()` lub `ifPresent()`.

**Łańcuchowanie:**

```java
Optional<String> name = Optional.of("anna");

// map() — przekształca wartość wewnątrz Optional
Optional<String> upper = name.map(String::toUpperCase);  // Optional["ANNA"]

// Na pustym Optional — map() nie wywołuje lambdy
Optional<String> nothing = Optional.<String>empty()
        .map(String::toUpperCase);  // Optional.empty()

// Łańcuch map() — bezpieczne nawigowanie
Optional<String> result = name
        .map(String::toUpperCase)
        .map(s -> "Hello, " + s + "!")
        .filter(s -> s.length() < 20);
// Optional["Hello, ANNA!"]
```

Kluczowa cecha: `map()` i `filter()` na pustym `Optional` zwracają `Optional.empty()` bez wywoływania lambdy. Dzięki temu możemy budować łańcuchy operacji bez sprawdzania `null` na każdym kroku.

**Reagowanie na obecność wartości:**

```java
// ifPresent() — wykonaj akcję tylko gdy wartość istnieje
Optional.of("hello").ifPresent(System.out::println);  // wypisuje "hello"
Optional.empty().ifPresent(System.out::println);       // nic się nie dzieje

// ifPresent() z lambdą — przydatne do ustawiania pól
Optional<Pizza> order = shop.getNextOrder();
order.ifPresent(pizza -> {
    workedPizza = pizza;
    shop.removePizza(pizza);
});
```

**Połączenie ze strumieniami:**

`findFirst()` zwraca `Optional`, więc naturalnie łączy się z dalszym łańcuchowaniem:

```java
// Znajdź pierwszy string dłuższy niż 4 znaki i zamień na wielkie litery
String result = List.of("a", "bb", "ccc", "dddd", "eeeee").stream()
        .filter(s -> s.length() > 4)
        .findFirst()
        .map(String::toUpperCase)
        .orElse("Nothing found");
// "EEEEE"

// To samo na liście bez pasujących elementów
String result2 = List.of("a", "bb").stream()
        .filter(s -> s.length() > 4)
        .findFirst()
        .map(String::toUpperCase)
        .orElse("Nothing found");
// "Nothing found"
```

Ten wzorzec — `stream().filter().findFirst().map().orElse()` — jest bardzo powszechny i za chwilę użyjemy go w projekcie pizzerii.

---

## Część 2 (8:30–9:00): Enkapsulacja projektu

Obecnie w projekcie pizzerii wiele klas ma pola z domyślną widocznością (package-private) — pracownicy bezpośrednio czytają i modyfikują wewnętrzne listy `PizzaShop`. To łamie zasadę **enkapsulacji** — ukrywania szczegółów implementacji za interfejsem klasy.

Dokumentacja: [Oracle — Encapsulation](https://docs.oracle.com/javase/tutorial/java/concepts/object.html)

Dlaczego to jest problem? Bo jeśli zmienimy `ArrayList<Pizza>` na inną strukturę danych (np. bazę danych, kolejkę komunikatów), musimy zmienić kod **w każdym pracowniku**, zamiast w jednym miejscu.

Cel: po refactoringu pracownicy powinni korzystać wyłącznie z metod `PizzaShop` — nie wiedzą i nie dbają o to, jak zamówienia są przechowywane.

### 2.1 Enkapsulacja klasy Pizza

Zacznijmy od klasy `Pizza`. Obecnie jej pola (`size`, `recipe`, `state`, `sauce`, `ingredientsOnPizza`) są package-private — dostępne dla każdej klasy w tym samym pakiecie.

**Krok 1 — zrób pola prywatnymi.**

Zmień widoczność pól:
```java
private final PizzaSize size;
private final PizzaRecipe recipe;
private final List<Ingredient> ingredientsOnPizza = new ArrayList<>();
private PizzaState state;
private String sauce = null;
```

Po tej zmianie **nic się nie skompiluje** — cała reszta kodu odwołuje się bezpośrednio do pól. To normalne — IntelliJ pokaże błędy na czerwono.

**Krok 2 — wygeneruj gettery i settery.**

Ustaw kursor wewnątrz klasy `Pizza` i użyj `Alt+Insert` (lub menu *Code → Generate*). Wybierz **Getter and Setter**. Zaznacz pola, dla których chcesz wygenerować metody.

Zastanów się, które pola powinny mieć setter:
- `size` — rozmiar pizzy nie zmienia się po zamówieniu. Tylko getter. Pole `final`.
- `recipe` — przepis też nie. Tylko getter. Pole `final`.
- `state` — stan się zmienia w trakcie przygotowania. Getter i setter.
- `sauce` — sos jest ustawiany podczas przygotowania. Getter i setter.
- `ingredientsOnPizza` — lista składników wymaga bardziej przemyślanego podejścia (patrz niżej).

**Krok 3 — metody operacji na składnikach.**

Zamiast udostępniać listę składników bezpośrednio, dodaj metody:

```java
public void addIngredient(Ingredient ingredient) {
    ingredientsOnPizza.add(ingredient);
}

public void clearIngredients() {
    ingredientsOnPizza.clear();
}

public Ingredient removeLastIngredient() {
    return ingredientsOnPizza.removeLast();
}

public List<Ingredient> getIngredientsOnPizza() {
    return Collections.unmodifiableList(ingredientsOnPizza);
}
```

Metoda `getIngredientsOnPizza()` zwraca **niemodyfikowalny widok** listy — ktokolwiek ją wywoła, może przeglądać składniki, ale nie może ich dodawać ani usuwać. Próba wywołania `add()` na zwróconym widoku rzuci `UnsupportedOperationException`.

Dokumentacja: [Collections.unmodifiableList](https://docs.oracle.com/javase/8/docs/api/java/util/Collections.html#unmodifiableList-java.util.List-)

**Krok 4 — napraw pozostałe klasy.**

Teraz przejdź po kolei przez błędy kompilacji i zamień bezpośredni dostęp do pól na wywołania metod. IntelliJ podświetla błędy na czerwono — klikaj na nie i poprawiaj.

Przykłady zamian:
- `workedPizza.state = PizzaState.Dough;` → `workedPizza.setState(PizzaState.Dough);`
- `workedPizza.ingredientsOnPizza.add(...)` → `workedPizza.addIngredient(...)`
- `workedPizza.ingredientsOnPizza.size()` → `workedPizza.getIngredientsOnPizza().size()`
- `workedPizza.recipe.ingredients()` → `workedPizza.getRecipe().ingredients()`

Po poprawieniu wszystkich błędów uruchom testy — powinny przechodzić tak samo jak przed refactoringiem.

> **Git:** Commit, np. *"Refactor: encapsulate Pizza fields"*.

### 2.2 Enkapsulacja klasy PizzaShop

To najważniejszy refactoring na tych zajęciach. Obecnie pracownicy bezpośrednio operują na liście `shop.pizzas` — dodają, usuwają, iterują. Po refactoringu lista powinna być prywatna, a `PizzaShop` powinien udostępniać metody do zarządzania zamówieniami.

**Krok 1 — zrób pola prywatnymi.**

```java
private final List<Pizza> pizzas = new ArrayList<>();
private final List<PizzaRecipe> menu;
private final List<PizzaShopWorker> workers = new ArrayList<>();
private final List<PizzaOven> ovens = new ArrayList<>();

private int ticksPassed = 0;
private int pizzasPrepared = 0;
private int pizzasBurned = 0;
```

**Krok 2 — dodaj metody zarządzania zamówieniami.**

Pracownicy potrzebują następujących operacji:

```java
public void removePizza(Pizza pizza) {
    pizzas.remove(pizza);
}

public void returnPizza(Pizza pizza) {
    pizzas.add(pizza);
}

public void completePizza(Pizza pizza) {
    pizza.setState(PizzaState.Boxed);
    pizzas.add(pizza);
    pizzasPrepared++;
}

public void addWorker(PizzaShopWorker worker) {
    workers.add(worker);
}

// tymczasowo dla PizzaShopHelper
// w części 3 zastąpimy to bardziej specyficznymi metodami
public List<Pizza> getPizzas() {
    return Collections.unmodifiableList(pizzas);
}

```

Zwróć uwagę na `completePizza()` — centralizuje logikę zapakowania pizzy. Zamiast rozrzucać `pizza.setState(Boxed)` i `pizzasPrepared++` po różnych pracownikach, mamy jedno miejsce, które to obsługuje.

**Krok 3 — napraw pozostałe klasy.**

Przejdź przez błędy kompilacji. Typowe zamiany:
- `shop.pizzas.add(pizza)` → `shop.returnPizza(pizza)` lub `shop.completePizza(pizza)`
- `shop.pizzas.remove(pizza)` → `shop.removePizza(pizza)`
- `shop.ovens` → wymaga dodania metody dostępowej (na razie getter, w następnej części zamienimy na metody strumieniowe)

Dla pieców na razie dodaj prosty getter:
```java
public List<PizzaOven> getOvens() {
    return Collections.unmodifiableList(ovens);
}
```

**Krok 4 — zaktualizuj testy.**

Testy z Lab 2 odwołują się bezpośrednio do pól — po enkapsulacji przestaną się kompilować. Typowe zamiany w `AppTest.java`:
- `shop.ovens.getFirst()` → `shop.getOvens().getFirst()`
- `shop.workers.get(0)` → wymaga dodania metody dostępowej lub przebudowy testu
- `helper.workedPizza = p;` → `workedPizza` jest `protected`, więc z poziomu tego samego pakietu nadal będzie dostępne. Jeśli zmienisz pakiet testów — rozważ dodanie metody testowej.

Po poprawieniu uruchom testy.

> **Git:** Commit, np. *"Refactor: encapsulate PizzaShop fields"*.

### 2.3 Enkapsulacja klasy PizzaOven

Pole `contents` klasy `PizzaOven` jest bezpośrednio odczytywane i modyfikowane przez pracowników (np. `oven.contents = null`, `oven.contents.getState() == PizzaState.Baked`). Dodaj metody:

```java
public boolean isEmpty() {
    return contents == null;
}

public Optional<Pizza> getBakedPizza() {
    if (contents != null && contents.getState() == PizzaState.Baked) {
        return Optional.of(contents);
    }
    return Optional.empty();
}

public Pizza ejectPizza() {
    Pizza pizza = contents;
    contents = null;
    timeLeft = 0;
    burnTimer = 0;
    return pizza;
}
```

Zwróć uwagę na `getBakedPizza()` — zwraca `Optional<Pizza>`. Jeśli w piecu nie ma pizzy, albo pizza nie jest jeszcze upieczona, zwraca `Optional.empty()`. Wywołujący nie musi sprawdzać `null` ani stanu pizzy — `Optional` wymusza to za niego.

Metoda `ejectPizza()` wyjmuje pizzę z pieca i resetuje timery. Po wywołaniu `isEmpty()` zwróci `true`.

Zrób pole `contents` prywatnym i napraw błędy kompilacji, korzystając z nowych metod.

> **Git:** Commit, np. *"Refactor: encapsulate PizzaOven fields"*.

### 2.4 Zadania domowe — enkapsulacja (opcjonalne)

**Konkretne ćwiczenia:**

1. **Niemutowalna lista składników.** Metoda `getIngredientsOnPizza()` zwraca `Collections.unmodifiableList()` — to widok, który rzuca wyjątek przy próbie modyfikacji. Alternatywa: `List.copyOf()` — zwraca kopię listy. Czym się różnią? Kiedy które podejście jest lepsze? Przetestuj oba i sprawdź, co się stanie, gdy zmodyfikujesz oryginalną listę po utworzeniu widoku / kopii.

2. **Metody domenowe zamiast settera.** Zamiast ogólnego `setState()`, dodaj metody opisujące przejścia stanów, np. `markAsFilled()`, `startBaking()`. Każda metoda może walidować, czy przejście jest dozwolone (np. nie można przejść z `Ordered` do `Baked` bez etapów pośrednich). Jak wpłynie to na kod pracowników?

3. **Kolejka zamiast listy.** Zastanów się, co by się stało, gdyby `PizzaShop.pizzas` było typu `Queue<Pizza>` zamiast `List<Pizza>`. Które metody `PizzaShop` musiałyby się zmienić? Czy pracownicy zauważyliby zmianę? (Jeśli enkapsulacja jest dobrze zrobiona — nie powinni.)

**Pomysły na dalszą eksplorację:**
- Przejrzyj cały projekt i policz miejsca, w których jeden obiekt modyfikuje wewnętrzny stan innego obiektu bezpośrednio. Ile takich miejsc zostało po refactoringu?
- Zastanów się nad wzorcem **Immutable Object** — czy klasa `Pizza` mogłaby być niemutowalna? Jakie konsekwencje miałoby to dla symulacji?

---

## Część 3 (9:00–9:30): Strumienie i Optional w projekcie

Po enkapsulacji `PizzaShop` udostępnia metody, które zwracają dane o stanie pizzerii. Teraz wykorzystamy strumienie i `Optional`, żeby te metody były czytelne, zwięzłe i odporne na błędy.

### 3.1 Metody pomocnicze w PizzaShop — strumienie

Pracownicy pizzerii nie powinni przeszukiwać listy zamówień ręcznie. Zamiast tego `PizzaShop` powinien udostępniać metody odpowiadające na pytania biznesowe: "czy jest jakieś nowe zamówienie?", "czy jest wolny piec?".

**Ćwiczenie: metoda `getNextOrder()`.**

Dodaj do klasy `PizzaShop` metodę, która zwraca pierwsze zamówienie w stanie `Ordered`:

```java
public Optional<Pizza> getNextOrder() {
    return pizzas.stream()
            .filter(pizza -> pizza.getState() == PizzaState.Ordered)
            .findFirst();
}
```

Zwróć uwagę na kilka elementów:
- `pizzas.stream()` tworzy strumień z listy zamówień.
- `.filter(pizza -> ...)` to operacja pośrednia — filtruje elementy według warunku podanego jako wyrażenie lambda.
- `.findFirst()` to operacja terminalna — zwraca `Optional<Pizza>`. Jeśli żadna pizza nie spełnia warunku, zwraca `Optional.empty()`.

Metoda zwraca `Optional<Pizza>`, a nie `Pizza` — dzięki temu wywołujący **musi** obsłużyć przypadek, gdy nie ma żadnego zamówienia. Nie ma ryzyka `NullPointerException`.

**Ćwiczenie: metoda `getNextFilledPizza()`.**

Napisz analogiczną metodę zwracającą pierwszą pizzę w stanie `Filled`. Dodaj walidację składników — metoda powinna pomijać pizze, które mają mniej składników niż wymaga przepis (nadgryziona pizza nie jest gotowa do pieca):

```java
public Optional<Pizza> getNextFilledPizza() {
    return pizzas.stream()
            .filter(pizza -> pizza.getState() == PizzaState.Filled)
            .filter(pizza -> pizza.getIngredientsOnPizza().size()
                    >= pizza.getRecipe().ingredients().size())
            .findFirst();
}
```

Zauważ, że dwa wywołania `.filter()` można połączyć operatorem `&&` w jednym lambdzie — ale rozdzielenie ich na dwa etapy jest czytelniejsze. Każdy filtr odpowiada na jedno pytanie: "czy pizza jest gotowa?" i "czy pizza jest kompletna?".

**Uwaga:** ta metoda pomija niekompletne pizze, ale ich nie naprawia. Niekompletne pizze (np. nadgryzione przez głodnego pomocnika) zostają w kolejce w stanie `Filled` na zawsze. Ktoś musi je zresetować — w dalszej części dodamy tę logikę do pomocnika.

**Ćwiczenie: metoda `getIncompleteFilledPizzas()`.**

Napisz metodę zwracającą **listę** niekompletnych pizz w stanie `Filled` (te nadgryzione przez głodnego pomocnika):

```java
public List<Pizza> getIncompleteFilledPizzas() {
    return pizzas.stream()
            .filter(pizza -> pizza.getState() == PizzaState.Filled)
            .filter(pizza -> pizza.getIngredientsOnPizza().size()
                    < pizza.getRecipe().ingredients().size())
            .toList();
}
```

Różnica: zamiast `.findFirst()` (zwraca `Optional` z jednym elementem) używamy `.toList()` (zwraca listę wszystkich pasujących elementów).

### 3.2 Metody pomocnicze — piece

**Ćwiczenie: metoda `findFreeOven()`.**

Dodaj metodę zwracającą pierwszy wolny piec:

```java
public Optional<PizzaOven> findFreeOven() {
    return ovens.stream()
            .filter(PizzaOven::isEmpty)
            .findFirst();
}
```

Tutaj zamiast lambdy `oven -> oven.isEmpty()` użyliśmy **referencji do metody**: `PizzaOven::isEmpty`. To skrócona forma zapisu — kompilator wie, że `isEmpty()` jest metodą bezargumentową zwracającą `boolean`, więc pasuje jako `Predicate<PizzaOven>`.

**Ćwiczenie: metoda `findOvenWithBakedPizza()`.**

Napisz metodę zwracającą pierwszy piec zawierający upieczoną pizzę. Wykorzystaj metodę `getBakedPizza()` dodaną w części 2 (która zwraca `Optional<Pizza>`):

```java
public Optional<PizzaOven> findOvenWithBakedPizza() {
    return ovens.stream()
            .filter(oven -> oven.getBakedPizza().isPresent())
            .findFirst();
}
```

### 3.3 Refactoring pracowników — użycie nowych metod

Teraz, gdy `PizzaShop` udostępnia metody pomocnicze, pracownicy mogą z nich korzystać zamiast ręcznie iterować po listach.

**Ćwiczenie: refactoring `PizzaShopCook.pickUpOrder()`.**

Przed (pętla po liście):
```java
private void pickUpOrder() {
    for (Pizza pizza : shop.getPizzas()) {
        if (pizza.getState() == PizzaState.Ordered) {
            workedPizza = pizza;
            break;
        }
    }
    if (workedPizza != null) {
        shop.removePizza(workedPizza);
    }
}
```

Po (z użyciem `Optional`):
```java
private void pickUpOrder() {
    shop.getNextOrder().ifPresent(pizza -> {
        workedPizza = pizza;
        shop.removePizza(pizza);
    });
}
```

Metoda `ifPresent()` przyjmuje `Consumer<Pizza>` — wyrażenie lambda, które zostanie wykonane **tylko jeśli** `Optional` zawiera wartość. Jeśli nie ma zamówień, nic się nie dzieje.

**Ćwiczenie: refactoring `PizzaShopHelper.checkOven()`.**

Zrefaktoruj metodę `checkOven()` tak, aby korzystała z `findOvenWithBakedPizza()`:

```java
private boolean checkOven() {
    Optional<PizzaOven> ovenOpt = shop.findOvenWithBakedPizza();
    if (ovenOpt.isPresent()) {
        PizzaOven oven = ovenOpt.get();
        workedPizza = oven.ejectPizza();
        return true;
    }
    return false;
}
```

Alternatywnie, z użyciem `map()` na `Optional`:

```java
private boolean checkOven() {
    Optional<Pizza> pizza = shop.findOvenWithBakedPizza()
            .map(PizzaOven::ejectPizza);
    pizza.ifPresent(p -> workedPizza = p);
    return pizza.isPresent();
}
```

Porównaj obie wersje — która jest czytelniejsza? Nie zawsze wersja ze strumieniami jest lepsza. Użyj tej, którą łatwiej zrozumieć na pierwszy rzut oka.

**Ćwiczenie: refactoring szukania wolnego pieca.**

W `PizzaShopCook.preparePizza()` i `PizzaShopHelper.handlePizza()` pętle szukające wolnego pieca można zamienić na:

```java
case Filled -> {
    Optional<PizzaOven> freeOven = shop.findFreeOven();
    if (freeOven.isPresent()) {
        freeOven.get().loadPizza(workedPizza);
        workedPizza = null;
    } else {
        shop.returnPizza(workedPizza);
        workedPizza = null;
    }
}
```

**Ćwiczenie: refactoring `PizzaShopHelper.pickUpFilledPizza()`.**

Zamień pętlę szukającą gotowej pizzy na `shop.getNextFilledPizza()`, a obsługę niekompletnych pizz na `shop.getIncompleteFilledPizzas()`:

```java
protected void pickUpFilledPizza() {
    // Reset incomplete pizzas (e.g. nibbled by HungryHelper)
    for (Pizza incomplete : shop.getIncompleteFilledPizzas()) {
        incomplete.clearIngredients();
        incomplete.setState(PizzaState.Ordered);
    }

    shop.getNextFilledPizza().ifPresent(pizza -> {
        workedPizza = pizza;
        shop.removePizza(pizza);
    });
}
```

Zwróć uwagę: `getIncompleteFilledPizzas()` zwraca nową listę (`.toList()`), więc modyfikowanie stanu pizz w pętli nie powoduje `ConcurrentModificationException` — iterujemy po kopii, a oryginalne pizze w kolejce zmieniają tylko swój stan.

**Ćwiczenie: refactoring `PizzaShopHelper.handlePizza()` — przypadek `Baked`.**

Zamiast ręcznie ustawiać stan i aktualizować statystyki, użyj `shop.completePizza()`:

```java
case Baked -> {
    shop.completePizza(workedPizza);
    workedPizza = null;
}
```

Po zakończeniu refactoringu uruchom wszystkie testy — powinny przechodzić tak samo jak przed zmianami. Upewnij się, że żadna pętla po `shop.getPizzas()` ani `shop.getOvens()` nie pozostała w kodzie pracowników.

### 3.4 Metody pickup/return w bazowej klasie pracownika

Spójrz na kod pracowników po refactoringu z sekcji 3.3. Zauważysz powtarzający się wzorzec:

```java
// Podjęcie pizzy — pojawia się w pickUpOrder(), pickUpFilledPizza()
workedPizza = pizza;
shop.removePizza(pizza);

// Odłożenie pizzy — pojawia się w preparePizza(), handlePizza()
shop.returnPizza(workedPizza);
workedPizza = null;

// Zapakowanie pizzy — pojawia się w handlePizza()
shop.completePizza(workedPizza);
workedPizza = null;
```

Te trzy operacje powtarzają się w każdym pracowniku. Warto wydzielić je do wspólnej klasy bazowej — eliminujemy duplikację kodu i tworzymy naturalne punkty synchronizacji, które będą istotne na Lab 5 (wielowątkowość).

**Ćwiczenie: stwórz abstrakcyjną klasę bazową.**

```java
abstract class BasePizzaWorker implements PizzaShopWorker {
    protected final PizzaShop shop;
    protected Pizza workedPizza = null;

    protected BasePizzaWorker(PizzaShop shop) {
        this.shop = shop;
    }

    protected void pickUp(Pizza pizza) {
        workedPizza = pizza;
        shop.removePizza(pizza);
    }

    protected void putBack(Pizza pizza) {
        shop.returnPizza(pizza);
        workedPizza = null;
    }

    protected void complete(Pizza pizza) {
        shop.completePizza(pizza);
        workedPizza = null;
    }
}
```

Zwróć uwagę na `protected` — te metody są dostępne dla podklas, ale nie dla kodu zewnętrznego. Pole `shop` jest `protected final` — podklasy mają do niego dostęp, ale nie mogą go zmienić.

**Refactoring:** Zamień `PizzaShopCook` i `PizzaShopHelper` tak, żeby dziedziczyły po `BasePizzaWorker` zamiast bezpośrednio implementować `PizzaShopWorker`. Usuń zduplikowane pola `shop` i `workedPizza` z obu klas — są teraz dziedziczone.

Następnie zamień powtarzający się kod na wywołania metod bazowych:

- `workedPizza = pizza; shop.removePizza(pizza);` → `pickUp(pizza)`
- `shop.returnPizza(workedPizza); workedPizza = null;` → `putBack(workedPizza)`
- `shop.completePizza(workedPizza); workedPizza = null;` → `complete(workedPizza)`

To upraszcza np. `pickUpOrder()` do jednej linii:
```java
private void pickUpOrder() {
    shop.getNextOrder().ifPresent(this::pickUp);
}
```

Referencja `this::pickUp` jest równoważna lambdzie `pizza -> this.pickUp(pizza)`.

Upewnij się, że `HungryHelper` również dziedziczy poprawnie — przez `PizzaShopHelper`, który teraz dziedziczy po `BasePizzaWorker`.

Po refactoringu uruchom testy.

> **Git:** Commit, np. *"Refactor: extract BasePizzaWorker, use shop helper methods"*.


### 3.5 Strumienie w metodzie `simulate()`

Pętla sprawdzająca, czy wszystkie pizze są gotowe, jest dobrym kandydatem do zamiany na strumień:

Przed:
```java
boolean allDone = true;
for (Pizza pizza : orderedPizzas) {
    if (pizza.getState() != PizzaState.Boxed) {
        allDone = false;
        break;
    }
}
```

Po:
```java
boolean allDone = orderedPizzas.stream()
        .allMatch(pizza -> pizza.getState() == PizzaState.Boxed);
```

Metoda `allMatch()` zwraca `true` tylko wtedy, gdy **wszystkie** elementy strumienia spełniają warunek. Dla pustej listy zwraca `true` — zastanów się, czy to jest poprawne zachowanie w tym przypadku.

**Ćwiczenie:** Zastanów się, jak użyć `anyMatch()` i `noneMatch()` do wyrażenia tego samego warunku. Która wersja jest najbardziej czytelna?

### 3.6 Podsumowanie statystyk ze strumieniami

**Ćwiczenie:** Dodaj do `PizzaShop` metodę generującą podsumowanie tekstowe:

```java
public String getStatsSummary() {
    long ordered = pizzas.stream()
            .filter(p -> p.getState() == PizzaState.Ordered)
            .count();
    long baking = ovens.stream()
            .filter(oven -> !oven.isEmpty())
            .count();

    return String.join("\n",
            "=== Pizza Shop Stats ===",
            "Ticks: " + ticksPassed,
            "Prepared: " + pizzasPrepared,
            "Burned: " + pizzasBurned,
            "In queue (ordered): " + ordered,
            "Ovens in use: " + baking + "/" + ovens.size()
    );
}
```

Użyj tej metody w `PizzaApp.main()` zamiast ręcznego wypisywania statystyk.

### 3.7 Zadania domowe — strumienie i Optional (opcjonalne)

**Konkretne ćwiczenia:**

1. **Refactoring `HungryHelper`.** Zrefaktoruj `pickUpFilledPizza()` w `HungryHelper` tak, żeby korzystał z `shop.getNextFilledPizza()` zamiast iterować po liście ręcznie. Zastanów się, jak obsłużyć logikę "co trzecia pizza" z użyciem `Optional.ifPresent()`.

2. **Statystyki per przepis.** Dodaj metodę `Map<String, Long> getPizzasPerRecipe()`, która grupuje zapakowane pizze według nazwy przepisu. Użyj `Collectors.groupingBy()` i `Collectors.counting()`:
   ```java
   pizzas.stream()
       .filter(p -> p.getState() == PizzaState.Boxed)
       .collect(Collectors.groupingBy(
           p -> p.getRecipe().name(),
           Collectors.counting()));
   ```

3. **Walidacja menu.** Napisz metodę `boolean isMenuValid()`, która sprawdza, czy wszystkie przepisy w menu mają niepustą listę składników i niepustą nazwę. Użyj `allMatch()`.

4. **Łańcuch Optional.** Napisz metodę `Optional<String> getNextOrderName()`, która zwraca nazwę przepisu pierwszego zamówienia w kolejce. Użyj `Optional.map()`:
   ```java
   public Optional<String> getNextOrderName() {
       return getNextOrder()
               .map(Pizza::getRecipe)
               .map(PizzaRecipe::name);
   }
   ```
   Co zwróci ta metoda, gdy nie ma żadnych zamówień?

**Pomysły na dalszą eksplorację:**
- Zamień `PizzaShop.update()` tak, żeby aktualizacja pieców używała `ovens.forEach(oven -> ...)`. Czy to lepsze niż pętla `for-each`? Kiedy `forEach` na strumieniu jest uzasadnione, a kiedy jest nadużyciem?
- Spróbuj użyć `Stream.peek()` do dodania logowania w łańcuchu strumieni — np. loguj każdą pizzę, która przechodzi przez filtr. Uwaga: `peek()` jest przeznaczone głównie do debugowania, nie do logiki biznesowej.
- Przeczytaj o `flatMap()` na `Optional` i `Stream`. Kiedy jest potrzebne? Wskazówka: gdy masz `Optional<Optional<T>>` lub `Stream<Stream<T>>`.

---

## Rozwiązania i podpowiedzi

> Poniższe rozwiązania są przeznaczone jako pomoc, gdy utkniesz. Spróbuj najpierw rozwiązać zadanie samodzielnie — samo przekopiowanie kodu nie daje okazji do nauki.

---

### Rozwiązanie 2.1: Enkapsulacja klasy Pizza

**Podpowiedzi:**
- Pola, które nigdy nie powinny się zmieniać po utworzeniu obiektu: `size`, `recipe`. Te powinny być `private final`.
- Pole `state` musi się zmieniać — potrzebuje gettera i settera.
- Lista `ingredientsOnPizza` potrzebuje metod `addIngredient()` i `getIngredientsOnPizza()`. Getter powinien zwracać niemodyfikowalny widok.

**Rozwiązanie:**

```java
class Pizza {
    private final PizzaSize size;
    private final PizzaRecipe recipe;
    private final List<Ingredient> ingredientsOnPizza = new ArrayList<>();
    private PizzaState state;
    private String sauce = null;

    public Pizza(PizzaSize size, PizzaRecipe recipe) {
        this.size = size;
        this.recipe = recipe;
        this.state = PizzaState.Ordered;
    }

    public PizzaSize getSize() { return size; }
    public PizzaRecipe getRecipe() { return recipe; }
    public PizzaState getState() { return state; }
    public void setState(PizzaState state) { this.state = state; }
    public String getSauce() { return sauce; }
    public void setSauce(String sauce) { this.sauce = sauce; }

    public List<Ingredient> getIngredientsOnPizza() {
        return Collections.unmodifiableList(ingredientsOnPizza);
    }

    public void addIngredient(Ingredient ingredient) {
        ingredientsOnPizza.add(ingredient);
    }

    public void clearIngredients() {
        ingredientsOnPizza.clear();
    }

    public Ingredient removeLastIngredient() {
        return ingredientsOnPizza.removeLast();
    }

    @Override
    public String toString() {
        return "Pizza{" +
                "size=" + size +
                ", recipe=" + recipe +
                ", ingredientsOnPizza=" + ingredientsOnPizza +
                ", state=" + state +
                ", sauce=" + sauce +
                '}';
    }
}
```

---

### Rozwiązanie 2.2: Enkapsulacja klasy PizzaShop

**Podpowiedzi:**
- Lista `pizzas` nie powinna być publiczna — pracownicy powinni korzystać z metod `getNextOrder()`, `returnPizza()` itp.
- Lista `ovens` może udostępniać tylko metody wyszukujące, nie bezpośredni dostęp.
- Statystyki powinny mieć tylko gettery — nikt z zewnątrz nie powinien móc zmienić licznika spalonych pizz.

**Rozwiązanie:**

```java
class PizzaShop {
    private final List<Pizza> pizzas = new ArrayList<>();
    private final List<PizzaRecipe> menu;
    private final List<PizzaShopWorker> workers = new ArrayList<>();
    private final List<PizzaOven> ovens = new ArrayList<>();

    private int ticksPassed = 0;
    private int pizzasPrepared = 0;
    private int pizzasBurned = 0;

    public PizzaShop(List<PizzaRecipe> menu, int cookCount, int helperCount, int ovenCount) {
        this.menu = List.copyOf(menu);

        for (int i = 0; i < cookCount; i++) {
            workers.add(new PizzaShopCook(this));
        }
        for (int i = 0; i < helperCount; i++) {
            workers.add(new PizzaShopHelper(this));
        }
        for (int i = 0; i < ovenCount; i++) {
            ovens.add(new PizzaOven());
        }
    }

    // --- Order management ---

    public Pizza placeOrder(PizzaRecipe recipe, PizzaSize size) {
        Pizza p = new Pizza(size, recipe);
        pizzas.add(p);
        return p;
    }

    public Optional<Pizza> getNextOrder() {
        return pizzas.stream()
                .filter(p -> p.getState() == PizzaState.Ordered)
                .findFirst();
    }

    public Optional<Pizza> getNextFilledPizza() {
        return pizzas.stream()
                .filter(p -> p.getState() == PizzaState.Filled)
                .filter(p -> p.getIngredientsOnPizza().size()
                        >= p.getRecipe().ingredients().size())
                .findFirst();
    }

    public List<Pizza> getIncompleteFilledPizzas() {
        return pizzas.stream()
                .filter(p -> p.getState() == PizzaState.Filled)
                .filter(p -> p.getIngredientsOnPizza().size()
                        < p.getRecipe().ingredients().size())
                .toList();
    }

    public List<Pizza> getPizzas() {
        return Collections.unmodifiableList(pizzas);
    }
    
    public void removePizza(Pizza pizza) {
        pizzas.remove(pizza);
    }

    public void returnPizza(Pizza pizza) {
        pizzas.add(pizza);
    }

    public void completePizza(Pizza pizza) {
        pizza.setState(PizzaState.Boxed);
        pizzas.add(pizza);
        pizzasPrepared++;
    }

    // --- Oven management ---

    public List<PizzaOven> getOvens() {
        return Collections.unmodifiableList(ovens);
    }

    public Optional<PizzaOven> findFreeOven() {
        return ovens.stream()
                .filter(PizzaOven::isEmpty)
                .findFirst();
    }

    public Optional<PizzaOven> findOvenWithBakedPizza() {
        return ovens.stream()
                .filter(oven -> oven.getBakedPizza().isPresent())
                .findFirst();
    }

    // --- Menu ---

    public List<PizzaRecipe> getMenu() {
        return menu;
    }

    public void addWorker(PizzaShopWorker worker) {
        workers.add(worker);
    }

    // --- Simulation ---

    public void update() {
        ticksPassed++;

        for (PizzaShopWorker worker : workers) {
            worker.update();
        }

        for (PizzaOven oven : ovens) {
            try {
                oven.update();
            } catch (PizzaBurnedException e) {
                pizzasBurned++;
            }
        }
    }

    public boolean simulate(List<Pizza> orderedPizzas, int maxTicks) {
        for (int tick = 0; tick < maxTicks; tick++) {
            update();

            boolean allDone = orderedPizzas.stream()
                    .allMatch(p -> p.getState() == PizzaState.Boxed);

            if (allDone) {
                return true;
            }
        }
        return false;
    }

    // --- Statistics ---

    public int getTicksPassed() { return ticksPassed; }
    public int getPizzasPrepared() { return pizzasPrepared; }
    public int getPizzasBurned() { return pizzasBurned; }

    public String getStatsSummary() {
        long ordered = pizzas.stream()
                .filter(p -> p.getState() == PizzaState.Ordered)
                .count();
        long baking = ovens.stream()
                .filter(oven -> !oven.isEmpty())
                .count();

        return String.join("\n",
                "=== Pizza Shop Stats ===",
                "Ticks: " + ticksPassed,
                "Prepared: " + pizzasPrepared,
                "Burned: " + pizzasBurned,
                "In queue (ordered): " + ordered,
                "Ovens in use: " + baking + "/" + ovens.size()
        );
    }
}
```

---

### Rozwiązanie 2.3: Enkapsulacja klasy PizzaOven

**Podpowiedzi:**
- `contents` powinno być prywatne. Dostęp przez `isEmpty()`, `getBakedPizza()`, `ejectPizza()`.
- `loadPizza()` już istnieje — to jedyny sposób na włożenie pizzy.
- `ejectPizza()` powinna zwracać pizzę i czyścić `contents`.

**Rozwiązanie:**

```java
class PizzaOven {
    static final int BAKE_TIME = 5;
    static final int TIME_TO_BURN = 3;

    private Pizza contents = null;
    private int timeLeft = 0;
    private int burnTimer = 0;

    public boolean isEmpty() {
        return contents == null;
    }

    public Optional<Pizza> getBakedPizza() {
        if (contents != null && contents.getState() == PizzaState.Baked) {
            return Optional.of(contents);
        }
        return Optional.empty();
    }

    public Pizza ejectPizza() {
        Pizza pizza = contents;
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
    }

    void update() throws PizzaBurnedException {
        if (contents == null) {
            return;
        }
        if (contents.getState() == PizzaState.Baking) {
            timeLeft--;
            if (timeLeft <= 0) {
                contents.setState(PizzaState.Baked);
                burnTimer = TIME_TO_BURN;
            }
        } else if (contents.getState() == PizzaState.Baked) {
            burnTimer--;
            if (burnTimer <= 0) {
                contents.setState(PizzaState.Burned);
                throw new PizzaBurnedException(contents);
            }
        }
    }
}
```

---

### Rozwiązanie 3.3a: Refactoring PizzaShopCook

**Podpowiedzi:**
- `pickUpOrder()` korzysta z `shop.getNextOrder()` i `Optional.ifPresent()`.
- Szukanie wolnego pieca korzysta z `shop.findFreeOven()`.
- Odkładanie pizzy korzysta z `putBack()` z klasy bazowej.

**Rozwiązanie:**

```java
class PizzaShopCook extends BasePizzaWorker {

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
        shop.getNextOrder().ifPresent(this::pickUp);
    }

    private void preparePizza() {
        switch (workedPizza.getState()) {
            case Ordered -> {
                workedPizza.setState(PizzaState.Dough);
            }
            case Dough -> {
                workedPizza.setSauce("Tomato");
                workedPizza.setState(PizzaState.Pie);
            }
            case Pie -> {
                int idx = workedPizza.getIngredientsOnPizza().size();
                if (idx < workedPizza.getRecipe().ingredients().size()) {
                    workedPizza.addIngredient(
                            workedPizza.getRecipe().ingredients().get(idx));
                } else {
                    workedPizza.setState(PizzaState.Filled);
                }
            }
            case Filled -> {
                Optional<PizzaOven> freeOven = shop.findFreeOven();
                if (freeOven.isPresent()) {
                    freeOven.get().loadPizza(workedPizza);
                    workedPizza = null;
                } else {
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

### Rozwiązanie 3.3b: Refactoring PizzaShopHelper

**Rozwiązanie:**

```java
class PizzaShopHelper extends BasePizzaWorker {

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
            return true;
        }
        return false;
    }

    protected void pickUpFilledPizza() {
        // Reset incomplete pizzas (e.g. nibbled by HungryHelper)
        for (Pizza incomplete : shop.getIncompleteFilledPizzas()) {
            incomplete.clearIngredients();
            incomplete.setState(PizzaState.Ordered);
        }

        shop.getNextFilledPizza().ifPresent(this::pickUp);
    }

    private void handlePizza() {
        switch (workedPizza.getState()) {
            case Filled -> {
                Optional<PizzaOven> freeOven = shop.findFreeOven();
                if (freeOven.isPresent()) {
                    freeOven.get().loadPizza(workedPizza);
                    workedPizza = null;
                } else {
                    putBack(workedPizza);
                }
            }
            case Baking -> {
                throw new RuntimeException("Hot pizza!");
            }
            case Baked -> {
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

### Rozwiązanie 3.4: BasePizzaWorker

**Podpowiedzi:**
- Klasa abstrakcyjna — nie można tworzyć jej instancji bezpośrednio.
- Metody `pickUp`, `putBack`, `complete` centralizują logikę przenoszenia pizz.
- Te metody będą punktami synchronizacji na Lab 5 (wielowątkowość).

**Rozwiązanie:**

```java
abstract class BasePizzaWorker implements PizzaShopWorker {
    protected final PizzaShop shop;
    protected Pizza workedPizza = null;

    protected BasePizzaWorker(PizzaShop shop) {
        this.shop = shop;
    }

    protected void pickUp(Pizza pizza) {
        workedPizza = pizza;
        shop.removePizza(pizza);
    }

    protected void putBack(Pizza pizza) {
        shop.returnPizza(pizza);
        workedPizza = null;
    }

    protected void complete(Pizza pizza) {
        shop.completePizza(pizza);
        workedPizza = null;
    }
}
```

---

### Rozwiązanie 3.5: allMatch w simulate()

**Rozwiązanie:**

```java
boolean allDone = orderedPizzas.stream()
        .allMatch(pizza -> pizza.getState() == PizzaState.Boxed);
```

Porównanie z `noneMatch`:
```java
boolean allDone = orderedPizzas.stream()
        .noneMatch(pizza -> pizza.getState() != PizzaState.Boxed);
```

Obie wersje są równoważne. `allMatch` jest czytelniejsze — bezpośrednio wyraża intencję: "czy wszystkie pizze są zapakowane?".

Porównanie z `anyMatch`:
```java
boolean anyNotDone = orderedPizzas.stream()
        .anyMatch(pizza -> pizza.getState() != PizzaState.Boxed);
boolean allDone = !anyNotDone;
```

Ta wersja wymaga negacji — mniej czytelna.

---

### Rozwiązanie: Zaktualizowane testy po enkapsulacji

Po wykonaniu wszystkich ćwiczeń z części 2 i 3, testy wymagają aktualizacji — bezpośrednie odwołania do pól należy zamienić na metody. Poniżej pełny zaktualizowany plik testowy:

```java
package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class AppTest {
    PizzaShop shop = null;

    @BeforeEach
    public void resetShop() {
        shop = new PizzaShop(List.of(
                new PizzaRecipe("Margherita", List.of(Ingredient.Cheese)),
                new PizzaRecipe("Peperoni", List.of(Ingredient.Cheese, Ingredient.Peperoni)),
                new PizzaRecipe("Hawaiian", List.of(Ingredient.Cheese, Ingredient.Ham, Ingredient.Pineapple))),
                1, 1, 1);
    }

    @Test
    public void testMargherita() {
        var m = shop.getMenu();
        assertFalse(m.isEmpty());

        var r = m.getFirst();
        assertEquals("Margherita", r.name());

        var p = shop.placeOrder(r, PizzaSize.L);

        boolean success = shop.simulate(List.of(p), 50);
        assertTrue(success);

        assertEquals(PizzaState.Boxed, p.getState());
        assertEquals(1, p.getIngredientsOnPizza().size());
        assertEquals(Ingredient.Cheese, p.getIngredientsOnPizza().getFirst());
    }

    @Test
    public void testPeperoni() {
        var m = shop.getMenu();
        assertFalse(m.isEmpty());

        var r = m.get(1);
        assertEquals("Peperoni", r.name());

        var p = shop.placeOrder(r, PizzaSize.L);

        boolean success = shop.simulate(List.of(p), 50);
        assertTrue(success);

        assertEquals(PizzaState.Boxed, p.getState());
        assertEquals(2, p.getIngredientsOnPizza().size());
        assertEquals(Ingredient.Cheese, p.getIngredientsOnPizza().get(0));
        assertEquals(Ingredient.Peperoni, p.getIngredientsOnPizza().get(1));
    }

    @Test
    public void testHawaiian() {
        assertEquals(3, shop.getMenu().size());

        PizzaRecipe hawaiian = null;
        for (PizzaRecipe recipe : shop.getMenu()) {
            if (recipe.name().equals("Hawaiian")) {
                hawaiian = recipe;
            }
        }
        assertNotNull(hawaiian, "Menu should contain Hawaiian pizza");

        Pizza p = shop.placeOrder(hawaiian, PizzaSize.M);
        boolean success = shop.simulate(List.of(p), 50);
        assertTrue(success);
        assertEquals(3, p.getIngredientsOnPizza().size());
    }

    @Test
    public void testHotPizza() {
        // workedPizza is protected — accessible from same package
        PizzaShopHelper helper = new PizzaShopHelper(shop);

        var p = new Pizza(PizzaSize.L, null);
        p.setState(PizzaState.Baking);
        helper.workedPizza = p;

        RuntimeException thrown = assertThrows(
                RuntimeException.class,
                helper::update,
                "Expected to throw, but didn't"
        );

        assertTrue(thrown.getMessage().contains("Hot pizza!"));
    }

    @Test
    public void testPizzaBurns() {
        PizzaOven oven = shop.getOvens().getFirst();
        Pizza p = new Pizza(PizzaSize.L, shop.getMenu().getFirst());
        oven.loadPizza(p);

        try {
            for (int i = 0; i < PizzaOven.BAKE_TIME; i++) {
                assertEquals(PizzaState.Baking, p.getState());
                oven.update();
            }
            assertEquals(PizzaState.Baked, p.getState());
        } catch (PizzaBurnedException e) {
            fail("Pizza burned prematurely!", e);
        }

        try {
            for (int i = 0; i < PizzaOven.TIME_TO_BURN; i++) {
                assertEquals(PizzaState.Baked, p.getState());
                oven.update();
            }
            fail("Pizza did not burn!");
        } catch (PizzaBurnedException e) {
            assertTrue(e.getMessage().startsWith("Pizza was burned!"));
        }
        assertEquals(PizzaState.Burned, p.getState());
    }

    @Test
    public void testStatistics() {
        Pizza p = shop.placeOrder(shop.getMenu().getFirst(), PizzaSize.M);

        boolean success = shop.simulate(List.of(p), 50);

        assertTrue(success);
        assertEquals(1, shop.getPizzasPrepared());
        assertEquals(0, shop.getPizzasBurned());
        assertTrue(shop.getTicksPassed() > 0);
    }

    @Test
    public void testPizzaCanHaveSauce() {
        Pizza p = new Pizza(PizzaSize.L, shop.getMenu().getFirst());
        p.setSauce("Tomato");
        assertEquals("Tomato", p.getSauce());
    }

    @Test
    public void testSauceAppliedDuringPreparation() {
        Pizza p = shop.placeOrder(shop.getMenu().getFirst(), PizzaSize.L);
        assertNull(p.getSauce());

        boolean success = shop.simulate(List.of(p), 50);
        assertTrue(success);
        assertNotNull(p.getSauce(), "Sauce should be applied during preparation");
    }

    @Test
    public void testBusyShop() {
        PizzaShop busyShop = new PizzaShop(List.of(
                new PizzaRecipe("Margherita", List.of(Ingredient.Cheese))),
                3, 1, 2);

        List<Pizza> orders = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            orders.add(busyShop.placeOrder(busyShop.getMenu().getFirst(), PizzaSize.L));
        }

        boolean success = busyShop.simulate(orders, 200);

        assertTrue(success, "All pizzas should be boxed");
        assertEquals(0, busyShop.getPizzasBurned(), "No pizzas should be burned");
        assertEquals(6, busyShop.getPizzasPrepared(), "All 6 pizzas should be prepared");
    }

    @Test
    public void testTamperedPizzaDetected() {
        PizzaRecipe recipe = shop.getMenu().get(1); // Peperoni: Cheese + Peperoni
        Pizza tampered = new Pizza(PizzaSize.L, recipe);
        tampered.setState(PizzaState.Filled);
        tampered.addIngredient(Ingredient.Cheese);
        // Missing Peperoni — only 1 of 2 ingredients!

        shop.returnPizza(tampered);
        shop.simulate(List.of(), 1);

        // Verify the tampered pizza was not loaded into any oven
        for (PizzaOven oven : shop.getOvens()) {
            assertTrue(oven.isEmpty()
                            || oven.getBakedPizza().isEmpty(),
                    "Tampered pizza should not be in an oven");
        }
    }

    @Test
    public void testHungryHelper() {
        PizzaShop hungryShop = new PizzaShop(List.of(
                new PizzaRecipe("Margherita", List.of(Ingredient.Cheese))),
                0, 0, 2);
        HungryHelper hungryHelper = new HungryHelper(hungryShop);
        hungryShop.addWorker(hungryHelper);

        List<Pizza> pizzas = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Pizza p = new Pizza(PizzaSize.L, hungryShop.getMenu().getFirst());
            p.setState(PizzaState.Filled);
            p.addIngredient(Ingredient.Cheese);
            hungryShop.returnPizza(p);
            pizzas.add(p);
        }

        // Tick 1: picks up pizza 0 (pickupCount=1, normal)
        // Tick 2: loads pizza 0 into oven
        // Tick 3: picks up pizza 1 (pickupCount=2, normal)
        // Tick 4: loads pizza 1 into oven
        // Tick 5: picks up pizza 2 (pickupCount=3, nibble!)
        for (int i = 0; i < 5; i++) {
            hungryShop.update();
        }

        assertEquals(3, hungryHelper.pickupCount, "Should have picked up 3 pizzas");

        assertEquals(PizzaState.Baking, pizzas.get(0).getState());
        assertEquals(PizzaState.Baking, pizzas.get(1).getState());

        assertTrue(pizzas.get(2).getIngredientsOnPizza().isEmpty(),
                "Third pizza should have its ingredient eaten");
    }
}
```

Uwaga do testu `testTamperedPizzaDetected`: po enkapsulacji nie mamy bezpośredniego dostępu do pola `contents` pieca. Weryfikujemy za pomocą `isEmpty()` i `getBakedPizza()`. Ten test sprawdza, czy niekompletna pizza **nie trafiła do pieca** — po jednym ticku helper powinien ją zresetować do stanu `Ordered` zamiast załadować.