package org.example;

import java.util.ArrayList;
import java.util.List;

enum PizzaState {
    Ordered,
    Dough,
    Pie,
    Filled,
    Baking,
    Baked,
    Burned,
    Boxed
}

enum PizzaSize {
    S,
    M,
    L
}

enum Ingredient {
    Cheese,
    Peperoni,
    Ham,
    Mushrooms,
    Pineapple
}

record PizzaRecipe(String name, List<Ingredient> ingredients) {
}

interface PizzaShopWorker {
    void update();
}

class PizzaShopCook implements PizzaShopWorker {
    PizzaShop shop;
    Pizza workedPizza = null;

    public PizzaShopCook(PizzaShop shop) {
        this.shop = shop;
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
        for (Pizza pizza : shop.pizzas) {
            if (pizza.state == PizzaState.Ordered) {
                workedPizza = pizza;
                break;
            }
        }
        if (workedPizza != null) {
            shop.pizzas.remove(workedPizza);
        }
    }

    private void preparePizza() {
        switch (workedPizza.state) {
            case Ordered -> {
                workedPizza.state = PizzaState.Dough;
            }
            case Dough -> {
                workedPizza.state = PizzaState.Pie;
            }
            case Pie -> {
                if (workedPizza.ingredientsOnPizza.size() < workedPizza.recipe.ingredients().size()) {
                    workedPizza.ingredientsOnPizza.add(Ingredient.Cheese);
                } else {
                    workedPizza.state = PizzaState.Filled;
                }
            }
            case Filled -> {
                for (PizzaFurnace f : shop.furnaces) {
                    if (f.contents == null) {
                        f.loadPizza(workedPizza);
                        workedPizza = null;
                        return;
                    }
                }
                // No free oven — cook puts pizza back to the order queue and is free to work on another order
                shop.pizzas.add(workedPizza);
                workedPizza = null;
            }
            default -> {
                shop.pizzas.add(workedPizza);
                workedPizza = null;
            }
        }
    }
}

class PizzaShopHelper implements PizzaShopWorker {
    PizzaShop shop;
    Pizza workedPizza = null;

    public PizzaShopHelper(PizzaShop shop) {
        this.shop = shop;
    }

    @Override
    public void update() {
        if (workedPizza != null) {
            handlePizza();
        } else if (!checkFurnace()) {
            pickUpFilledPizza();
        }
    }

    private boolean checkFurnace() {
        for (PizzaFurnace f : shop.furnaces) {
            if (f.contents != null && f.contents.state == PizzaState.Baked) {
                workedPizza = f.contents;
                f.contents = null;
                return true;
            }
        }
        return false;
    }

    private void pickUpFilledPizza() {
        for (Pizza pizza : shop.pizzas) {
            if (pizza.state == PizzaState.Filled) {
                workedPizza = pizza;
                break;
            }
        }
        if (workedPizza != null) {
            shop.pizzas.remove(workedPizza);
        }
    }

    private void handlePizza() {
        switch (workedPizza.state) {
            case Filled -> {
                for (PizzaFurnace f : shop.furnaces) {
                    if (f.contents == null) {
                        f.loadPizza(workedPizza);
                        workedPizza = null;
                        return;
                    }
                }
            }
            case Baking -> {
                throw new RuntimeException("Hot pizza!");
            }
            case Baked -> {
                workedPizza.state = PizzaState.Boxed;
                shop.pizzas.add(workedPizza);
                shop.pizzasPrepared++;
                workedPizza = null;
            }
            default -> {
                shop.pizzas.add(workedPizza);
                workedPizza = null;
            }
        }
    }
}

class Pizza {
    PizzaSize size;
    PizzaRecipe recipe;
    List<Ingredient> ingredientsOnPizza = new ArrayList<>();
    PizzaState state;

    public Pizza(PizzaSize size, PizzaRecipe recipe) {
        this.size = size;
        this.recipe = recipe;
        this.state = PizzaState.Ordered;
    }

    @Override
    public String toString() {
        return "Pizza{" +
                "size=" + size +
                ", recipe=" + recipe +
                ", ingredientsOnPizza=" + ingredientsOnPizza +
                ", state=" + state +
                '}';
    }
}

class PizzaBurnedException extends Exception{
    final Pizza burnedPizza;
    public PizzaBurnedException(Pizza burnedPizza) {
        super("Pizza was burned! [%s]".formatted(burnedPizza));
        this.burnedPizza = burnedPizza;
    }
}

class PizzaFurnace {
    static final int BAKE_TIME = 5;
    static final int TIME_TO_BURN = 3;

    Pizza contents = null;
    int timeLeft = 0;
    int burnTimer = 0;

    void update() throws PizzaBurnedException{
        if (contents == null) {
            return;
        }
        if (contents.state == PizzaState.Baking) {
            timeLeft--;
            if (timeLeft <= 0) {
                contents.state = PizzaState.Baked;
                burnTimer = TIME_TO_BURN;
            }
        } else if (contents.state == PizzaState.Baked) {
            burnTimer--;
            if (burnTimer <= 0) {
                contents.state = PizzaState.Burned;
                throw new PizzaBurnedException(contents);
            }
        }
    }

    public void loadPizza(Pizza pizza) {
        contents = pizza;
        contents.state = PizzaState.Baking;
        timeLeft = BAKE_TIME;
        burnTimer = 0;
    }
}

class PizzaShop {
    List<Pizza> pizzas = new ArrayList<>();
    List<PizzaRecipe> menu;
    List<PizzaShopWorker> workers = new ArrayList<>();
    List<PizzaFurnace> furnaces = new ArrayList<>();

    int ticksPassed = 0;
    int pizzasPrepared = 0;
    int pizzasBurned = 0;

    public PizzaShop(List<PizzaRecipe> menu, int cookCount, int helperCount, int furnaceCount) {
        this.menu = menu;

        for (int i = 0; i < cookCount; i++) {
            workers.add(new PizzaShopCook(this));
        }

        for (int i = 0; i < helperCount; i++) {
            workers.add(new PizzaShopHelper(this));
        }

        for (int i = 0; i < furnaceCount; i++) {
            furnaces.add(new PizzaFurnace());
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

    public void update() {
        ticksPassed++;

        for (PizzaShopWorker worker : workers) {
            worker.update();
        }

        for (PizzaFurnace furnace : furnaces) {
            try {
                furnace.update();
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

public class PizzaApp {

    public static void main(String[] args) {
        PizzaShop shop = new PizzaShop(List.of(
                new PizzaRecipe("Margherita", List.of(Ingredient.Cheese)),
                new PizzaRecipe("Peperoni", List.of(Ingredient.Cheese, Ingredient.Peperoni))),
                1, 1, 1);

        List<Pizza> orderedPizzas = new ArrayList<>();
        for (PizzaRecipe recipe : shop.getMenu()) {
            orderedPizzas.add(shop.placeOrder(recipe, PizzaSize.L));
        }

        boolean success = shop.simulate(orderedPizzas, 100);

        System.out.println("--- Simulation complete ---");
        System.out.println("Success: " + success);
        System.out.println("Ticks: " + shop.getTicksPassed());
        System.out.println("Pizzas prepared: " + shop.getPizzasPrepared());
        System.out.println("Pizzas burned: " + shop.getPizzasBurned());

        for (Pizza pizza : orderedPizzas) {
            System.out.println(pizza);
        }
    }
}