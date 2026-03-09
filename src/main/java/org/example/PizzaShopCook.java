package org.example;

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
                workedPizza.sauce = "Tomato";
                workedPizza.state = PizzaState.Pie;
            }
            case Pie -> {
                int idx = workedPizza.ingredientsOnPizza.size();
                if (idx < workedPizza.recipe.ingredients().size()) {
                    workedPizza.ingredientsOnPizza.add(
                            workedPizza.recipe.ingredients().get(idx));
                } else {
                    workedPizza.state = PizzaState.Filled;
                }
            }
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
            default -> {
                shop.pizzas.add(workedPizza);
                workedPizza = null;
            }
        }
    }
}