# Laboratorium 2: Praca z IDE i testami — 03.03.2026, 8:00–9:30

## Temat zajęć
Praca z IDE (IntelliJ IDEA), nawigacja po projekcie, refactoring, debugowanie, testy jednostkowe, wprowadzenie do TDD.

Pracujemy z programem symulującym pracę pizzerii. Program znajduje się w repozytorium Git — klonujemy go i pracujemy lokalnie.

### Przydatne skróty klawiszowe (IntelliJ IDEA)

| Skrót                   | Działanie                                            |
|-------------------------|------------------------------------------------------|
| `Ctrl+Shift+A`          | Wyszukaj akcję (przydatne, gdy nie pamiętasz skrótu) |
| `Ctrl+Click` / `Ctrl+B` | Przejdź do definicji                                 |
| `Alt+F7`                | Znajdź użycia                                        |
| `Shift+Shift`           | Wyszukaj wszędzie                                    |
| `Ctrl+Shift+F`          | Szukaj w plikach                                     |
| `Ctrl+Shift+R`          | Zamień w plikach                                     |
| `Shift+F6`              | Zmień nazwę (Rename)                                 |
| `F6`                    | Przenieś (Move)                                      |
| `Alt+Enter`             | Podpowiedzi i szybkie poprawki                       |
| `Shift+F9`              | Uruchom w trybie debugowania                         |
| `Shift+F10`             | Uruchom                                              |
| `Ctrl+F8`               | Wstaw/usuń breakpoint                                |

