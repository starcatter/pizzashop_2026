package org.example;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

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

    // --- Menu & Workers ---

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