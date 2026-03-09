package org.example;

class PizzaOven {
    static final int BAKE_TIME = 5;
    static final int TIME_TO_BURN = 10;

    Pizza contents = null;
    int timeLeft = 0;
    int burnTimer = 0;

    void update() throws PizzaBurnedException {
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