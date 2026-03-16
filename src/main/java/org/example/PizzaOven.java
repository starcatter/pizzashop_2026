package org.example;

import java.util.Optional;

class PizzaOven {
    static final int BAKE_TIME = 5;
    static final int TIME_TO_BURN = 10;

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