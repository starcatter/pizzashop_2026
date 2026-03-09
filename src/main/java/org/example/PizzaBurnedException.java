package org.example;

class PizzaBurnedException extends Exception {
    final Pizza burnedPizza;

    public PizzaBurnedException(Pizza burnedPizza) {
        super("Pizza was burned! [%s]".formatted(burnedPizza));
        this.burnedPizza = burnedPizza;
    }
}