Pełna lista skrótów: [IntelliJ IDEA Reference Card](https://resources.jetbrains.com/storage/products/intellij-idea/docs/IntelliJIDEA_ReferenceCard.pdf)

---

## Część 1 (8:00–8:30): Konfiguracja środowiska i zapoznanie z projektem

### 1.1 Instalacja IntelliJ IDEA (przypomnienie)

Jeśli nie masz jeszcze zainstalowanego IDE:

1. Pobierz IntelliJ IDEA ze strony [jetbrains.com/idea/download](https://www.jetbrains.com/idea/download/).
    - **Community Edition** jest darmowa i wystarczająca do naszych zajęć.
    - **Ultimate Edition** jest dostępna za darmo dla studentów — licencję można uzyskać na [jetbrains.com/community/education](https://www.jetbrains.com/community/education/).
2. Zainstaluj program zgodnie z instrukcjami instalatora.
3. Po pierwszym uruchomieniu, zaakceptuj domyślne ustawienia.

Dokumentacja IDE: [IntelliJ IDEA Help](https://www.jetbrains.com/help/idea/)

### 1.2 Pobranie projektu z GitHub

Projekt jest udostępniony w repozytorium Git:

> **https://github.com/starcatter/pizzaShop_2026**

Aby go pobrać:

1. Otwórz IntelliJ IDEA.
2. Użyj `Ctrl+Shift+A` i wyszukaj **"Get from Version Control"** (lub z menu: *File → New → Project from Version Control*).
3. Wklej adres repozytorium i wybierz katalog docelowy.
4. Kliknij **Clone**.
5. Poczekaj, aż IntelliJ pobierze projekt i zaindeksuje pliki. W dolnej belce statusu zobaczysz postęp.

Jeśli pojawi się pytanie *"Trust this project?"*, kliknij **Trust Project**.

### 1.3 Uruchomienie programu

1. W drzewie plików po lewej stronie znajdź plik `src/main/java/org/example/PizzaApp.java`.
2. Otwórz go i znajdź metodę `main`.
3. Kliknij zieloną strzałkę obok `public static void main` i wybierz **Run 'PizzaApp.main()'** (lub użyj `Shift+F10`).
4. W konsoli na dole ekranu powinien pojawić się wynik symulacji — podsumowanie pracy pizzerii.

Przejrzyj kod klasy `PizzaApp` i spróbuj zrozumieć, co program robi. Zwróć uwagę na:
- Jakie klasy są używane (`PizzaShop`, `Pizza`, `PizzaRecipe`, ...).
- Jak działa symulacja (metoda `simulate`).
- Co wypisuje program na koniec.

Użyj `Ctrl+Click` na nazwach klas i metod, żeby przejść do ich definicji.

### 1.4 Uruchomienie testów

1. Znajdź plik `src/test/java/org/example/AppTest.java`.
2. Otwórz go i kliknij zieloną strzałkę obok nazwy klasy `AppTest`, następnie wybierz **Run 'AppTest'**.
3. Sprawdź wynik w panelu na dole — jeden z testów powinien zakończyć się **niepowodzeniem** (czerwony).

Nie naprawiaj jeszcze błędu — zajmiemy się tym w części 2. Zanotuj, który test nie przechodzi i jaki jest komunikat błędu.

### 1.5 Debugger — pierwsze kroki

Debugger pozwala zatrzymać program w dowolnym miejscu i zbadać stan zmiennych. Przećwiczmy to na teście `testMargherita`:

1. Otwórz plik `AppTest.java`.
2. Znajdź metodę `testMargherita`.
3. Kliknij na szary margines po lewej stronie, na wysokości linii `boolean success = shop.simulate(...)` — pojawi się czerwona kropka (**breakpoint**). Możesz też użyć `Ctrl+F8`.
4. Kliknij prawym przyciskiem na zieloną strzałkę obok `testMargherita` i wybierz **Debug 'testMargherita()'** (lub ustaw kursor w metodzie i użyj `Shift+F9`).
5. Program zatrzyma się na breakpoincie. W dolnym panelu zobaczysz:
    - **Variables** — wartości aktualnych zmiennych. Rozwiń obiekt `p` (pizza), żeby zobaczyć jego pola.
    - **Frames** — stos wywołań.
6. Użyj przycisków w pasku debuggera:
    - **Step Over** (`F8`) — wykonaj bieżącą linię i przejdź do następnej.
    - **Step Into** (`F7`) — wejdź do wywoływanej metody.
    - **Resume** (`F9`) — kontynuuj do następnego breakpointa lub do końca programu.
7. Kliknij **Resume** (`F9`), aby zakończyć test.

Spróbuj postawić breakpoint wewnątrz metody `simulate` klasy `PizzaShop` i prześledzić, jak działa symulacja krok po kroku.

Dokumentacja debuggera: [Debugging in IntelliJ IDEA](https://www.jetbrains.com/help/idea/debugging-code.html)

---

## Część 2 (8:30–9:00): Praca z IDE

### 2.1 Szukanie i naprawa błędu

Jeden z testów — `testPeperoni` — nie przechodzi. Twoim zadaniem jest znaleźć i naprawić błąd.

**Krok 1: Analiza komunikatu błędu.**
Uruchom testy ponownie i przeczytaj komunikat błędu testu `testPeperoni`. Powinien wskazywać na różnicę między oczekiwaną a faktyczną wartością składnika.

**Krok 2: Użyj debuggera.**
1. Otwórz `AppTest.java` i postaw breakpoint wewnątrz metody `testPeperoni`, na linii `boolean success = shop.simulate(...)`.
2. Uruchom `testPeperoni` w trybie debug (`Shift+F9`).
3. Zanim program dotrze do breakpointa, postaw dodatkowy breakpoint w klasie `PizzaShopCook`, w metodzie `preparePizza()`, wewnątrz przypadku (case) `Pie`.
4. Kliknij **Resume** (`F9`) — program powinien zatrzymać się w `preparePizza`.
5. Obserwuj, jaki składnik jest dodawany do pizzy. Porównaj go z tym, co powinno być dodane na podstawie przepisu (`workedPizza.recipe.ingredients()`).

**Krok 3: Napraw błąd.**
Po zidentyfikowaniu problemu wprowadź poprawkę i uruchom ponownie **wszystkie** testy, aby upewnić się, że naprawiony test przechodzi, a pozostałe nadal działają.

> **Git:** Wykonaj commit z opisem, np. *"Fix: correct ingredient bug in PizzaShopCook"*.
> W IntelliJ: `Ctrl+K` otwiera okno commita. Zaznacz zmienione pliki, wpisz opis, kliknij **Commit**.

### 2.2 Refactoring: wyodrębnianie klas do osobnych plików

Plik `PizzaApp.java` zawiera wiele klas, enumów i interfejsów w jednym pliku. Przez to jest mało czytelny.

**Wyodrębnij każdą klasę i enum do osobnego pliku:**

Dostępne metody:
- Kliknij prawym przyciskiem na nazwie klasy/enuma → *Refactor → Move Class...*
- Ustaw kursor na nazwie klasy i naciśnij `F6`.
- W drzewie plików po lewej: rozwiń plik `PizzaApp.java` — powinny być widoczne zagnieżdżone klasy. Przeciągnij je do pakietu `org.example`.

Wyodrębnij kolejno: `PizzaState`, `PizzaSize`, `Ingredient`, `PizzaRecipe`, `PizzaShopWorker`, `PizzaShopCook`, `PizzaShopHelper`, `Pizza`, `PizzaBurnedException`, `PizzaFurnace`, `PizzaShop`.

W pliku `PizzaApp.java` powinna pozostać tylko klasa `PizzaApp` z metodą `main`.

**Uwaga:** wyodrębnione typy mogą nie mieć modyfikatora `public` — są wtedy *package-private*, czyli widoczne tylko wewnątrz pakietu `org.example`. Dla naszych celów to wystarczy.

Po zakończeniu uruchom testy — powinny nadal przechodzić (oprócz `testPeperoni`, jeśli jeszcze nie został naprawiony).

> **Git:** Commit, np. *"Refactor: extract classes to separate files"*.

### 2.3 Refactoring: poprawienie nazw

Nazwa klasy `PizzaFurnace` jest niepoprawna — *"furnace"* oznacza piec przemysłowy lub grzewczy. Piec do pizzy to *"oven"*.

**Zmień nazwę klasy na `PizzaOven`:**
1. Otwórz plik `PizzaFurnace.java`.
2. Ustaw kursor na nazwie klasy `PizzaFurnace` i naciśnij `Shift+F6`.
3. Wpisz `PizzaOven` i zatwierdź. IntelliJ zmieni nazwę klasy, pliku i wszystkich odwołań.

**Wyszukaj i popraw pozostałe odwołania do "furnace":**
1. Użyj `Ctrl+Shift+F` i wyszukaj `furnace` w całym projekcie.
2. Zmień nazwy pól i zmiennych, np. `furnaces` → `ovens`, `furnace` → `oven`, `furnaceCount` → `ovenCount`.
3. Użyj `Shift+F6` na każdej zmiennej — IntelliJ zaproponuje lepszą nazwę.

**Popraw jednoliterowe nazwy zmiennych**, które znajdziesz w kodzie (np. `f`, `p`, `r`, `m`). Ustawiając kursor na zmiennej i naciskając `Shift+F6`, automatycznie uzyskujesz podpowiedź lepszej nazwy. Poszukaj również w kodzie testów.

Po zakończeniu uruchom testy — powinny nadal przechodzić.

> **Git:** Commit, np. *"Refactor: rename Furnace to Oven, improve variable names"*.

### 2.4 Konwersja pętli z użyciem refactoringu

IntelliJ potrafi automatycznie konwertować między różnymi rodzajami pętli i wyrażeniami strumieniowymi.

**Ćwiczenie:** Znajdź pętlę w metodzie `PizzaShopCook.pickUpOrder()`:
```java
for (Pizza pizza : shop.pizzas) {
    if (pizza.state == PizzaState.Ordered) {
        workedPizza = pizza;
        break;
    }
}
```

1. Ustaw kursor na słowie `for` i naciśnij `Alt+Enter`.
2. Sprawdź, jakie opcje konwersji są dostępne (np. *"Replace with `while`"*, *"Replace with indexed `for`"*).
3. Wybierz jedną z opcji i obejrzyj wynik. Użyj `Ctrl+Z`, żeby cofnąć zmianę.

**Ćwiczenie z wyrażeniami strumieniowymi:** Zamień powyższą pętlę ręcznie na strumień:
```java
workedPizza = shop.pizzas.stream()
        .filter(pizza -> pizza.state == PizzaState.Ordered)
        .findFirst()
        .orElse(null);
```

Następnie ustaw kursor na `.stream()` i naciśnij `Alt+Enter` — sprawdź, czy IntelliJ oferuje zamianę z powrotem na pętlę.

Dokumentacja strumieni: [Oracle — Stream API](https://docs.oracle.com/javase/tutorial/collections/streams/)

> **Git:** Commit, np. *"Refactor: experiment with loop conversions"*.

### 2.5 Zadania domowe (opcjonalne)

**Konkretne ćwiczenia:**

1. **Zmiana nazwy pakietu.** Obecna nazwa `org.example` jest generyczna. Zmień ją na coś bardziej adekwatnego, np. `pl.edu.uksw.java.lab2`. Użyj *Refactor → Rename* na nazwie pakietu. Zwróć uwagę na strukturę katalogów po zmianie.

2. **Wyrażenia strumieniowe.** Znajdź w programie co najmniej 3 pętle, które można zamienić na operacje strumieniowe. Wykonaj zamianę i upewnij się, że testy nadal przechodzą. Wskazówka: pętle w `PizzaShopHelper`, `PizzaShop.update()`, `PizzaApp.main()`.

3. **Zapoznaj się ze skrótami.** Otwórz [IntelliJ IDEA Reference Card](https://resources.jetbrains.com/storage/products/intellij-idea/docs/IntelliJIDEA_ReferenceCard.pdf) i przetestuj 5 skrótów, których nie znasz.

**Pomysły na dalszą eksplorację:**
- Sprawdź, jak działa *Ctrl+Shift+A* → *"Column Selection Mode"* i w jakich sytuacjach może być przydatny.
- Przetestuj *Live Templates* (`Ctrl+J`) — gotowe szablony kodu, np. `sout` wstawia `System.out.println()`.
- Włącz plugin *Key Promoter X* (w *Settings → Plugins*) — będzie pokazywał skrót klawiszowy za każdym razem, gdy użyjesz myszki do akcji, która ma skrót.

---

## Część 3 (9:00–9:30): Testy i TDD

### 3.1 Wprowadzenie do TDD

**Test-Driven Development** (TDD) to podejście, w którym:
1. **Najpierw** piszemy test opisujący oczekiwane zachowanie.
2. Uruchamiamy test — **nie przechodzi** (bo funkcjonalność jeszcze nie istnieje).
3. Piszemy **minimalny** kod, który sprawia, że test przechodzi.
4. Refaktoryzujemy kod (poprawiamy jakość bez zmiany zachowania).

Ten cykl powtarza się dla każdej nowej funkcjonalności. Dzięki temu mamy pewność, że każda zmiana w kodzie jest pokryta testami.

Dokumentacja JUnit 5: [JUnit 5 User Guide](https://junit.org/junit5/docs/current/user-guide/)

Przećwiczymy teraz trzy warianty cyklu TDD.

### 3.2 Ćwiczenie A: Nowy przepis w menu

**Cel:** dodać do menu pizzerii przepis *"Hawaiian"* ze składnikami: Cheese, Ham, Pineapple.

**Krok 1 — napisz test.** W pliku `AppTest.java` dodaj nową metodę testową:

```java
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
    assertEquals(3, p.ingredientsOnPizza.size());
}
```

**Krok 2 — uruchom test.** Test się kompiluje i uruchamia, ale kończy się niepowodzeniem — menu zawiera tylko 2 przepisy.

**Krok 3 — napraw kod.** Dodaj przepis *Hawaiian* do menu w metodzie `resetShop()` w `AppTest.java`. Uruchom test ponownie — powinien przechodzić.

> **Git:** Commit, np. *"TDD: add Hawaiian recipe"*.

### 3.3 Ćwiczenie B: Sos na pizzy

**Cel:** każda pizza powinna mieć pole przechowujące rodzaj sosu.

**Krok 1 — napisz test:**
```java
@Test
public void testPizzaCanHaveSauce() {
    Pizza p = new Pizza(PizzaSize.L, shop.getMenu().getFirst());
    p.sauce = "Tomato";
    assertEquals("Tomato", p.sauce);
}
```

**Krok 2 — uruchom test.** Test **nie skompiluje się** — klasa `Pizza` nie ma pola `sauce`.

**Krok 3 — dodaj pole.** W klasie `Pizza` dodaj pole:
```java
String sauce = null;
```

**Krok 4 — uruchom test.** Teraz test się kompiluje i przechodzi — pole istnieje i można je ustawić.

> **Git:** Commit, np. *"TDD: add sauce field to Pizza"*.

### 3.4 Ćwiczenie C: Nakładanie sosu w trakcie przygotowania

**Cel:** sos powinien być nakładany automatycznie podczas przygotowywania pizzy, a nie ręcznie.

**Krok 1 — napisz test:**
```java
@Test
public void testSauceAppliedDuringPreparation() {
    Pizza p = shop.placeOrder(shop.getMenu().getFirst(), PizzaSize.L);
    assertNull(p.sauce);

    boolean success = shop.simulate(List.of(p), 50);
    assertTrue(success);
    assertNotNull(p.sauce, "Sauce should be applied during preparation");
}
```

**Krok 2 — uruchom test.** Test się kompiluje (pole `sauce` istnieje z ćwiczenia B), ale nie przechodzi — nikt nie ustawia sosu podczas symulacji.

**Krok 3 — zmodyfikuj kod.** Musisz znaleźć odpowiednie miejsce w klasie `PizzaShopCook`, w którym sos zostanie nałożony. Zastanów się, na którym etapie przygotowania pizzy powinno to nastąpić — czy sos nakłada się na ciasto (`Dough`), placek (`Pie`), czy w innym momencie?

Wprowadź zmianę i uruchom **wszystkie** testy, żeby upewnić się, że nic nie zepsułeś.

> **Git:** Commit, np. *"TDD: apply sauce during pizza preparation"*.

### 3.5 Zadanie: Blokada pizzerii

W pizzerii pracują kucharze (`PizzaShopCook`) i pomocnicy (`PizzaShopHelper`). Kucharze przygotowują pizzę i wkładają ją do pieca (albo odkładają do kolejki, jeśli żaden piec nie jest wolny). Pomocnicy wyjmują upieczoną pizzę z pieca, pakują ją do pudełka, a dodatkowo mogą ładować do pieców gotowe do wypieku pizze z kolejki.

**Co się stanie, jeśli kucharzy jest znacznie więcej niż pomocników?**

Kucharze szybko przygotowują wiele pizz, a jedyny pomocnik musi nadążyć z obsługą pieców. Jeśli tego nie zrobi, pizze spalą się w piecu — piec rzuci wyjątek `PizzaBurnedException`.

**Krok 1 — napisz test.** Stwórzmy scenariusz z dużą presją:

```java
@Test
public void testBusyShop() {
    // 3 cooks, 1 helper, 2 ovens — cooks produce pizzas faster than the helper can handle
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
```

**Krok 2 — uruchom test.** Test powinien **nie przejść**. W konsoli mogą być widoczne komunikaty o spalonych pizzach. Symulacja prawdopodobnie nie kończy się sukcesem — metoda `simulate` zwraca `false` po wyczerpaniu ticków.

**Krok 3 — zbadaj problem z użyciem debuggera.**

1. Postaw breakpoint w metodzie `handlePizza()` klasy `PizzaShopHelper`, w przypadku `Filled`.
2. Uruchom test w trybie debug.
3. Obserwuj, co robi pomocnik, gdy trzyma gotową do wypieku pizzę (`Filled`), ale żaden piec nie jest wolny. Czy pomocnik się z niej zwalnia?
4. Porównaj to zachowanie z tym, co robi kucharz (`PizzaShopCook.preparePizza()`) w analogicznej sytuacji `Filled`.

Wskazówka: spróbuj postawić **breakpoint warunkowy**. Kliknij prawym przyciskiem na breakpoincie i w polu *Condition* wpisz wyrażenie, np.:
```
workedPizza != null && workedPizza.state == PizzaState.Filled
```
Dzięki temu debugger zatrzyma się tylko w interesującym nas momencie.

Dokumentacja breakpointów warunkowych: [IntelliJ IDEA — Breakpoints](https://www.jetbrains.com/help/idea/using-breakpoints.html)

**Krok 4 — zrozum mechanizm blokady.**

Przebieg problemu wygląda mniej więcej tak:
1. Kucharze szybko przygotowują pizze i zapełniają piece.
2. Pomocnik podnosi gotową pizzę (`Filled`) z kolejki i próbuje załadować ją do pieca — ale oba piece są zajęte.
3. Pomocnik **utknął** — trzyma pizzę i nie może jej nigdzie odłożyć. Nie sprawdza pieców, bo ma zajęte ręce.
4. Pizze w piecach się pieką, potem stają się `Baked`, ale nikt ich nie wyjmuje (pomocnik jest zablokowany).
5. Po `TIME_TO_BURN` dodatkowych tickach pizza się spala — piec rzuca `PizzaBurnedException`.
6. Spalona pizza zostaje w piecu na zawsze (nikt jej nie usuwa), blokując piec.
7. Symulacja utyka — żadne zamówienie nie zostanie zrealizowane.

To jest prosty przypadek **wzajemnej blokady** (deadlock) — pracownik czeka na zasób (wolny piec), który nigdy się nie zwolni, bo to właśnie ten pracownik powinien go obsłużyć. Jak widać, nie trzeba programowania wielowątkowego, by wywołać taką sytuację!

**Krok 5 — napraw problem.**

Najprostsze rozwiązanie: gdy pomocnik nie znajdzie wolnego pieca, powinien odłożyć pizzę z powrotem do kolejki i zająć się czymś innym (np. sprawdzeniem pieców w następnym ticku).

Porównaj przypadek `Filled` w kodzie `PizzaShopCook` — kucharz już radzi sobie z tą sytuacją prawidłowo. Zastosuj analogiczne rozwiązanie w `PizzaShopHelper`.

Po naprawieniu uruchom `testBusyShop` — powinien przechodzić. Uruchom też wszystkie pozostałe testy.

> **Git:** Commit, np. *"Fix: helper no longer gets stuck with filled pizza"*.

**Dodatkowe wyzwanie (opcjonalne):** Co jeśli pech sprawi, że jakaś pizza mimo wszystko się spali? W obecnym kodzie spalona pizza zostaje w piecu na zawsze, blokując go. Spróbuj dodać obsługę spalonych pizz:
- Kto powinien wyjmować spalone pizze z pieca?
- Co powinno się stać ze spalonym zamówieniem — wyrzucić je i zamówić na nowo?
- Gdzie najlepiej umieścić tę logikę — w pracowniku, w piecu, czy w klasie `PizzaShop`?

### 3.6 Bonus: Głodny pomocnik

**Cel:** dodaj nowy typ pracownika — *"głodny pomocnik"* (`HungryHelper`), który implementuje interfejs `PizzaShopWorker`.

Niestety, nie każdy pracownik pizzerii jest profesjonalistą. Nasz nowy pomocnik ma pewną słabość — po dłuższej zmianie zaczyna się robić głodny. Widok kolejnej gotowej, pachnącej pizzy jest zbyt wielką pokusą.

**Zachowanie:**
- Głodny pomocnik zachowuje się tak jak zwykły `PizzaShopHelper` — podnosi gotowe pizze (`Filled`) z kolejki i ładuje je do pieców, wyjmuje upieczone pizze i pakuje do pudełek. Normalny, sumienny pracownik. Przez większość czasu.
- Różnica: co trzecia pizza w stanie `Filled`, którą podniesie z kolejki, zostaje "nadgryziona" — pomocnik zjada górny składnik (`.removeLast()`) i odkłada pizzę z powrotem do kolejki. Zostawia na niej ewidentne ślady zębów, ale liczy, że nikt nie zauważy.
- Pozostałe pizze obsługuje normalnie, choć pewnie im się przygląda.

Dlaczego `.removeLast()`? Bo nawet głodny pomocnik ma *pewne* standardy — grzebie po wierzchu, zdejmując ostatnio dodany składnik. Nie będzie przecież podważał sera spod peperoni jak archeolog na wykopaliskach.

W efekcie w kolejce pojawia się pizza z niepełną listą składników. Ktoś musi to wykryć i odpowiednio zareagować — pytanie, czy pizzeria chce to załatwić po cichu, czy profesjonalnie.

**Zanim zaczniesz kodować — pytania projektowe.**

Zastanów się nad tymi kwestiami, bo wpłyną na to, jak napiszesz testy i kod:

1. **Jak oznaczyć nadgryzioną pizzę?** Kilka opcji:
    - Nie oznaczać jawnie — porównać `ingredientsOnPizza.size()` z `recipe.ingredients().size()`. Jeśli lista jest krótsza niż przepis wymaga, pizza jest niekompletna. Nie pytamy dlaczego — po prostu jest.
    - Dodać flagę `boolean nibbled` do klasy `Pizza`. Brutalna szczerość.
    - Dodać nowy stan w `PizzaState` (np. `Nibbled`). Oficjalne uznanie problemu.
    - Zmienić stan pizzy z powrotem na `Pie` (bo wymaga dokończenia składania). Dyplomatyczne podejście — "pizza po prostu nie jest jeszcze gotowa".

   Każde podejście jest poprawne — wybierz to, które wydaje ci się najbardziej naturalne.

2. **Kto powinien obsłużyć nadgryzioną pizzę?**
    - Pomocnik (zwykły lub głodny) przy podjęciu z kolejki — wykrywa problem, wyrzuca pizzę i składa nowe zamówienie (`Ordered`)? To podejście profesjonalne i uczciwe wobec klientów.
    - Kucharz — wykrywa niekompletną pizzę i po cichu dokłada brakujące składniki? To podejście... ekonomiczne. Klient się nie dowie. Prawdopodobnie.
    - Coś innego?

3. **Jak zrefaktorować `PizzaShopHelper`, żeby `HungryHelper` mógł z niego skorzystać?**
   Głodny pomocnik robi *prawie* to samo co zwykły — różni się tylko w jednym momencie (co trzecie podjęcie). Kopiowanie całego kodu to złamanie zasady DRY (*Don't Repeat Yourself*). Zastanów się:
    - Które metody w `PizzaShopHelper` są `private`, a które mogłyby być `protected`, żeby podklasa mogła je nadpisać?
    - Czy `HungryHelper` powinien dziedziczyć po `PizzaShopHelper` (`extends`), czy lepiej zaimplementować interfejs `PizzaShopWorker` od zera?
    - Czy warto wydzielić wspólną logikę do osobnej metody, którą obie klasy mogą współdzielić?

   Dokumentacja o dziedziczeniu: [Oracle — Inheritance](https://docs.oracle.com/javase/tutorial/java/IandI/subclasses.html)

**Podejście TDD — zacznij od testów.**

**Test 1: Wykrywanie nadgryzionej pizzy.**

Niezależnie od tego, jakie podejście wybierzesz, pierwszy test powinien weryfikować, że *ktoś* w pizzerii potrafi rozpoznać niekompletną pizzę i odpowiednio zareagować. Nie chcemy wysyłać klientowi pizzy Peperoni z samym serem — nawet jeśli technicznie jest w stanie `Filled` i wygląda na gotową.

Stwórz pizzę `Filled` z brakującym składnikiem, umieść ją w kolejce, uruchom tick symulacji i sprawdź, co się z nią stało — czy trafiła do pieca w niekompletnym stanie, czy została przechwycona.

```java
@Test
public void testTamperedPizzaDetected() {
    PizzaRecipe recipe = shop.getMenu().get(1); // Peperoni: Cheese + Peperoni
    Pizza tampered = new Pizza(PizzaSize.L, recipe);
    tampered.state = PizzaState.Filled;
    tampered.ingredientsOnPizza.add(Ingredient.Cheese);
    // Missing Peperoni — only 1 of 2 ingredients!

    shop.pizzas.add(tampered);
    shop.simulate(List.of(), 1);

    // Verify the tampered pizza was NOT loaded into an oven
    for (PizzaFurnace f : shop.furnaces) {
        if (f.contents != null) {
            assertNotSame(tampered, f.contents,
                    "Tampered pizza should not be loaded into an oven");
        }
    }
}
```

Uruchom test — nie przechodzi, bo obecny `PizzaShopHelper` ładuje każdą pizzę `Filled` do pieca bez pytania. Bezgranicznie ufny. Zmodyfikuj odpowiedni fragment `PizzaShopHelper`, żeby test przeszedł.

**Test 2: Zachowanie głodnego pomocnika.**

Następnie napisz test weryfikujący, że `HungryHelper` dwie pierwsze pizze obsługuje normalnie, a trzecią nadgryza:

```java
@Test
public void testHungryHelper() {
    PizzaShop hungryShop = new PizzaShop(List.of(
            new PizzaRecipe("Margherita", List.of(Ingredient.Cheese))),
            0, 0, 2);
    HungryHelper hungryHelper = new HungryHelper(hungryShop);
    hungryShop.workers.add(hungryHelper);

    // Add 3 filled pizzas to the queue
    List<Pizza> pizzas = new ArrayList<>();
    for (int i = 0; i < 3; i++) {
        Pizza p = new Pizza(PizzaSize.L, hungryShop.getMenu().getFirst());
        p.state = PizzaState.Filled;
        p.ingredientsOnPizza.add(Ingredient.Cheese);
        hungryShop.pizzas.add(p);
        pizzas.add(p);
    }

    // Tick 1 — picks up first pizza, loads into oven (normal)
    hungryShop.update();
    assertEquals(PizzaState.Baking, pizzas.get(0).state);

    // Tick 2 — picks up second pizza, loads into oven (normal)
    hungryShop.update();
    assertEquals(PizzaState.Baking, pizzas.get(1).state);

    // Tick 3 — picks up third pizza, eats the top ingredient, puts back
    hungryShop.update();
    assertTrue(pizzas.get(2).ingredientsOnPizza.isEmpty(),
            "Third pizza should have its ingredient eaten");
    assertTrue(hungryShop.pizzas.contains(pizzas.get(2)),
            "Nibbled pizza should be back in the queue");
}
```

Zaimplementuj `HungryHelper` i upewnij się, że oba testy przechodzą. Na koniec uruchom **wszystkie** testy — w tym te sprawdzające, czy normalne pizze nadal poprawnie przechodzą przez system, mimo obecności głodnego sabotażysty w zespole.

> **Git:** Commit, np. *"Feature: add HungryHelper, detect tampered pizzas"*.

### 3.7 Zadania domowe (opcjonalne)

**Konkretne ćwiczenia:**

1. **Więcej przepisów.** Dodaj co najmniej 3 nowe przepisy do menu (np. Capricciosa, Veggie, BBQ Chicken). Napisz test dla każdego z nich, weryfikujący poprawność składników po wypieku.

2. **Rozmiar ma znaczenie.** Zmień logikę przygotowania pizzy tak, aby liczba każdego składnika zależała od rozmiaru: S = 1 sztuka, M = 2 sztuki, L = 3 sztuki. Napisz test sprawdzający, że pizza L z 2 składnikami w przepisie ma 6 elementów w `ingredientsOnPizza`.

3. **Sos jako enum.** Zamień pole `String sauce` na enum `Sauce` z wartościami: `Tomato`, `Garlic`, `BBQ`, `None`. Zmodyfikuj przepisy tak, aby każdy definiował rodzaj sosu. Napisz test weryfikujący, że różne przepisy używają różnych sosów.

4. **Statystyki szczegółowe.** Dodaj do `PizzaShop` statystykę średniego czasu przygotowania pizzy (od zamówienia do zapakowania). Wskazówka: dodaj pole `int orderTick` do klasy `Pizza`, zapisujące numer ticka, w którym pizza została zamówiona.

**Pomysły na dalszą eksplorację:**
- Co się stanie, jeśli piec będzie bardzo wolny (`BAKE_TIME = 50`), a zamówień bardzo dużo? Ile pizz się spali?
- Dodaj interfejs tekstowy: program pyta użytkownika o liczbę kucharzy, pomocników, pieców i zamówień, a następnie wyświetla wynik symulacji. Porównaj różne konfiguracje.
- Zaimplementuj wariant, w którym spalona pizza powoduje tymczasową awarię pieca — piec staje się niedostępny na kilka ticków. Jak to wpływa na pracę pizzerii?

---

## Rozwiązania i podpowiedzi

> Poniższe rozwiązania są przeznaczone jako pomoc, gdy utkniesz. Spróbuj najpierw rozwiązać zadanie samodzielnie — samo przekopiowanie kodu nie daje okazji do nauki. Zajęcia nie są oceniane — to plac zabaw do nauki Javy.

---

### Rozwiązanie 2.1: Błąd w `testPeperoni`

**Podpowiedzi:**
- Błąd dotyczy metody `preparePizza()` w klasie `PizzaShopCook`.
- Zwróć uwagę na przypadek `Pie` — jaki składnik jest zawsze dodawany?
- Porównaj dodawany składnik z listą składników w przepisie pizzy.

**Rozwiązanie:**

Problem polega na tym, że w przypadku `Pie` zawsze dodawany jest `Ingredient.Cheese`, niezależnie od przepisu. Linia:
```java
workedPizza.ingredientsOnPizza.add(Ingredient.Cheese);
```
powinna używać składnika z przepisu odpowiadającego aktualnej pozycji:
```java
case Pie -> {
    int idx = workedPizza.ingredientsOnPizza.size();
    if (idx < workedPizza.recipe.ingredients().size()) {
        workedPizza.ingredientsOnPizza.add(
                workedPizza.recipe.ingredients().get(idx));
    } else {
        workedPizza.state = PizzaState.Filled;
    }
}
```

---

### Rozwiązanie 3.2: Nowy przepis w menu

**Podpowiedzi:**
- Test oczekuje 3 przepisów w menu — dodaj trzeci w `resetShop()`.
- Upewnij się, że nowy przepis używa istniejących wartości z enuma `Ingredient`.

**Rozwiązanie:**

Zmień metodę `resetShop()` w `AppTest.java`:
```java
@BeforeEach
public void resetShop() {
    shop = new PizzaShop(List.of(
            new PizzaRecipe("Margherita", List.of(Ingredient.Cheese)),
            new PizzaRecipe("Peperoni", List.of(Ingredient.Cheese, Ingredient.Peperoni)),
            new PizzaRecipe("Hawaiian", List.of(Ingredient.Cheese, Ingredient.Ham, Ingredient.Pineapple))),
            1, 1, 1);
}
```

---

### Rozwiązanie 3.3: Sos na pizzy

**Podpowiedzi:**
- Wystarczy dodać jedno pole do klasy `Pizza`.

**Rozwiązanie:**

W klasie `Pizza` dodaj pole:
```java
String sauce = null;
```

---

### Rozwiązanie 3.4: Nakładanie sosu w trakcie przygotowania

**Podpowiedzi:**
- Naturalne miejsce na nałożenie sosu to przejście ze stanu `Dough` do `Pie` — sos nakłada się na ciasto, zanim dodaje się składniki.
- Wystarczy jedna dodatkowa linia w istniejącym przypadku `Dough`.

**Rozwiązanie:**

W metodzie `preparePizza()` klasy `PizzaShopCook` zmodyfikuj przypadek `Dough`:
```java
case Dough -> {
    workedPizza.sauce = "Tomato";
    workedPizza.state = PizzaState.Pie;
}
```

Jeśli chcesz, żeby rodzaj sosu zależał od przepisu, możesz rozbudować `PizzaRecipe` o dodatkowe pole — to dobry temat na zadanie domowe.

---

### Rozwiązanie 3.5: Blokada pizzerii

**Analiza problemu krok po kroku:**

Przy konfiguracji 3 kucharzy, 1 pomocnik, 2 piece, 6 zamówień:

1. Kucharze równolegle przygotowują pizze (ticki 1–6). Dwie pierwsze trafiają do pieców.
2. Pomocnik podnosi trzecią gotową pizzę (`Filled`) z kolejki i próbuje załadować do pieca — oba zajęte.
3. W kodzie `PizzaShopHelper.handlePizza()`, przypadek `Filled`, po nieudanej pętli po piecach nie ma żadnego kodu — **pomocnik utyka** z pizzą w rękach:
```java
case Filled -> {
    for (PizzaFurnace f : shop.furnaces) {
        if (f.contents == null) {
            f.loadPizza(workedPizza);
            workedPizza = null;
            return;
        }
    }
    // Nothing here! Helper is stuck forever!
}
```
4. Piece dopiekają pizze → `Baked` → nikt nie wyjmuje → `Burned` → `PizzaBurnedException`.
5. Spalone pizze zostają w piecach, piece są zablokowane, pomocnik jest zablokowany — pełna blokada.

**Rozwiązanie — odłóż pizzę i wróć do sprawdzania pieców:**

Dodaj po pętli w przypadku `Filled` kod odkładający pizzę do kolejki, analogicznie do tego, co robi kucharz:

```java
case Filled -> {
    for (PizzaFurnace f : shop.furnaces) {
        if (f.contents == null) {
            f.loadPizza(workedPizza);
            workedPizza = null;
            return;
        }
    }
    // No free oven — put pizza back in queue
    shop.pizzas.add(workedPizza);
    workedPizza = null;
}
```

Dzięki temu w następnym ticku pomocnik:
1. Nie trzyma żadnej pizzy (`workedPizza == null`).
2. W `update()` najpierw wywołuje `checkFurnace()`.
3. Znajduje upieczoną pizzę → wyjmuje ją → pakuje do pudełka.
4. Piec jest wolny — w kolejnym cyklu pomocnik załaduje czekającą pizzę.

Uruchom `testBusyShop` po poprawce — powinien przechodzić.

**Rozwiązania alternatywne** (do przemyślenia, nie wymagane):

*Wariant A — kucharze sprawdzają piece.*
Dodaj do `PizzaShopCook.update()` sprawdzanie pieców, gdy kucharz jest wolny. Kucharz mógłby wyjmować upieczone (albo spalone) pizze z pieca, gdy nie ma co robić. Wymaga to rozbudowy logiki kucharza o obsługę stanów `Baked` i `Boxed`, oraz ewentualnie `Burned`.

*Wariant B — automatyczny wyrzutnik.*
Zmień `PizzaFurnace` tak, by po spaleniu pizzy piec sam czyścił `contents` (zamiast rzucać wyjątek, lub po rzuceniu wyjątku). To najprostsze rozwiązanie, ale nierealistyczne i ukrywa problem. Pamiętaj, że spalone zamówienie powinno zostać ponowione — w przeciwnym razie klient nie dostanie pizzy.

*Wariant C — obsługa spalonych pizz.*
Dodaj logikę, która po złapaniu `PizzaBurnedException` w `PizzaShop.update()` czyści piec i wstawia nowe zamówienie z tą samą recepturą i rozmiarem z powrotem do kolejki. Wymaga zapamiętania przepisu i rozmiaru spalonej pizzy.

Każde z tych rozwiązań ma swoje zalety i wady. Wariant z odkładaniem pizzy jest najprostszy i naprawia główną przyczynę problemu — dlatego jest rekomendowany.

---

### Rozwiązanie 3.6: Głodny pomocnik (Hungry Helper)

**Podpowiedzi krok po kroku:**

1. **Refactoring `PizzaShopHelper`.** Żeby `HungryHelper` mógł dziedziczyć po `PizzaShopHelper` i nadpisywać tylko moment podjęcia pizzy z kolejki, metoda `pickUpFilledPizza()` musi być `protected` zamiast `private`. To samo dotyczy pola `workedPizza`. Zmień widoczność tych elementów, zanim zaczniesz pisać `HungryHelper`.

2. **Walidacja składników.** Porównaj rozmiar `ingredientsOnPizza` z rozmiarem `recipe.ingredients()`. Jeśli lista jest krótsza, pizza jest niekompletna. Nie wnikamy w przyczynę — może to głodny pomocnik, może losowa anomalia kosmiczna. Ważne, że nie wolno jej wsadzić do pieca.

3. **Co zrobić z niekompletną pizzą?** Rekomendowane podejście: wyczyść składniki, ustaw stan na `Ordered`, zostaw w kolejce. Kucharz przygotuje ją od nowa, nie zadając zbędnych pytań. Kontrola jakości, nie śledztwo.

**Rozwiązanie — refactoring PizzaShopHelper:**

Zmień widoczność metody i dodaj walidację:

```java
class PizzaShopHelper implements PizzaShopWorker {
    PizzaShop shop;
    Pizza workedPizza = null;  // package-private — accessible to subclasses

    // ... constructor and update() unchanged ...

    protected void pickUpFilledPizza() {
        for (Pizza pizza : shop.pizzas) {
            if (pizza.state == PizzaState.Filled) {
                // Quality control — verify ingredient count before accepting
                if (pizza.ingredientsOnPizza.size() < pizza.recipe.ingredients().size()) {
                    // Incomplete pizza detected — discard and reorder
                    pizza.ingredientsOnPizza.clear();
                    pizza.state = PizzaState.Ordered;
                    continue;
                }
                workedPizza = pizza;
                break;
            }
        }
        if (workedPizza != null) {
            shop.pizzas.remove(workedPizza);
        }
    }

    // ... rest unchanged ...
}
```

**Rozwiązanie — klasa HungryHelper:**

```java
class HungryHelper extends PizzaShopHelper {
    int pickupCount = 0;

    public HungryHelper(PizzaShop shop) {
        super(shop);
    }

    @Override
    protected void pickUpFilledPizza() {
        super.pickUpFilledPizza();

        if (workedPizza != null) {
            pickupCount++;
            if (pickupCount % 3 == 0 && !workedPizza.ingredientsOnPizza.isEmpty()) {
                // The temptation is too great...
                workedPizza.ingredientsOnPizza.removeLast();
                shop.pizzas.add(workedPizza);
                workedPizza = null;
            }
        }
    }
}
```

Głodny pomocnik wywołuje `super.pickUpFilledPizza()` — dzięki temu korzysta z logiki walidacji z klasy bazowej (w tym wykrywania niekompletnych pizz, nawet tych nadgryzionych przez siebie samego w poprzednim cyklu — zero wyrzutów sumienia). Jedyna różnica: po podjęciu co trzeciej pizzy zjada górny składnik i odkłada ją. W kolejnym cyklu ta sama lub inna instancja pomocnika wykryje niekompletność i zresetuje pizzę do stanu `Ordered`.

Zwróć uwagę na elegancję tego rozwiązania: głodny pomocnik nie musi wiedzieć, jak obsługiwać nadgryzioną pizzę — to robota zwykłego pomocnika w ramach standardowej kontroli jakości. Zasada jednej odpowiedzialności (*Single Responsibility Principle*) w akcji: `HungryHelper` odpowiada za nadgryzanie, `PizzaShopHelper` za wykrywanie i naprawę. Sprawna organizacja — poza tą jedną drobną kwestią kadrową.

**Rozwiązania alternatywne:**

*Wariant A — flaga `nibbled`.*
Dodaj pole `boolean nibbled = false` do klasy `Pizza`. Głodny pomocnik ustawia flagę po nadgryzieniu. Pomocnicy sprawdzają flagę zamiast liczyć składniki. Prostsze, ale wymaga modyfikacji klasy `Pizza` i pamiętania o resetowaniu flagi. To trochę jak przywieszanie karteczki "NIE JEŚĆ" do pizzy — działa, ale nie rozwiązuje problemu systemowo.

*Wariant B — stan `Nibbled` w enumie.*
Dodaj `Nibbled` do `PizzaState`. Głodny pomocnik zmienia stan pizzy na `Nibbled`. Pomocnicy lub kucharze szukają pizz w tym stanie i obsługują je odpowiednio. Czytelne, ale sztywne — nowy stan w enumie to zmiana widoczna w całym programie. Poza tym oficjalne uznanie nadgryzania pizz w systemie informatycznym pizzerii mogłoby wzbudzić pewne wątpliwości audytorów.

*Wariant C — zmiana stanu na `Pie`.*
Zamiast dodawać nowe oznaczenia, głodny pomocnik zmienia stan pizzy z powrotem na `Pie` po zjedzeniu składnika. Kucharz, który szuka pizz w stanie `Ordered`, musiałby wtedy szukać też `Pie`. Logicznie spójne (pizza wymaga dokończenia składania), ale wymaga zmiany logiki kucharza.

Każde podejście jest poprawne. Rozwiązanie referencyjne (porównywanie list) nie wymaga żadnych zmian w klasie `Pizza` — wykorzystuje dane, które już tam są. Poza tym, gdyby ktoś inny niż głodny pomocnik uszkodził pizzę (np. spadła ze stołu, kot wszedł do kuchni, anomalia kosmiczna), walidacja nadal zadziała.
