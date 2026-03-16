package org.example;

import java.util.Optional;

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