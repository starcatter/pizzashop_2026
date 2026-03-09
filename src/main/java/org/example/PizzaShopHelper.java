package org.example;

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
        } else if (!checkOven()) {
            pickUpFilledPizza();
        }
    }

    private boolean checkOven() {
        for (PizzaOven oven : shop.ovens) {
            if (oven.contents != null && oven.contents.state == PizzaState.Baked) {
                workedPizza = oven.contents;
                oven.contents = null;
                return true;
            }
        }
        return false;
    }

    protected void pickUpFilledPizza() {
        for (Pizza pizza : shop.pizzas) {
            if (pizza.state == PizzaState.Filled) {
                // Check if pizza has correct number of ingredients
                if (pizza.ingredientsOnPizza.size() < pizza.recipe.ingredients().size()) {
                    // Incomplete pizza — discard and reorder
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

    private void handlePizza() {
        switch (workedPizza.state) {
            case Filled -> {
                for (PizzaOven oven : shop.ovens) {
                    if (oven.contents == null) {
                        oven.loadPizza(workedPizza);
                        workedPizza = null;
                        return;
                    }
                }
                // No free oven — put pizza back in queue
                shop.pizzas.add(workedPizza);
                workedPizza = null;
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