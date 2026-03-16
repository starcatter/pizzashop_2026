package org.example;

import java.util.Optional;

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