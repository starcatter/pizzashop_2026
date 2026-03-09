package org.example;

import java.util.ArrayList;
import java.util.List;

class PizzaShop {
    List<Pizza> pizzas = new ArrayList<>();
    List<PizzaRecipe> menu;
    List<PizzaShopWorker> workers = new ArrayList<>();
    List<PizzaOven> ovens = new ArrayList<>();

    int ticksPassed = 0;
    int pizzasPrepared = 0;
    int pizzasBurned = 0;

    public PizzaShop(List<PizzaRecipe> menu, int cookCount, int helperCount, int ovenCount) {
        this.menu = menu;

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

    public Pizza placeOrder(PizzaRecipe recipe, PizzaSize size) {
        Pizza p = new Pizza(size, recipe);
        pizzas.add(p);
        return p;
    }

    public List<PizzaRecipe> getMenu() {
        return menu;
    }

    public void addWorker(PizzaShopWorker worker) {
        workers.add(worker);
    }

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

            boolean allDone = true;
            for (Pizza pizza : orderedPizzas) {
                if (pizza.state != PizzaState.Boxed) {
                    allDone = false;
                    break;
                }
            }

            if (allDone) {
                return true;
            }
        }
        return false;
    }

    public int getTicksPassed() {
        return ticksPassed;
    }

    public int getPizzasPrepared() {
        return pizzasPrepared;
    }

    public int getPizzasBurned() {
        return pizzasBurned;
    }
}